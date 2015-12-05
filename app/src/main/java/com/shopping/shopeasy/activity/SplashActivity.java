package com.shopping.shopeasy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.shopping.shopeasy.R;
import com.shopping.shopeasy.authorization.AuthorizationDelegate;
import com.shopping.shopeasy.identity.AuthToken;
import com.shopping.shopeasy.util.ShopException;

import java.util.concurrent.atomic.AtomicInteger;

public class SplashActivity extends AppCompatActivity implements AuthorizationDelegate.OAuthCallback {

    private ProgressBar progressBar;
    private AuthorizationDelegate authDelegate;
    private ScrollView mAuthProviderView;
    private Button mRetryBtn;
    private final AtomicInteger tries = new AtomicInteger(0);
    private boolean maxRetryReached = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Check if authorization has already been finished for this application.
        progressBar = (ProgressBar)findViewById(R.id.authorization_progress);
        mAuthProviderView = (ScrollView)findViewById(R.id.auth_provider_scroll_view);
        mRetryBtn = (Button)findViewById(R.id.retry_button);
        authDelegate = AuthorizationDelegate.getInstance(this);
        progressBar.setVisibility(View.VISIBLE);
        //Try authorizing.
        //Register the oauth callback
        authDelegate.registerOAuthCallback(this);
        authDelegate.checkAndAuthorize();

        //Set listener for retry button.
        mRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tries.incrementAndGet();
                authDelegate.checkAndAuthorize();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAuthorizationSucceeded(AuthToken authToken) {
        //TODO Do things when authorization succeeds.
        progressBar.setVisibility(View.GONE);
        //Take the user to the Products activity.
    }

    @Override
    public void onAuthorizationFailed(ShopException shopException) {
        //TODO Do things when authorization fails.
        //Depending on what caused the failure, we show the Retry button
        //For example, if its a network time out exception, we show the
        //retry button. Otherwise, we show the list of providers
        //with a toast message informing the user why the authorization
        //failed and provide them with options to authorize with a
        //different federated identity provider.
        progressBar.setVisibility(View.GONE);
        if ( shopException.getErrorType() == ShopException.ErrorType.NETWORK_TIMEOUT ) {
            //Show the retry button.
            //If pressed ok, increment the no of tries.
            if ( tries.get() >= 1 ) {
                //Hide the retry button if visible
                if ( mRetryBtn != null &&
                        mRetryBtn.getVisibility() == View.VISIBLE ) {
                    mRetryBtn.setVisibility(View.GONE);
                    Toast.makeText(this,"Authorization Failed with provider. Please re-login again",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                mRetryBtn.setVisibility(View.VISIBLE);
                return;
            }
        }
        mAuthProviderView.setVisibility(View.VISIBLE);
    }
}
