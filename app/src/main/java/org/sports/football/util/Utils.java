package org.sports.football.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import junit.framework.Assert;

import org.apache.commons.lang3.math.NumberUtils;
import org.sports.football.database.ShopDBHelper;
import org.sports.football.identity.AuthToken;
import org.sports.football.identity.EAuthenticationProvider;
import org.sports.football.network.HttpParam;
import org.sports.football.network.Response;
import org.sports.football.network.ServiceCall;
import org.sports.football.receiver.TokenAlarmReceiver;

import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();
    public static final String UTF8 = "utf-8";
    private static final char[] secretString = Constants.SECRET.toCharArray();
    public static final Set<String> specialCharacters = ImmutableSet.of(".", "-", "*", "_");
    public static final ObjectMapper safeMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,false)
            .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,false)
            .configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS,false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

    public static ObjectMapper getSafeMapper() {
        return safeMapper;
    }

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

    public static String encrypt(String value) {
        try {
            final byte[] bytes = value !=null ? value.getBytes(UTF8) : new byte[0];
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(secretString));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.ENCRYPT_MODE,key,
                    new PBEParameterSpec(Build.SERIAL.getBytes(UTF8),20));
            return new String(Base64.encode(pbeCipher.doFinal(bytes), Base64.NO_WRAP),UTF8);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String value) {
        try {
            final byte[] bytes = value != null ? Base64.decode(value.getBytes(UTF8), Base64.DEFAULT) : new byte[0];
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(secretString));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.DECRYPT_MODE,key,
                    new PBEParameterSpec(Build.SERIAL.getBytes(UTF8),20));
            return new String(pbeCipher.doFinal(bytes),UTF8);

        } catch (Exception e) {
            Log.e(TAG, "Could not decrypt value. Value may be in plain text.");
            return value;
        }
    }

    public static void writeTokenToDatabase(final AuthToken authToken,
                                            final Context context) {
        final ShopDBHelper shopDBHelper = new ShopDBHelper(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final SQLiteDatabase db = shopDBHelper.getWritableDatabase();
                final ContentValues cv = new ContentValues();
                cv.put(ShopDBHelper.ACCESS_TOKEN,encrypt(authToken.getAccess_token()));
                cv.put(ShopDBHelper.EXPIRES,authToken.getExpires_in());
                cv.put(ShopDBHelper.REFRESH_TOKEN,authToken.getRefresh_token());
                cv.put(ShopDBHelper.TIME_STAMP,authToken.getTokenObtainedTime());
                cv.put(ShopDBHelper.TOKEN_TYPE,authToken.getToken_type());
                final Map<String,Object> extras = authToken.getExtras();
                if ( extras != null ) {
                    try {
                        final String extrasJSON = getSafeMapper().writeValueAsString(extras);
                        cv.put(ShopDBHelper.EXTRAS,extrasJSON);
                    } catch (Exception ignore){
                        Log.e(TAG,"Failed to save token extra info to database");
                    }
                }
                long newRowId;
                newRowId = db.insert(ShopDBHelper.AUTH_TOKEN,null,cv);
                if (NumberUtils.isNumber(String.valueOf(newRowId))) {
                    Log.i(TAG,"Auth token successfully saved into the database");
                } else {
                    Log.e(TAG,"Failed to save auth token into database");
                }
            }
        }).start();
    }

    /**
     *
     * @param context THe context to be used to obtain the Shared Preference.
     * @param authToken The authorization token obtained from the web server.
     * @param provider The authentication provider that was used to obtain the auth token.
 *                 Will be utilized when the auth token needs to be verified in
 *
     */
    public static void writeToPreferences(final @NonNull Context context,
                                          final @NonNull AuthToken authToken,
                                          final @NonNull EAuthenticationProvider provider) {
        try {
            final SharedPreferences preferences = context.getSharedPreferences(Constants.AUTH_PREFERENCE,0);
            preferences.edit()
                    .putString(Constants.AUTH_TOKEN,getSafeMapper().writeValueAsString(authToken))
                    .putString(Constants.AUTH_PROVIDER,provider.toString())
                    .apply();

        } catch (Exception e) {
            Log.e(TAG,"Failed to write auth token and provider to preferences");
        }
    }

    public static void setAlarmForPreferences(final @NonNull Context context,
                                              final @NonNull AuthToken authToken,
                                              final @NonNull EAuthenticationProvider provider) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //Set pending intent for the TokenRefreshReciver class.
        Intent tokenrefreshIntent = new Intent(context, TokenAlarmReceiver.class);
        tokenrefreshIntent.putExtra(Constants.AUTH_TOKEN, authToken);
        tokenrefreshIntent.putExtra(Constants.AUTH_PROVIDER, provider);
        tokenrefreshIntent.setAction(Constants.TOKEN_REFRESH_ACTION);
        //Set an action for this intent so that we can cancel it later if needed.
        final Long expiresInTime  = authToken.getExpires_in();
        final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis((calendar.getTimeInMillis() + (expiresInTime * 1000L)) - 120000L);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, tokenrefreshIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public interface ConnectionCallback {
        void onConnectionSucceeded();
        void onConnectionFailed(final Exception exception);
    }

    public static void validateNetworkConnection(final ConnectionCallback connectionCallback) {
        final ServiceCall networkValidationCall = new ServiceCall.ServiceCallBuilder()
                .setUrl(Constants.NETWORK_VALIDATION_URL)
                .setMethod(ServiceCall.EMethodType.GET)
                .overrideCache(true)
                .shouldLog(true)
                .build();
        try {
            final Response networkValidationResponse = networkValidationCall.executeRequest();
            final Object emptyString = networkValidationResponse.getConvertedEntity();
            Assert.assertTrue(Strings.isNullOrEmpty(emptyString.toString()));
            connectionCallback.onConnectionSucceeded();
        } catch ( Exception e) {
            Log.e(TAG,"Network unavailable with message "+e.getMessage(),e);
            connectionCallback.onConnectionFailed(e);
        }
    }
}
