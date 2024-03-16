package io.quarkusrobotshop.counter.domain;

import io.quarkusrobotshop.counter.domain.Order;
import io.quarkusrobotshop.counter.domain.commands.PlaceOrderCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderCreationTest {

    @Test
    public void testOrderCreationWithSingleBeverage() {

        PlaceOrderCommand placeOrderCommand = TestUtil.stubPlaceOrderCommand();
        Order order = Order.fromPlaceOrderCommand(placeOrderCommand);
        assertNotNull(order);
        assertNotNull(order.getOrderId());
        assertEquals(1, order.getHomerobotLineItems().get().size());
        assertFalse(order.getProrobotLineItems().isPresent());
    }

    @Test
    public void testOrderCreationWithSingleCroissant() {

        PlaceOrderCommand placeOrderCommand = TestUtil.stubPlaceOrderCommandSingleCroissant();
        Order order = Order.fromPlaceOrderCommand(placeOrderCommand);
        assertNotNull(order);
        assertNotNull(order.getOrderId());
        assertEquals(1, order.getProrobotLineItems().get().size());
        assertFalse(order.getHomerobotLineItems().isPresent());
    }

    @Test
    public void testOrderCreationWithBeverageAndProrobotItems() {

        PlaceOrderCommand placeOrderCommand = TestUtil.stubPlaceOrderCommandBlackCoffeeAndCroissant();
        Order order = Order.fromPlaceOrderCommand(placeOrderCommand);
        assertNotNull(order);
        assertNotNull(order.getOrderId());
        assertEquals(1, order.getHomerobotLineItems().get().size());
        assertEquals(1, order.getProrobotLineItems().get().size());
    }
}
