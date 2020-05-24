package me.kxtre.trainbuddy.models;

import android.media.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import me.kxtre.trainbuddy.interfaces.BasicCallBack;

public abstract class Exercise {
    private Integer ID;
    private String name;
    private String logic;
    private Integer repeats;
    private Integer progress;

    public Exercise(Integer ID, String name, Integer repeats) {
        this.ID = ID;
        this.name = name;
        this.repeats = repeats;
        this.progress = 0;
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
    protected void addToProgress(Integer n) {
        progress+=n;
    }

    public abstract void registerCountMechanism(BasicCallBack callBack);
    public abstract void notifyAccelerometerChange(Float x, Float y, Float z);

    public static List<Exercise> parseArray(JSONArray array) throws JSONException {
        List<Exercise> exercises = new LinkedList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                exercises.add(Exercise.parse(array.getJSONObject(i)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return exercises;
    }

    private static Exercise parse(JSONObject o) throws Exception {
        if(o.getString("type").equals("pushUp")) {
            return new ExercisePushup(o.getInt("id"), o.getString("name"),
                    o.getJSONObject("pivot").getInt("quantity")
            );
        }
        throw new Exception("not known exercise");
    }
}
