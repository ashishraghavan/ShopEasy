package org.sports.football.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PersistentCookieStore implements CookieStore {

    private static final String TAG = PersistentCookieStore.class
            .getSimpleName();

    // Persistence
    private static final String SP_COOKIE_STORE = "cookieStore";
    private static final String SP_KEY_DELIMITER = "|"; // Unusual char in URL
    private static final String SP_KEY_DELIMITER_REGEX = "\\"
            + SP_KEY_DELIMITER;
    private SharedPreferences sharedPreferences;

    // In memory
    private final Map<URI, Set<HttpCookie>> allCookies = Maps.newHashMap();

    public PersistentCookieStore(Context context) {
        sharedPreferences = context.getSharedPreferences(SP_COOKIE_STORE,
                Context.MODE_PRIVATE);
        loadAllFromPersistence();
    }

    //TODO try avoiding repeated calls to loadAllFromPersistence if nothing has changed. Implement SharedPreference.onPreferenceChangeListener.
    private void loadAllFromPersistence() {
        allCookies.clear();

        Map<String, ?> allPairs = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allPairs.entrySet()) {
            String[] uriAndName = entry.getKey().split(SP_KEY_DELIMITER_REGEX,
                    2);
            try {
                URI uri = new URI(uriAndName[0]);
                String encodedCookie = (String) entry.getValue();
                HttpCookie cookie = new SerializableHttpCookie()
                        .decode(encodedCookie);

                Set<HttpCookie> targetCookies = allCookies.get(uri);
                if (targetCookies == null) {
                    targetCookies = Sets.newHashSet();
                    allCookies.put(uri, targetCookies);
                }
                // Repeated cookies cannot exist in persistence
                // targetCookies.remove(cookie)
                targetCookies.add(cookie);
            } catch (URISyntaxException e) {
                Log.w(TAG, e);
            }

        }
    }

    @Override
    public synchronized void add(URI uri, HttpCookie cookie) {
        uri = cookieUri(uri, cookie);

        Set<HttpCookie> targetCookies = allCookies.get(uri);
        if (targetCookies == null) {
            targetCookies = Sets.newHashSet();
            allCookies.put(uri, targetCookies);
        }

        targetCookies.remove(cookie);
        targetCookies.add(cookie);

        saveToPersistence(uri, cookie);
    }

    /**
     * Get the real URI from the cookie "domain" and "path" attributes, if they
     * are not set then uses the URI provided (coming from the response)
     *
     * @param uri
     * @param cookie
     * @return
     */
    private static URI cookieUri(URI uri, HttpCookie cookie) {
        URI cookieUri = uri;
        if (cookie.getDomain() != null) {
            try {
                cookieUri = new URI(uri.getScheme() == null ? "http"
                        : uri.getScheme(), cookie.getDomain(),
                        cookie.getPath() == null ? "/" : cookie.getPath(), null);
            } catch (URISyntaxException e) {
                Log.w(TAG, e);
            }
        }
        return cookieUri;
    }

    private void saveToPersistence(URI uri, HttpCookie cookie) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(uri.toString() + SP_KEY_DELIMITER + cookie.getName(),
                new SerializableHttpCookie().encode(cookie));

        editor.apply();
    }

    @Override
    public synchronized List<HttpCookie> get(URI uri) {
        return getValidCookies(uri);
    }

    @Override
    public synchronized List<HttpCookie> getCookies() {
        List<HttpCookie> allValidCookies = Lists.newArrayList();
        for (URI uri : allCookies.keySet()) {
            allValidCookies.addAll(getValidCookies(uri));
        }

        return allValidCookies;
    }

    private List<HttpCookie> getValidCookies(URI uri) {

        //load from persistence again if something has changed.
        loadAllFromPersistence();

        Set<HttpCookie> targetCookies = Sets.newHashSet();
        // If the stored URI does not have a path then it must match any URI in
        // the same domain
        for (URI storedUri : allCookies.keySet()) {

            // Check if the two domains are the same
            if (storedUri.getHost().equals(uri.getHost())) {
                // Check if the stored path is null or "/"
                // OR
                // if the two the paths are the same
                /*if ((storedUri.getPath() == null || storedUri.getPath().equals(
                        "/"))
                        || storedUri.getPath().equals(uri.getPath())) {


                }*/

                //Checking of domains is enough for now. If domain is the same, add that cookie.
                targetCookies.addAll(allCookies.get(storedUri));
            }
        }

        // Check it there are expired cookies and remove them
        if (targetCookies != null) {
            List<HttpCookie> cookiesToRemoveFromPersistence = Lists.newArrayList();
            for (Iterator<HttpCookie> it = targetCookies.iterator(); it
                    .hasNext();) {
                HttpCookie currentCookie = it.next();
                if (currentCookie.hasExpired()) {
                    cookiesToRemoveFromPersistence.add(currentCookie);
                    it.remove();
                }
            }

            if (!cookiesToRemoveFromPersistence.isEmpty()) {
                removeFromPersistence(uri, cookiesToRemoveFromPersistence);
            }
        }
        return Lists.newArrayList(targetCookies);
    }

    private void removeFromPersistence(URI uri, List<HttpCookie> cookiesToRemove) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (HttpCookie cookieToRemove : cookiesToRemove) {
            editor.remove(uri.toString() + SP_KEY_DELIMITER
                    + cookieToRemove.getName());
        }
        editor.apply();
    }

    @Override
    public synchronized List<URI> getURIs() {
        return Lists.newArrayList(allCookies.keySet());
    }

    @Override
    public synchronized boolean remove(URI uri, HttpCookie cookie) {
        Set<HttpCookie> targetCookies = allCookies.get(uri);
        boolean cookieRemoved = targetCookies != null && targetCookies
                .remove(cookie);
        if (cookieRemoved) {
            removeFromPersistence(uri, cookie);
        }
        return cookieRemoved;

    }

    private void removeFromPersistence(URI uri, HttpCookie cookieToRemove) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(uri.toString() + SP_KEY_DELIMITER
                + cookieToRemove.getName());
        editor.apply();
    }

    @Override
    public synchronized boolean removeAll() {
        allCookies.clear();
        removeAllFromPersistence();
        return true;
    }

    private void removeAllFromPersistence() {
        sharedPreferences.edit().clear().apply();
    }
}
