package de.example.donutqueue.core.logic.exception;

import de.example.donutqueue.core.logic.usecase.UseCase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Thrown if a {@link UseCase} encounters syntactically bad input parameters.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyntaxException extends Exception {
    private String message;
    private String details;
}
