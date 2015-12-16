package com.shopping.shopeasy.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.shopping.shopeasy.authorization.module.AuthSupport;
import com.shopping.shopeasy.authorization.module.FacebookAuthSupport;
import com.shopping.shopeasy.authorization.module.LinkedInAuthSupport;
import com.shopping.shopeasy.identity.AuthToken;
import com.shopping.shopeasy.identity.EAuthenticationProvider;
import com.shopping.shopeasy.util.Constants;
import com.shopping.shopeasy.util.Utils;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class TokenRefreshService extends IntentService {

    public static final String TAG = TokenRefreshService.class.getSimpleName();

    public TokenRefreshService() {
        super(TokenRefreshService.class.getSimpleName());
    }

    /**
     * EAuthenticationProvider, Context.
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG,"Service to refresh auth token started");
        Log.i(TAG,"Getting auth provider and auth token from the intent extras");
        final EAuthenticationProvider provider = (EAuthenticationProvider)intent.getSerializableExtra(Constants.AUTH_PROVIDER);
        final AuthToken authToken = intent.getParcelableExtra(Constants.AUTH_TOKEN);
        Log.i(TAG,"Auth provider and auth token deserialized successfully from intent extras");

        AuthSupport authSupport = null;
        if ( provider == EAuthenticationProvider.LINKEDIN ) {
            authSupport = new LinkedInAuthSupport();
        }
        if ( provider == EAuthenticationProvider.FACEBOOK ) {
            authSupport = new FacebookAuthSupport();
        }

        if ( authSupport == null ) {
            Log.e(TAG,"Only facebook and linkedin doesn't accept" +
                    "use of expired access token to obtain refresh token");
            return;
        }

        final AuthToken refreshedToken = authSupport.refreshToken(authToken);
        if ( authToken.getAccess_token().equalsIgnoreCase(refreshedToken.getAccess_token())) {
            Log.e(TAG,"Failed to refresh access token");
            return;
        }

        Log.i(TAG,"Access token refreshed successfully, updating preferences with" +
                "updated access token");
        final SharedPreferences preferences = this.getSharedPreferences(Constants.AUTH_PREFERENCE,0);
        final String providerStr = preferences.getString(Constants.AUTH_PROVIDER, null);
        final String authTokenStr = preferences.getString(Constants.AUTH_TOKEN,null);

        //Checking to see if current user's provider is same as one being rereshed.
        if ( EAuthenticationProvider.valueOf(providerStr) == provider ) {
            final SharedPreferences.Editor preferenceEditor = preferences.edit();
            preferenceEditor.putString(Constants.AUTH_PROVIDER,providerStr);
            try {
                preferenceEditor.putString(Constants.AUTH_TOKEN, Utils.getSafeMapper().writeValueAsString(authTokenStr));
                preferenceEditor.apply();
                Log.i(TAG, "AUth token refreshed successfully for " + providerStr);
                Log.i(TAG, "The refreshed token will now expire after " + refreshedToken.getExpires_in() + " seconds");
                //Set alarm to repeat
                Utils.setAlarmForPreferences(this, refreshedToken, provider);
            } catch(Exception e) {
                Log.e(TAG,"Failed to update auth token for preference with message "+e.getMessage(),e);
            }
        }
    }
}
