package io.quarkuscoffeeshop.counter.domain;

import io.quarkuscoffeeshop.counter.domain.commands.PlaceOrderCommand;
import io.quarkuscoffeeshop.counter.domain.events.LoyaltyMemberPurchaseEvent;
import io.quarkuscoffeeshop.counter.domain.events.OrderCreatedEvent;
import io.quarkuscoffeeshop.counter.domain.events.OrderUpdatedEvent;
import io.quarkuscoffeeshop.counter.domain.valueobjects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;


public class Order {

  @Transient
  static Logger logger = LoggerFactory.getLogger(Order.class);

  private OrderRecord orderRecord;

  protected static Order fromOrderRecord(OrderRecord orderRecord) {
    Order order = new Order();
    order.orderRecord = orderRecord;
    return order;
  }

  /**
   * Each time a TicketUp is received the Order should be checked for completion.
   * An Order is complete when every LineItem is fulfilled.
   *
   * @param ticketUp
   * @return OrderEventResult
   */
  public OrderEventResult applyOrderTicketUp(final TicketUp ticketUp) {

    // set the LineItem's new status
    if (this.getHomerobotLineItems().isPresent()) {
      this.getHomerobotLineItems().get().stream().forEach(lineItem -> {
        if(lineItem.getItemId().equals(ticketUp.lineItemId)){
          lineItem.setLineItemStatus(LineItemStatus.FULFILLED);
        }
      });
    }
    if (this.getProrobotLineItems().isPresent()) {
      this.getProrobotLineItems().get().stream().forEach(lineItem -> {
        if(lineItem.getItemId().equals(ticketUp.lineItemId)){
          lineItem.setLineItemStatus(LineItemStatus.FULFILLED);
        }
      });
    }

    // if there are both homerobot and prorobot items concatenate them before checking status
    if (this.getHomerobotLineItems().isPresent() && this.getProrobotLineItems().isPresent()) {
      // check the status of the Order itself and update if necessary
      if(Stream.concat(this.getHomerobotLineItems().get().stream(), this.getProrobotLineItems().get().stream())
              .allMatch(lineItem -> {
                return lineItem.getLineItemStatus().equals(LineItemStatus.FULFILLED);
              })){
        this.setOrderStatus(OrderStatus.FULFILLED);
      };
    } else if (this.getHomerobotLineItems().isPresent()) {
      if(this.getHomerobotLineItems().get().stream()
              .allMatch(lineItem -> {
                return lineItem.getLineItemStatus().equals(LineItemStatus.FULFILLED);
              })){
        this.setOrderStatus(OrderStatus.FULFILLED);
      };
    }else if (this.getProrobotLineItems().isPresent()) {
      if(this.getProrobotLineItems().get().stream()
              .allMatch(lineItem -> {
                return lineItem.getLineItemStatus().equals(LineItemStatus.FULFILLED);
              })){
        this.setOrderStatus(OrderStatus.FULFILLED);
      };
    }

    // create the domain event
    OrderUpdatedEvent orderUpdatedEvent = OrderUpdatedEvent.of(this);

    // create the update value object
    OrderUpdate orderUpdate = new OrderUpdate(ticketUp.getOrderId(), ticketUp.getLineItemId(), ticketUp.getName(), ticketUp.getItem(), OrderStatus.FULFILLED, ticketUp.madeBy);

    OrderEventResult orderEventResult = new OrderEventResult();
    orderEventResult.setOrder(this);
    orderEventResult.addEvent(orderUpdatedEvent);
    orderEventResult.setOrderUpdates(new ArrayList<>() {{
      add(orderUpdate);
    }});
    return orderEventResult;
  }

  /**
   * Create a new Order from a PlaceOrderCommand
   *
   * @param placeOrderCommand
   * @return
   */
  protected static Order fromPlaceOrderCommand(final PlaceOrderCommand placeOrderCommand) {

    logger.debug("creating a new Order from: {}", placeOrderCommand);

    // build the order from the PlaceOrderCommand
    Order order = new Order(placeOrderCommand.getId());
    order.setOrderSource(placeOrderCommand.getOrderSource());
    order.setLocation(placeOrderCommand.getLocation());
    order.setTimestamp(placeOrderCommand.getTimestamp());
    order.setOrderStatus(OrderStatus.IN_PROGRESS);
    if (placeOrderCommand.getLoyaltyMemberId().isPresent()) {
      order.setLoyaltyMemberId(placeOrderCommand.getLoyaltyMemberId().get());
    }

    if (placeOrderCommand.getHomerobotLineItems().isPresent()) {
      placeOrderCommand.getHomerobotLineItems().get().forEach(commandItem -> {
        logger.debug("createOrderFromCommand adding homerobotItem from {}", commandItem.toString());
        LineItem lineItem = new LineItem(commandItem.getItem(), commandItem.getName(), commandItem.getPrice(), LineItemStatus.IN_PROGRESS, order.getOrderRecord());
        order.addHomerobotLineItem(lineItem);
      });
    }

    if (placeOrderCommand.getProrobotLineItems().isPresent()) {
      logger.debug("createOrderFromCommand adding prorobotOrders {}", placeOrderCommand.getProrobotLineItems().get().size());
      placeOrderCommand.getProrobotLineItems().get().forEach(commandItem -> {
        LineItem lineItem = new LineItem(commandItem.getItem(), commandItem.getName(), commandItem.getPrice(), LineItemStatus.IN_PROGRESS, order.getOrderRecord());
        order.addProrobotLineItem(lineItem);
      });
    }

    return order;
  }

  private static List<OrderUpdate> createOrderUpdates(Order order) {

    List<OrderUpdate> orderUpdates = new ArrayList<>();

    // create required HomerobotTicket, ProrobotTicket, and OrderUpdate value objects
    if (order.getHomerobotLineItems().isPresent()) {
      order.getHomerobotLineItems().get().forEach(lineItem -> {
        orderUpdates.add(new OrderUpdate(order.getOrderId(), lineItem.getItemId(), lineItem.getName(), lineItem.getItem(), OrderStatus.IN_PROGRESS));
      });
    }
    if (order.getProrobotLineItems().isPresent()) {
      order.getProrobotLineItems().get().forEach(lineItem -> {
        orderUpdates.add(new OrderUpdate(order.getOrderId(), lineItem.getItemId(), lineItem.getName(), lineItem.getItem(), OrderStatus.IN_PROGRESS));
      });
    }
    return orderUpdates;
  }

  private static List<OrderTicket> createOrderTickets(String orderId, List<LineItem> lineItems) {
    List<OrderTicket> orderTickets = new ArrayList<>(lineItems.size());
    lineItems.forEach(lineItem -> {
      orderTickets.add(new OrderTicket(orderId, lineItem.getItemId(), lineItem.getItem(), lineItem.getName()));
    });
    return orderTickets;
  }
  /**
   * Creates and returns a new OrderEventResult containing the Order aggregate built from the PlaceOrderCommand
   * and an OrderCreatedEvent
   *
   * @param placeOrderCommand PlaceOrderCommand
   * @return OrderEventResult
   */
  public static OrderEventResult createFromCommand(final PlaceOrderCommand placeOrderCommand) {

    Order order = Order.fromPlaceOrderCommand(placeOrderCommand);

    // create the return value
    OrderEventResult orderEventResult = new OrderEventResult();
    orderEventResult.setOrder(order);

    // create required HomerobotTicket, ProrobotTicket, and OrderUpdate value objects
    if (order.getHomerobotLineItems().isPresent()) {
      orderEventResult.setHomerobotTickets(createOrderTickets(order.getOrderId(), order.getHomerobotLineItems().get()));
    }

    if (order.getProrobotLineItems().isPresent()) {
      orderEventResult.setProrobotTickets(createOrderTickets(order.getOrderId(), order.getProrobotLineItems().get()));
    }

    // add updates
    orderEventResult.setOrderUpdates(createOrderUpdates(order));

    orderEventResult.addEvent(OrderCreatedEvent.of(order));

    // if this order was placed by a Loyalty Member add the appropriate event
    if (placeOrderCommand.getLoyaltyMemberId().isPresent()) {
      orderEventResult.addEvent(LoyaltyMemberPurchaseEvent.of(order));
    }

    logger.debug("returning {}", orderEventResult);
    return orderEventResult;
  }


  /**
   * Convenience method to prevent Null Pointer Exceptions
   *
   * @param lineItem
   */
  public void addHomerobotLineItem(LineItem lineItem) {
    if (getHomerobotLineItems().isPresent()) {
      lineItem.setOrder(this.orderRecord);
      this.getHomerobotLineItems().get().add(lineItem);
    }else{
      if (this.orderRecord.getHomerobotLineItems() == null) {
        this.orderRecord.setHomerobotLineItems(new ArrayList<LineItem>(){{ add(lineItem); }});
      }else{
        this.orderRecord.getHomerobotLineItems().add(lineItem);
      }
    }
  }

  /**
   * Convenience method to prevent Null Pointer Exceptions
   *
   * @param lineItem
   */
  public void addProrobotLineItem(LineItem lineItem) {
    if (this.getProrobotLineItems().isPresent()) {
      lineItem.setOrder(this.orderRecord);
      this.getProrobotLineItems().get().add(lineItem);
    }else {
      if (this.orderRecord.getProrobotLineItems() == null) {
        this.orderRecord.setProrobotLineItems(new ArrayList<LineItem>(){{ add(lineItem); }});
      }else{
        this.orderRecord.getProrobotLineItems().add(lineItem);
      }
    }
  }

  public Optional<List<LineItem>> getHomerobotLineItems() {
    return Optional.ofNullable(this.orderRecord.getHomerobotLineItems());
  }

  public void setHomerobotLineItems(List<LineItem> homerobotLineItems) {
    this.orderRecord.setHomerobotLineItems(homerobotLineItems);
  }

  public Optional<List<LineItem>> getProrobotLineItems() {
    return Optional.ofNullable(this.orderRecord.getProrobotLineItems());
  }

  public void setProrobotLineItems(List<LineItem> prorobotLineItems) {
    this.orderRecord.setProrobotLineItems(prorobotLineItems);
  }

  public Optional<String> getLoyaltyMemberId() {
    return Optional.ofNullable(this.orderRecord.getLoyaltyMemberId());
  }

  public void setLoyaltyMemberId(String loyaltyMemberId) {
    this.orderRecord.setLoyaltyMemberId(loyaltyMemberId);
  }

  public Order() {
    this.orderRecord = new OrderRecord();
    this.orderRecord.setOrderId(UUID.randomUUID().toString());
    this.orderRecord.setTimestamp(Instant.now());
  }

  public Order(final String orderId){
    this.orderRecord = new OrderRecord();
    this.orderRecord.setOrderId(orderId);
    this.orderRecord.setTimestamp(Instant.now());
  }

  public Order(final String orderId, final OrderSource orderSource, final Location location, final String loyaltyMemberId, final Instant timestamp, final OrderStatus orderStatus, final List<LineItem> homerobotLineItems, final List<LineItem> prorobotLineItems) {
    this.orderRecord.setOrderId(orderId);
    this.orderRecord.setOrderSource(orderSource);
    this.orderRecord.setLocation(location);
    this.orderRecord.setLoyaltyMemberId(loyaltyMemberId);
    this.orderRecord.setTimestamp(timestamp);
    this.orderRecord.setOrderStatus(orderStatus);
    this.orderRecord.setHomerobotLineItems(homerobotLineItems);
    this.orderRecord.setProrobotLineItems(prorobotLineItems);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Order.class.getSimpleName() + "[", "]")
            .add("orderId='" + orderRecord.getOrderId() + "'")
            .add("orderSource=" + orderRecord.getOrderSource())
            .add("loyaltyMemberId='" + orderRecord.getLoyaltyMemberId() + "'")
            .add("timestamp=" + orderRecord.getTimestamp())
            .add("orderStatus=" + orderRecord.getOrderStatus())
            .add("location=" + orderRecord.getLocation())
            .add("homerobotLineItems=" + orderRecord.getHomerobotLineItems())
            .add("prorobotLineItems=" + orderRecord.getProrobotLineItems())
            .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Order order = (Order) o;

    return orderRecord != null ? orderRecord.equals(order.orderRecord) : order.orderRecord == null;
  }

  @Override
  public int hashCode() {
    return orderRecord != null ? orderRecord.hashCode() : 0;
  }

  public String getOrderId() {
    return this.orderRecord.getOrderId();
  }

  public OrderSource getOrderSource() {
    return this.orderRecord.getOrderSource();
  }

  public void setOrderSource(OrderSource orderSource) {
    this.orderRecord.setOrderSource(orderSource);
  }

  public Location getLocation() {
    return this.orderRecord.getLocation();
  }

  public void setLocation(Location location) {
    this.orderRecord.setLocation(location);
  }

  public OrderStatus getOrderStatus() {
    return this.orderRecord.getOrderStatus();
  }

  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderRecord.setOrderStatus(orderStatus);
  }

  public Instant getTimestamp() {
    return this.orderRecord.getTimestamp();
  }

  public void setTimestamp(Instant timestamp) {
    this.orderRecord.setTimestamp(timestamp);
  }

  protected OrderRecord getOrderRecord() {
    return this.orderRecord;
  }
}
