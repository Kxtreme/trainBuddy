package me.kxtre.trainbuddy.models;

import android.media.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class Exercise {
    private Integer ID;
    private String name;
    private String logic;
    private Integer repeats;
    private Integer progress;

    public Exercise(Integer ID, String name, Integer repeats, String logic) {
        this.ID = ID;
        this.name = name;
        this.repeats = repeats;
        this.progress = 0;
        this.logic = logic;
    }

    public Integer getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getLogic() {
        return logic;
    }

    public Integer getRepeats() {
        return repeats;
    }

    public Integer getProgress() {
        return progress;
    }
    public void addToProgress() {
        progress++;
    }

    public static List<Exercise> parseArray(JSONArray array) throws JSONException {
        List<Exercise> exercises = new LinkedList<>();
        for (int i = 0; i < array.length(); i++) {
            exercises.add(Exercise.parse(array.getJSONObject(i)));
        }
        return exercises;
    }

    private static Exercise parse(JSONObject o) throws JSONException {
        return new Exercise(o.getInt("id"), o.getString("name"),
                o.getJSONObject("pivot").getInt("repeat"),
                o.getString("formula"));
    }
}
