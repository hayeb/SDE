package giphouse.nl.proprapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author haye
 */
public class AuthPreferences {
    private static final String PREFS_NAME = "auth";
    private static final String KEY_ACCOUNT_NAME = "account_name";
    private static final String KEY_AUTH_TOKEN = "auth_token";

    private SharedPreferences preferences;

    public AuthPreferences(final Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getAccountName() {
        return preferences.getString(KEY_ACCOUNT_NAME, null);
    }

    public String getAuthToken() {
        return preferences.getString(KEY_AUTH_TOKEN, null);
    }

    public void setUsername(final String accountName) {
        final Editor editor = preferences.edit();
        editor.putString(KEY_ACCOUNT_NAME, accountName);
        editor.apply();
    }

    public void setAuthToken(final String authToken) {
        final Editor editor = preferences.edit();
        editor.putString(KEY_AUTH_TOKEN, authToken);
        editor.apply();
    }
}
