package rs.elfak.mosis.marko.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddNotes extends AppCompatActivity {

    private static final String PHOTO_URL="http://10.10.1.114:45455/Home/Upload";
    private static String username = "";
    ProgressDialog pDialog;
    ImageView imageView;
    EditText text;
    private static final int CAMERA_REQUEST = 1888;
    String imageEncoded;
    String name;
    String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        Intent i=getIntent();
        username=i.getStringExtra("username");

        text=(EditText)findViewById(R.id.editText12);
        //Take Photo
       Button b=(Button) findViewById(R.id.TakePhoto);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                catch (Exception e){}
            }
        });
        //Upload photo
        Button b2=(Button) findViewById(R.id.AddNote);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    description=text.getText().toString();
                    new UploadPhoto().execute(imageEncoded,name,description);

                    //dodato
                    Intent i=new Intent(getApplicationContext(),ListViewActivity.class);
                    i.putExtra("username",username);
                    startActivity(i);
                }
                catch (Exception e){}
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            Bitmap photo1 = (Bitmap) data.getExtras().get("data");
            imageView=(ImageView)findViewById(R.id.imageView6);
            imageView.setImageBitmap(photo1);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
             name = "IMG_" + timeStamp + ".jpg";

            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Compress image to lower quality scale 1 - 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] image = stream.toByteArray();
            imageEncoded = Base64.encodeToString(image, Base64.DEFAULT);
        }
    }

    private class UploadPhoto extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddNotes.this);
            pDialog.setMessage("Uploading ....");
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            InputStream inputStream = null;
            String result = "";
            String encode = args[0]; //data to post
            String name=args[1];
            String desc=args[2];


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
                result1.append("&");
                result1.append(URLEncoder.encode("desc", "UTF-8"));
                result1.append("=");
                result1.append(URLEncoder.encode(desc, "UTF-8"));

                writer.write(result1.toString());
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

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
            if (buckets != null) {
                pDialog.dismiss();
            } else
            {
                pDialog.dismiss();
                pDialog.setMessage("Network Error....");
                pDialog.show();
            }
        }
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

