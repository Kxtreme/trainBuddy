package me.kxtre.trainbuddy.controllers;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import me.kxtre.trainbuddy.MainActivity;
import me.kxtre.trainbuddy.interfaces.Callback;

public class AuthenticationController {
    public static User user;
    public static Boolean checkAuthentication(Context context, Callback callback) {
        Headers headers= generateAuthenticationHeaders(getStoredUserJWT(context));
        return GetRequest(context, mainUrl + "/api/user", headers, callback);
    }

}
