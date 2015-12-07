package com.shopping.shopeasy.authorization;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.shopping.shopeasy.R;
import com.shopping.shopeasy.authorization.module.AuthSupport;
import com.shopping.shopeasy.authorization.module.FacebookAuthSupport;
import com.shopping.shopeasy.authorization.module.GoogleAuthSupport;
import com.shopping.shopeasy.authorization.module.LinkedInAuthSupport;
import com.shopping.shopeasy.identity.AuthToken;
import com.shopping.shopeasy.identity.EAuthenticationProvider;
import com.shopping.shopeasy.util.ShopException;
import com.shopping.shopeasy.util.Utils;

public class AuthorizationDelegate {

    private static AuthorizationDelegate authorizationDelegate;
    private AuthorizationDelegate(){}
    private static Context context;
    private OAuthCallback oAuthCallback;
    private static AuthSupport authSupport;
    private EAuthenticationProvider provider;
    private static final String TAG = AuthorizationDelegate.class.getSimpleName();
    public static final String ACCESS_TOKEN = "access_token";

    //Initialize appropriate oauth module based on the provider.
    public static AuthorizationDelegate getInstance(@NonNull final Context ctx) {
        if ( authorizationDelegate == null ) {
            authorizationDelegate = new AuthorizationDelegate();
        }

        //Make sure the context belongs to an activity.
        context = ctx;
        if ( !(context instanceof AppCompatActivity) ) {
            throw new RuntimeException("Context needs to belong to an activity.");
        }
        return authorizationDelegate;
    }

    public void registerOAuthCallback(final OAuthCallback oAuthCallback) {
        this.oAuthCallback = oAuthCallback;
    }

    public void setProvider(EAuthenticationProvider provider) {
        this.provider = provider;
        setAuthorizationModule(this.provider);
    }

    public EAuthenticationProvider getProvider() {
        return provider;
    }

    public AuthSupport getAuthSupport() {
        return authSupport;
    }

    public OAuthCallback getoAuthCallback() {
        return this.oAuthCallback;
    }

    public void checkForAuthorization(final @NonNull OAuthCallback oAuthCallback) {
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

    public void checkForAuthorization() {
        if ( this.oAuthCallback == null ) {
            throw new IllegalStateException("Callback initialization error.Call registerOAuthCallback() before calling this method.");
        }
        checkForAuthorization(this.oAuthCallback);
    }


    public void doAuthorize() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AuthorizationDialogFragment.AUTH_SUPPORT,getAuthSupport());
        bundle.putParcelable(AuthorizationDialogFragment.PROVIDER,getProvider());
        final AuthorizationDialogFragment authorizationDialogFragment = AuthorizationDialogFragment.newInstance(bundle);
        authorizationDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager().beginTransaction(),
                AuthorizationDialogFragment.class.getSimpleName());
    }

    private boolean hasAuthorized() {
        //Logic to check if we have a valid access token.
        return false;
    }

    public interface OAuthCallback {
        void onAuthorizationSucceeded(AuthToken authToken);
        void onAuthorizationFailed(final ShopException shopException);
    }

    private static void setAuthorizationModule(@NonNull final EAuthenticationProvider provider) {
        if ( provider == EAuthenticationProvider.GOOGLE ) {
            authSupport = new GoogleAuthSupport();
        }
        if ( provider == EAuthenticationProvider.FACEBOOK) {
            authSupport = new FacebookAuthSupport();
        }
        if ( provider == EAuthenticationProvider.LINKEDIN ) {
            authSupport = new LinkedInAuthSupport();
        }
        if ( authSupport == null ) {
            throw new RuntimeException("Null authentication module");
        }
    }

    public static class AuthorizationDialogFragment extends DialogFragment {

        private FloatingActionButton mCancelBtn;
        private ProgressBar progressBar;
        private WebView webView;

        public static final String AUTH_SUPPORT = "auth_support";
        public static final String PROVIDER = "provider";
        private AuthSupport authSupport;
        private EAuthenticationProvider provider;
        private OAuthCallback authCallback;
        static AuthorizationDialogFragment newInstance(Bundle bundle) {
            final AuthorizationDialogFragment dialogFragment = new AuthorizationDialogFragment();
            dialogFragment.setArguments(bundle);
            return dialogFragment;
        }

        @Override
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            final Bundle bundledArguments = getArguments();
            if ( bundledArguments == null || bundledArguments.getParcelable(PROVIDER) == null ||
                    bundledArguments.getParcelable(AUTH_SUPPORT) == null ) {
                throw new RuntimeException("Bundle needs to be non null and contain an instance of class extending "+AuthSupport.class.getSimpleName()+
                        " and a provider type");
            }

            provider = bundledArguments.getParcelable(PROVIDER);
            authSupport = bundledArguments.getParcelable(AUTH_SUPPORT);
            setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogFragment);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            //try casting this activity instance to the parent activity.
            //i.e The acitivity which added this dialog fragment in the first place.
            try {
                authCallback = (OAuthCallback)activity;
            } catch (ClassCastException e) {
                Log.e(TAG,"The parent activity "+activity.getClass().getSimpleName()+ " must implement the interface "
                        +OAuthCallback.class.getSimpleName());
                throw new RuntimeException("The parent activity "+activity.getClass().getSimpleName()+ " must implement the interface "
                        +OAuthCallback.class.getSimpleName());
            }
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.authorization_dialog,container,false);
            mCancelBtn = (FloatingActionButton)view.findViewById(R.id.btn_cancel);
            progressBar = (ProgressBar)view.findViewById(R.id.webview_progress);
            webView = (WebView)view.findViewById(R.id.authorization_webview);
            final WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.setWebViewClient(new MyWebViewClient());
            return view;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mCancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            webView.loadUrl(authSupport.getTokenEndpoint());
        }

        @Override
        public void onStart() {
            super.onStart();
            final Dialog dialog = getDialog();
            if ( dialog != null ) {
                final Point point = new Point();
                dialog.getWindow().getWindowManager().getDefaultDisplay().getSize(point);
                final int width = (int)(point.x * 0.90);
                final int height = (int)(point.y * 0.90);
                dialog.getWindow().setLayout(width,height);
            }
        }

        class MyWebViewClient extends WebViewClient {

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                //if the desired page has started to load, dismiss the dialog
                //and transfer control back to the parent activity.
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                if ( url.startsWith(authSupport.getRedirectUrl()) ) {
                    final String uriPart = url.substring(url.indexOf(ACCESS_TOKEN));
                    if ( !Strings.isNullOrEmpty(uriPart) ) {
                        //TODO Verify token
                        final AuthToken authToken = Utils.constructAuthToken(uriPart);
                        //TODO Write token to local database.
                        dismiss();
                        authCallback.onAuthorizationSucceeded(authToken);
                    }
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        }
    }

    /**
     * An interface to notify the caller that
     * a token verification either succeeded or failed.
     */
    public interface TokenVerificationCallback {
        void onTokenVerified(final AuthToken authToken);
        void onTokenVerificationFailed(final ShopException shopException);
    }
}
