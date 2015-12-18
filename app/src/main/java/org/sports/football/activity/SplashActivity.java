package org.sports.football.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.common.base.Strings;

import org.sports.football.R;
import org.sports.football.authorization.module.AuthSupport;
import org.sports.football.authorization.module.FacebookAuthSupport;
import org.sports.football.authorization.module.GoogleAuthSupport;
import org.sports.football.authorization.module.LinkedInAuthSupport;
import org.sports.football.identity.AuthToken;
import org.sports.football.identity.EAuthenticationProvider;
import org.sports.football.util.Constants;
import org.sports.football.util.Utils;

import java.util.Arrays;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final SharedPreferences authPreferences = SplashActivity.this.
                getSharedPreferences(Constants.AUTH_PREFERENCE, 0);
        final String providerStr = authPreferences.getString(Constants.AUTH_PROVIDER, null);
        final String tokenJSON = authPreferences.getString(Constants.AUTH_TOKEN,null);

        if (Strings.isNullOrEmpty(providerStr) ||
                Strings.isNullOrEmpty(tokenJSON)) {
            Log.e(TAG, "Invalid or null token or provider.");
            final Intent intent = new Intent(SplashActivity.this, AuthorizationActivity.class);
            intent.setAction(Constants.ACTION_NO_AUTH_TOKEN);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                }
            }, 3000);
        } else {
            new AsyncTokenTask(EAuthenticationProvider.valueOf(providerStr), tokenJSON)
                    .execute();
        }
    }

    private class AsyncTokenTask extends AsyncTask<String,Integer,AuthToken> {

        private EAuthenticationProvider provider;
        private String tokenJSON;

        public AsyncTokenTask(@NonNull final EAuthenticationProvider provider,
                              @NonNull final String tokenJSON) {
            this.provider = provider;
            this.tokenJSON = tokenJSON;
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected AuthToken doInBackground(String... params) {

            try {
                final AuthToken authToken = Utils.getSafeMapper().readValue(tokenJSON, AuthToken.class);
                //Give 25% to deserializing the value of auth token present in the preferences.
                final AuthSupport authSupport;
                if ( provider == EAuthenticationProvider.GOOGLE) {
                    authSupport = new GoogleAuthSupport();
                } else if ( provider == EAuthenticationProvider.FACEBOOK) {
                    authSupport = new FacebookAuthSupport();
                } else if ( provider == EAuthenticationProvider.LINKEDIN) {
                    authSupport = new LinkedInAuthSupport();
                } else {
                    Log.e(TAG,"Invalid value of auth provider. " +
                            "Must be one of ["+ Arrays.asList(EAuthenticationProvider.values())+"]");
                    return null;
                }

                if (!authSupport.verifyToken(authToken)) {
                    //Token verification failed. Refresh this token.
                    final AuthToken refreshedToken = authSupport.refreshToken(authToken);
                    //Just be sure, the original token and the refreshed token won't match.
                    if ( !authToken.getAccess_token().equalsIgnoreCase(refreshedToken.getAccess_token())) {
                        Log.d(TAG,"Original - "+authToken.getAccess_token()+" and refreshed token - "
                                +refreshedToken.getAccess_token()+" does not match");
                        Utils.writeToPreferences(SplashActivity.this, refreshedToken, provider);
                        return refreshedToken;
                    }
                }

                Log.i(TAG,"Token is valid and will expire at "+authToken.getExpires_in());
                return authToken;
            } catch (Exception e) {
                Log.e(TAG,"Invalid/null auth token from token verification call with message "+e.getMessage(),e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(AuthToken authToken) {
            super.onPostExecute(authToken);

            if ( authToken == null || Strings.isNullOrEmpty(authToken.getAccess_token())) {
                //Token refresh or verification for current provider failed.
                final Intent expiredTokenIntent = new Intent(SplashActivity.this,AuthorizationActivity.class);
                /** Set action as {@link Constants#ACTION_EXPIRED_AUTH_TOKEN} and start {@link AuthorizationActivity}*/
                expiredTokenIntent.setAction(Constants.ACTION_EXPIRED_AUTH_TOKEN);
                expiredTokenIntent.putExtra(Constants.AUTH_PROVIDER, provider);
                startActivity(expiredTokenIntent);
                return;
            }

            //Start the product listing activity
            final Intent productListingIntent = new Intent(SplashActivity.this,Football.class);
            startActivity(productListingIntent);
        }
    }
}
