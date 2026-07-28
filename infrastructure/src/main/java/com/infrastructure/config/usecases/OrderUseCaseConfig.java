package com.infrastructure.config.usecases;

import com.application.order.check.CheckOrderUseCase;
import com.application.order.check.DefaultCheckOrderUseCaseImpl;
import com.application.order.create.CreateOrderUseCase;
import com.application.order.create.DefaultCreateOrderUseCaseImpl;
import com.application.order.delete.DefaultDeleteOrderUseCaseImpl;
import com.application.order.delete.DeleteOrderUseCase;
import com.application.order.get.DefaultGetOrderByUseCaseImpl;
import com.application.order.get.GetOrderByUseCase;
import com.application.order.list.DefaultListOrderUseCaseImpl;
import com.application.order.list.ListOrderUseCase;
import com.application.order.list.external.DefaultListOrderUseCaseExternalImpl;
import com.application.order.list.external.ListOrderUseCaseExternal;
import com.application.order.update.DefaultUpdateOrderUseCaseImpl;
import com.application.order.update.UpdateOrderUseCase;
import com.domain.order.OrderGateway;
import com.domain.order.report.OrderReportGateway;
import com.infrastructure.order.OrderEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderUseCaseConfig {

    private final OrderGateway orderGateway;
    private final OrderGateway orderKSQLGateway;
    private final OrderReportGateway orderReportGateway;

    public OrderUseCaseConfig(@Qualifier("orderSQLGateway") OrderGateway orderGateway,
                              @Qualifier("orderKSQLGateway") OrderGateway orderKSQLGateway,
                              OrderReportGateway orderReportGateway) {
        this.orderGateway = orderGateway;
        this.orderKSQLGateway = orderKSQLGateway;
        this.orderReportGateway = orderReportGateway;
    }

    @Bean
    public CreateOrderUseCase createOrderUseCase() {
        return new DefaultCreateOrderUseCaseImpl(orderGateway);
    }

    @Bean
    public UpdateOrderUseCase updateOrderUseCase() {
        return new DefaultUpdateOrderUseCaseImpl(orderGateway);
    }

    @Bean
    public GetOrderByUseCase getOrderByUseCase() {
        return new DefaultGetOrderByUseCaseImpl(orderGateway);
    }

    @Bean
    public ListOrderUseCase listOrderUseCase() {
        return new DefaultListOrderUseCaseImpl(orderGateway);
    }

    @Bean
    public ListOrderUseCaseExternal listOrderUseCaseExternal() {
        return new DefaultListOrderUseCaseExternalImpl(orderKSQLGateway);
    }

    @Bean
    public DeleteOrderUseCase deleteOrderUseCase() {
        return new DefaultDeleteOrderUseCaseImpl(orderGateway);
    }

    @Bean
    public CheckOrderUseCase checkOrderUseCase() {
        return new DefaultCheckOrderUseCaseImpl(orderReportGateway);
    }

}
