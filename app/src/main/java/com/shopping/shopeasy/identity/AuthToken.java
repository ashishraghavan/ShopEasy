package com.shopping.shopeasy.identity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.Lists;

import java.util.List;

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

    private String accessToken;
    private Long expiresIn;
    private String tokenType;
    private String refreshToken;
    private List<Object> parameterList;

    public AuthToken(){}

    public AuthToken(@NonNull final String accessToken,
                     @NonNull final Long expiresIn,
                     @NonNull final String tokenType,
                     @Nullable final String refreshToken,
                     @Nullable final List<Object> parameterList) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
        this.refreshToken = refreshToken;
        this.parameterList = parameterList;
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
        dest.writeString(accessToken);
        dest.writeLong(expiresIn);
        dest.writeString(tokenType);
        dest.writeString(refreshToken);
        dest.writeList(parameterList);
    }

    public AuthToken(Parcel pc) {
        this.accessToken = pc.readString();
        this.expiresIn = pc.readLong();
        this.tokenType = pc.readString();
        this.refreshToken = pc.readString();
        if ( this.parameterList == null ) {
            parameterList = Lists.newArrayList();
        }
        pc.readList(parameterList,AuthToken.class.getClassLoader());
    }
}
