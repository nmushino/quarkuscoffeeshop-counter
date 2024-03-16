package io.quarkusrobotshop.infrastructure;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import io.quarkusrobotshop.counter.domain.valueobjects.OrderTicket;

public class OrderTicketDeserializer  extends ObjectMapperDeserializer<OrderTicket> {

    public OrderTicketDeserializer() {
        super(OrderTicket.class);
    }
}
