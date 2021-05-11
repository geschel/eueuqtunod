package de.example.donutqueue.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * API response in case of an error.
 */
@Getter
@Setter
public class ApiError {

    private int code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String details;

    public ApiError(int code, String error) {
        this.code = code;
        this.message = error;
    }

    public ApiError(int code, String message, String details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

}