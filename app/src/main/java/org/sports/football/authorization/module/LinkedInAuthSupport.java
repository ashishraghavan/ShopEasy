package org.sports.football.authorization.module;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.sports.football.authorization.AuthorizationDelegate;
import org.sports.football.identity.AuthToken;
import org.sports.football.network.HttpParam;
import org.sports.football.network.Response;
import org.sports.football.network.ServiceCall;
import org.sports.football.util.ShopException;

import junit.framework.Assert;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.message.BasicHeader;

import java.util.List;
import java.util.Map;

/**
 * https://www.linkedin.com/uas/oauth2/authorization
 https://www.linkedin.com/uas/oauth2/authorization?response_type=code&client_id=123456789
 &redirect_uri=https%3A%2F%2Fwww.example.com%2Fauth%2Flinkedin&state=987654321&scope=r_basicprofile

 Exchange authorization code for a request token
 https://www.linkedin.com/uas/oauth2/accessToken

 POST /uas/oauth2/accessToken HTTP/1.1
 Host: www.linkedin.com
 Content-Type: application/x-www-form-urlencoded

 grant_type=authorization_code&code=987654321&redirect_uri=https%3A%2F%2Fwww.myapp.com%2Fauth%2Flinkedin&client_id=123456789&client_secret=shhdonottell
 Make authorized requests
 GET /v1/people/~ HTTP/1.1
 Host: api.linkedin.com
 Connection: Keep-Alive
 Authorization: Bearer AQXdSP_W41_UPs5ioT_t8HESyODB4FqbkJ8LrV_5mff4gPODzOYR

 If you make an API call using an invalid token, you will receive a "401 Unauthorized" response back from the server.  A token could be invalid and in need of regeneration because:

 It has expired.
 The user has revoked the permission they initially granted to your application.
 You have changed the member permissions (scope) your application is requesting.
 */
public class LinkedInAuthSupport extends AuthSupport {

    private static final String CODE = "code";
    private static final String REDIRECT_URI = "redirect_uri";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String GRANT_TYPE = "grant_type";

    private static final String redirect_uri = "https://linkedin.com/success";
    private static final String client_id = "75zomdyjvbmlu2";
    private static final String client_secret = "fuW3wTRpbm8YXlwA";
    private static final String response_type = CODE;
    private static final String grant_type = "authorization_code";
    private static String state;

    private static final String TAG = LinkedInAuthSupport.class.getSimpleName();

    public static final String AUTHORIZATION_CODE_ENDPOINT = "https://www.linkedin.com/uas/oauth2/authorization?"
            + "response_type="+ response_type
            + "&client_id=%s"
            + "&redirect_uri=%s"
            + "&state=%s"
            + "&scope=r_basicprofile";

    public static final String TOKEN_ENDPOINT = "https://www.linkedin.com/uas/oauth2/accessToken";

    public static final String TOKEN_VALIDATION_ENDPOINT = "https://api.linkedin.com/v1/people/~";
    //".", "-", "*", and "_"

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
        return String.format(AUTHORIZATION_CODE_ENDPOINT,client_id,redirect_uri,getState());
    }

    @Override
    public String getTokenEndpoint() {
        return TOKEN_ENDPOINT;
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
        return ServiceCall.EMethodType.POST;
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
     * Escaping has to be performed on the code for the following
     * The special characters ".", "-", "*", and "_".
     * @param code
     * @return
     */
    @Override
    public List<HttpParam> getTokenParams(String code) {
        final List<HttpParam> httpParamList = Lists.newArrayList();
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
        return null;
    }

    @Override
    public String getRedirectUrl() {
        return null;
    }

    @Override
    public String getTokenValidationEndpoint() {
        return TOKEN_VALIDATION_ENDPOINT;
    }

    /**
     * @param authToken                 The auth token that was obtained when either the user authorized first
     *                                  or subsequent token verification calls.
     * @param tokenVerificationCallback The callback to be called once the token verification
     */
    @Override
    public void verifyToken(@NonNull AuthToken authToken, @NonNull AuthorizationDelegate.TokenVerificationCallback tokenVerificationCallback) {

    }

    /**
     * A syncronous way of verifying if the auth token was valid.
     *
     * @param authToken
     * @throws ShopException
     *
     * {
        "firstName": "Ashish",
        "headline": "J2EE/Android Engineer at Halosys Technologies Inc",
        "id": "B5HTIdX1yA",
        "lastName": "Raghavan",
        "siteStandardProfileRequest": {"url": "https://www.linkedin.com/profile/view?id=82990649&authType=name&authToken=gs6x&trk=api*a4725851*s5042541*"}
    }
     */
    @Override
    public boolean verifyToken(@NonNull AuthToken authToken) {
        try {
            final ServiceCall serviceCall = new ServiceCall.ServiceCallBuilder()
                    .setMethod(ServiceCall.EMethodType.GET)
                    .setUrl(TOKEN_VALIDATION_ENDPOINT)
                    .setHeaderElements(ImmutableList.<BasicHeader>builder().
                            add(new BasicHeader("x-li-format","json")).
                            add(new BasicHeader("Authorization","Bearer "+authToken.getAccess_token())).build())
                    .build();
            final Response serviceCallResponse = serviceCall.executeRequest();
            final Map tokenResponseMap = serviceCallResponse.getResponseAsType(Map.class);
            final String id = tokenResponseMap.get("id").toString();
            final String firstName = tokenResponseMap.get("firstName").toString();
            final String lastName = tokenResponseMap.get("lastName").toString();
            Assert.assertTrue( !Strings.isNullOrEmpty(id) &&
                    !Strings.isNullOrEmpty(firstName) &&
                    !Strings.isNullOrEmpty(lastName));
            return true;
        } catch ( Exception e) {
            Log.e(TAG,"Token verification failed with message "+e.getMessage(),e);
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
     */
    @Override
    public AuthToken refreshToken(@NonNull AuthToken authToken) {
        return null;
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

    private String getState() {
        if ( state != null ) {
            return state;
        }
        state = RandomStringUtils.randomAlphanumeric(15);
        return state;
    }
}
