package de.example.donutqueue.server.filter;

import de.example.donutqueue.api.ApiError;
import de.example.donutqueue.server.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import java.io.IOException;

@PreMatching
public class PreMatchingFilter implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(PreMatchingFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Do any global sanity checks that apply to all requests, no matter if they match a resource or not
        // Example: Ensure that PUT and POST requests only get through if they have a body
        rejectPutPostWithoutContent(requestContext);

    }

    /**
     * Aborts the request if the request is a POST or PUT without message body.
     */
    private void rejectPutPostWithoutContent(ContainerRequestContext requestContext) {

        String method = requestContext.getMethod();
        if (("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method))
                && !requestContext.hasEntity()) {
            LOG.error("Missing message body for {} request.", method);
            ApiError error = new ApiError(400, "Missing request body",
                    method + " requests must not have empty content");
            requestContext.abortWith(Response
                    .status(400)
                    .type(ContentType.JSON)
                    .entity(error).build());
        }

    }

}