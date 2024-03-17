package io.quarkusrobotshop.testing;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkusrobotshop.counter.domain.Item;
import io.quarkusrobotshop.counter.domain.Location;
import io.quarkusrobotshop.counter.domain.OrderSource;
import io.quarkusrobotshop.counter.domain.commands.CommandItem;
import io.quarkusrobotshop.counter.domain.commands.PlaceOrderCommand;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlaceOrderCommandTestUtil {

    private String id;

    private OrderSource orderSource;

    private Location location;

    private String loyaltyMemberId;

    private List<CommandItem> homerobotLineItems;

    private List<CommandItem> prorobotLineItems;

    private Instant timestamp;

    public PlaceOrderCommandTestUtil() {
        this.id = UUID.randomUUID().toString();
    }

    public PlaceOrderCommandTestUtil(String id, OrderSource orderSource, Location location, String loyaltyMemberId, List<CommandItem> homerobotLineItems, List<CommandItem> prorobotLineItems, Instant timestamp) {
        this.id = id;
        this.orderSource = orderSource;
        this.location = location;
        this.loyaltyMemberId = loyaltyMemberId;
        this.homerobotLineItems = homerobotLineItems;
        this.prorobotLineItems = prorobotLineItems;
        this.timestamp = timestamp;
    }

    public PlaceOrderCommandTestUtil create() {
        this.id = UUID.randomUUID().toString();
        return this;
    }

    public void withId(final String id) {
        this.id = id;
    }

    public PlaceOrderCommandTestUtil withBlackrobot() {
        if (this.homerobotLineItems == null) {
            this.homerobotLineItems = new ArrayList<>();
        }
        this.homerobotLineItems.add(new CommandItem(Item.CP0FB2_BLACK, "Jerry", BigDecimal.valueOf(3.50)));
        return this;
    }

    public void withBlackrobotFor(final String name) {
        this.homerobotLineItems.add(new CommandItem(Item.CP0FB2_BLACK, name, BigDecimal.valueOf(3.50)));
    }

    public PlaceOrderCommand build() {
        return new PlaceOrderCommand(
            this.id,
            this.orderSource,
            this.location,
            this.loyaltyMemberId,
            Optional.ofNullable(this.homerobotLineItems),
            Optional.ofNullable(this.prorobotLineItems)
        );
    }
}
