package com.application.order;

import java.math.BigDecimal;

public record CreateOrderCommand(BigDecimal orderValue,String channel, String paymentStatus) {

    public static CreateOrderCommand apply
            (BigDecimal orderValue, String channel, String paymentStatus){
        return new CreateOrderCommand(orderValue,channel,paymentStatus);
    }
}
