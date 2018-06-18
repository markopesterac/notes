package rs.elfak.mosis.marko.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListNotesActivity extends AppCompatActivity {

    private static String username = "";
    private ArrayList<Note> notes;
    private NoteAdapter noteAdapter;
    DatabaseReference dref;
    //NOVO
    ListView listview;
    List<Note> list;
    List<String> notesID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_notes);

        Intent i=getIntent();
        username=i.getStringExtra("username");
        // FUNKCIJA KOJA VRACA LISTU NOTES
        getNotes();
    }

    public void getNotes()
    {
        listview=(ListView)findViewById(R.id.main_listview);
        list= new ArrayList<>();
        notesID=new ArrayList<>();
        noteAdapter = new NoteAdapter(this, list);

        dref = FirebaseDatabase.getInstance().getReference("user/"+username);
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Getting the data from snapshot
                    Note note = postSnapshot.getValue(Note.class);
                    String noteID= postSnapshot.getKey();

                    notesID.add(noteID);
                    list.add(note);
                    listview.setAdapter(noteAdapter);
                    noteAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


    });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

              //  String name=String.valueOf(adapterView.getItemAtPosition(i));
             //   Toast.makeText(ListNotesActivity.this,name,Toast.LENGTH_LONG).show();

                Intent showNote=new Intent(ListNotesActivity.this.getApplicationContext(),ShowNoteActivity.class);
                showNote.putExtra("noteID",notesID.get(i));
                showNote.putExtra("username",username);
                startActivity(showNote);

            }
        });


    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.list_note_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.item1:
                Toast.makeText(ListNotesActivity.this, "Add new Note", Toast.LENGTH_SHORT).show();
                Intent addNote = new Intent(ListNotesActivity.this.getApplicationContext(), AddNoteActivity.class);
                addNote.putExtra("username",username);
                startActivity(addNote);
                return true;
        }
        return true;
    }
}
