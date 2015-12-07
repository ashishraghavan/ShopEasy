package com.shopping.shopeasy.authorization.module;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.shopping.shopeasy.authorization.AuthorizationDelegate;
import com.shopping.shopeasy.identity.AuthToken;
import com.shopping.shopeasy.network.HttpParam;
import com.shopping.shopeasy.util.Utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.Map;

/**
 * https://accounts.google.com/o/oauth2/v2/auth?
 scope=email%20profile&
 state=%2Fprofile&
 redirect_uri=https%3A%2F%2Foauth2-login-demo.appspot.com%2Foauthcallback&
 response_type=token&
 client_id=812741506391.apps.googleusercontent.com&
 */
public class GoogleAuthSupport extends AuthSupport {
    private static final String scope = "email profile";
    private static final String redirect_uri = "https://www.shopping.com/success/";
    private static final String response_type = "token";
    private static final String client_id = "675482710608-rs4lb0raai9gcerb2vr95iv00sjt7ck8.apps.googleusercontent.com";
    private static final String client_secret = "AwWoXRvbwWXyZ62CBR3mZDTr";

    private static final String TOKEN_ENDPOINT = "https://accounts.google.com/o/oauth2/v2/auth";
    //Add the query paramter access_token=1/fFBGRNJru1FQd44AzqT3Zg
    private static final String TOKEN_VALIDATION_ENDPOINT = "https://www.googleapis.com/oauth2/v3/tokeninfo";

    private static final String SCOPE = "scope";
    private static final String REDIRECT_URI = "redirect_uri";
    private static final String RESPONSE_TYPE = "response_type";
    private static final String CLIENT_ID = "client_id";
    private static final String PROMPT = "prompt";
    private static final String NONCE = "nonce";

    private static final List<String> fieldList = ImmutableList.of(SCOPE,
            REDIRECT_URI,RESPONSE_TYPE,CLIENT_ID);

    private final Map<String,Object> queryMap = ImmutableMap.<String,Object>builder()
            .put(SCOPE,scope)
            .put(REDIRECT_URI,redirect_uri)
            .put(RESPONSE_TYPE,response_type)
            .put(CLIENT_ID,client_id)
            .build();

    private static final List<HttpParam> httpParamList = Lists.newArrayList();

    @Override
    public String getTokenEndpoint() {
        httpParamList.clear();
        for ( String key : queryMap.keySet()) {
            httpParamList.add(new HttpParam(key,queryMap.get(key)));
        }
        return TOKEN_ENDPOINT + "?"+Utils.appendParams(httpParamList);
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

    @Override
    public void verifyToken(@NonNull AuthToken authToken,
                            @NonNull AuthorizationDelegate.TokenVerificationCallback tokenVerificationCallback) {

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
