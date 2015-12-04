package com.shopping.shopeasy.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.shopping.shopeasy.R;
import com.shopping.shopeasy.authorization.AuthorizationDelegate;
import com.shopping.shopeasy.authorization.AuthorizationFragment;
import com.shopping.shopeasy.identity.User;
import com.shopping.shopeasy.util.ShopException;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private AuthorizationDelegate authDelegate;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Check if authorization has already been finished for this application.
        progressBar = (ProgressBar)findViewById(R.id.authorization_progress);
        frameLayout = (FrameLayout)findViewById(R.id.authorization_container);
        authDelegate = AuthorizationDelegate.getInstance(this);
        if ( !authDelegate.hasAuthorized() ) {
            progressBar.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            //show the list of authorization oauth endpoints that are available
            final AuthorizationFragment authorizationFragment = AuthorizationFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.authorization_container,
                    authorizationFragment,AuthorizationFragment.class.getSimpleName()).commit();
            return;
        }

        //Do a login
        progressBar.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.GONE);
        authDelegate.checkAndAuthorize(new AuthorizationDelegate.OnAuthorizationFinished() {
            @Override
            public void onAuthorizationSucceeded(User user) {
                //TODO Do things when authorization succeeds.
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onAuthorizationFailed(ShopException shopException) {
                //TODO Do things when authorization fails.
                progressBar.setVisibility(View.GONE);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
