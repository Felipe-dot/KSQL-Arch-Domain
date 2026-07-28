package com.application.order.check;

import com.domain.order.report.OrderReportGateway;

public class DefaultCheckOrderUseCaseImpl extends CheckOrderUseCase {

    private final OrderReportGateway orderReportGateway;

    public DefaultCheckOrderUseCaseImpl(final OrderReportGateway orderReportGateway) {
        this.orderReportGateway = orderReportGateway;
    }

    @Override
    public Boolean execute(final String orderId) {
        return true;
    }
}
