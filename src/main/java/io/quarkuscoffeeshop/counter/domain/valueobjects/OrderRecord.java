package io.quarkusrobotshop.counter.domain.valueobjects;

import io.quarkusrobotshop.counter.domain.LineItem;

import java.util.List;

public record OrderRecord(String orderId, List<LineItem> lineItemList) {
}
