package com.paytmmall.spellchecker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class CustomExceptions {

    private static final long serialVersionUID = -1451545022162714730L;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class FileNotFoundException extends RuntimeException {

        public FileNotFoundException(String exception) {
            super(exception);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidRequestException extends RuntimeException {

        public InvalidRequestException(String exception) {
            super(exception);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class FileReadException  extends RuntimeException {

        public FileReadException(String exception) {
            super(exception);
        }
    }
}
