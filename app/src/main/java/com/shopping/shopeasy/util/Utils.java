package com.shopping.shopeasy.util;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.shopping.shopeasy.identity.AuthToken;
import com.shopping.shopeasy.network.HttpParam;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();
    public static final ObjectMapper safeMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,false)
            .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,false)
            .configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS,false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    @SuppressWarnings("unchecked")
    public static String appendParams(@NonNull List<HttpParam> params) {
        StringBuilder query = new StringBuilder();

        try {
            int k = 0;
            if (!params.isEmpty()) {

                for ( HttpParam httpParam : params ) {
                    if (k > 0) {
                        query.append("&");
                    }

                    final String key = httpParam.getName();
                    final Object value = httpParam.getValue();
                    if (value instanceof List) {

                        if (!((List) value).isEmpty()) {

                            switch (key) {
                                default:
                                    List genericList = ((List) value);
                                    int j = 0;
                                    for (Object id : genericList) {
                                        if (j > 0) {
                                            query.append("&");
                                        }
                                        query.append(key);
                                        query.append("=").append(id);
                                        j++;
                                    }

                                    break;
                            }
                        }

                    } else {
                        query.append(key)
                                .append("=").
                                append(value != null ?
                                        URLEncoder.encode(value.toString(), "UTF-8")
                                        : "");
                    }
                    k++;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }

        String queryString = query.toString();
        if ( queryString.contains("+")) {
            queryString = queryString.replace("+","%20");
        }
        return queryString;
    }

    public static AuthToken constructAuthToken(final String uri) {
        Map<String, Object> resultQueryMap = null;
        final String[] splitParameterKV = uri.split("&");
        if ( splitParameterKV.length > 0 ) {
            resultQueryMap = Maps.newHashMap();
            for (String splitParameterKVPair : splitParameterKV) {
                //split further using = as the split paramter
                final String[] splitParameter = splitParameterKVPair.split("=");
                if ( splitParameter.length != 2) {
                    continue;
                }
                resultQueryMap.put(splitParameter[0],splitParameter[1]);
            }
        }

        if ( resultQueryMap != null ) {
            resultQueryMap.put("tokenObtainedTime",System.currentTimeMillis());
        }

        return safeMapper.convertValue(resultQueryMap,AuthToken.class);
    }
}
