package de.example.donutqueue.server.resource;

import de.example.donutqueue.api.ApiError;
import de.example.donutqueue.api.request.FillDeliveryCartRequest;
import de.example.donutqueue.api.response.DeliveryCartResponse;
import de.example.donutqueue.core.logic.usecase.FillDeliveryCartLogic;
import de.example.donutqueue.db.DaoFactory;
import de.example.donutqueue.server.ContentType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("delivery_cart")
@Produces(ContentType.JSON)
public class DeliveryCartResource extends AbstractResource {

    private final DaoFactory daoFactory;

    public DeliveryCartResource(DaoFactory daoFactory) {
        super();
        this.daoFactory = daoFactory;
    }

    @POST
    @Operation(summary = "Fill delivery cart by removing orders from queue.",
            description = "An endpoint to retrieve orders with up to 50 donuts in total from the queue in order to put them into the cart. The orders are sorted first by premium status then by waiting time.",
            tags = {"delivery_cart"},
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Successful operation. Orders have been removed from queue and put into delivery cart.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DeliveryCartResponse.class))),
                    @ApiResponse(responseCode = "500",
                            description = "Unexpected error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)))
            })
    public Response postParticipant(final @RequestBody FillDeliveryCartRequest deliverycartRequest) {
        FillDeliveryCartLogic useCase = new FillDeliveryCartLogic(deliverycartRequest, daoFactory);
        return super.handleUseCase(useCase);
    }

}