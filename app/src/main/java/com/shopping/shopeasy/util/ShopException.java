package com.shopping.shopeasy.util;

import java.util.Map;

public class ShopException extends Exception {

    private String errorMessage;
    private Map<String,Object> errorMap;
    private ErrorType errorType;

    public ShopException() {super();}

    public ShopException(final String errorMessage, final Map<String, Object> errorMap,
                         ErrorType errorType) {
        super(errorMessage);
        this.errorMap = errorMap;
        this.errorMessage = errorMessage;
        this.errorType = errorType;
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
        UNKNOWN("Unknown error occured");

        private String errorType;
        private ErrorType(final String errorType) {
            this.errorType = errorType;
        }

        public String getErrorType() {
            return this.errorType;
        }
    }
}
