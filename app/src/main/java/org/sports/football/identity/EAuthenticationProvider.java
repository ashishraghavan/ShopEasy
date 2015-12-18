package org.sports.football.identity;

public enum EAuthenticationProvider {

    LINKEDIN("LINKEDIN"),
    MICROSOFT("MICROSOFT"),
    GOOGLE("GOOGLE"),
    FACEBOOK("FACEBOOK"),
    YAHOO("YAHOO");

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
