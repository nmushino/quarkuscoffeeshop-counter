
package io.quarkusrobotshop.infrastructure;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import io.quarkusrobotshop.counter.domain.commands.PlaceOrderCommand;

/**
 * Custom Jackson deserializer for PlaceOrderCommands
 */
public class PlaceOrderCommandDeserializer extends ObjectMapperDeserializer<PlaceOrderCommand> {

    public PlaceOrderCommandDeserializer() {
        super(PlaceOrderCommand.class);
    }

}
