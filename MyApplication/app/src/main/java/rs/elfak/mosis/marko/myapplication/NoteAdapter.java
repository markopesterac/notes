package rs.elfak.mosis.marko.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Marko on 11/3/2017.
 */

public class NoteAdapter extends ArrayAdapter<Note> {

   /* public NoteAdapter(Context context, int resource, List<Note> objects) {
        super(context, resource, objects);
    }*/

    public NoteAdapter(Context context, List<Note> objects) {
        super(context,0,objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        if(convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.view_note_item, null);
        }

        Note note=getItem(position);

        if(note != null) {
            TextView title = (TextView) convertView.findViewById(R.id.list_note_title);
            TextView date = (TextView) convertView.findViewById(R.id.list_note_date);
            TextView content = (TextView) convertView.findViewById(R.id.list_note_content_preview);

            content.setText(note.getText());
            title.setText(note.getTitle());
            date.setText(note.getDateTimeFormatted(getContext()));
        }
        return convertView;
    }
}


