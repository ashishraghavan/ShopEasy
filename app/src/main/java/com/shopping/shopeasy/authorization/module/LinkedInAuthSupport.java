package com.shopping.shopeasy.authorization.module;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.shopping.shopeasy.authorization.AuthorizationDelegate;
import com.shopping.shopeasy.identity.AuthToken;
import com.shopping.shopeasy.network.HttpParam;
import com.shopping.shopeasy.util.ShopException;

import java.util.List;

/**
 * https://www.linkedin.com/uas/oauth2/authorization
 https://www.linkedin.com/uas/oauth2/authorization?response_type=code&client_id=123456789
 &redirect_uri=https%3A%2F%2Fwww.example.com%2Fauth%2Flinkedin&state=987654321&scope=r_basicprofile

 Exchange authorization code for a request token
 https://www.linkedin.com/uas/oauth2/accessToken

 POST /uas/oauth2/accessToken HTTP/1.1
 Host: www.linkedin.com
 Content-Type: application/x-www-form-urlencoded

 grant_type=authorization_code&code=987654321&redirect_uri=https%3A%2F%2Fwww.myapp.com%2Fauth%2Flinkedin&client_id=123456789&client_secret=shhdonottell
 Make authorized requests
 GET /v1/people/~ HTTP/1.1
 Host: api.linkedin.com
 Connection: Keep-Alive
 Authorization: Bearer AQXdSP_W41_UPs5ioT_t8HESyODB4FqbkJ8LrV_5mff4gPODzOYR

 If you make an API call using an invalid token, you will receive a "401 Unauthorized" response back from the server.  A token could be invalid and in need of regeneration because:

 It has expired.
 The user has revoked the permission they initially granted to your application.
 You have changed the member permissions (scope) your application is requesting.
 */
public class LinkedInAuthSupport extends AuthSupport {

    /**
     * A boolean flat indicating if a subclass requires a two step
     * process to get the authorization token.
     *
     * @return
     */
    @Override
    public boolean isTwoStepOAuth() {
        return false;
    }

    /**
     * A field which indicates which field in the redirect
     * url is to be parsed to obtain the authorization code.
     * Is read only if the oauth is a two step process.
     *
     * @return
     */
    @Override
    public String getCodeField() {
        return null;
    }

    /**
     * When requesting a token through a web application path,
     * often auth service providers will use a two step process.
     * In step 1, the authorization code is retrieved. We parse the
     * authorization code from the redirect url, use it to get the
     * auth token using the token endpoint.
     * Most often the token and code endpoints are the same.
     *
     * @return
     */
    @Override
    public String getAuthorizationCodeEndpoint() {
        return null;
    }

    @Override
    public String getTokenEndpoint() {
        return null;
    }

    /**
     * Is read for two step auth providers.
     * Since we would be calling an oauth token endpoint using
     * POST call, the webview needs the post parameters in a
     * byte array format.
     *
     * @param code
     * @return
     */
    @Override
    public byte[] getTokenByteParams(String code) {
        return new byte[0];
    }

    /**
     * Is read for two step auth providers where a list of
     * {@link HttpParam} is used instead of a byte array.
     *
     * @param code
     * @return
     */
    @Override
    public List<HttpParam> getTokenParams(String code) {
        return null;
    }

    @Override
    public String getPermissionsEndpoint() {
        return null;
    }

    @Override
    public String getRefreshTokenEndpoint() {
        return null;
    }

    @Override
    public String getLogoutEndpoint() {
        return null;
    }

    @Override
    public List<String> getFieldList() {
        return null;
    }

    @Override
    public String getRedirectUrl() {
        return null;
    }

    @Override
    public String getTokenValidationEndpoint() {
        return null;
    }

    /**
     * @param authToken                 The auth token that was obtained when either the user authorized first
     *                                  or subsequent token verification calls.
     * @param tokenVerificationCallback The callback to be called once the token verification
     */
    @Override
    public void verifyToken(@NonNull AuthToken authToken, @NonNull AuthorizationDelegate.TokenVerificationCallback tokenVerificationCallback) {

    }

    /**
     * A syncronous way of verifying if the auth token was valid.
     *
     * @param authToken
     * @throws ShopException
     */
    @Override
    public boolean verifyToken(@NonNull AuthToken authToken) {
        return true;
    }

    /**
     * An asynchronous way of refreshing the auth token.
     * The refreshed auth token will be written back to the
     * Shared preferences.
     *
     * @param authToken
     * @param tokenRefreshCallback
     */
    @Override
    public void refreshToken(@NonNull AuthToken authToken, @NonNull AuthorizationDelegate.TokenRefreshCallback tokenRefreshCallback) {

    }

    /**
     * A syncronous way of refreshing the token.
     *
     * @param authToken The token that needs to be refreshed
     * @return {@link AuthToken} with the modified access_token value
     * and the expires information.
     */
    @Override
    public AuthToken refreshToken(@NonNull AuthToken authToken) {
        return null;
    }

    @Override
    public String getSuccessRedirectionEndpoint() {
        return null;
    }

    @Override
    public String getErrorRedirectionEndpoint() {
        return null;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
