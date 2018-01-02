package rs.elfak.mosis.marko.myapplication;

import android.Manifest;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
//import android.media.session.MediaController;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.StrictMode;
import android.widget.VideoView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.net.ssl.HttpsURLConnection;

import io.minio.MinioClient;

import static android.R.attr.bitmap;
import static android.R.attr.data;

public class MainActivity extends AppCompatActivity  {
    private static int RESULT_LOAD_IMAGE = 1;
    private Uri selectedImage;
    private static final String PHOTOSERVICE_URL = "http://10.10.1.114:45455/Home/ListAllBuckets";
    private static final String PHOTO_URL="http://10.10.1.114:45455/Home/Upload";
    private static final String LOGIN_URL="http://10.10.1.114:45455/Home/Login";
    private static String username = "";
    ProgressDialog pDialog;
    TextView textView;

    private int ACTIVITY_START_VIDEO=0;
    private Button recordV,playV;
    private VideoView videoView;
    MediaController mediaC;



    private ListView listView2;
    private VideoAdapter videoAdapter;
    private ArrayList<MyVideo> videos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get extra
        Intent i=getIntent();
        username=i.getStringExtra("username");

      //  new Login().execute(LOGIN_URL);
        mediaC=new MediaController(this);


        recordV=(Button) findViewById(R.id.record);
        playV=(Button)findViewById(R.id.play);
        videoView=(VideoView)findViewById(R.id.videoView);


        recordV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent callVideoIntent=new Intent();
                callVideoIntent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(callVideoIntent,ACTIVITY_START_VIDEO);


            }
        });

        playV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            videoView.start();
            }
        });



        videos = new ArrayList();
        videoAdapter = new VideoAdapter(this, videos);
        listView2 = (ListView) findViewById(R.id.videoLW);
        listView2.setAdapter(videoAdapter);

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //String name=String.valueOf(adapterView.getItemAtPosition(i));
                //Toast.makeText(ListViewActivity.this,name,Toast.LENGTH_LONG).show();
                MyVideo mv=(MyVideo)adapterView.getItemAtPosition(i);
                videoView.start();

            }
        });


    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.mymenu,menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            //Dodavanje note-a u firebase
            case R.id.item1:


                return true;

            //Dodavanje slike u listview
            case R.id.item2:


                return true;
            case R.id.item3:
                //Take a video

                Intent callVideoIntent=new Intent();
                callVideoIntent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(callVideoIntent,ACTIVITY_START_VIDEO);

                return true;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==ACTIVITY_START_VIDEO && resultCode==RESULT_OK)
        {
            Uri videoUri= data.getData();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String name = "VID_" + timeStamp + ".mp4";

            MyVideo video = new MyVideo();
            video.setTitle(name);
            video.setUri(videoUri);
            //  video.setBitmap(bitmap);
            videos.add(video);
        }

    }



    public class VideoAdapter extends ArrayAdapter<MyVideo>
    {
        private  class ViewHolder {
            VideoView videoSpace;
            //   TextView description;
        }

        public VideoAdapter(Context context, ArrayList<MyVideo> videos) {
            super(context, 0, videos);
        }

        @Override public View getView(int position, View convertView,
                                      ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_video, parent, false);
                viewHolder.videoSpace = (VideoView) convertView.findViewById(R.id.videoView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            MyVideo video = getItem(position);

            final int THUMBSIZE = 724;



            viewHolder.videoSpace.setVideoURI(video.uri);

            viewHolder.videoSpace.setMediaController(mediaC);
            mediaC.setAnchorView(viewHolder.videoSpace);



            // Return the completed view to render on screen
            return convertView;
        }


    }


    public class MyVideo {
        private String title;
        Bitmap bitmap;
        Uri uri;

        public Uri getUri() {
            return uri;
        }

        public void setUri(Uri uri) {
            this.uri = uri;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }
    }

/*   private class LoadBuckets extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Fetching BUCKETS from Minio Server....");
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            InputStream inputStream = null;
            String result = "";

            try {
                URL url = new URL(args[0]);

                HttpURLConnection httpCon =
                        (HttpURLConnection) url.openConnection();
                httpCon.setRequestMethod("POST");
                httpCon.connect();

                if (httpCon.getResponseCode() != 200)
                    throw new Exception("Failed to connect");
                inputStream = httpCon.getInputStream();
                // convert inputstream to string
                if (inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
            if (result == "")
                return null;
            else
                return result;
        }

        protected void onPostExecute(String buckets) {

            System.out.println("In Post Execute");
            if (buckets != null) {
                pDialog.dismiss();
                textView=(TextView)findViewById(R.id.TextView);
                textView.setText(buckets);
            } else
            {
                pDialog.dismiss();
                pDialog.setMessage("BUCKETS Does Not exist or Network Error....");
                pDialog.show();
            }
        }
    }
*/

  /* private class Login extends AsyncTask<String,Void,Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading profile....");
            pDialog.show();
        }

        protected Integer doInBackground(String... args) {
            InputStream inputStream = null;

            try {

                URL url = new URL(LOGIN_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                StringBuilder result1 = new StringBuilder();
                result1.append(URLEncoder.encode("username", "UTF-8"));
                result1.append("=");
                result1.append(URLEncoder.encode("marko", "UTF-8"));
                username = "marko";

                writer.write(result1.toString());
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

                float i = conn.getResponseCode();
                if (conn.getResponseCode() != 200)
                    throw new Exception("Failed to connect");

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return 1;
        }
        protected void onPostExecute(String buckets) {

           System.out.println("In Post Execute");
            if (buckets != null) {
                pDialog.dismiss();
                textView=(TextView)findViewById(R.id.TextView);
                textView.setText(buckets);
            } else
            {
                pDialog.dismiss();
                pDialog.setMessage("Notes does not exist or Network Error....");
                pDialog.show();
            }
        }
    }*/

  /*  private class UploadPhoto extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Fetching Photo from local storage....");
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            InputStream inputStream = null;
            String result = "";
            String encode = args[0]; //data to post
            String name=args[1];

            OutputStream out = null;

            try {

                URL url = new URL(PHOTO_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                StringBuilder result1 = new StringBuilder();
                result1.append(URLEncoder.encode("encode", "UTF-8"));
                result1.append("=");
                result1.append(URLEncoder.encode(encode, "UTF-8"));
                result1.append("&");
                result1.append(URLEncoder.encode("name", "UTF-8"));
                result1.append("=");
                result1.append(URLEncoder.encode(name, "UTF-8"));
                result1.append("&");
                result1.append(URLEncoder.encode("username", "UTF-8"));
                result1.append("=");
                result1.append(URLEncoder.encode(username, "UTF-8"));

                writer.write(result1.toString());
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

                float i = conn.getResponseCode();
                if (conn.getResponseCode() != 200)
                    throw new Exception("Failed to connect");


                inputStream = conn.getInputStream();
                // convert inputstream to string
                if (inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
            if (result == "")
                return null;
            else
                return result;
        }

        protected void onPostExecute(String buckets) {

            System.out.println("In Post Execute");
            if (buckets != null) {
                pDialog.dismiss();
                textView=(TextView)findViewById(R.id.TextView);
                textView.setText(buckets);
            } else
            {
                pDialog.dismiss();
                pDialog.setMessage("BUCKETS Does Not exist or Network Error....");
                pDialog.show();
            }
        }
    }*/

 /*   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            selectedImage = data.getData();
            String s = getRealPathFromURI(selectedImage);
            String name1[]=s.split("/");
            String name=name1[name1.length-1];
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageURI(selectedImage);

            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Compress image to lower quality scale 1 - 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] image = stream.toByteArray();
            String imageEncoded = Base64.encodeToString(image, Base64.DEFAULT);
            //String url=PHOTO_URL + imageEncoded;

           new UploadPhoto().execute(imageEncoded,name);

        }
    }*/


    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private static String convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        // Loop through the stream line by line and convert to a String.
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
