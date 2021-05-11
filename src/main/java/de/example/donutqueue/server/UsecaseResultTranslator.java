package de.example.donutqueue.server;

import de.example.donutqueue.api.ApiError;
import de.example.donutqueue.api.response.DeliveryCartResponse;
import de.example.donutqueue.api.response.PriorityQueueResponse;
import de.example.donutqueue.api.response.QueuedOrderResponse;
import de.example.donutqueue.core.logic.exception.ProcessingException;
import de.example.donutqueue.core.logic.usecase.*;
import de.example.donutqueue.db.pojo.OrderRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.util.ArrayList;

/**
 * Translates use case results from the business logic domain to the webservice domain.
 */
public class UsecaseResultTranslator {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsecaseResultTranslator.class);

    public Response translateUseCaseResult(UseCase finishedUseCase) throws ProcessingException {
        try {
            if (null == finishedUseCase) {
                LOGGER.error("Use case result was null.");
                return Response.serverError().entity(new ApiError(500, "Internal server error.", "Use case has no result.")).build();
            }
            if (finishedUseCase instanceof CreateOrderLogic) {
                return Response.status(201).entity(translateCreateOrderLogic((CreateOrderLogic) finishedUseCase)).build();
            } else if (finishedUseCase instanceof GetQueuedOrderLogic) {
                return Response.status(200).entity(translateGetQueuedOrderLogic((GetQueuedOrderLogic) finishedUseCase)).build();
            } else if (finishedUseCase instanceof GetOrdersLogic) {
                return Response.status(200).entity(translateGetOrdersLogic((GetOrdersLogic) finishedUseCase)).build();
            } else if (finishedUseCase instanceof DeleteOrderLogic) {
                return Response.status(204).entity("").build();
            } else if (finishedUseCase instanceof FillDeliveryCartLogic) {
                return Response.status(200).entity(translateFillDeliveryCartLogic((FillDeliveryCartLogic) finishedUseCase)).build();
            }
            LOGGER.error("Could not translate use case result {}.", finishedUseCase.getClass().getName());
            return Response.serverError().entity(new ApiError(500, "Internal server error.", "Use case answer not translatable.")).build();
        } catch (Exception e) {
            LOGGER.error("Translation of use case result failed.", e);
            throw new ProcessingException("Translation error", e.getMessage());
        }
    }

    /**
     * Translates a list of orders from use case 'GetOrdersLogic' to a pojo list.
     *
     * @param finishedUseCase - GetOrdersLogic with result data for translation
     */
    private Object translateGetOrdersLogic(GetOrdersLogic finishedUseCase) {
        PriorityQueueResponse responseWrapper = new PriorityQueueResponse();
        responseWrapper.setPriorityQueue(new ArrayList<>());
        int startPosition = finishedUseCase.getResultOffsetFirstOrder();
        for (OrderRow order : finishedUseCase.getResultOrders()) {
            responseWrapper.getPriorityQueue().add(buildQueuedOrderResponse(order, startPosition++));
        }
        return responseWrapper;
    }

    private Object translateGetQueuedOrderLogic(GetQueuedOrderLogic useCase) {
        return buildQueuedOrderResponse(useCase.getResultOrder(), useCase.getResultPositionInQueue());
    }

    private Object translateCreateOrderLogic(CreateOrderLogic useCase) {
        return buildQueuedOrderResponse(useCase.getResultOrder(), useCase.getResultPositionInQueue());
    }

    private DeliveryCartResponse translateFillDeliveryCartLogic(FillDeliveryCartLogic finishedUseCase) {
        DeliveryCartResponse wrapper = new DeliveryCartResponse();
        wrapper.setDeliveryCart(new ArrayList<>());
        int queuePosition = finishedUseCase.getResultPositionInQueue();
        if (null != finishedUseCase.getFilledDeliveryCart()) {
            for (OrderRow order : finishedUseCase.getFilledDeliveryCart()) {
                wrapper.getDeliveryCart().add(buildQueuedOrderResponse(order, queuePosition++));
            }
        }
        return wrapper;
    }

    /**
     * Translates domain entity to API domain.
     *
     * @param order           - a queued order from the queue
     * @param positionInQueue - the order's position in the queue
     * @return a presentable object
     */
    private QueuedOrderResponse buildQueuedOrderResponse(OrderRow order, int positionInQueue) {
        QueuedOrderResponse responsePojo = new QueuedOrderResponse();

        responsePojo.setClientId(order.getClient_id());
        responsePojo.setQuantity(order.getQuantity());
        responsePojo.setSecondsInQueue(computeDiffInSeconds(System.currentTimeMillis(), order.getCreate_time().getTime()));
        responsePojo.setPositionInQueue(positionInQueue);

        return responsePojo;
    }

    protected long computeDiffInSeconds(long now, long backThen) {
        return (now - backThen) / 1000L;
    }
}
