package io.quarkusrobotshop.counter.domain.events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.debezium.outbox.quarkus.ExportedEvent;
import io.quarkusrobotshop.counter.domain.LineItem;
import io.quarkusrobotshop.counter.domain.Order;

import java.time.Instant;

public class OrderCreatedEvent implements ExportedEvent<String, JsonNode> {

    private static ObjectMapper mapper = new ObjectMapper();

    private static final String TYPE = "Order";
    private static final String EVENT_TYPE = "OrderCreated";

    private final String orderId;
    private final JsonNode jsonNode;
    private final Instant timestamp;

    private OrderCreatedEvent(String orderId, JsonNode jsonNode, Instant timestamp) {
        this.orderId = orderId;
        this.jsonNode = jsonNode;
        this.timestamp = timestamp;
    }

    public static OrderCreatedEvent of(final Order order) {

        ObjectNode asJson = mapper.createObjectNode()
                .put("orderId", order.getOrderId())
                .put("orderSource", order.getOrderSource().toString())
                .put("timestamp", order.getTimestamp().toString());

        if (order.getHomerobotLineItems().isPresent()) {
            ArrayNode homerobotLineItems = asJson.putArray("homerobotLineItems") ;
            for (LineItem lineItem : order.getHomerobotLineItems().get()) {
                ObjectNode lineAsJon = mapper.createObjectNode()
                        .put("item", lineItem.getItem().toString())
                        .put("name", lineItem.getName());
                homerobotLineItems.add(lineAsJon);
            }
        }

        if (order.getProrobotLineItems().isPresent()) {
            ArrayNode prorobotLineItems = asJson.putArray("prorobotLineItems") ;
            for (LineItem lineItem : order.getProrobotLineItems().get()) {
                ObjectNode lineAsJon = mapper.createObjectNode()
                        .put("item", lineItem.getItem().toString())
                        .put("name", lineItem.getName());
                prorobotLineItems.add(lineAsJon);
            }
        }

        return new OrderCreatedEvent(
                order.getOrderId(),
                asJson,
                order.getTimestamp());
    }

    @Override
    public String getAggregateId() {
        return orderId;
    }

    @Override
    public String getAggregateType() {
        return TYPE;
    }

    @Override
    public JsonNode getPayload() {
        return jsonNode;
    }

    @Override
    public String getType() {
        return EVENT_TYPE;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }
}
