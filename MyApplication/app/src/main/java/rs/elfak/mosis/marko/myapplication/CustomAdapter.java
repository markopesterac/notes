package rs.elfak.mosis.marko.myapplication;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Marko on 11/2/2017.
 */

public class CustomAdapter extends ArrayAdapter<String> {


    public CustomAdapter(@NonNull Context context, String[] foods) {
        super(context,R.layout.custom_row ,foods);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(getContext());
        View customView=layoutInflater.inflate(R.layout.custom_row,parent,false);

        String fooditem=getItem(position);
        TextView t=(TextView) customView.findViewById(R.id.textView);
        t.setText(fooditem);



        return customView;
    }
}
