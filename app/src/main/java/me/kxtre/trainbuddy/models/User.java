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

    private User(String email, String name, String gender, String bornDate) {
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.bornDate = bornDate;
    }

    public String getEmail() {
        return email;
    }


    public String getName() { return name; }

    public String getGender() { return gender; }

    public String getBornDate() { return bornDate; }

    public static User parseUser(String response) throws JSONException {
        JSONObject object = new JSONObjectEnhanced(response);
        String name;
        String gender;
        String bornDate;
            name = object.getString("name");
            gender = object.getString("gender");
            bornDate = object.getString("birthDate").replace("-", "/");
            if(name.equals("null")) {
                name = "";
            }
            if(bornDate.equals("null")) {
                bornDate = "";
            }

        return new User(
                object.getString("email"),
                name,
                gender,
                bornDate
        );
    }
}
