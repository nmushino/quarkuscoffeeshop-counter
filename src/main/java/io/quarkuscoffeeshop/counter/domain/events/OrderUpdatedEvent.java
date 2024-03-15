package io.quarkusrobotshop.counter.domain.events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.debezium.outbox.quarkus.ExportedEvent;
import io.quarkusrobotshop.counter.domain.LineItem;
import io.quarkusrobotshop.counter.domain.Order;

import java.time.Instant;

public class OrderUpdatedEvent implements ExportedEvent<String, JsonNode> {

  private static ObjectMapper mapper = new ObjectMapper();

  private static final String TYPE = "Order";
  private static final String EVENT_TYPE = "OrderUpdated";

  private final String orderId;
  private final JsonNode jsonNode;
  private final Instant timestamp;

  private OrderUpdatedEvent(String orderId, JsonNode jsonNode, Instant timestamp) {
    this.orderId = orderId;
    this.jsonNode = jsonNode;
    this.timestamp = Instant.now();
  }

  public static OrderUpdatedEvent of(final Order order) {

    ObjectNode asJson = mapper.createObjectNode()
      .put("orderId", order.getOrderId())
      .put("orderSource", order.getOrderSource().toString())
      .put("timestamp", order.getTimestamp().toString());

    if (order.getHomerobotLineItems().isPresent()) {
      ArrayNode homerobotLineItems = asJson.putArray("homerobotLineItems") ;
      for (LineItem lineItem : order.getHomerobotLineItems().get()) {
        ObjectNode lineAsJon = mapper.createObjectNode()
          .put("item", lineItem.getItem().toString())
          .put("name", lineItem.getName())
          .put("lineItemStatus", lineItem.getLineItemStatus().toString());
        homerobotLineItems.add(lineAsJon);
      }
    }

    if (order.getProrobotLineItems().isPresent()) {
      ArrayNode prorobotLineItems = asJson.putArray("prorobotLineItems") ;
      for (LineItem lineItem : order.getProrobotLineItems().get()) {
        ObjectNode lineAsJon = mapper.createObjectNode()
          .put("item", lineItem.getItem().toString())
          .put("name", lineItem.getName())
          .put("lineItemStatus", lineItem.getLineItemStatus().toString());
        prorobotLineItems.add(lineAsJon);
      }
    }

    return new OrderUpdatedEvent(
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
  public String getType() {
    return EVENT_TYPE;
  }

  @Override
  public Instant getTimestamp() {
    return timestamp;
  }

  @Override
  public JsonNode getPayload() {
    return jsonNode;
  }
}
