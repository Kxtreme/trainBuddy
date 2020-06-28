package me.kxtre.trainbuddy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import me.kxtre.trainbuddy.R;
import me.kxtre.trainbuddy.models.Training;

public class TrainingsAdapter  extends ArrayAdapter<Training> {
    public TrainingsAdapter(@NonNull Context context, int resource, @NonNull List<Training> objects) {
        super(context, resource, objects);
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null) {
            LayoutInflater vi;
            vi  = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_training, null);
        }
        Training p = getItem(position);
        if(p != null) {
            TextView name = v.findViewById(R.id.textView_name);
            TextView date = v.findViewById(R.id.textView_date);
            TextView done = v.findViewById(R.id.textView_state);

            if(name != null) name.setText(p.getName());
            if(date != null) date.setText(p.getDate()+"");
            if(done != null) done.setText(p.getDone() ? "Done": "Todo");

        }
        return v;
    }
}