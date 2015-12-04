package com.shopping.shopeasy.identity;

public enum EAuthenticationProvider {

    LINKEDIN("linkedin"),
    MICROSOFT("microsoft"),
    GOOGLE("google"),
    FACEBOOK("facebook"),
    YAHOO("yahoo");

    private String name;
    EAuthenticationProvider(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
