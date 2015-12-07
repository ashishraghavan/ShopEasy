package com.shopping.shopeasy.authorization.module;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.shopping.shopeasy.authorization.AuthorizationDelegate;
import com.shopping.shopeasy.identity.AuthToken;

import java.util.List;

/**
 * Facebook support class.
 * https://www.facebook.com/dialog/oauth?
 client_id={app-id}
 &amp;redirect_uri={redirect-uri}
 lient_id. The ID of your app, found in your app's dashboard.
 redirect_uri. The URL that you want to redirect the person logging in back to.
 This URL will capture the response from the Login Dialog.
 If you are using this in a webview within a desktop app,
 this must be set to https://www.facebook.com/connect/login_success.html.
 */
public class FacebookAuthSupport extends AuthSupport {

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
