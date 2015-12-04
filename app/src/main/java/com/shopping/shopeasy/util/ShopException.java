package com.shopping.shopeasy.util;

import java.util.Map;

public class ShopException extends Exception {

    private String errorMessage;
    private Map<String,Object> errorMap;

    public ShopException(final String errorMessage,final Map<String,Object> errorMap) {
        this.errorMap = errorMap;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return this.errorMessage;
    }

    @Override
    public String getLocalizedMessage() {
        return this.errorMessage;
    }
}
