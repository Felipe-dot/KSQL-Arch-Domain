package com.application.order.list.external;

import com.application.order.list.OrderListOutput;
import com.domain.order.OrderGateway;
import com.domain.pagination.Pagination;
import com.domain.pagination.SearchQuery;

public class DefaultListOrderUseCaseExternalImpl extends ListOrderUseCaseExternal {

    private final OrderGateway orderGateway;

    public DefaultListOrderUseCaseExternalImpl(final OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

    @Override
    public Pagination<OrderListOutput> execute(final SearchQuery searchQuery) {
        return this.orderGateway.findAllOrders(searchQuery)
                .map(OrderListOutput::from);
    }
}
