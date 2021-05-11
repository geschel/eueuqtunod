package de.example.donutqueue.server.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.example.donutqueue.api.ApiError;
import de.example.donutqueue.core.logic.exception.NotFoundException;
import de.example.donutqueue.core.logic.exception.ProcessingException;
import de.example.donutqueue.core.logic.exception.ForbiddenException;
import de.example.donutqueue.core.logic.exception.SyntaxException;
import de.example.donutqueue.core.logic.usecase.UseCase;
import de.example.donutqueue.core.util.JsonUtil;
import de.example.donutqueue.server.UsecaseResultTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;

/**
 * Provides translation between business logic results and protocol specific return entities.
 */
public class AbstractResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractResource.class);

    /**
     * Provides a string serialization of passed object.
     *
     * @param input - the object to be serialized
     * @return
     */
    protected String dump(Object input) {
        if (null == input) {
            return null;
        }
        try {
            return JsonUtil.strictMapper().writeValueAsString(input);
        } catch (JsonProcessingException e) {
            LOGGER.error("Serialization error.", e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Translates a custom exception to a json response returned via http.<p>
     * A syntax exception means that your input was bad and therefore execution is not possible.
     *
     * @param syntaxException - what went wrong
     */
    protected Response translate(SyntaxException syntaxException) {
        return Response.status(400)
                .entity(new ApiError(400,
                        syntaxException.getMessage(),
                        null == syntaxException.getDetails() ? null : syntaxException.getDetails()))
                .build();
    }

    /**
     * Translates a custom exception to a json response returned via http.<p>
     * A forbidden exception means that your request was syntactically correct BUT some requirements are not met
     * in order to process it. The result is a http 403 (Forbidden)
     *
     * @param forbiddenException - some requirement have not been met
     */
    protected Response translate(ForbiddenException forbiddenException) {
        return Response.status(403)
                .entity(new ApiError(403,
                        forbiddenException.getMessage(),
                        null == forbiddenException.getDetails() ? null : forbiddenException.getDetails()))
                .build();
    }

    /**
     * Translates a custom exception to a json response returned via http.<p>
     * A not found exception means that your request was syntactically correct BUT the addressed entity
     * could not be found. The result is a http 404 (NotFound)
     *
     * @param notFoundException - some requirement have not been met
     */
    protected Response translate(NotFoundException notFoundException) {
        return Response.status(404)
                .entity(new ApiError(404,
                        notFoundException.getMessage(),
                        null == notFoundException.getDetails() ? null : notFoundException.getDetails()))
                .build();
    }

    /**
     * Translates our custom exception to a json response returned via http.<p>
     * Some unexpected server error occurred. This leads to a 500 with details.
     *
     * @param processingException - something unexpected happened
     */
    protected Response translate(ProcessingException processingException) {
        return Response.status(500)
                .entity(new ApiError(500,
                        processingException.getMessage(),
                        null == processingException.getDetails() ? null : processingException.getDetails()))
                .build();
    }


    /**
     * Translates an very unexpectedly thrown exception to a json response returned via http.<p>
     * This leads to a 500.
     *
     * @param exception - something very unexpected happened
     */
    protected Response translate(Exception exception) {
        return Response.status(500)
                .entity(new ApiError(500,
                        exception.getMessage(),
                        null == exception.getCause() ? null : exception.getCause().getMessage()))
                .build();
    }

    protected Response handleUseCase(UseCase useCase) {
        try {
            useCase.checkSyntax();
            useCase.checkRequirements();
            useCase.execute();
            return new UsecaseResultTranslator().translateUseCaseResult(useCase);
        } catch (SyntaxException se) {
            LOGGER.info("Syntax exception: {}, {}", se.getMessage(), se.getDetails());
            return translate(se);
        } catch (ForbiddenException se) {
            LOGGER.info("Forbidden exception: {}, {}", se.getMessage(), se.getDetails());
            return translate(se);
        } catch (NotFoundException nfe) {
            LOGGER.info("Not found exception exception: {}, {}", nfe.getMessage(), nfe.getDetails());
            return translate(nfe);
        } catch (ProcessingException pe) {
            LOGGER.error("Server processing error: {}, details: {}, input was: {}", pe.getMessage(), pe.getDetails(), dump(useCase));
            return translate(pe);
        } catch (Exception e) {
            LOGGER.error("Server processing error: {}, details: {}, input was: {}", e.getMessage(), null == e.getCause() ? null : e.getCause().getMessage(), dump(useCase));
            return translate(e);
        }
    }
}