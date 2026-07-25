package com.infrastructure.order.persistence;

import com.domain.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class OrderJDBCRepository {

    private static final Logger LOG = LoggerFactory.getLogger(OrderJDBCRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public OrderJDBCRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Order persist(Order order) {

        final String SQL =
                "INSERT INTO order_oms " +
                        "(id,order_value,channel,payment_status, created_at, updated_at)" +
                        " VALUES (?, ?, ?, ?, ? , ?)";

        final var orderId = order.getId().valueId();

        try {

            jdbcTemplate.update(SQL, orderId, order.getOrderValue(), order.getChannel(), order.getPaymentStatus(),
                    order.getCreated(), order.getUpdated());

            LOG.info("ORDER INSERTED VIA JDBC {}", order.getId().valueId());
        } catch (Exception cause) {
            LOG.error(cause.getMessage());
        }
        return order;
    }
}
