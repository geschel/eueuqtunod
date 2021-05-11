package de.example.donutqueue.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Client orders donuts.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    /**
     * required<p>
     * in range [1;20000]<p>
     * customers with IDs < 1000 are premium customers
     */
    @JsonProperty("client_id")
    private Integer clientId;

    /**
     * required<p>
     * in range [1;50]<p>
     */
    private Integer quantity;

}