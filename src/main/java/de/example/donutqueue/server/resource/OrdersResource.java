package de.example.donutqueue.server.resource;

import de.example.donutqueue.api.ApiError;
import de.example.donutqueue.api.request.CreateOrderRequest;
import de.example.donutqueue.api.response.QueuedOrderResponse;
import de.example.donutqueue.core.logic.usecase.CreateOrderLogic;
import de.example.donutqueue.core.logic.usecase.DeleteOrderLogic;
import de.example.donutqueue.core.logic.usecase.GetOrdersLogic;
import de.example.donutqueue.core.logic.usecase.GetQueuedOrderLogic;
import de.example.donutqueue.db.DaoFactory;
import de.example.donutqueue.server.ContentType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("orders")
@Produces(ContentType.JSON)
public class OrdersResource extends AbstractResource {

    private final DaoFactory daoFactory;

    public OrdersResource(DaoFactory daoFactory) {
        super();
        this.daoFactory = daoFactory;
    }

    @POST
    @Path("/")
    @Operation(summary = "Create and insert a donut order in the priority queue.",
            description = "An endpoint for adding items to the priority queue. This endpoint takes two parameters: the id of the client and a quantity of donuts.",
            tags = {"order"},
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "Order created successfully and inserted into queue.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = QueuedOrderResponse.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Bad params.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500",
                            description = "Unexpected error.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)))
            })
    public Response createOrder(@RequestBody CreateOrderRequest createRequest) {
        CreateOrderLogic useCase = new CreateOrderLogic(createRequest, daoFactory);
        return super.handleUseCase(useCase);
    }

    @GET
    @Path("/{client_id}")
    @Operation(summary = "Retrieve specific order from queue.",
            description = "An endpoint for the client to check his queue position and approximate wait time. Counting starts at 1.",
            tags = {"order"},
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Order created successfully and inserted into queue.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = QueuedOrderResponse.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Bad param.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404",
                            description = "Order hot found.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500",
                            description = "Unexpected error.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)))
            })
    public Response getOrder(@PathParam(value = "client_id") String clientId) {
        GetQueuedOrderLogic useCase = new GetQueuedOrderLogic(clientId, daoFactory);
        return super.handleUseCase(useCase);
    }

    @GET
    @Path("/")
    @Operation(summary = "Retrieve queue.",
            description = "Returns all queued orders ordered by premium status first, then by time spent in queue.",
            tags = {"order"},
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "List of order in queue",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = QueuedOrderResponse.class))),
                    @ApiResponse(responseCode = "500",
                            description = "Unexpected error.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)))
            })
    public Response getAllOrders() {
        GetOrdersLogic useCase = new GetOrdersLogic(daoFactory);
        return super.handleUseCase(useCase);
    }

    @DELETE
    @Path("/{client_id}")
    @Operation(summary = "Delete specific order from queue.",
            description = "An endpoint to cancel an order. This endpoint accepts only the client id.",
            tags = {"order"},
            responses = {
                    @ApiResponse(responseCode = "204",
                            description = "Order has been deleted successfully. No content will be returned.",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404",
                            description = "Order hot found.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500",
                            description = "Unexpected error.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)))
            })
    public Response deleteOrder(@PathParam(value = "client_id") String clientId) {
        DeleteOrderLogic useCase = new DeleteOrderLogic(clientId, daoFactory);
        return super.handleUseCase(useCase);
    }

}