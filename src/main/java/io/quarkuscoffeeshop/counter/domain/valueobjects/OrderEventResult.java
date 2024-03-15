package io.quarkuscoffeeshop.counter.domain.valueobjects;

import io.debezium.outbox.quarkus.ExportedEvent;
import io.quarkuscoffeeshop.counter.domain.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Value object returned from an Order.  Contains the Order aggregate and a List ExportedEvent
 */
public class OrderEventResult {

  private Order order;

  private List<ExportedEvent> outboxEvents;

  private List<OrderTicket> homerobotTickets;

  private List<OrderTicket> prorobotTickets;

  private List<OrderUpdate> orderUpdates;

  public OrderEventResult() {
  }

  public Order getOrder() {
    return order;
  }

  public void addEvent(final ExportedEvent event) {
    if (this.outboxEvents == null) {
      this.outboxEvents = new ArrayList<>();
    }
    this.outboxEvents.add(event);
  }

  public void addUpdate(final OrderUpdate orderUpdate) {
    if (this.orderUpdates == null) {
      this.orderUpdates = new ArrayList<>();
    }
    this.orderUpdates.add(orderUpdate);
  }

  public void addHomerobotTicket(final OrderTicket orderTicket) {
    if (this.homerobotTickets == null) {
      this.homerobotTickets = new ArrayList<>();
    }
    this.homerobotTickets.add(orderTicket);
  }

  public void addProrobotTicket(final OrderTicket orderTicket) {
    if (this.prorobotTickets == null) {
      this.prorobotTickets = new ArrayList<>();
    }
    this.prorobotTickets.add(orderTicket);
  }

  public Optional<List<OrderTicket>> getHomerobotTickets() {
    return Optional.ofNullable(this.homerobotTickets);
  }

  public Optional<List<OrderTicket>> getProrobotTickets() {
    return Optional.ofNullable(this.prorobotTickets);
  }



  @Override
  public String toString() {
    return "OrderEventResult{" +
      "order=" + order +
      ", outboxEvents=" + outboxEvents +
      ", homerobotTickets=" + homerobotTickets +
      ", prorobotTickets=" + prorobotTickets +
      ", orderUpdates=" + orderUpdates +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof OrderEventResult)) return false;

    OrderEventResult that = (OrderEventResult) o;

    if (getOrder() != null ? !getOrder().equals(that.getOrder()) : that.getOrder() != null) return false;
    if (outboxEvents != null ? !outboxEvents.equals(that.outboxEvents) : that.outboxEvents != null) return false;
    if (homerobotTickets != null ? !homerobotTickets.equals(that.homerobotTickets) : that.homerobotTickets != null)
      return false;
    if (prorobotTickets != null ? !prorobotTickets.equals(that.prorobotTickets) : that.prorobotTickets != null)
      return false;
    return orderUpdates != null ? orderUpdates.equals(that.orderUpdates) : that.orderUpdates == null;
  }

  @Override
  public int hashCode() {
    int result = getOrder() != null ? getOrder().hashCode() : 0;
    result = 31 * result + (outboxEvents != null ? outboxEvents.hashCode() : 0);
    result = 31 * result + (homerobotTickets != null ? homerobotTickets.hashCode() : 0);
    result = 31 * result + (prorobotTickets != null ? prorobotTickets.hashCode() : 0);
    result = 31 * result + (orderUpdates != null ? orderUpdates.hashCode() : 0);
    return result;
  }

  public List<ExportedEvent> getOutboxEvents() {
    return outboxEvents;
  }

  public void setOutboxEvents(List<ExportedEvent> outboxEvents) {
    this.outboxEvents = outboxEvents;
  }

  public void setHomerobotTickets(List<OrderTicket> homerobotTickets) {
    this.homerobotTickets = homerobotTickets;
  }

  public void setProrobotTickets(List<OrderTicket> prorobotTickets) {
    this.prorobotTickets = prorobotTickets;
  }

  public List<OrderUpdate> getOrderUpdates() {
    return orderUpdates;
  }

  public void setOrderUpdates(List<OrderUpdate> orderUpdates) {
    this.orderUpdates = orderUpdates;
  }

  public void setOrder(final Order order) {
    this.order = order;
  }
}
