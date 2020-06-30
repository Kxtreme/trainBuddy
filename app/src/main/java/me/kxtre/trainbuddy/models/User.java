package me.kxtre.trainbuddy.models;

import org.json.JSONException;
import org.json.JSONObject;

import me.kxtre.trainbuddy.controllers.DataManager;
import me.kxtre.trainbuddy.utils.JSONObjectEnhanced;


public class User {
    private String name;
    private String email;
    private String gender;
    private String bornDate;
    private String height;
    private String weight;

    private User(String email, String name, String gender, String bornDate, String height, String weight) {
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.bornDate = bornDate;
        this.height = height;
        this.weight = weight;
    }

    public String getEmail() {
        return email;
    }

    public String getName() { return name; }

    public String getGender() { return gender; }

    public String getBornDate() { return bornDate; }

    public String getHeight() { return height; }

    public String getWeight() { return weight; }

    public static User parseUser(String response) throws JSONException {
        JSONObject object = new JSONObjectEnhanced(response);
        String name;
        String gender;
        String bornDate;
        String height;
        String weight;

        name = object.getString("name");
        gender = object.getString("gender");
        bornDate = object.getString("birthDate").replace("-", "/");
        if(name.equals("null")) {
            name = "";
        }
        if(bornDate.equals("null")) {
            bornDate = "";
        }
        height = object.getString("height");
        weight = object.getString("weight");

        return new User(
                object.getString("email"),
                name,
                gender,
                bornDate,
                height,
                weight
        );
    }
}
