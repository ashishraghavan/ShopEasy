package com.shopping.shopeasy.util;

public class Constants {
    public static final String SECRET = "11235813213455";

    //Preferences for storing the currently used access token with
    //the type of the provider.

    public static final String AUTH_PREFERENCE = "auth_preference";
    /*The provider which is currently being used*/
    public static final String AUTH_PROVIDER = "auth_provider";
    /*The JSON format of the auth token class*/
    public static final String AUTH_TOKEN = "auth_token";
    /*Action when no auth token is found from preferences*/
    public static final String ACTION_NO_AUTH_TOKEN = "action_no_auth_token";
    /*Action when auth token is found but token has expired.*/
    public static final String ACTION_EXPIRED_AUTH_TOKEN = "action_expired_auth_token";
    /* Check network connection URL */
    public static final String NETWORK_VALIDATION_URL = "http://connectivitycheck.android.com/generate_204";
}
