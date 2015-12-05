package com.shopping.shopeasy.authorization;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableMap;
import com.shopping.shopeasy.identity.AuthToken;
import com.shopping.shopeasy.util.ShopException;

public class AuthorizationDelegate {

    private static AuthorizationDelegate authorizationDelegate;
    private AuthorizationDelegate(){}
    private static Context context;
    private OAuthCallback oAuthCallback;

    public static AuthorizationDelegate getInstance(@NonNull final Context ctx) {
        if ( authorizationDelegate == null ) {
            authorizationDelegate = new AuthorizationDelegate();
        }

        context = ctx;
        return authorizationDelegate;
    }

    public void registerOAuthCallback(final OAuthCallback oAuthCallback) {
        this.oAuthCallback = oAuthCallback;
    }

    public OAuthCallback getoAuthCallback() {
        return this.oAuthCallback;
    }

    public void checkAndAuthorize(final @NonNull OAuthCallback oAuthCallback) {
        this.oAuthCallback = oAuthCallback;
        //Do actual login.
        if ( hasAuthorized() ) {
            //build the currently authorized user and call
            oAuthCallback.onAuthorizationSucceeded(null);
            return;
        }
        final ShopException shopException = new ShopException("Test Error Message",
                ImmutableMap.<String,Object>builder()
                .put("Test Error Map Key","Test Error Map Value")
        .build(), ShopException.ErrorType.TEST);
        oAuthCallback.onAuthorizationFailed(shopException);
    }

    public void checkAndAuthorize() {
        if ( this.oAuthCallback == null ) {
            throw new IllegalStateException("Callback initialization error.Call registerOAuthCallback() before calling this method.");
        }
        checkAndAuthorize(this.oAuthCallback);
    }

    private boolean hasAuthorized() {
        return false;
    }

    public interface OAuthCallback {
        void onAuthorizationSucceeded(AuthToken authToken);
        void onAuthorizationFailed(final ShopException shopException);
    }
}
