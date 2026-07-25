package com.infrastructure.order;

import com.domain.order.Order;
import com.domain.order.OrderGateway;
import com.domain.order.OrderID;
import com.domain.pagination.Pagination;
import com.domain.pagination.SearchQuery;
import com.infrastructure.order.persistence.OrderJPARepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


import java.util.Optional;

import static com.infrastructure.utils.InfraStructureUtils.like;
import static com.infrastructure.utils.InfraStructureUtils.createAtToday;

@Component
@Qualifier("orderSQLGateway")
public class OrderSQLGateway implements OrderGateway {

    private final OrderJPARepository orderJPARepository;
    private final RedisTemplate<String,OrderEntity> redisTemplate;
    private final KafkaTemplate<String,OrderEntity> kafkaTemplateEntity;
    private final AmqpTemplate amqpTemplate;

    public OrderSQLGateway(final OrderJPARepository orderJPARepository,
                           final RedisTemplate<String, OrderEntity> redisTemplate,
                           final KafkaTemplate<String, OrderEntity> kafkaTemplateEntity,
                           final AmqpTemplate amqpTemplate) {
        this.orderJPARepository = orderJPARepository;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplateEntity = kafkaTemplateEntity;
        this.amqpTemplate = amqpTemplate;
    }

    @Override
    public Order persist(Order order) {
        try {
            redisTemplate.opsForValue().set(order.getId().valueId(),OrderEntity.from(order));
            //kafkaTemplateEntity.send("order-guarantee-save-db", order.getId().valueId(),OrderEntity.from(order));
            amqpTemplate.convertAndSend("order-exchange","order-guarantee-save-db", OrderEntity.from(order));
            return order;
        }catch (Exception cause){
            throw new RuntimeException("Failed to persist order: " + cause.getMessage(), cause);
        }
    }

    @Override
    public Order update(Order order) {
        return persist(order);
    }

    @Override
    public Optional<Order> findById(OrderID id) {

        if (id == null || id.valueId() == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        try {
            OrderEntity orderEntity = redisTemplate.opsForValue().get(id.valueId());
            if (orderEntity != null) {
                return Optional.of(orderEntity.aggregation());
            }
        } catch (Exception cause) {
            throw new RuntimeException("Failed to find order by ID: " + cause.getMessage(), cause);
        }

        //Fallback to database if not found in Redis
        Optional<Order> orderToReturn = this.orderJPARepository.findById(id.valueId())
                .map(OrderEntity::aggregation);

        return orderToReturn;
    }

    @Override
    public void deleteOrderById(OrderID id) {
        final String orderId = id.valueId();
        if (this.orderJPARepository.existsById(orderId)) {
            this.orderJPARepository.deleteById(orderId);
        }
    }

    @Override
    public Pagination<Order> findAllOrders(SearchQuery query) {
        final var pages = PageRequest.of(query.currentPage(), query.page(),
                Sort.by(Sort.Direction.fromString(query.direction()), query.sort()));

        final var specification = Optional.ofNullable(query.terms())
                .filter(s -> !s.isBlank())
                .map(this::filterAndExecute)
                .orElse(null);

        final var todaySpecification = createAtToday();
        final var combinedSpecification = Specification.where(specification).and(todaySpecification);
        final var pageResult = this.orderJPARepository
                .findAll(Specification.where(combinedSpecification), pages);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(OrderEntity::aggregation).toList()
        );
    }

    private Specification<OrderEntity> filterAndExecute(String query) {
        final Specification<OrderEntity> orderId = like("id", query);
        final Specification<OrderEntity> channel = like("channel", query);
        return orderId.or(channel);
    }
}
