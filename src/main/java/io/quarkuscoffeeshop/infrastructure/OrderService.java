package io.quarkusrobotshop.infrastructure;

import io.debezium.outbox.quarkus.ExportedEvent;
import io.quarkusrobotshop.counter.domain.Order;
import io.quarkusrobotshop.counter.domain.OrderRepository;
import io.quarkusrobotshop.counter.domain.commands.PlaceOrderCommand;
import io.quarkusrobotshop.counter.domain.valueobjects.OrderEventResult;
import io.quarkusrobotshop.counter.domain.valueobjects.OrderTicket;
import io.quarkusrobotshop.counter.domain.valueobjects.OrderUpdate;
import io.quarkusrobotshop.counter.domain.valueobjects.TicketUp;
import org.eclipse.microprofile.context.ThreadContext;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class OrderService {

    final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Inject
    ThreadContext threadContext;

    @Inject
    OrderRepository orderRepository;

    @Inject
    Event<ExportedEvent<?, ?>> event;

    @Channel("homerobot")
    Emitter<OrderTicket> homerobotEmitter;

    @Channel("prorobot")
    Emitter<OrderTicket> prorobotEmitter;

    @Channel("web-updates")
    Emitter<OrderUpdate> orderUpdateEmitter;

    public void onOrderIn(final PlaceOrderCommand placeOrderCommand) {

        logger.debug("onOrderIn {}", placeOrderCommand);

        OrderEventResult orderEventResult = Order.createFromCommand(placeOrderCommand);

        logger.debug("OrderEventResult returned: {}", orderEventResult);

        orderRepository.persist(orderEventResult.getOrder());

        orderEventResult.getOutboxEvents().forEach(exportedEvent -> {
            logger.debug("Firing event: {}", exportedEvent);
            event.fire(exportedEvent);
        });

        if (orderEventResult.getHomerobotTickets().isPresent()) {
            orderEventResult.getHomerobotTickets().get().forEach(homerobotTicket -> {
                logger.debug("Sending Ticket to Homerobot Service: {}", homerobotTicket);
                homerobotEmitter.send(homerobotTicket);
            });
        }

        if (orderEventResult.getProrobotTickets().isPresent()) {
            orderEventResult.getProrobotTickets().get().forEach(prorobotTicket -> {
                prorobotEmitter.send(prorobotTicket);
            });
        }

        orderEventResult.getOrderUpdates().forEach(orderUpdate -> {
            orderUpdateEmitter.send(orderUpdate);
        });

    }

    @Transactional
    public void onOrderUp(final TicketUp ticketUp) {

        logger.debug("onOrderUp: {}", ticketUp);
        Order order = orderRepository.findById(ticketUp.getOrderId());
        OrderEventResult orderEventResult = order.applyOrderTicketUp(ticketUp);
        logger.debug("OrderEventResult returned: {}", orderEventResult);
        orderRepository.persist(orderEventResult.getOrder());
        orderEventResult.getOrderUpdates().forEach(orderUpdate -> {
            orderUpdateEmitter.send(orderUpdate);
        });
        orderEventResult.getOutboxEvents().forEach(exportedEvent -> {
            event.fire(exportedEvent);
        });
    }

    @Override
    public String toString() {
        return "OrderService{" +
                "threadContext=" + threadContext +
                ", orderRepository=" + orderRepository +
                ", event=" + event +
                ", homerobotEmitter=" + homerobotEmitter +
                ", prorobotEmitter=" + prorobotEmitter +
                ", orderUpdateEmitter=" + orderUpdateEmitter +
                '}';
    }

}
