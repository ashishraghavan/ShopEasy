package com.shopping.shopeasy.authorization.module;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.shopping.shopeasy.authorization.AuthorizationDelegate;
import com.shopping.shopeasy.identity.AuthToken;

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

    @Override
    public String getTokenEndpoint() {
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
