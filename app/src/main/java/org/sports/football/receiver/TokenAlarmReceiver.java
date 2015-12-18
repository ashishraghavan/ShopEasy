package org.sports.football.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import org.sports.football.identity.AuthToken;
import org.sports.football.identity.EAuthenticationProvider;
import org.sports.football.service.TokenRefreshService;
import org.sports.football.util.Constants;

public class TokenAlarmReceiver extends BroadcastReceiver {
    public TokenAlarmReceiver() {}

    private static final String TAG = TokenAlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {


        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Constants.AUTH_TOKEN);
        wl.acquire();

        try {
            if ( !intent.getAction().equalsIgnoreCase(Constants.TOKEN_REFRESH_ACTION) ) {
                abortBroadcast();
                return;
            }

            //Start the TokenRefreshService
            final EAuthenticationProvider provider = (EAuthenticationProvider)intent.getSerializableExtra(Constants.AUTH_PROVIDER);
            final AuthToken authToken = intent.getParcelableExtra(Constants.AUTH_TOKEN);
            final Intent tokenRefreshService = new Intent(context, TokenRefreshService.class);
            tokenRefreshService.putExtra(Constants.AUTH_PROVIDER,provider);
            tokenRefreshService.putExtra(Constants.AUTH_TOKEN,authToken);
            context.startService(tokenRefreshService);
            abortBroadcast();
        } catch ( Exception e ) {
            Log.e(TAG,"Failed to process broadcast with exception "+e.getMessage(),e);
        } finally {
            releaseWakeLock(wl);
        }
    }


    private void releaseWakeLock(PowerManager.WakeLock wl) {
        //Release the wakelock
        if ( wl != null ) {
            wl.release();
        }
    }
}
