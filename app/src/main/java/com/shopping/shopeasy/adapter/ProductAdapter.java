package com.shopping.shopeasy.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.shopping.shopeasy.R;
import com.shopping.shopeasy.model.EProductSource;
import com.shopping.shopeasy.model.Product;

import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private Context context;
    private Map<EProductSource,Drawable> productSourceMap;

    public ProductAdapter(List<Product> productList,final Context context) {
        this.productList = productList;
        this.context = context;
        setProductSourceMap();
    }

    private void setProductSourceMap() {
        final Resources resources = context.getResources();
        productSourceMap = Maps.newHashMap();
        productSourceMap.put(EProductSource.Ebay,resources.getDrawable(R.drawable.ebay_img));
        productSourceMap.put(EProductSource.Walmart,resources.getDrawable(R.drawable.walmart_logo));
        productSourceMap.put(EProductSource.Amazon,resources.getDrawable(R.drawable.amazon_logo));
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.list_row_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        final Product product = productList.get(position);
        if ( product != null ) {
            holder.mProductNameView.setText(product.getTitle());
            if ( product.getReview() != null ) {

                if ( !Strings.isNullOrEmpty(product.getReview().getRating()) ) {
                    holder.mRatingBar.setRating(Float.parseFloat(product.getReview().getRating()));
                }

                if ( !Strings.isNullOrEmpty(product.getReview().getMaxiumuRating()) ) {
                    holder.mRatingBar.setMax(Integer.parseInt(product.getReview().getMaxiumuRating()));
                }
            }
            if ( product.getReviewCount() != null ) {
                holder.mReviewView.setText(String.format(context.getResources().getString(R.string.review_number),
                        product.getReviewCount().toString()));
            }
            if ( !Strings.isNullOrEmpty(product.getCost())) {
                holder.mProductCostView.setVisibility(View.VISIBLE);
                holder.mProductCostView.setText(String.format(context.getResources().
                        getString(R.string.default_product_cost),
                        product.getCurrency(),product.getCost()));
            }

            if ( product.getSource() != null ) {
                holder.mProductSourceView.setImageDrawable(productSourceMap.get(product.getSource()));
            }

            if ( product.getImage() != null ) {
                holder.mImageView.setImageBitmap(product.getImage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mImageView;
        TextView mProductNameView;
        RatingBar mRatingBar;
        TextView mReviewView;
        TextView mProductCostView;
        ImageView mProductSourceView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView)itemView.findViewById(R.id.product_image);
            mProductNameView = (TextView)itemView.findViewById(R.id.product_name);
            mRatingBar = (RatingBar)itemView.findViewById(R.id.product_rating);
            mReviewView = (TextView)itemView.findViewById(R.id.review_number);
            mProductCostView = (TextView)itemView.findViewById(R.id.product_cost);
            mProductSourceView = (ImageView)itemView.findViewById(R.id.product_source);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
