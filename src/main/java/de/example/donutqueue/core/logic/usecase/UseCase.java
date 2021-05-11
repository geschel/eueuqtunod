package de.example.donutqueue.core.logic.usecase;

import de.example.donutqueue.core.logic.exception.NotFoundException;
import de.example.donutqueue.core.logic.exception.ProcessingException;
import de.example.donutqueue.core.logic.exception.ForbiddenException;
import de.example.donutqueue.core.logic.exception.SyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * A UseCase encapsulates business logic and every instance must be executed only once.<p>
 * It takes input parameters, executes business logic and provides either a sensible result or throws an exception.<p>
 * UseCase is protocol agnostic (no http requests etc.) in order to enable easy unit testing.
 */
public abstract class UseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(UseCase.class);

    /**
     * Override if there are input parameters that need checking.
     *
     * @throws SyntaxException - if at least one parameter has wrong syntax
     */
    public void checkSyntax() throws SyntaxException {
        //everything is fine, overwrite only if necessary
    }

    /**
     * Override if there are input parameters that need checking.
     *
     * @throws ForbiddenException - if at least one parameter does not make sense
     */
    public void checkRequirements() throws ForbiddenException, NotFoundException {
        //override only if necessary
    }

    /**
     * Execute the business logic and return either an object which can be translated into a sensible result or throw an exception if something unexpected happens.
     *
     * @throws ProcessingException - if something very unexpected happens, will lead to a 500 server error
     */
    public void execute() throws ProcessingException, NotFoundException {
        try {
            handleExecution();
        } catch (NotFoundException nfe) {
            //rethrow because this is somewhat expected behaviour
            throw nfe;
        } catch (Exception e) {
            final String errorId = UUID.randomUUID().toString();
            LOGGER.error("Execution failed for params {} with message {} and id {}.", dumpInput(), e.getMessage(), errorId);
            throw new ProcessingException("Internal Server Error. It has been logged.", errorId);
        }
    }

    /**
     * Every use case should be able to dump it's input params as string for debugging purposes.
     */
    public abstract String dumpInput();

    /**
     * This method does the actual work. It needs an override in specialized use case classes.
     */
    public abstract void handleExecution() throws NotFoundException;

}
