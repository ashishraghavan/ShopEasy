package com.shopping.shopeasy.manager;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.shopping.shopeasy.model.EProductSource;
import com.shopping.shopeasy.model.Product;

import java.util.List;
import java.util.Map;

public class ProductManager {

    public static final Map<String,String> currencyMap = ImmutableMap.<String,String>builder()
            .put("USD", "$")
            .build();

    @SuppressWarnings("unchecked")
    public static Product parseProduct(final Map<String,Object> resource,
                                       final EProductSource productSource) {
        final Product product = new Product();
        if ( productSource == EProductSource.Ebay ) {
            if ( resource.get("Title") != null ) {
                product.setTitle(resource.get("Title").toString());
            }

            if ( resource.get("GalleryURL") != null ) {
                product.setImageUrl(resource.get("GalleryURL").toString());
            }

            if ( resource.get("ConvertedCurrentPrice") != null && resource.get("ConvertedCurrentPrice") instanceof Map) {
                final Map<String,Object> currencyMap = (Map<String,Object>)resource.get("ConvertedCurrentPrice");
                product.setCost(currencyMap.get("Value").toString());
                if ( currencyMap.get("CurrencyID") != null ) {
                    product.setCurrency(ProductManager.currencyMap.get(currencyMap.get("CurrencyID").toString()));
                }
            }

            product.setId(resource.get("ItemID").toString());
        }
        return product;
    }

    @SuppressWarnings("unchecked")
    public static List<Product> parseProducts(final Map<String,Object> resourceMap, final EProductSource productSource) {
        final List<Product> productList = Lists.newArrayList();
        if ( productSource == EProductSource.Ebay ) {
            final Map<String,Object> itemSubMap = (Map<String,Object>)resourceMap.get("ItemArray");
            final List<Map<String,Object>> itemSubArray = (List<Map<String,Object>>)itemSubMap.get("Item");
            for ( Map<String,Object> productMap : itemSubArray ) {
                productList.add(parseProduct(productMap,productSource));
            }
        }

        return productList;
    }
}
