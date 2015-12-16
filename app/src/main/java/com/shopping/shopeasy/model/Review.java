package com.shopping.shopeasy.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Review implements Parcelable {

    private String comment;
    private String rating;
    private String maxiumuRating;
    private Date created;

    public Review(){}

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getMaxiumuRating() {
        return maxiumuRating;
    }

    public void setMaxiumuRating(String maxiumuRating) {
        this.maxiumuRating = maxiumuRating;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        public Review createFromParcel(Parcel pc) {
            return new Review(pc);
        }
        public Review[] newArray(int size) {
            return new Review[size];
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
        dest.writeString(getComment());
        dest.writeString(getRating());
        dest.writeString(getMaxiumuRating());
        dest.writeSerializable(getCreated());
    }

    public Review(Parcel pc) {
        comment = pc.readString();
        rating = pc.readString();
        maxiumuRating = pc.readString();
        created = (Date)pc.readSerializable();
    }
}
