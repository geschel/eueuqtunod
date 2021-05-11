package end2end;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class OrderIT extends BaseResourceIT {

    private static final String PATH_ORDERS = ItUtils.BASE_URL + "/orders";
    private static final String PATH_DELIVERY_CART = ItUtils.BASE_URL + "/delivery_cart";

    /**
     * Tests http-POST '/orders'
     */
    @Test
    public void allHappyPaths() {

        String goodSyntax0 = "{ \"client_id\": 1000, \"quantity\": 2 }";
        String goodSyntax1 = "{ \"client_id\": 1001, \"quantity\": 3 }";
        String goodSyntax2 = "{ \"client_id\": 1002, \"quantity\": 4 }";
        String goodSyntax3 = "{ \"client_id\": 1003, \"quantity\": 15 }";
        String goodSyntax4 = "{ \"client_id\": 1004, \"quantity\": 45 }";
        String goodSyntax5 = "{ \"client_id\": 10, \"quantity\": 24 }";
        String goodSyntax6 = "{ \"client_id\": 20, \"quantity\": 24 }";
        String goodSyntax7 = "{ \"client_id\": 30, \"quantity\": 1 }";
        String goodSyntax8 = "{ \"client_id\": 40, \"quantity\": 1 }";
        String goodSyntax9 = "{ \"client_id\": 50, \"quantity\": 1 }";
        String goodSyntax10 = "{ \"client_id\": 1, \"quantity\": 24 }";

        // run tests
        assertQueueLength(0);
        createAndAssert(goodSyntax0, 1000, 2, 1, 0);
        assertQueueLength(1);
        createAndAssert(goodSyntax1, 1001, 3, 2, 0);
        createAndAssert(goodSyntax2, 1002, 4, 3, 0);
        createAndAssert(goodSyntax3, 1003, 15, 4, 0);
        createAndAssert(goodSyntax4, 1004, 45, 5, 0);
        assertQueueLength(5);
        createAndAssert(goodSyntax5, 10, 24, 1, 0);
        createAndAssert(goodSyntax6, 20, 24, 2, 0);
        createAndAssert(goodSyntax7, 30, 1, 3, 0);
        createAndAssert(goodSyntax8, 40, 1, 4, 0);
        createAndAssert(goodSyntax9, 50, 1, 5, 0);
        createAndAssert(goodSyntax10, 1, 24, 6, 0);
        assertQueueLength(11);
        assertPositionInQueue(1004, 11);
        assertPositionInQueue(50, 5);
        assertRetrieve404(5000);
        deleteByIdAssertReturnCode(50, 204);
        assertRetrieve404(50);
        deleteByIdAssertReturnCode(50, 404);
        assertRetrieve404(50);
        assertQueueLength(10);

        //test delivery cart
        assertDeliveryCartSize(4);
        assertPositionInQueue(1, 1);
        assertPositionInQueue(1004, 6);
        assertDeliveryCartSize(5);
        assertPositionInQueue(1004, 1);
        assertDeliveryCartSize(1);
        assertDeliveryCartSize(0);
        assertDeliveryCartSize(0);
    }

    private void assertDeliveryCartSize(int deliveryCartSize) {
        given().when()
                .header("Content-Type", "application/json")
                .body("{}")
                .post(PATH_DELIVERY_CART)
                .then()
                .statusCode(200)
                .body("delivery_cart.size()", equalTo(deliveryCartSize))
        ;
    }

    private void deleteByIdAssertReturnCode(int clientId, int returnCode) {
        given().when()
                .header("Content-Type", "application/json")
                .delete(PATH_ORDERS + "/" + clientId)
                .then()
                .statusCode(returnCode)
        ;
    }

    private void assertRetrieve404(int clientId) {
        given().when()
                .header("Content-Type", "application/json")
                .get(PATH_ORDERS + "/" + clientId)
                .then()
                .statusCode(404)
                .body("message", equalTo("Order not found."))
        ;
    }

    private void assertPositionInQueue(int clientId, int position) {
        given().when()
                .header("Content-Type", "application/json")
                .get(PATH_ORDERS + "/" + clientId)
                .then()
                .statusCode(200)
                .body("position_in_queue", equalTo(position))
        ;
    }

    private void assertQueueLength(int length) {
        given().when()
                .header("Content-Type", "application/json")
                .get(PATH_ORDERS)
                .then()
                .statusCode(200)
                .body("priority_queue.size()", equalTo(length))
        ;
    }

    private void createAndAssert(String goodSyntax0, int clientId, int quantity, int positionInQueue, int secondsInQueue) {
        given().when()
                .body(goodSyntax0)
                .header("Content-Type", "application/json")
                .post(PATH_ORDERS)
                .then()
                .statusCode(201)
                .body("client_id", equalTo(clientId))
                .body("quantity", equalTo(quantity))
                .body("position_in_queue", equalTo(positionInQueue))
                .body("seconds_in_queue", equalTo(secondsInQueue))
        ;
    }

    /**
     * Tests http-POST '/orders'
     */
    @Test
    public void testCreateOrderBadSyntax() {

        String badSyntax0 = "{ \"client_id: ö, \"quantity\": 1 }";
        String badSyntax1 = "{ \"client_id\": 0, \"quantity\": 1 }";
        String badSyntax2 = "{ \"client_id\": -1, \"quantity\": 1 }";
        String badSyntax3 = "{ \"client_id\": 1, \"quantity\": 51 }";
        String badSyntax4 = "{ \"client_id\": 20001, \"quantity\": 1 }";
        String badSyntax5 = "{ \"client_id\": 500, \"quantity\": 0 }";
        String badSyntax6 = "{ \"client_id\": null, \"quantity\": 1 }";
        String badSyntax7 = "{ \"client_id\": 1, \"quantity\": null }";

        // run tests
        assertCreateFailed(badSyntax0, 400, "Unable to process JSON");
        assertCreateFailed(badSyntax1, 400, "Bad param.");
        assertCreateFailed(badSyntax2, 400, "Bad param.");
        assertCreateFailed(badSyntax3, 400, "Bad param.");
        assertCreateFailed(badSyntax4, 400, "Bad param.");
        assertCreateFailed(badSyntax5, 400, "Bad param.");
        assertCreateFailed(badSyntax6, 400, "Missing param.");
        assertCreateFailed(badSyntax7, 400, "Missing param.");
    }

    private void assertCreateFailed(String badSyntax, int code, String message) {
        given().when()
                .body(badSyntax)
                .header("Content-Type", "application/json")
                .post(PATH_ORDERS)
                .then()
                .statusCode(code)
                .body("message", equalTo(message))
        ;
    }

}
