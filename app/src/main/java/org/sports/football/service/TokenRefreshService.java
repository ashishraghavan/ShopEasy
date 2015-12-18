package org.sports.football.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.sports.football.authorization.module.AuthSupport;
import org.sports.football.authorization.module.FacebookAuthSupport;
import org.sports.football.authorization.module.GoogleAuthSupport;
import org.sports.football.authorization.module.LinkedInAuthSupport;
import org.sports.football.identity.AuthToken;
import org.sports.football.identity.EAuthenticationProvider;
import org.sports.football.util.Constants;
import org.sports.football.util.Utils;

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
        if ( provider == EAuthenticationProvider.GOOGLE ) {
            authSupport = new GoogleAuthSupport();
        }

        if ( authSupport == null ) {
            Log.e(TAG,"Only facebook and linkedin doesn't accept" +
                    "use of expired access token to obtain refresh token");
            return;
        }

        final AuthToken refreshedToken = authSupport.refreshToken(authToken);
        //Write the time stamp along with the auth token class
        refreshedToken.setTokenObtainedTime(System.currentTimeMillis());

        try {
            final SharedPreferences refreshTokenPreferences = getApplication().getSharedPreferences(Constants.LOG_PREFERENCE, 0);
            final String refreshTokenJSON = Utils.getSafeMapper().writeValueAsString(refreshedToken);
            refreshTokenPreferences.edit().putString(Constants.REFRESH_AUTH_TOKEN,refreshTokenJSON).apply();
        } catch ( Exception e ) {
            Log.e(TAG,"Failed to write refreshed token log to preferences");
        }

        if ( authToken.getAccess_token().equalsIgnoreCase(refreshedToken.getAccess_token())) {
            Log.e(TAG,"Failed to refresh access token");
            return;
        }

        Log.i(TAG,"Access token refreshed successfully, updating preferences with" +
                "updated access token");
        final SharedPreferences preferences = this.getSharedPreferences(Constants.AUTH_PREFERENCE,0);
        final String providerStr = preferences.getString(Constants.AUTH_PROVIDER, null);

        //Checking to see if current user's provider is same as one being rereshed.
        if ( EAuthenticationProvider.valueOf(providerStr) == provider ) {
            final SharedPreferences.Editor preferenceEditor = preferences.edit();
            preferenceEditor.putString(Constants.AUTH_PROVIDER,providerStr);
            try {
                preferenceEditor.putString(Constants.AUTH_TOKEN, Utils.getSafeMapper().writeValueAsString(refreshedToken));
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
