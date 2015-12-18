package org.sports.football.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.sports.football.R;


public class LeagueAdapter extends RecyclerView.Adapter<LeagueAdapter.ProductViewHolder> {

    private Context context;
    public LeagueAdapter(final Context context) {
        this.context = context;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.list_row_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
