package com.shopping.shopeasy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.shopping.shopeasy.identity.AuthToken;
import com.shopping.shopeasy.identity.EAuthenticationProvider;
import com.shopping.shopeasy.service.TokenRefreshService;
import com.shopping.shopeasy.util.Constants;

public class TokenAlarmReceiver extends BroadcastReceiver {
    public TokenAlarmReceiver() {}
    private PowerManager.WakeLock wl;

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Constants.AUTH_TOKEN);
        wl.acquire();

        //Start the TokenRefreshService
        final EAuthenticationProvider provider = (EAuthenticationProvider)intent.getSerializableExtra(Constants.AUTH_PROVIDER);
        final AuthToken authToken = intent.getParcelableExtra(Constants.AUTH_TOKEN);
        final Intent tokenRefreshService = new Intent(context, TokenRefreshService.class);
        tokenRefreshService.putExtra(Constants.AUTH_PROVIDER,provider);
        tokenRefreshService.putExtra(Constants.AUTH_TOKEN,authToken);
        context.startService(tokenRefreshService);
        abortBroadcast();
    }
}
