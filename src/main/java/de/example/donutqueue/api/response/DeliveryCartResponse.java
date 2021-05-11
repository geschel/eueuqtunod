package de.example.donutqueue.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Server responds with a list of orders.<p>
 * These orders have been removed from the priority queue in order to be put into the delivery cart.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryCartResponse {

    @JsonProperty("delivery_cart")
    private List<QueuedOrderResponse> deliveryCart;

}