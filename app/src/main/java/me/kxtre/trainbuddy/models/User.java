package me.kxtre.trainbuddy.models;

import org.json.JSONException;
import org.json.JSONObject;

import me.kxtre.cooking.Utils.HttpUtils;
import me.kxtre.cooking.Utils.JSONObjectEnhanced;

public class User {
    private String name;
    private String email;
    private String phone;
    private String gender;
    private String bornDate;
    private String city;
    private String image;

    private User(String email, String phone, String name, String gender, String bornDate, String city, String image) {
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.gender = gender;
        this.bornDate = bornDate;
        this.city = city;
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() { return name; }

    public String getGender() { return gender; }

    public String getBornDate() { return bornDate; }

    public String getCity() { return city; }

    public String getImage() {
        return image.equals("") ? "" : HttpUtils.baseUrl + "/" + image;
    }

    public static User parseUser(String response) throws JSONException {
        JSONObject object = new JSONObjectEnhanced(response);
        JSONObject client = object.optJSONObject("client");
        String phone;
        String name;
        String gender;
        String bornDate;
        String city;
        String image;
        if(client == null) {
            phone = "N/D";
            name = "";
            gender = "null";
            bornDate = "";
            city = "";
            image = "";
        } else {
            phone = client.getString("phone");
            name = client.getString("name");
            gender = client.getString("gender");
            bornDate = client.getString("born-date").replace("-", "/");
            city = client.getString("city");
            image = client.getString("picture");
            if(phone.equals("null")) {
                phone = "N/D";
            }
            if(name.equals("null")) {
                name = "";
            }
            if(bornDate.equals("null")) {
                bornDate = "";
            }
            if(city.equals("null")) {
                city = "";
            }
            if(image.equals("null")) {
                image = "";
            }
        }

        return new User(
                object.getString("email"),
                phone,
                name,
                gender,
                bornDate,
                city,
                image
        );
    }
}
