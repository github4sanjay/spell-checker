package com.paytmmall.spellchecker.handler;

import com.paytmmall.spellchecker.exception.CustomExceptions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {CustomExceptions.FileNotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),
                "File not found");
        return new ResponseEntity<>(errorDetails, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {CustomExceptions.FileReadException.class})
    protected ResponseEntity<Object> handleFileRead(RuntimeException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),
                "Exception during file reading.");
        return new ResponseEntity<>(errorDetails, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

}
