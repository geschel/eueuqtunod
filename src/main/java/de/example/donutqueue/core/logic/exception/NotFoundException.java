package de.example.donutqueue.core.logic.exception;

import de.example.donutqueue.core.logic.usecase.UseCase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Thrown if a {@link UseCase} can not be processed because required entity could not be found.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotFoundException extends Exception {
    private String message;
    private String details;
}
