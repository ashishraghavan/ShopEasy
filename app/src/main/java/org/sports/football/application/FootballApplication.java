package org.sports.football.application;

import android.content.Context;
import android.net.http.HttpResponseCache;
import android.support.multidex.MultiDex;
import android.util.Log;

import java.io.File;

public class FootballApplication extends android.support.multidex.MultiDexApplication {

    private static final String TAG = FootballApplication.class.getSimpleName();
    private static final String CACHE_NAME = "ShopEasyCache";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            File httpCacheDir = new File(getCacheDir(), CACHE_NAME);
            long httpCacheSize = 20L * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (Exception e) {
            Log.i(TAG, "HTTP response cache installation failed with message" + e);
        }
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
