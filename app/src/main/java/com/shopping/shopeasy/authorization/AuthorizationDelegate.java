package com.shopping.shopeasy.authorization;

import android.content.Context;
import android.support.annotation.NonNull;

import com.shopping.shopeasy.identity.User;
import com.shopping.shopeasy.util.ShopException;

public class AuthorizationDelegate {

    private static AuthorizationDelegate authorizationDelegate;
    private AuthorizationDelegate(){}
    private static Context context;
    private OnAuthorizationFinished authorizationListener;

    public static AuthorizationDelegate getInstance(@NonNull final Context ctx) {
        if ( authorizationDelegate == null ) {
            authorizationDelegate = new AuthorizationDelegate();
        }

        context = ctx;
        return authorizationDelegate;
    }

    public void checkAndAuthorize(final OnAuthorizationFinished authorizationListener) {
        this.authorizationListener = authorizationListener;
        //Do actual login.
        authorizationListener.onAuthorizationSucceeded(null);
    }

    public boolean hasAuthorized() {
        return false;
    }

    public interface OnAuthorizationFinished {
        void onAuthorizationSucceeded(final User user);
        void onAuthorizationFailed(final ShopException shopException);
    }
}
