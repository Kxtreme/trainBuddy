package me.kxtre.trainbuddy.models;

import me.kxtre.trainbuddy.interfaces.BasicCallBack;

public class ExercisePushup extends Exercise {
    private BasicCallBack countCallBack = null;
    private Float lastX = 0f;
    private Float lastY = 0f;
    private Float lastZ = 0f;

    public ExercisePushup(Integer ID, String name, Integer repeats) {
        super(ID, name, repeats);
    }

    @Override
    public void registerCountMechanism(BasicCallBack callBack) {
        this.countCallBack = callBack;
    }

    @Override
    public void notifyAccelerometerChange(Float x, Float y, Float z) {
        if (countCallBack == null) return;
        float diffX = lastX - x;
        float diffY = lastY - y;
        float diffZ = lastZ - z;
        if (diffX > 10 && diffY > 10) {
            this.addToProgress(1);
            countCallBack.onEvent();
        }
    }
}
