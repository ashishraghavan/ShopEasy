package com.shopping.shopeasy.authorization.module;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.shopping.shopeasy.authorization.AuthorizationDelegate;
import com.shopping.shopeasy.identity.AuthToken;

import java.util.List;

public abstract class AuthSupport implements Parcelable {

    /**
     * The auth token endpoint used to verify the identity of the user.
     * Make sure to append query parameters to this endpoint.
     * @return The token endpoint.
     */
    public abstract String getTokenEndpoint();
    public abstract String getPermissionsEndpoint();
    public abstract String getRefreshTokenEndpoint();
    public abstract String getLogoutEndpoint();
    public abstract List<String> getFieldList();

    /**
     * Get the redirect url which the authentication server uses to append the
     * access token.
     * @return The redirect url or null if there isn't specified one.
     */
    public abstract String getRedirectUrl();
    public abstract String getTokenValidationEndpoint();

    /**
     *
     * @param authToken The auth token that was obtained when either the user authorized first
     *                  or subsequent token verification calls.
     * @param tokenVerificationCallback The callback to be called once the token verification
     *                                  either passes or fails.
     */
    public abstract void verifyToken(final @NonNull AuthToken authToken,
                                     final @NonNull AuthorizationDelegate.TokenVerificationCallback tokenVerificationCallback);
}
