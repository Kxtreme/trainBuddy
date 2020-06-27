package me.kxtre.trainbuddy.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import me.kxtre.trainbuddy.R;
import me.kxtre.trainbuddy.controllers.StateController;
import me.kxtre.trainbuddy.models.Exercise;

public class ExercisesAdapter  extends ArrayAdapter<Exercise> {
    public ExercisesAdapter(@NonNull Context context, int resource, @NonNull List<Exercise> objects) {
        super(context, resource, objects);
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null) {
            LayoutInflater vi;
            vi  = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_exercise, null);
        }
        Exercise p = getItem(position);
        if(p != null) {
            TextView name = v.findViewById(R.id.textView_name);
            TextView total = v.findViewById(R.id.textView_total);
            TextView progress = v.findViewById(R.id.textView_progress);
            LinearLayout base = v.findViewById(R.id.linearLayout_base);

            if(name != null) name.setText(p.getName());
            if(total != null) total.setText(p.total()+"");
            if(progress != null) progress.setText(p.getProgress()+"");
            if(p == StateController.INSTANCE.getExercise()) {
                base.setBackgroundColor(0xFFE1E1E1);
            } else {
                base.setBackgroundColor(0xFFFFFFFF);
            }
        }
        return v;
    }
}