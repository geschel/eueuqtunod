package end2end;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

/**
 * Tests availability of swagger route.
 */
public class SwaggerIT extends BaseResourceIT {

    @Test
    public void testGetJson() {
        given().when()
                .get(ItUtils.BASE_URL + "/swagger.json")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("openapi", equalTo("3.0.1"))
        ;
    }

}