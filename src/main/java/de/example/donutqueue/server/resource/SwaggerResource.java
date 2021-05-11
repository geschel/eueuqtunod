package de.example.donutqueue.server.resource;


import io.swagger.v3.jaxrs2.integration.resources.BaseOpenApiResource;
import io.swagger.v3.oas.annotations.Operation;

import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;

/**
 * Resource that makes Swagger JSON or YAML available at routes:
 *
 * /swagger
 * /openapi
 * /swagger.json
 * /openapi.json
 * /swagger.yaml
 * /openapi.yaml
 * /swagger.yml
 * /openapi.yml
 *
 */
@Path("{a:(swagger|openapi)(\\.json|\\.yaml|\\.yml)?}")
public class SwaggerResource extends BaseOpenApiResource {

    @Context
    ServletConfig config;

    @Context
    Application app;

    @GET
    @Produces({MediaType.APPLICATION_JSON, "application/yaml"})
    @Operation(hidden = true)
    public Response getOpenApi(@Context HttpHeaders headers,
                               @Context UriInfo uriInfo,
                               @PathParam("a") String filename) throws Exception {

        String type;

        if (filename.endsWith(".yaml") || filename.endsWith(".yml")) {
            type = "yaml";
        } else {
            type = "json";
        }

        return super.getOpenApi(headers, config, app, uriInfo, type);
    }

}
