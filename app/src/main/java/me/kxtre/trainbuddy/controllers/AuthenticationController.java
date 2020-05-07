package me.kxtre.trainbuddy.controllers;

import android.content.Context;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import me.kxtre.trainbuddy.interfaces.Callback;
import me.kxtre.trainbuddy.utils.HttpCallBack;
import me.kxtre.trainbuddy.utils.HttpUtils;

public class AuthenticationController {
    public static String mainURL = "trainbuddy.vascocarreira.com";
    public static void checkAuthentication(Context context, final Callback callback) {
        List<Pair<String, String>> headers= generateAuthenticationHeaders(DataManager.INSTANCE.getStoredUserJWT());
        HttpUtils.Get(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {
                String user = response.getString("user");

                DataManager.INSTANCE.registerUser(response);
                callback.onSucess();
            }

            @Override
            public void onResult(String response) {
                callback.onError();
            }

            @Override
            public void onFail(String error) {

            }
        }, mainURL + "/api/user",context,  true, headers);
    }

    private static List<Pair<String, String>> generateAuthenticationHeaders(String token) {
        List<Pair<String, String>> headers = new LinkedList<>();
        headers.add(new Pair<>("Authorization", "Bearer " + token));
        headers.add(new Pair<>("Accept", "application/json"));
        headers.add(new Pair<>("Content-Type", "application/json"));
        return headers;
    }

}
