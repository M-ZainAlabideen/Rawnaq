package app.rawnaq.classes;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SessionManager {
    Context context;
    public static SharedPreferences sharedPref;
    public static SharedPreferences.Editor editor;
    public static final String USER_PREF = "user_pref";
    private static final String IS_LOGGED = "is_logged";
    private static final String IS_PROVIDER = "is_provider";
    private static final String PROVIDER_ID = "provider_id";
    private static final String HAS_SHOP = "has_shop";
    private static final String VALIDATION = "validation";
    private static final String USER_TOKEN = "token";
    private static final String USER_NAME = "user_name";
    private static final String PHONE = "phone";
    private static final String EMAIL = "email";
    private static final String AS_GUEST = "continue_as_guest";
    private static final String LANGUAGE_CODE = "language_code";


    public SessionManager(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        editor = sharedPref.edit();
    }


    public void LoginSession() {
        editor.putBoolean(IS_LOGGED, true);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return sharedPref.getBoolean(IS_LOGGED, false);
    }

    public void logout() {
        editor.putBoolean(IS_LOGGED, false);
        editor.putString(USER_NAME,null);
        editor.putString(EMAIL,null);
        editor.putString(PHONE,null);
        editor.putBoolean(VALIDATION,false);
        editor.putBoolean(IS_PROVIDER,false);
        editor.putBoolean(HAS_SHOP,false);
        editor.putString(USER_TOKEN,null);
        editor.putInt(PROVIDER_ID,0);
        editor.commit();
    }
    public void ContinueAsGuest() {
        editor.putBoolean(AS_GUEST, true);
        editor.apply();
    }
    public void guestLogout() {
        editor.putBoolean(AS_GUEST, false);
        editor.apply();
    }
    public boolean isGuest() {
        return sharedPref.getBoolean(AS_GUEST, false);
    }
    public void setProvider() {
        editor.putBoolean(IS_PROVIDER,true);
        editor.apply();
    }
    public boolean isProvider() {
        return sharedPref.getBoolean(IS_PROVIDER, false);
    }
    public void setShop() {
        editor.putBoolean(HAS_SHOP,true);
        editor.apply();
    }
    public boolean hasShop() {
        return sharedPref.getBoolean(HAS_SHOP, false);
    }
    public void setValidation() {
        editor.putBoolean(VALIDATION,true);
        editor.apply();
    }
    public boolean isValidation() {
        return sharedPref.getBoolean(VALIDATION, false);
    }
    public void setUserToken(String token) {
        editor.putString(USER_TOKEN, token);
        editor.apply();
    }
    public String getUserToken() {
        return sharedPref.getString(USER_TOKEN, "");
    }
    public void setProviderId(int providerId) {
        editor.putInt(PROVIDER_ID, providerId);
        editor.apply();
    }
    public int getProviderId() {
        return sharedPref.getInt(PROVIDER_ID, 0);
    }
    public void setUserName(String name) {
        editor.putString(USER_NAME, name);
        editor.apply();
    }
    public String getUserName() {
        return sharedPref.getString(USER_NAME, "");
    }
    public void setUserPhone(String phone) {
        editor.putString(PHONE, phone);
        editor.apply();
    }
    public String getUserPhone() {
        return sharedPref.getString(PHONE, "");
    }
    public void setUserMail(String email) {
        editor.putString(EMAIL, email);
        editor.apply();
    }
    public String getUserMail() {
        return sharedPref.getString(EMAIL, "");
    }
    public void setUserLanguage(String languageCode) {
        editor.putString(LANGUAGE_CODE, languageCode);
        editor.apply();

    }
    public String getUserLanguage() {
        return sharedPref.getString(LANGUAGE_CODE, "ar");
    }


}
