package com.shopping.shopeasy.authorization.module;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.shopping.shopeasy.authorization.AuthorizationDelegate;
import com.shopping.shopeasy.authorization.ValidatedToken;
import com.shopping.shopeasy.identity.AuthToken;
import com.shopping.shopeasy.network.HttpParam;
import com.shopping.shopeasy.network.Response;
import com.shopping.shopeasy.network.ServiceCall;
import com.shopping.shopeasy.util.ShopException;
import com.shopping.shopeasy.util.Utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

/**
 * https://accounts.google.com/o/oauth2/v2/auth?
 scope=email%20profile&
 state=%2Fprofile&
 redirect_uri=https%3A%2F%2Foauth2-login-demo.appspot.com%2Foauthcallback&
 response_type=token&
 client_id=812741506391.apps.googleusercontent.com&

 Google does a two step process.
 */
public class GoogleAuthSupport extends AuthSupport {
    //email profile for new auth code
    private static final String scope = "%s";
    //consent select_account when requesting a new auth code.
    private static final String prompt = "%s";
    private static final String redirect_uri = "https://www.shopping.com/success/";
    //code when requesting an authorization code, token when exchanging token for code.
    private static final String response_type = "%s";
    private static final String client_id = "675482710608-6sbo35eqrc3cg7hsusr9di5gtekd4ii4.apps.googleusercontent.com";
    private static final String client_secret = "m0IhRH_O6XxeJKFUJYORaVJW";
    private static final String access_type = "offline";
    private static final String grant_type = "authorization_code";
    private static final String TAG = GoogleAuthSupport.class.getSimpleName();

    private static final String CODE_ENDPOINT = "https://accounts.google.com/o/oauth2/auth";
    private static final String TOKEN_ENDPOINT = "https://accounts.google.com/o/oauth2/token";
    private static final String TOKEN_VERIFICATION_ENDPOINT = "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=%s";

    private static final String SCOPE = "scope";
    private static final String REDIRECT_URI = "redirect_uri";
    private static final String RESPONSE_TYPE = "response_type";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String PROMPT = "prompt";
    private static final String ACCESS_TYPE = "access_type";
    private static final String CODE = "code";
    private static final String GRANT_TYPE = "grant_type";

    private static final List<String> fieldList = ImmutableList.of(SCOPE,
            REDIRECT_URI, RESPONSE_TYPE, CLIENT_ID,CLIENT_SECRET,PROMPT,ACCESS_TYPE,CODE,GRANT_TYPE);

    private static final List<HttpParam> httpParamList = Lists.newArrayList();

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
     *
     * https://accounts.google.com/o/oauth2/v2/auth?scope=profile email
     * &response_type=code&client_id=675482710608-6sbo35eqrc3cg7hsusr9di5gtekd4ii4.apps.googleusercontent.com
     * &prompt=consent select_account
     * &access_type=offline&redirect_uri=https://www.shopping.com/success/
     *
     * The prompt is where the difference lies. We use prompt = consent, if we need to authorize the
     * user again.
     * @return
     */
    @Override
    public String getAuthorizationCodeEndpoint() {

        httpParamList.clear();
        httpParamList.add(new HttpParam(SCOPE, String.format(scope, "email profile")));
        httpParamList.add(new HttpParam(RESPONSE_TYPE,String.format(response_type,"code")));
        httpParamList.add(new HttpParam(CLIENT_ID,client_id));
        httpParamList.add(new HttpParam(PROMPT,String.format(prompt,"consent select_account")));
        httpParamList.add(new HttpParam(ACCESS_TYPE,access_type));
        httpParamList.add(new HttpParam(REDIRECT_URI,redirect_uri));
        return CODE_ENDPOINT + "?"+Utils.appendParams(httpParamList);
    }

    /**
     *  code=4/P7q7W91a-oMsCeLvIaQm6bTrgtp7&
     *  client_id=8819981768.apps.googleusercontent.com&
     *  client_secret={client_secret}&
        redirect_uri=https://oauth2-login-demo.appspot.com/code&
        grant_type=authorization_code
     * @return
     */
    @Override
    public String getTokenEndpoint() {
        return TOKEN_ENDPOINT;
    }

    /**
     * Is read for two step auth providers.
     * Since we would be calling an oauth token endpoint using
     * POST call, the webview needs the post parameters in a
     * byte array format.
     *
     * @return
     * @param authorizationCode
     */
    @Override
    public byte[] getTokenByteParams(String authorizationCode) {
        final StringBuilder paramBuilder = new StringBuilder();
        if ( !Strings.isNullOrEmpty(authorizationCode)) {
            paramBuilder.append(CODE).append("=").append(authorizationCode)
                    .append("&");
        }

        paramBuilder.append(CLIENT_ID).append("=").append(client_id)
                .append("&")
                .append(CLIENT_SECRET).append("=").append(client_secret)
                .append("&")
                .append(REDIRECT_URI).append("=").append(redirect_uri)
                .append("&")
                .append(GRANT_TYPE).append("=").append(grant_type);
        try {
            return paramBuilder.toString().getBytes();
        } catch ( Exception e) {
            Log.i(TAG,e.getLocalizedMessage(),e);
            throw new RuntimeException("Failed to url encode params");
        }
    }

    /**
     * Is read for two step auth providers where a list of
     * {@link HttpParam} is used instead of a byte array.
     *
     * @param code
     * @return
     */
    @Override
    public List<HttpParam> getTokenParams(String code) {
        httpParamList.clear();
        httpParamList.add(new HttpParam(CODE, code));
        httpParamList.add(new HttpParam(CLIENT_ID,client_id));
        httpParamList.add(new HttpParam(CLIENT_SECRET,client_secret));
        httpParamList.add(new HttpParam(REDIRECT_URI,redirect_uri));
        httpParamList.add(new HttpParam(GRANT_TYPE,grant_type));
        return httpParamList;
    }

    @Override
    public String getPermissionsEndpoint() {
        return null;
    }

    @Override
    public String getRefreshTokenEndpoint() {
        return null;
    }

    @Override
    public String getLogoutEndpoint() {
        return null;
    }

    @Override
    public List<String> getFieldList() {
        return fieldList;
    }

    @Override
    public String getRedirectUrl() {
        return redirect_uri;
    }

    @Override
    public String getTokenValidationEndpoint() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void verifyToken(@NonNull AuthToken authToken,
                            AuthorizationDelegate.TokenVerificationCallback tokenVerificationCallback)  throws ShopException{
        //Use the token verification endpoint to validate a token.

        final ServiceCall serviceCall = new ServiceCall.ServiceCallBuilder()
                .setUrl(String.format(TOKEN_VERIFICATION_ENDPOINT,authToken.getAccess_token()))
                .setConnectionTimeOut(80000L)
                .setSocketTimeOut(80000L)
                .setMethod(ServiceCall.EMethodType.GET)
                .overrideCache(true)
                .build();
        try {
            final Response response = serviceCall.executeRequest();
            final ValidatedToken validatedToken = response.getResponseAsType(ValidatedToken.class);
            Log.i(TAG,validatedToken.getExp());
            Log.i(TAG,validatedToken.getEmail());
            Log.i(TAG,validatedToken.getExpires_in());
        } catch (Exception e) {
            Log.e(TAG,e.getLocalizedMessage(),e);
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
     */
    @Override
    public boolean verifyToken(@NonNull final AuthToken authToken) {
        try {
            verifyToken(authToken,null);
            return true;
        } catch (ShopException e) {
            Log.i(TAG,"Token verification failed with message "+e.getMessage());
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
    public void refreshToken(@NonNull AuthToken authToken,
                             @NonNull AuthorizationDelegate.TokenRefreshCallback tokenRefreshCallback) {
        final ServiceCall serviceCall = new ServiceCall.ServiceCallBuilder()
                .overrideCache(true)
                .setConnectionTimeOut(75000L)
                .setSocketTimeOut(75000L)
                .setMethod(ServiceCall.EMethodType.GET)
                .setUrl(String.format(getRefreshTokenEndpoint(),authToken.getAccess_token()))
                .shouldLog(true)
                .build();
    }

    /**
     * A syncronous way of refreshing the token.
     *
     * @param authToken The token that needs to be refreshed
     * @return {@link AuthToken} with the modified access_token value
     * and the expires information.
     */
    @Override
    public AuthToken refreshToken(@NonNull AuthToken authToken) {
        return null;
    }

    @Override
    public String getSuccessRedirectionEndpoint() {
        return getRedirectUrl() + "?code=";
    }

    @Override
    public String getErrorRedirectionEndpoint() {
        return getRedirectUrl() + "?error=access_denied";
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

    public String getRandomNonce() {
        return RandomStringUtils.randomAlphanumeric(10);
    }
}
