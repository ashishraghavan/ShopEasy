package org.sports.football.authorization.module;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.sports.football.authorization.AuthorizationDelegate;
import org.sports.football.identity.AuthToken;
import org.sports.football.network.HttpParam;
import org.sports.football.network.ServiceCall;
import org.sports.football.util.ShopException;

import java.util.List;

public abstract class AuthSupport implements Parcelable {

    /**
     * A boolean flat indicating if a subclass requires a two step
     * process to get the authorization token.
     * @return
     */
    public abstract boolean isTwoStepOAuth();

    /**
     * A field which indicates which field in the redirect
     * url is to be parsed to obtain the authorization code.
     * Is read only if the oauth is a two step process.
     * @return
     */
    public abstract String getCodeField();

    /**
     * When requesting a token through a web application path,
     * often auth service providers will use a two step process.
     * In step 1, the authorization code is retrieved. We parse the
     * authorization code from the redirect url, use it to get the
     * auth token using the token endpoint.
     * Most often the token and code endpoints are the same.
     * @return
     */
    public abstract String getAuthorizationCodeEndpoint();
    /**
     * The auth token endpoint used to verify the identity of the user.
     * Make sure to append query parameters to this endpoint.
     * @return The token endpoint.
     */
    public abstract String getTokenEndpoint();

    /**
     * All providers don't have a POST method to obtain the token
     * by passing in code as a body paramter. This method returns
     * if a provider obtains an access token by the GET or POST
     * method
     * @return The method type. Possible values are {@link ServiceCall.EMethodType#GET}
     * or {@link ServiceCall.EMethodType#POST}.
     */
    public abstract ServiceCall.EMethodType getTokenMethod();

    /**
     * Is read for two step auth providers.
     * Since we would be calling an oauth token endpoint using
     * POST call, the webview needs the post parameters in a
     * byte array format.
     * @return
     * @param code
     */
    public abstract byte[] getTokenByteParams(String code);

    /**
     * Is read for two step auth providers where a list of
     * {@link HttpParam} is used instead of a byte array.
     * @param code
     * @return
     */
    public abstract List<HttpParam> getTokenParams(final String code);

    public abstract String getPermissionsEndpoint();
    public abstract String getRefreshTokenEndpoint();
    public abstract String getLogoutEndpoint();
    public abstract List<String> getFieldList();

    /**
     * Get the redirect url which the authentication server uses to append the
     * access token.
     * @return The redirect url or null if there isn't specified one.
     */
    public abstract String getRedirectUrl();
    public abstract String getTokenValidationEndpoint();

    /**
     *
     * @param authToken The auth token that was obtained when either the user authorized first
     *                  or subsequent token verification calls.
     * @param tokenVerificationCallback The callback to be called once the token verification
     *                                  either passes or fails.
     */
    public abstract void verifyToken(final @NonNull AuthToken authToken,
                                     final AuthorizationDelegate.TokenVerificationCallback tokenVerificationCallback) throws ShopException;

    /**
     * A syncronous way of verifying if the auth token was valid.
     * @param authToken
     * @throws ShopException
     */
    public abstract boolean verifyToken(final @NonNull AuthToken authToken);

    /**
     * An asynchronous way of refreshing the auth token.
     * The refreshed auth token will be written back to the
     * Shared preferences.
     * @param tokenRefreshCallback
     */
    public abstract void refreshToken(final @NonNull AuthToken authToken,
                                      final @NonNull AuthorizationDelegate.TokenRefreshCallback tokenRefreshCallback);

    /**
     * A syncronous way of refreshing the token.
     * @param authToken The token that needs to be refreshed
     * @return {@link AuthToken} with the modified access_token value
     * and the expires information.
     */
    public abstract AuthToken refreshToken(final @NonNull AuthToken authToken);

    public abstract String getSuccessRedirectionEndpoint();

    public abstract String getErrorRedirectionEndpoint();
}
