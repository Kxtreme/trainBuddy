package me.kxtre.trainbuddy.controllers;

import android.content.Context;
import android.util.Pair;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import me.kxtre.trainbuddy.interfaces.Callback;
import me.kxtre.trainbuddy.utils.HttpCallBack;
import me.kxtre.trainbuddy.utils.HttpUtils;

public class AuthenticationController {
    public static void checkAuthentication(final Context context, final Callback callback) {
        List<Pair<String, String>> headers = generateAuthenticationHeaders(DataManager.INSTANCE.getStoredUserJWT());
        HttpUtils.Get(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {

                DataManager.INSTANCE.registerUser(response);
                Controller.INSTANCE.fetchTrainings(context, callback);
            }

            @Override
            public void onResult(String response) {
                callback.onError();
            }

            @Override
            public void onFail(String error) {

                callback.onError();
            }
        }, DataManager.INSTANCE.mainURL + "/api/user",context,  true, headers);
    }

    public static List<Pair<String, String>> generateAuthenticationHeaders(String token) {
        List<Pair<String, String>> headers = new LinkedList<>();
        headers.add(new Pair<>("Authorization", "Bearer " + token));
        return headers;
    }

    public static void login(@NotNull String username, @NotNull String password, @NotNull final Callback callback, final Context context) {
        List<Pair<String, String>> params = new LinkedList<>();
        params.add(new Pair<>("email", username));
        params.add(new Pair<>("password", password));
        HttpUtils.Post(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {
                DataManager.INSTANCE.registerUser(response);
                checkAuthentication(context, callback);
            }

            @Override
            public void onResult(String response) {
            }

            @Override
            public void onFail(String error) {
                try {
                    JSONObject o = new JSONObject(error);
                    Toast.makeText(context, o.getString("message"), Toast.LENGTH_SHORT).show();
                    callback.onError();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, DataManager.INSTANCE.mainURL + "/api/login", params, context);

    }

    public static void register(@NotNull String email, @NotNull String password, @NotNull String passwordConfirmation, @NotNull String name, @NotNull String birth, String height, String weight, String gender, @NotNull final Callback callback, @NotNull final Context context) {
        List<Pair<String, String>> params = new LinkedList<>();
        params.add(new Pair<>("name", name));
        params.add(new Pair<>("email", email));
        params.add(new Pair<>("password", password));
        params.add(new Pair<>("password_confirmation", passwordConfirmation));
        params.add(new Pair<>("birthDate", birth));
        params.add(new Pair<>("gender", gender));
        params.add(new Pair<>("weight", weight));
        params.add(new Pair<>("height", height));

        HttpUtils.Post(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {
                callback.onSucess();
            }

            @Override
            public void onResult(String response) {
                callback.onError();
            }

            @Override
            public void onFail(String error) {
                try {
                    JSONObject o = new JSONObject(error);
                    Toast.makeText(context, o.getString("message"), Toast.LENGTH_SHORT).show();
                    callback.onError();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, DataManager.INSTANCE.mainURL + "/api/register", params, context);
    }

    public static void forgotPassword(@NotNull String email, @NotNull final Callback callback, @NotNull final Context context) {
        List<Pair<String, String>> params = new LinkedList<>();
        params.add(new Pair<>("email", email));

        HttpUtils.Post(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {
                callback.onError();
            }

            @Override
            public void onResult(String response) {
                callback.onSucess();
            }

            @Override
            public void onFail(String error) {
                try {
                    JSONObject o = new JSONObject(error);
                    Toast.makeText(context, o.getString("message"), Toast.LENGTH_SHORT).show();
                    callback.onError();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, DataManager.INSTANCE.mainURL + "/api/password/reset", params, context);
    }

    public static void editProfile(String height, String weight, @NotNull final Callback callback, @NotNull final Context context) {
        List<Pair<String, String>> headers = generateAuthenticationHeaders(DataManager.INSTANCE.getStoredUserJWT());

        List<Pair<String, String>> params = new LinkedList<>();
        params.add(new Pair<>("weight", weight));
        params.add(new Pair<>("height", height));

        HttpUtils.Post(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {
                callback.onError();
            }

            @Override
            public void onResult(String response) {
                callback.onSucess();
            }

            @Override
            public void onFail(String error) {
                try {
                    JSONObject o = new JSONObject(error);
                    Toast.makeText(context, o.getString("message"), Toast.LENGTH_SHORT).show();
                    callback.onError();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, DataManager.INSTANCE.mainURL + "/api/profile", params, context, headers);
    }

    public static void logout(@NotNull final Callback callback, @NotNull final Context context) {
        List<Pair<String, String>> headers = generateAuthenticationHeaders(DataManager.INSTANCE.getStoredUserJWT());

        HttpUtils.Post(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {
                callback.onSucess();
            }

            @Override
            public void onResult(String response) {
                callback.onError();
            }

            @Override
            public void onFail(String error) {
                callback.onError();
            }

        }, DataManager.INSTANCE.mainURL + "/api/logout", new LinkedList<Pair<String, String>>(), context, headers);
    }
}
