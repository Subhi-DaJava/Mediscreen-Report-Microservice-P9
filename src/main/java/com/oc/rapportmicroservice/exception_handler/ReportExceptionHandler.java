package com.oc.rapportmicroservice.exception_handler;

import com.oc.rapportmicroservice.exception.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ReportExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleResourceNotFoundException(ResourceNotFoundException noteNotFoundException, WebRequest webRequest) {
        ResponseMessage errorResponse = new ResponseMessage(
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                noteNotFoundException.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { HttpClientErrorException.NotFound.class })
    protected ResponseEntity<Object> handleNotFoundException(HttpClientErrorException ex, WebRequest request) {
        String errorMessage = ex.getStatusText() + " : " + ex.getResponseBodyAsString();
        return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
