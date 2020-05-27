package me.kxtre.trainbuddy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import me.kxtre.trainbuddy.R;
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
            TextView percentage = v.findViewById(R.id.textView_percentage);
            TextView total = v.findViewById(R.id.textView_total);
            TextView progress = v.findViewById(R.id.textView_progress);

            if(name != null) name.setText(p.getName());
            if(percentage != null) percentage.setText(p.getPercentage() + "%");
            if(total != null) total.setText(p.total()+"");
            if(progress != null) progress.setText(p.getProgress()+"");
        }
        return v;
    }
}