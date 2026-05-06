package lk.javainstitute.govisevana_admin.model;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHelper {
    private static final String PREF_NAME = "GoviSevanaAdminPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ROLE = "role";
    private static final String KEY_IS_ACTIVE = "isActive";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public SharedPreferenceHelper(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void saveAdminSession(String phone, String role, String isActive) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_IS_ACTIVE, isActive);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getPhone() {
        return preferences.getString(KEY_PHONE, null);
    }

    public String getRole() {
        return preferences.getString(KEY_ROLE, null);
    }

    public String getIsActive() {
        return preferences.getString(KEY_IS_ACTIVE, "false");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}

