package io.quarkusrobotshop.counter.domain;

import io.debezium.outbox.quarkus.ExportedEvent;
import io.quarkusrobotshop.counter.domain.commands.CommandItem;
import io.quarkusrobotshop.counter.domain.commands.PlaceOrderCommand;
import io.quarkusrobotshop.counter.domain.events.OrderCreatedEvent;
import io.quarkusrobotshop.counter.domain.valueobjects.OrderEventResult;
import io.quarkusrobotshop.counter.domain.valueobjects.OrderTicket;
import io.quarkusrobotshop.counter.domain.valueobjects.TicketUp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TestUtil {

    public static PlaceOrderCommand stubPlaceOrderCommand(final String id) {
        return new PlaceOrderCommand(
                id,
                OrderSource.WEB,
                Location.TOKYO,
                UUID.randomUUID().toString(),
                Optional.of(stubSingleHomerobotItem()),
                Optional.empty());
    }

    public static PlaceOrderCommand stubPlaceOrderCommand() {
        return stubPlaceOrderCommand(UUID.randomUUID().toString());
    };

    private static List<CommandItem> stubSingleHomerobotItem() {
        return Arrays.asList(new CommandItem(Item.CP0FB2_BLACK, "Foo", BigDecimal.valueOf(150000)));
    }

    private static List<CommandItem> stubSingleProrobotItem() {
        return Arrays.asList(new CommandItem(Item.FAC94S3, "Foo", BigDecimal.valueOf(3.25)));
    }

    public static Order stubOrder() {
        OrderRecord orderRecord = new OrderRecord(
                UUID.randomUUID().toString(),
                OrderSource.COUNTER,
                null,
                Instant.now(),
                OrderStatus.PLACED,
                Location.TOKYO,
                null,
                null);

        Order order = Order.fromOrderRecord(orderRecord);

        order.addHomerobotLineItem(new LineItem(Item.CP0FB2_BLACK, "Rocky", BigDecimal.valueOf(150000), LineItemStatus.PLACED, orderRecord));
        return order;
    }

    public static OrderEventResult stubOrderEventResult() {

        // create the return value
        OrderEventResult orderEventResult = new OrderEventResult();

        // build the order from the PlaceOrderCommand
        Order order = new Order(UUID.randomUUID().toString());
        order.setOrderSource(OrderSource.WEB);
        order.setLocation(Location.TOKYO);
        order.setTimestamp(Instant.now());
        order.setOrderStatus(OrderStatus.IN_PROGRESS);

        orderEventResult.setOrder(order);
        orderEventResult.setHomerobotTickets(TestUtil.stubHomerobotTickets());
        orderEventResult.setOutboxEvents(mockOrderInEvent());
        return orderEventResult;
    }

    private static List<ExportedEvent> mockOrderInEvent() {
        return Arrays.asList(OrderCreatedEvent.of(stubOrder()));
    }

    private static List<OrderTicket> stubHomerobotTickets() {
        return Arrays.asList(new OrderTicket(UUID.randomUUID().toString(), UUID.randomUUID().toString(), Item.CP0FB2_BLACK, "Rocky"));
    }

    public static TicketUp stubOrderTicketUp() {

        return new TicketUp(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                Item.CP0FB2_BLACK,
                "Capt. Kirk",
                "Mr. Spock"
        );
    }

    public static PlaceOrderCommand stubPlaceOrderCommandSingleCroissant() {

        return new PlaceOrderCommand(
                UUID.randomUUID().toString(),
                OrderSource.WEB,
                Location.TOKYO,
                UUID.randomUUID().toString(),
                Optional.empty(),
                Optional.of(stubSingleProrobotItem()));

    }

    public static PlaceOrderCommand stubPlaceOrderCommandBlackCoffeeAndCroissant() {

        return new PlaceOrderCommand(
                UUID.randomUUID().toString(),
                OrderSource.WEB,
                Location.TOKYO,
                UUID.randomUUID().toString(),
                Optional.of(stubSingleHomerobotItem()),
                Optional.of(stubSingleProrobotItem()));
    }
}
