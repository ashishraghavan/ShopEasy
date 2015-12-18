package org.sports.football.authorization.module;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.sports.football.authorization.AuthorizationDelegate;
import org.sports.football.identity.AuthToken;
import org.sports.football.network.HttpParam;
import org.sports.football.network.Response;
import org.sports.football.network.ServiceCall;
import org.sports.football.util.ShopException;

import junit.framework.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Facebook support class.
 * https://www.facebook.com/dialog/oauth?
 client_id={app-id}
 &amp;redirect_uri={redirect-uri}
 lient_id. The ID of your app, found in your app's dashboard.
 redirect_uri. The URL that you want to redirect the person logging in back to.
 This URL will capture the response from the Login Dialog.
 If you are using this in a webview within a desktop app,
 this must be set to https://www.facebook.com/connect/login_success.html.
 */
public class FacebookAuthSupport extends AuthSupport {

    private static final String AUTHORIZATION_CODE_ENDPOINT = "https://www.facebook.com/dialog/oauth?" +
            "client_id=%s" +
            "&redirect_uri=%s" +
            "&response_type=%s";
    //&code=%s is also required, will be done in delegate class.
    private static final String TOKEN_ENDPOINT = "https://graph.facebook.com/v2.3/oauth/access_token?" +
            "client_id=%s&redirect_uri=%s&client_secret=%s";

    // The input token is the token to be inspected, whereas
    private static final String TOKEN_VALIDATION_ENDPOINT = "https://graph.facebook.com/debug_token?" +
            "input_token=%s" +
            "&access_token=%s";


    private static final String TOKEN_REFRESH_ENDPOINT = "https://www.facebook.com/oauth/access_token?" +
            "grant_type=fb_exchange_token" +
            "&client_id=%s"+
            "&client_secret=%s" +
            "&fb_exchange_token=%s";

    private static final String ERROR_REDIRECTION_ENDPOINT = "https://www.facebook.com/connect/login_success.html?" +
            "error_reason=%s" +
            "&error=%s" +
            "&error_description=%s";

    private static final String TAG = FacebookAuthSupport.class.getSimpleName();

    //Facebook requires us to generate an app access token to validate a previously acquired token.
    private static final String APP_ACCESS_TOKEN_ENDPOINT = "https://graph.facebook.com/oauth/access_token?" +
            "client_id=%s" +
            "&client_secret=%s" +
            "&grant_type=client_credentials";

    private static final String CLIENT_ID = "client_id";
    private static final String REDIRECT_URI = "redirect_uri";
    private static final String RESPONSE_TYPE = "response_type";
    private static final String CODE = "code";
    private static final String GRANTED_SCOPES = "granted_scopes";
    private static final String SCOPE = "scope";

    private static final String client_id = "963307897090232";
    private static final String client_secret = "5b406fcf65e4b7732b3c0cf62f02d03f";
    private static final String response_type = "code";
    private static final String redirect_uri = "https://www.facebook.com/connect/login_success.html";

    private static final List<String> fieldList = ImmutableList.of(CLIENT_ID,REDIRECT_URI,
            RESPONSE_TYPE,CODE,GRANTED_SCOPES,SCOPE);
    /**
     * A boolean flat indicating if a subclass requires a two step
     * process to get the authorization token.
     *
     * @return
     */
    @Override
    public boolean isTwoStepOAuth() {
        return true;
    }

    /**
     * A field which indicates which field in the redirect
     * url is to be parsed to obtain the authorization code.
     * Is read only if the oauth is a two step process.
     *
     * @return
     */
    @Override
    public String getCodeField() {
        return CODE;
    }

    /**
     * When requesting a token through a web application path,
     * often auth service providers will use a two step process.
     * In step 1, the authorization code is retrieved. We parse the
     * authorization code from the redirect url, use it to get the
     * auth token using the token endpoint.
     * Most often the token and code endpoints are the same.
     *
     * @return
     */
    @Override
    public String getAuthorizationCodeEndpoint() {
        return String.format(AUTHORIZATION_CODE_ENDPOINT,client_id,redirect_uri,response_type);
    }

    @Override
    public String getTokenEndpoint() {
        return String.format(TOKEN_ENDPOINT,client_id,redirect_uri,client_secret);
    }

    /**
     * All providers don't have a POST method to obtain the token
     * by passing in code as a body paramter. This method returns
     * if a provider obtains an access token by the GET or POST
     * method
     *
     * @return The method type. Possible values are {@link ServiceCall.EMethodType#GET}
     * or {@link ServiceCall.EMethodType#POST}.
     */
    @Override
    public ServiceCall.EMethodType getTokenMethod() {
        return ServiceCall.EMethodType.GET;
    }

    /**
     * Is read for two step auth providers.
     * Since we would be calling an oauth token endpoint using
     * POST call, the webview needs the post parameters in a
     * byte array format.
     *
     * @param code
     * @return
     */
    @Override
    public byte[] getTokenByteParams(String code) {
        return new byte[0];
    }

    /**
     * Is read for two step auth providers where a list of
     * {@link HttpParam} is used instead of a byte array.
     * Facebook uses a GET method to obtain access token.
     * This method should return null or an empty List
     * @param code
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<HttpParam> getTokenParams(String code) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public String getPermissionsEndpoint() {
        return null;
    }

    @Override
    public String getRefreshTokenEndpoint() {
        return TOKEN_REFRESH_ENDPOINT;
    }

    @Override
    public String getLogoutEndpoint() {
        return null;
    }

    @Override
    public List<String> getFieldList() {
        return null;
    }

    @Override
    public String getRedirectUrl() {
        return REDIRECT_URI;
    }

    @Override
    public String getTokenValidationEndpoint() {
        return null;
    }

    /**
     * @param authToken                 The auth token that was obtained when either the user authorized first
     *                                  or subsequent token verification calls.
     * @param tokenVerificationCallback The callback to be called once the token verification
     *
     * {
            "data": {
                "app_id": 138483919580948,
                "application": "Social Cafe",
                "expires_at": 1352419328,
                "is_valid": true,
                "issued_at": 1347235328,
                "metadata": {
                    "sso": "iphone-safari"
                },
                "scopes": [
                    "email",
                    "publish_actions"
                ],
                "user_id": 1207059
            }
        }
     */
    @Override
    public void verifyToken(@NonNull AuthToken authToken,
                            AuthorizationDelegate.TokenVerificationCallback tokenVerificationCallback)
            throws ShopException {
        try {
            //Get the app user access token.
            ServiceCall serviceCall = new ServiceCall.ServiceCallBuilder()
                    .setMethod(ServiceCall.EMethodType.GET)
                    .setUrl(String.format(APP_ACCESS_TOKEN_ENDPOINT,client_id,client_secret))
                    .build();

            final Response appAccessTokenResponse = serviceCall.executeRequest();
            final String appAccessToken = (String)appAccessTokenResponse.getConvertedEntity();

            //Use split to separate out the app_id|access_token
            final String[] appAccTokenArray = appAccessToken.split("=");
            //Encode the pipe character (|).

            serviceCall = new ServiceCall.ServiceCallBuilder()
                    .setMethod(ServiceCall.EMethodType.GET)
                    .setUrl(String.format(TOKEN_VALIDATION_ENDPOINT,authToken.getAccess_token(),appAccTokenArray[1].replace("|", "%7C")))
                    .overrideCache(true)
                    .build();

            final Response tokenValidationResponse = serviceCall.executeRequest();
            final Map validationResponseMap = tokenValidationResponse.getResponseAsType(Map.class);
            final Map dataMap = (Map)validationResponseMap.get("data");
            final String userId = dataMap.get("user_id").toString();
            final boolean isValid = Boolean.parseBoolean(dataMap.get("is_valid").toString());
            final String appId = dataMap.get("app_id").toString();
            Assert.assertNotNull(userId);
            Assert.assertTrue(isValid);
            Assert.assertNotNull(appId);
        } catch ( Exception e) {
            Log.e(TAG,"Token verification failed with message "+e.getLocalizedMessage(),e);
            throw new ShopException.ShopExceptionBuilder()
                    .errorType(ShopException.ErrorType.INVALID_TOKEN)
                    .message("Invalid/expired token.")
                    .build();
        }
    }

    /**
     * A syncronous way of verifying if the auth token was valid.
     *
     * @param authToken
     * @throws ShopException
     *
     * https://graph.facebook.com/endpoint?key=value&access_token=app_secret
     */
    @Override
    public boolean verifyToken(@NonNull AuthToken authToken) {
        try {
            verifyToken(authToken,null);
            return true;
        } catch (ShopException e) {
            Log.i(TAG, "Token verification failed with message " + e.getMessage());
            return false;
        }
    }

    /**
     * An asynchronous way of refreshing the auth token.
     * The refreshed auth token will be written back to the
     * Shared preferences.
     *
     * @param authToken
     * @param tokenRefreshCallback
     */
    @Override
    public void refreshToken(@NonNull AuthToken authToken, @NonNull AuthorizationDelegate.TokenRefreshCallback tokenRefreshCallback) {

    }

    /**
     * A syncronous way of refreshing the token.
     *
     * @param authToken The token that needs to be refreshed
     * @return {@link AuthToken} with the modified access_token value
     * and the expires information.
     * At this point, we know that the token was invalid or expired.
     * This will generate only a long lived token, not refresh it.
     */
    @Override
    public AuthToken refreshToken(@NonNull AuthToken authToken) {

        final ServiceCall serviceCall = new ServiceCall.ServiceCallBuilder()
                .setUrl(String.format(TOKEN_REFRESH_ENDPOINT,client_id,client_secret,authToken.getAccess_token()))
                .overrideCache(true)
                .shouldLog(true)
                .setMethod(ServiceCall.EMethodType.GET)
                .build();

        try {
            final Response serviceResponse = serviceCall.executeRequest();
            final AuthToken refreshedToken = serviceResponse.getResponseAsType(AuthToken.class);
            if (Strings.isNullOrEmpty(refreshedToken.getAccess_token()) ) {
                return authToken;
            }
            return refreshedToken;
        } catch ( Exception e) {
            Log.e(TAG,"Token refresh failed with message "+e.getMessage(),e);
            return authToken;
        }
    }

    @Override
    public String getSuccessRedirectionEndpoint() {
        return redirect_uri;
    }

    @Override
    public String getErrorRedirectionEndpoint() {
        return null;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
