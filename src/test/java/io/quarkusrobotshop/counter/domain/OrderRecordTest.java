package io.quarkusrobotshop.counter.domain;

import io.quarkusrobotshop.counter.domain.Order;
import io.quarkusrobotshop.counter.domain.valueobjects.OrderEventResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderRecordTest {

    @Test
    public void testOrderRecord(){

        OrderEventResult orderEventResult = Order.createFromCommand(TestUtil.stubPlaceOrderCommand());
        assertNotNull(orderEventResult.getOrder().getOrderId());
        System.out.println(orderEventResult.getOrder());
    };
}
