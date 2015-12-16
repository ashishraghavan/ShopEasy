package com.shopping.shopeasy.model;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.shopping.shopeasy.R;
import com.shopping.shopeasy.network.ServiceCall;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

public class Product implements Parcelable {

    private static final String TAG = Product.class.getSimpleName();

    private String id;
    private String url;
    private String imageUrl;
    private byte[] thumbnail;
    private String title;
    private String description;
    private EProductSource source;
    private Date modified;
    private Review review;
    private Integer reviewCount;
    private String cost;
    private String currency;
    private String shippingCost;
    private String delivery;
    private List<byte[]> detailImages;
    private String detailedDesc;
    private List<Product> relatedItems;
    private Bitmap image;

    public Product() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EProductSource getSource() {
        return source;
    }

    public void setSource(EProductSource source) {
        this.source = source;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(String shippingCost) {
        this.shippingCost = shippingCost;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public List<byte[]> getDetailImages() {
        return detailImages;
    }

    public void setDetailImages(List<byte[]> detailImages) {
        this.detailImages = detailImages;
    }

    public String getDetailedDesc() {
        return detailedDesc;
    }

    public void setDetailedDesc(String detailedDesc) {
        this.detailedDesc = detailedDesc;
    }

    public List<Product> getRelatedItems() {
        return relatedItems;
    }

    public void setRelatedItems(List<Product> relatedItems) {
        this.relatedItems = relatedItems;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        public Product createFromParcel(Parcel pc) {
            return new Product(pc);
        }
        public Product[] newArray(int size) {
            return new Product[size];
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
        dest.writeString(id);
        dest.writeString(url);
        dest.writeString(imageUrl);
        if ( thumbnail == null ) {
            dest.writeInt(0);
        } else {
            dest.writeInt(thumbnail.length);
            dest.writeByteArray(thumbnail);
        }
        dest.writeString(title);
        dest.writeString(description);
        dest.writeSerializable(source);
        dest.writeSerializable(modified);
        dest.writeParcelable(review, 0);
        dest.writeString(cost);
        dest.writeString(currency);
        dest.writeString(shippingCost);
        dest.writeString(delivery);
        dest.writeList(detailImages);
        dest.writeString(detailedDesc);
        dest.writeList(relatedItems);
        dest.writeInt(reviewCount);
    }

    public Product(Parcel pc) {
        id = pc.readString();
        url = pc.readString();
        imageUrl = pc.readString();
        int thumbnaillen = pc.readInt();
        if ( thumbnaillen >0 ) {
            thumbnail = new byte[thumbnaillen];
            pc.readByteArray(thumbnail);
        }
        title = pc.readString();
        description = pc.readString();
        source = (EProductSource)pc.readSerializable();
        modified = (Date)pc.readSerializable();
        review = pc.readParcelable(Product.class.getClassLoader());
        cost = pc.readString();
        currency = pc.readString();
        shippingCost = pc.readString();
        delivery = pc.readString();
        detailImages = Lists.newArrayList();
        pc.readList(detailImages, Product.class.getClassLoader());
        detailedDesc = pc.readString();
        relatedItems = Lists.newArrayList();
        pc.readList(relatedItems, Product.class.getClassLoader());
        reviewCount = pc.readInt();
    }

    public void loadProductImage(@Nullable final ProgressBar progressBar,
                                 @NonNull final RecyclerView.Adapter adapter,
                                 @NonNull final Context context) {
        new AsyncTask<Void,Void,Bitmap>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if ( progressBar != null ) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

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
            protected Bitmap doInBackground(Void... params) {

                if (Strings.isNullOrEmpty(getImageUrl())) {
                    return BitmapFactory.
                            decodeResource(context.getResources(), R.drawable.noimageavailable);
                }

                Bitmap bitmap = null;
                final ServiceCall binarySerCall = new ServiceCall.ServiceCallBuilder()
                        .setUrl(getImageUrl())
                        .setMethod(ServiceCall.EMethodType.GET)
                        .overrideCache(false)
                        .shouldFollowRedirects(true)
                        .shouldLog(true)
                        .build();


                try {
                    final InputStream inputStream = binarySerCall.executeRequestForStream();
                    if ( inputStream != null ) {
                        final byte[] imageArray = ByteStreams.toByteArray(inputStream);
                        if ( imageArray != null &&
                                imageArray.length > 0) {
                            bitmap = BitmapFactory.decodeByteArray(imageArray,0,imageArray.length);
                        }
                    }
                } catch ( Exception e) {
                    Log.e(TAG,"Failed to obtain image for product with id "+getId(),e);
                }

                if ( bitmap == null ) {
                    bitmap = BitmapFactory.
                            decodeResource(context.getResources(), R.drawable.noimageavailable);
                }
                //Return the default image.
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                if ( bitmap == null ) {
                    Log.e(TAG,"Null/Invalid bitmap obtained from service call.");
                } else {
                    setImage(bitmap);
                }
                adapter.notifyDataSetChanged();
            }
        }.execute();

    }
}
