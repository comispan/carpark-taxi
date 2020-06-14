package com.example.carparktaxi.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.util.WebUtils;

import java.util.Collections;
import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler({
            WebClientException.class, HttpMessageNotReadableException.class
    })
    @Nullable
    public final ResponseEntity<ApiError> handleException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();

        log.error("Handling " + ex.getClass().getSimpleName() + " due to " + ex.getMessage());

        if (ex instanceof WebClientException) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            WebClientException wce = (WebClientException) ex;
            return handleWebClientException(wce, headers, status, request);
        } else if (ex instanceof HttpMessageNotReadableException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            HttpMessageNotReadableException hmnre = (HttpMessageNotReadableException) ex;
            return handleHttpMessageNotReadableException(hmnre, headers, status, request);
        }
        else {
            if (log.isWarnEnabled()) {
                log.warn("Unknown exception type: " + ex.getClass().getName());
            }

            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleExceptionInternal(ex, null, headers, status, request);
        }

    }

    protected ResponseEntity<ApiError> handleWebClientException(WebClientException ex,
                                                                             HttpHeaders headers, HttpStatus status,
                                                                             WebRequest request) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        return handleExceptionInternal(ex, new ApiError(errors), headers, status, request);
    }

    protected ResponseEntity<ApiError> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex,
                                                                             HttpHeaders headers, HttpStatus status,
                                                                             WebRequest request) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        return handleExceptionInternal(ex, new ApiError(errors), headers, status, request);
    }


    /**
     * A single place to customize the response body of all Exception types.
     *
     * @param ex The exception
     * @param body The body for the response
     * @param headers The headers for the response
     * @param status The response status
     * @param request The current request
     */
    protected ResponseEntity<ApiError> handleExceptionInternal(Exception ex, @Nullable ApiError body,
                                                               HttpHeaders headers, HttpStatus status,
                                                               WebRequest request)
    {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        return new ResponseEntity<>(body, headers, status);
    }

    @Getter
    @Setter
    private class ApiError {

        private List<String> errors;

        public ApiError(List<String> errors) {
            this.errors = errors;
        }
    }
}
