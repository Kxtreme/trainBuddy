package me.kxtre.trainbuddy.models;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import me.kxtre.trainbuddy.interfaces.BasicCallBack;

public class Training {
    private Integer ID;
    private String name;
    private List<Exercise> exercises;

    public Training(Integer ID, String name, List<Exercise> exercises) {
        this.ID = ID;
        this.name = name;
        this.exercises = exercises;
    }

    public Integer getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    @NotNull
    public static List<Training> parseResponse(@NotNull JSONObject response){
         List<Training> trainings = new LinkedList<>();
        try {
            JSONArray trainingsArray = response.getJSONArray("data");
            for (int i = 0; i < trainingsArray.length(); i++) {
                trainings.add(Training.parse(trainingsArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return trainings;
    }

    private static Training parse(JSONObject o) throws JSONException {
            return new Training(o.getInt("id"),
            o.getString("name"),
            Exercise.parseArray(o.getJSONArray("exercises")));
    }
}
