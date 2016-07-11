/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University Ghent
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *      The above copyright notice and this permission notice shall be included in all copies or
 *      substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package be.ugent.android.sdk.oauth.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import be.ugent.android.sdk.oauth.json.BearerToken;

import java.util.Calendar;

/**
 * Storage manager aids in the persistence of OAuth authorization data.
 * It stores the last retrieved token and authorization grant for the
 * user so it can be reused in future sessions.
 *
 * @author kevin
 */
public class StorageManager {

    private static final String TAG = "StorageManager";

    // Preference File
    private static final String PREFS_NAME = "UGentOAuthPreferences";

    // Token Storage
    private static final String PREFS_TOKEN = "tok";

    // Context
    protected final Context context;

    public StorageManager(Context context) {
        this.context = context;
    }

    /**
     * Builds and returns the TokenData object that will be persisted
     * into storage.
     *
     * @param token
     */
    public TokenData saveToken(BearerToken token) {
        Calendar createdOn = Calendar.getInstance();
        Calendar validUntil = (Calendar)createdOn.clone();
        validUntil.add(Calendar.SECOND, token.expiresIn);

        // load old refresh token
        TokenData oldToken = loadToken();
        // use old refresh token if no new one given
        String refreshToken = token.refreshToken;
        if (token.refreshToken == null || token.refreshToken.isEmpty())
            refreshToken = oldToken.getRefreshToken();

        TokenData tokenData = new TokenData(
                token.accessToken,
                refreshToken,
                token.tokenType,
                createdOn,
                validUntil);

        this.persistToken(tokenData);
        return tokenData;
    }


    /**
     * Loads the TokenData from persistent storage.
     *
     * @return Previously saved TokenData.
     */
    public TokenData loadToken() {
        String serializedTokenData = getSharedPreferences().getString(PREFS_TOKEN, null);
        return TokenData.create(serializedTokenData);
    }

    /**
     * Removes TokenData from persistent storage.
     */
    public void removeToken() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.remove(PREFS_TOKEN);
        editor.commit();
        Log.i(TAG, "Deleted TokenData from shared preferences");
    }


    /**
     * Stores the TokenData in persistent storage.
     *
     * @param tokenData
     */
    private void persistToken(TokenData tokenData) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFS_TOKEN, tokenData.serialize());
        editor.commit();
        Log.i(TAG, String.format("Saved TokenData to shared preferences, AT=%s", tokenData.getAccessToken()));
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}