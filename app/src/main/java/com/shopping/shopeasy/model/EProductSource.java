package com.shopping.shopeasy.model;

public enum EProductSource {

    Ebay("Ebay"),
    Walmart("Walmart"),
    Amazon("Amazon");

    private String productSourceType;
    private EProductSource(final String productSourceType) {
        this.productSourceType = productSourceType;
    }

    public String getProductSourceType() {
        return productSourceType;
    }
}
