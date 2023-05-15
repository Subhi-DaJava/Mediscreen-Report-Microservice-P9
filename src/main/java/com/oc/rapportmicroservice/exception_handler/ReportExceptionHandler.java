package com.oc.rapportmicroservice.exception_handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ReportExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { HttpClientErrorException.NotFound.class })
    protected ResponseEntity<Object> handleNotFoundException(HttpClientErrorException ex, WebRequest request) {
        String errorMessage = ex.getStatusText() + " : " + ex.getResponseBodyAsString();
        return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }


}
