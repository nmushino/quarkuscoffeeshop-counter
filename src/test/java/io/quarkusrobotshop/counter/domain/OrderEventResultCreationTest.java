package io.quarkusrobotshop.counter.domain;

import io.quarkusrobotshop.counter.domain.Order;
import io.quarkusrobotshop.counter.domain.commands.PlaceOrderCommand;
import io.quarkusrobotshop.counter.domain.valueobjects.OrderEventResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderEventResultCreationTest {

    @Test
    public void testOrderCreationWithSingleBeverage() {

        PlaceOrderCommand placeOrderCommand = TestUtil.stubPlaceOrderCommand();
        OrderEventResult orderEventResult = Order.createFromCommand(placeOrderCommand);
        assertNotNull(orderEventResult.getOrder());
        assertNotNull(orderEventResult.getOrder().getOrderId());
        assertEquals(1, orderEventResult.getOrder().getHomerobotLineItems().get().size());
        assertFalse(orderEventResult.getOrder().getProrobotLineItems().isPresent());
    }

    @Test
    public void testOrderCreationWithSingleCroissant() {

        PlaceOrderCommand placeOrderCommand = TestUtil.stubPlaceOrderCommandSingleCroissant();
        OrderEventResult orderEventResult = Order.createFromCommand(placeOrderCommand);
        assertNotNull(orderEventResult.getOrder());
        assertNotNull(orderEventResult.getOrder().getOrderId());
        assertEquals(1, orderEventResult.getOrder().getProrobotLineItems().get().size());
        assertFalse(orderEventResult.getOrder().getHomerobotLineItems().isPresent());
    }

    @Test
    public void testOrderCreationWithBeverageAndProrobotItems() {

        PlaceOrderCommand placeOrderCommand = TestUtil.stubPlaceOrderCommandBlackrobotAndCroissant();
        OrderEventResult orderEventResult = Order.createFromCommand(placeOrderCommand);
        assertNotNull(orderEventResult.getOrder());
        assertNotNull(orderEventResult.getOrder().getOrderId());
        assertEquals(1, orderEventResult.getOrder().getHomerobotLineItems().get().size());
        assertEquals(1, orderEventResult.getOrder().getProrobotLineItems().get().size());
    }

}
