package de.example.donutqueue.core.logic.exception;

import de.example.donutqueue.core.logic.usecase.UseCase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Thrown if a {@link UseCase} fails unexpectedly. This should lead to a 500 with a detailed explanation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingException extends Exception {
    private String message;
    private String details;
}
