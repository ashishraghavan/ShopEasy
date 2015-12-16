package com.shopping.shopeasy.identity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * https://tools.ietf.org/html/rfc6749#section-4.3.3
 * HTTP/1.1 200 OK
     Content-Type: application/json;charset=UTF-8
     Cache-Control: no-store
     Pragma: no-cache

     {
     "access_token":"2YotnFZFEjr1zCsicMWpAA",
     "token_type":"example",
     "expires_in":3600,
     "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA",
     "example_parameter":"example_value"
     }
 */
public class AuthToken implements Parcelable {

    private String access_token;
    private Long tokenObtainedTime;
    private Long expires_in;
    private String token_type;
    private String refresh_token;
    private Map<String, Object> extras;
    private String authorizationCode;
    private boolean expired;

    public AuthToken(){}

    public AuthToken(@NonNull final String access_token,
                     @NonNull final Long expires_in,
                     @Nullable final Long tokenObtainedTime,
                     @NonNull final String token_type,
                     @Nullable final String refresh_token,
                     @Nullable final Map<String, Object> extras) {
        this.access_token = access_token;
        this.expires_in = expires_in;
        this.token_type = token_type;
        this.refresh_token = refresh_token;
        this.extras = extras;
        this.tokenObtainedTime = tokenObtainedTime;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public Long getTokenObtainedTime() {
        return tokenObtainedTime;
    }

    public void setTokenObtainedTime(Long tokenObtainedTime) {
        this.tokenObtainedTime = tokenObtainedTime;
    }

    public Long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Long expires_in) {
        this.expires_in = expires_in;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public Map<String, Object> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, Object> extras) {
        this.extras = extras;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean isExpired() {
        return expired;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public static final Creator<AuthToken> CREATOR = new Creator<AuthToken>() {
        public AuthToken createFromParcel(Parcel pc) {
            return new AuthToken(pc);
        }
        public AuthToken[] newArray(int size) {
            return new AuthToken[size];
        }
    };
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
        dest.writeString(access_token);
        dest.writeLong(expires_in);
        dest.writeLong(tokenObtainedTime);
        dest.writeString(token_type);
        dest.writeString(refresh_token);
        dest.writeByte((byte)(expired ? 1 : 0));
        dest.writeMap(extras);
        dest.writeString(authorizationCode);
    }

    public AuthToken(Parcel pc) {
        access_token = pc.readString();
        expires_in = pc.readLong();
        tokenObtainedTime = pc.readLong();
        token_type = pc.readString();
        refresh_token = pc.readString();
        expired = pc.readByte() !=0;
        if ( extras == null ) {
            extras = Maps.newHashMap();
        }
        pc.readMap(extras, AuthToken.class.getClassLoader());
        authorizationCode = pc.readString();
    }


}
