package io.quarkusdroneshop.counter.infrastructure.orderservice;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkusdroneshop.counter.domain.*;
import io.quarkusdroneshop.counter.domain.commands.PlaceOrderCommand;
import io.quarkusdroneshop.counter.domain.valueobjects.TicketUp;
import io.quarkusdroneshop.infrastructure.OrderService;
import io.quarkusdroneshop.counter.domain.TestUtil;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This test verifies that the status of an Order is accurate when entered and updated.
 * The initial status should be "IN_PROGRESS"
 * After a TicketUp is received the status should change to "FULFILLED"
 * OrderService is the object under test because it orchestrates the creation and persistence of the Order, the parent object of the LineItem
 */
@QuarkusTest
@Transactional
@TestProfile(OrderServiceTestProfile.class)
public class OrderStatusTest {

    final String orderId = UUID.randomUUID().toString();

    @Inject
    OrderService orderService;

    @Inject
    OrderRepository orderRepository;

    // @Test
    // public void testStatusAfterOrderIsPlaced() {

    //     PlaceOrderCommand placeOrderCommand = TestUtil.stubPlaceOrderCommand(orderId);
    //     orderService.onOrderIn(placeOrderCommand);

    //     await().atLeast(2, TimeUnit.SECONDS);

    //     Order order = orderRepository.findById(orderId);
    //     assertNotNull(order);
    //     assertTrue(order.getQdca10LineItems().isPresent());
    //     assertEquals(1, order.getQdca10LineItems().get().size());
    //     assertEquals(OrderStatus.IN_PROGRESS, order.getOrderStatus());


    //     LineItem lineItem = order.getQdca10LineItems().get().get(0);
    //     final TicketUp ticketUp = new TicketUp(
    //             orderId,
    //             lineItem.getItemId(),
    //             lineItem.getItem(),
    //             lineItem.getName(),
    //             "Bart"
    //     );

    //     orderService.onOrderUp(ticketUp);

    //     await().atLeast(2, TimeUnit.SECONDS);

    //     Order updatedOrder = orderRepository.findById(orderId);
    //     assertNotNull(updatedOrder);
    //     assertTrue(updatedOrder.getQdca10LineItems().isPresent());
    //     assertEquals(1, updatedOrder.getQdca10LineItems().get().size());
    //     assertEquals(OrderStatus.FULFILLED, updatedOrder.getOrderStatus());
    // }
}
