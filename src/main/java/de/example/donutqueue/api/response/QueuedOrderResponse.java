package de.example.donutqueue.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Server responds with a queued order.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueuedOrderResponse {

    @JsonProperty("client_id")
    private Integer clientId;

    @JsonProperty("position_in_queue")
    private Integer positionInQueue;

    private Integer quantity;

    @JsonProperty("seconds_in_queue")
    private Long secondsInQueue;
}