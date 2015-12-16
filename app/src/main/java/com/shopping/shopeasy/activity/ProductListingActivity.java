package com.shopping.shopeasy.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.common.collect.Lists;
import com.shopping.shopeasy.R;
import com.shopping.shopeasy.adapter.ProductAdapter;
import com.shopping.shopeasy.manager.ProductManager;
import com.shopping.shopeasy.model.EProductSource;
import com.shopping.shopeasy.model.Product;
import com.shopping.shopeasy.network.HttpParam;
import com.shopping.shopeasy.network.Response;
import com.shopping.shopeasy.network.ServiceCall;

import java.util.List;
import java.util.Map;

public class ProductListingActivity extends AppCompatActivity {
    private ProductAdapter productAdapter;
    private RecyclerView mRecyclerView;
    private List<Product> productList;
    private static final String TAG = ProductListingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_listing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView)findViewById(R.id.product_recycler_view);
        productList = Lists.newArrayList();
        productAdapter = new ProductAdapter(productList,this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(productAdapter);

        final List<HttpParam> paramList = Lists.newArrayList();
        paramList.add(new HttpParam("callname","FindPopularItems"));
        paramList.add(new HttpParam("responseencoding","JSON"));
        paramList.add(new HttpParam("appid","AshishRa-8ce5-427f-b4f5-0201c683052c"));
        paramList.add(new HttpParam("siteid",0));
        paramList.add(new HttpParam("QueryKeywords","mlb"));
        paramList.add(new HttpParam("version", "713"));

        final ServiceCall productSerCall = new ServiceCall.ServiceCallBuilder()
                .setUrl("http://open.api.ebay.com/shopping")
                .setParams(paramList)
                .overrideCache(false)
                .shouldLog(true)
                .setMethod(ServiceCall.EMethodType.GET)
                .build();
        new AsyncGetProductList().execute(productSerCall);
    }

    class AsyncGetProductList extends AsyncTask<ServiceCall,Void,List<Product>> {

        @Override
        @SuppressWarnings("unchecked")
        protected List<Product> doInBackground(ServiceCall... params) {
            final ServiceCall serviceCall = params[0];
            try {
                final Response response = serviceCall.executeRequest();
                final Map<String,Object> responseMap = response.getResponseAsType(Map.class);
                return ProductManager.parseProducts(responseMap, EProductSource.Ebay);
            } catch (Exception e) {
                Log.e(TAG,"Failed to retrieve product list with message "+e.getMessage(),e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Product> productList) {
            super.onPostExecute(productList);

            if (productAdapter != null ) {
                ProductListingActivity.this.productList.addAll(productList);
                productAdapter.notifyDataSetChanged();

                for ( Product product : productList ) {
                    product.loadProductImage(null,productAdapter, ProductListingActivity.this);
                }
            }
        }
    }
}
