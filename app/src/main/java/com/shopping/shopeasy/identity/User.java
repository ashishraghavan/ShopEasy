package com.shopping.shopeasy.identity;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{

    private String userName;
    private EAuthenticationProvider provider;


    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel pc) {
            return new User(pc);
        }
        public User[] newArray(int size) {
            return new User[size];
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
        dest.writeString(userName);
        dest.writeString(provider.toString());
    }

    public User(Parcel pc) {
        userName = pc.readString();
        provider = EAuthenticationProvider.valueOf(pc.readString());
    }
}
