package com.example.carrental.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class SessionManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String PREF_NAME = "CarRentalSession";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_USER_ID = "UserId";
    private static final String KEY_NAME = "Name";
    private static final String KEY_EMAIL = "Email";
    private static final String KEY_IS_FIRST_TIME = "IsFirstTimeLaunch";
    private static final String KEY_FAVORITES = "FavoriteCars";
    private static final String KEY_IS_PARTNER = "IsPartner";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String id, String name, String email) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USER_ID, id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(KEY_IS_FIRST_TIME, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(KEY_IS_FIRST_TIME, true);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public String getUserName() {
        return pref.getString(KEY_NAME, null);
    }

    public String getUserEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public void logoutUser() {
        editor.putBoolean(IS_LOGIN, false);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_NAME);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_IS_PARTNER);
        editor.commit();
    }

    public void setPartner(boolean isPartner) {
        editor.putBoolean(KEY_IS_PARTNER, isPartner);
        editor.commit();
    }

    public boolean isPartner() {
        return pref.getBoolean(KEY_IS_PARTNER, false);
    }

    public void addFavorite(String carId) {
        Set<String> favorites = new HashSet<>(pref.getStringSet(KEY_FAVORITES, new HashSet<>()));
        favorites.add(carId);
        editor.putStringSet(KEY_FAVORITES, favorites);
        editor.commit();
    }

    public void removeFavorite(String carId) {
        Set<String> favorites = new HashSet<>(pref.getStringSet(KEY_FAVORITES, new HashSet<>()));
        favorites.remove(carId);
        editor.putStringSet(KEY_FAVORITES, favorites);
        editor.commit();
    }

    public boolean isFavorite(String carId) {
        Set<String> favorites = pref.getStringSet(KEY_FAVORITES, new HashSet<>());
        return favorites.contains(carId);
    }

    public Set<String> getFavorites() {
        return pref.getStringSet(KEY_FAVORITES, new HashSet<>());
    }
}
