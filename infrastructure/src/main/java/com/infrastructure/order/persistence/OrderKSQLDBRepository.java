package com.infrastructure.order.persistence;

import com.domain.order.Order;
import com.domain.order.OrderID;
import com.infrastructure.order.OrderEntity;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.Row;
import io.confluent.ksql.api.client.StreamedQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.infrastructure.utils.InfraStructureUtils.*;

@Repository
public class OrderKSQLDBRepository {

    private static final Logger LOG = LoggerFactory.getLogger(OrderKSQLDBRepository.class);
    private final Client ksqlClient;

    public OrderKSQLDBRepository(Client ksqlClient) {
        this.ksqlClient = ksqlClient;
    }

    public Page<OrderEntity> findAll(final Specification<OrderEntity> specification, final Pageable pageable, final String parameter) {
        StringBuilder SQL = new StringBuilder("SELECT * FROM oms_table");

        try {
            if (specification != null) {
                SQL.append(" WHERE channel ").append(convert(parameter));
            }
            SQL.append(String.format(" LIMIT %d;", pageable.getPageSize() + pageable.getOffset()));

            LOG.info("Final Query: {}", SQL);

            CompletableFuture<StreamedQueryResult> queryResultFuture = ksqlClient.streamQuery(SQL.toString());
            StreamedQueryResult queryResult = queryResultFuture.get();
            Row row;
            List<OrderEntity> ordersList = new ArrayList<>();
            while ((row = queryResult.poll()) != null) {
                ordersList.add(OrderEntity.from(mapRowForOrdersList(row)));
            }
            LOG.debug("Select Orders By K-SQL ");
            return new PageImpl<>(ordersList, pageable, ordersList.size());
        } catch (Exception cause) {
            LOG.error("Failed to Execute Query K-SQL : {0}", cause.getCause());
        }
        return Page.empty();
    }

    private Order mapRowForOrdersList(Row row) {
        return Order.aggregate(OrderID.from(row.getString("ORDERID")),
                row.getDecimal("ORDERVALUE"),
                row.getString("CHANNEL"),
                row.getString("PAYMENTSTATUS"),
                Instant.now(), Instant.now());
    }
}
