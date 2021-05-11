package de.example.donutqueue.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Server responds with a list of queued orders.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriorityQueueResponse {

    @JsonProperty("priority_queue")
    private List<QueuedOrderResponse> priorityQueue;

}