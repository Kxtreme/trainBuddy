package me.kxtre.trainbuddy.controllers;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import me.kxtre.trainbuddy.models.User;

public enum DataManager {
    INSTANCE;

    private SharedPreferences sharedPreferences;
    private User user;
    public String getStoredUserJWT() {
        return this.sharedPreferences.getString("access_token", null);
    }

    public void registerUser(JSONObject response) throws JSONException {
        String user = response.getString("user");
        this.user = User.parseUser(user);
        try {
            SharedPreferences.Editor editSharedPreferences = sharedPreferences.edit();
            editSharedPreferences.putString("access_token", response.getString("access_token"));
            editSharedPreferences.putString("refresh_token", response.getString("access_token"));
            editSharedPreferences.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //SharedPreferences
    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

}