package org.sports.football.util;

import java.util.Map;

public class ShopException extends Exception {

    String errorMessage;
    Map<String,Object> errorMap;
    ErrorType errorType;

    public ShopException() {super();}

    public ShopException(final String errorMessage, final Map<String, Object> errorMap,
                         ErrorType errorType) {
        super(errorMessage);
        this.errorMap = errorMap;
        this.errorMessage = errorMessage;
        this.errorType = errorType;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static class ShopExceptionBuilder {

        private String newErrorMessage;
        private ErrorType newErrorType;
        private Map<String,Object> newErrorMap;
        public ShopExceptionBuilder message(final String message) {
            this.newErrorMessage = message;
            return this;
        }

        public ShopExceptionBuilder map(final Map<String,Object> errorMap) {
            this.newErrorMap = errorMap;
            return this;
        }

        public ShopExceptionBuilder errorType(final ErrorType errorType) {
            this.newErrorType = errorType;
            return this;
        }

        public ShopException build() {
            return new ShopException(newErrorMessage,newErrorMap,newErrorType);
        }
    }

    @Override
    public String getMessage() {
        return this.errorMessage;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String getLocalizedMessage() {
        return this.errorMessage;
    }

    public enum ErrorType {
        NETWORK_TIMEOUT("Network timed out"),
        TEST("Test"),
        UNKNOWN("Unknown error occured"),
        INVALID_TOKEN("Invalid token");

        private String errorType;
        private ErrorType(final String errorType) {
            this.errorType = errorType;
        }

        public String getErrorType() {
            return this.errorType;
        }
    }
}
