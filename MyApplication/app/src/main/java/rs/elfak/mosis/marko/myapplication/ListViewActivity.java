package rs.elfak.mosis.marko.myapplication;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListViewActivity extends AppCompatActivity {

    private static String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        Intent i=getIntent();
        username=i.getStringExtra("username");

        String[] noteNames={"IMG_21102017_21332","IMG_20183809_2130","IMG_201723298_43201","IMG_20172105_215959"};
        ListAdapter la=new CustomAdapter(this,noteNames);
        ListView listView=(ListView) findViewById(R.id.listView);
        listView.setAdapter(la);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
             String name=String.valueOf(adapterView.getItemAtPosition(i));
                Toast.makeText(ListViewActivity.this,name,Toast.LENGTH_LONG).show();

                Intent showNote=new Intent(ListViewActivity.this.getApplicationContext(),ShowNoteActivity.class);
                showNote.putExtra("noteName",name);
                showNote.putExtra("username",username);
                ListViewActivity.this.startActivity(showNote);

            }
        });

        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent an = new Intent(ListViewActivity.this.getApplicationContext(), AddNoteActivity.class);
                an.putExtra("username",username);
                ListViewActivity.this.startActivity(an);
            }
        });

    }
}
