package org.sports.football.authorization;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.common.base.Strings;

import org.sports.football.R;
import org.sports.football.authorization.module.AuthSupport;
import org.sports.football.authorization.module.FacebookAuthSupport;
import org.sports.football.authorization.module.GoogleAuthSupport;
import org.sports.football.authorization.module.LinkedInAuthSupport;
import org.sports.football.identity.AuthToken;
import org.sports.football.identity.EAuthenticationProvider;
import org.sports.football.network.Response;
import org.sports.football.network.ServiceCall;
import org.sports.football.util.ShopException;
import org.sports.football.util.Utils;

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

    public void doAuthorize() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AuthorizationDialogFragment.AUTH_SUPPORT,getAuthSupport());
        bundle.putSerializable(AuthorizationDialogFragment.PROVIDER, getProvider());
        final AuthorizationDialogFragment authorizationDialogFragment = AuthorizationDialogFragment.newInstance(bundle);
        authorizationDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager().beginTransaction(),
                AuthorizationDialogFragment.class.getSimpleName());
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
            if ( bundledArguments == null || bundledArguments.getSerializable(PROVIDER) == null ||
                    bundledArguments.getParcelable(AUTH_SUPPORT) == null ) {
                throw new RuntimeException("Bundle needs to be non null and contain an instance of class extending "+AuthSupport.class.getSimpleName()+
                        " and a provider type");
            }

            provider = (EAuthenticationProvider)bundledArguments.getSerializable(PROVIDER);
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
            webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) " +
                    "AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19");
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
            if ( authSupport.isTwoStepOAuth() ) {
                webView.loadUrl(authSupport.getAuthorizationCodeEndpoint());
            } else {
                webView.loadUrl(authSupport.getTokenEndpoint());
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            final Dialog dialog = getDialog();
            if ( dialog != null ) {
                final Point point = new Point();
                dialog.getWindow().getWindowManager().getDefaultDisplay().getSize(point);
                //Get the height of the toolbar.
                final Toolbar toolbar = (Toolbar)((AppCompatActivity)context).findViewById(R.id.toolbar);
                int toolbarHeight = 0;
                if ( toolbar != null ) {
                    toolbarHeight = toolbar.getMeasuredHeight();
                }
                final int width = point.x;
                final int height = point.y - toolbarHeight;
                dialog.getWindow().setLayout(width,height);
            }
        }

        class MyWebViewClient extends WebViewClient {

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                //if the desired page has started to load, dismiss the dialog
                //and transfer control back to the parent activity.
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //If no error has occurred, load the url normally.
                if ( /*Success*/ url.startsWith(authSupport.getSuccessRedirectionEndpoint())) {
                    view.stopLoading();
                    if ( !authSupport.isTwoStepOAuth() ) {
                        final int accessTokenIndex = url.indexOf(ACCESS_TOKEN);
                        if ( accessTokenIndex > 0 ) {
                            //redirect url contains the access token.
                            final String uriPart = url.substring(accessTokenIndex);
                            if ( !Strings.isNullOrEmpty(uriPart) ) {
                                final AuthToken authToken = Utils.constructAuthToken(uriPart);
                                //Set the time the token was obtained.
                                authToken.setTokenObtainedTime(System.currentTimeMillis());
                                Utils.writeToPreferences(context,authToken,provider);
                                dismiss();
                                authCallback.onAuthorizationSucceeded(authToken);
                            }
                        }
                    } else {
                        //parse authorization code out of the uri.
                        final String codePair = url.substring(url.indexOf(authSupport.getCodeField()));
                        final String[] splitCodePair = codePair.split("=");
                        final String authorizationCode = splitCodePair[1];
                        //& is the token used for url paramter separation.
                        //TODO Do this in a clean way!
                        if ( authorizationCode.contains("&") ) {
                            postCodeForToken(authorizationCode.substring(0,authorizationCode.indexOf("&")));
                        } else {
                            postCodeForToken(authorizationCode);
                        }
                    }
                }

                if ( /*Failure */ authSupport.getErrorRedirectionEndpoint() != null && url.startsWith(authSupport.getErrorRedirectionEndpoint()) ) {
                    view.stopLoading();
                    dismiss();
                } else {
                    view.loadUrl(url);
                }
                return true;
            }



            /**
             * Does the job of exchanging a token for a code.
             * This applies only to auth providers with two step
             * oauth process (mostly for web applications).
             * @param code The authorization code obtained from step 1 (code parsed from
             *             that appended to the redirect uri.)
             */
            void postCodeForToken(final String code) {
                final ServiceCall serviceCall = new ServiceCall.ServiceCallBuilder()
                        .setMethod(authSupport.getTokenMethod())
                        .setUrl(authSupport.getTokenMethod() == ServiceCall.EMethodType.GET ?
                                authSupport.getTokenEndpoint() + "&code=" + code : authSupport.getTokenEndpoint())
                        .setParams(authSupport.getTokenParams(code))
                        .setConnectionTimeOut(80000L)
                        .setSocketTimeOut(80000L)
                        .build();
                progressBar.setVisibility(View.VISIBLE);
                new AsyncTask<Void,Void,AuthToken>() {

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
                    protected AuthToken doInBackground(Void... params) {
                        try {
                            final Response response = serviceCall.executeRequest();
                            final AuthToken authToken = response.getResponseAsType(AuthToken.class);
                            if ( Strings.isNullOrEmpty(authToken.getAccess_token()) ) {
                                Log.e(TAG,"Null access token on deserialized auth token from server response");
                                return null;
                            }
                            //Print token details.
                            Log.i(TAG,"Access token "+Utils.encrypt(authToken.getAccess_token()));
                            Log.i(TAG,"Expires in "+authToken.getExpires_in());
                            //Set token obtained time to current time.
                            authToken.setTokenObtainedTime(System.currentTimeMillis());
                            //Set the code required to retrieve the token at a later time.
                            authToken.setAuthorizationCode(code);
                            //Write auth token to storage.
                            Utils.writeToPreferences(context, authToken, provider);
                            Utils.setAlarmForPreferences(context,authToken,provider);
                            return authToken;
                        } catch ( Exception e) {
                            Log.e(TAG,"Failed to obtain token in exchange for a code with message "+e.getMessage());
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(AuthToken authToken) {
                        super.onPostExecute(authToken);
                        if ( progressBar.getVisibility() == View.VISIBLE ) {
                            progressBar.setVisibility(View.GONE);
                        }

                        if (authToken != null) {
                            progressBar.setVisibility(View.GONE);
                            //Pass control back to the activity.
                            if (authCallback != null) {
                                authCallback.onAuthorizationSucceeded(authToken);
                                dismiss();
                            }
                        } else {
                            //Token failed. stay on this web page. Ask the user to refresh.
                            Toast.makeText(context,"An unknown error occured while obtaining token. Please try again.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }.execute();
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

    /**
     * An interface to notify the caller that
     * a token refresh request succeeded or failed.
     */
    public interface TokenRefreshCallback {
        void onTokenRefreshed(final AuthToken authToken);
        void onTokenRefreshFailed(final ShopException shopException);
    }
}
