package me.kxtre.trainbuddy.utils;


import org.json.JSONException;

public class JSONObjectEnhanced extends org.json.JSONObject {
    public JSONObjectEnhanced(String response) throws JSONException {
        super(response);
    }

    @Override
    public String optString(String name, String fallback) {
        if(name.equals("null")) {
            name = null;
        }
        return super.optString(name, fallback);
    }

    @Override
    public JSONObjectEnhanced getJSONObject(String name) throws JSONException {
        return (JSONObjectEnhanced) super.getJSONObject(name);
    }
}
