package de.example.donutqueue.core.logic.exception;

import de.example.donutqueue.core.logic.usecase.UseCase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Thrown if a {@link UseCase} can not be processed because requirements are not met and therefore access is forbidden.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForbiddenException extends Exception {
    private String message;
    private String details;
}
