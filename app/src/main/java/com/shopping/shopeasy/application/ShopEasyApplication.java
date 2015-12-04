package com.shopping.shopeasy.application;

import android.content.Context;
import android.support.multidex.MultiDex;

public class ShopEasyApplication extends android.support.multidex.MultiDexApplication {

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
