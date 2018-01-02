package rs.elfak.mosis.marko.myapplication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddNoteActivity extends AppCompatActivity{

    ProgressDialog pDialog;
    private Uri selectedImage;
    private static String username = "";
  //  private static final String PHOTO_URL="http://10.10.1.114:45455/Home/Upload";
    private static final String PHOTO_URL="http://192.168.0.100:45455/Home/Upload";
    EditText titleET;
    EditText textET;
    String imageEncoded;
    String name;
    List<String> minioPaths;
    List<String> encodedImages;
    MediaController mediaC;
    //NOVO

    private ArrayList<MyImage> images;
    private ImageAdapter imageAdapter;
    private ListView listView;
    private static final int ACTIVITY_START_VIDEO=0;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        Intent i=getIntent();
        username=i.getStringExtra("username");

        minioPaths=new ArrayList<String>();
        encodedImages=new ArrayList<String>();

        titleET = (EditText) findViewById(R.id.titleEditText);
        textET = (EditText) findViewById(R.id.textEditText);

        images = new ArrayList();
        imageAdapter = new ImageAdapter(this, images);
        listView = (ListView) findViewById(R.id.picturesListView);
        listView.setAdapter(imageAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String name=String.valueOf(adapterView.getItemAtPosition(i));
                Toast.makeText(AddNoteActivity.this,name,Toast.LENGTH_LONG).show();
            }
        });

    }

    private void activeTakePhoto() {
           try {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
            catch (Exception e){
            }

    }


    private void activeGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, RESULT_LOAD_IMAGE);
        }
        catch (Exception e){
        }
    }


    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case ACTIVITY_START_VIDEO:
                if(requestCode==ACTIVITY_START_VIDEO && resultCode==RESULT_OK)
                {
                    //iz uria vadi bitmapu
                    Uri videoUri= data.getData();
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    name = "VID_" + timeStamp + ".mp4";

                    byte[] vb=convertVideoToBytes(videoUri);
                    imageEncoded = Base64.encodeToString(vb, Base64.DEFAULT);
                    encodedImages.add(imageEncoded);

                    MyImage video = new MyImage();
                    video.setTitle(name);
                    video.setUri(videoUri);
                    video.setBitmap(null);
                    images.add(video);

                    String picturePathToMinio=username.toLowerCase()+"/"+name;
                    //videosPaths.add(picturePathToMinio);
                    minioPaths.add(picturePathToMinio);
                }

            case RESULT_LOAD_IMAGE:
                if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

                    selectedImage = data.getData();
                    String ss = getRealPathFromURI(selectedImage);
                    String name1[]=ss.split("/");
                    String name2=name1[name1.length-1];
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // Compress image to lower quality scale 1 - 100
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] imageBytes = stream.toByteArray();
                    imageEncoded = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    encodedImages.add(imageEncoded);

                    MyImage image = new MyImage();
                    image.setTitle(name2);
                    image.setBitmap(bitmap);
                    image.setPath(ss);
                    image.setUri(null);
                    images.add(image);

                    String picturePathToMinio=username.toLowerCase()+"/"+name2;
                    minioPaths.add(picturePathToMinio);

                }
            case REQUEST_IMAGE_CAPTURE:
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

                    Bitmap photo1 = (Bitmap) data.getExtras().get("data");
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    name = "IMG_" + timeStamp + ".jpg";
                    Bitmap bitmap=photo1;

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // Compress image to lower quality scale 1 - 100
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] imageBytes = stream.toByteArray();
                    imageEncoded = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    encodedImages.add(imageEncoded);

                    MyImage image = new MyImage();
                    image.setTitle(name);
                    image.setBitmap(bitmap);
                    image.setPath(name);
                    image.setUri(null);
                    images.add(image);

                    String picturePathToMinio=username.toLowerCase()+"/"+name;
                    minioPaths.add(picturePathToMinio);
                }
        }
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

                final DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().getReference();

                String title = titleET.getText().toString();
                String text = textET.getText().toString();
                List<String> slike=new ArrayList<String>();
                slike=minioPaths;
                boolean enable=true;
                String timeStamp = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa").format(new Date());
                String date=timeStamp;

                Note note = new Note();

                note.setTitle(title);
                note.setText(text);
                note.setSlike(slike);
                note.setEnable(enable);
                note.setDateTime(date);

                final String keyID=mDatabase.child("user/"+username).push().getKey();
                mDatabase.child("user/"+username).child(keyID).setValue(note);

                // Dodavanje slika u MINIO
                if(encodedImages!=null)
                for(int i=0;i<encodedImages.size();i++)
                {
                    String[] s=minioPaths.get(i).split("/");
                    String objName=s[s.length-1];
                    new UploadPhoto().execute(encodedImages.get(i),objName);
                }
               // Toast.makeText(AddNoteActivity.this, "Uspesno ste dodali spot", Toast.LENGTH_SHORT).show();
                //dodaju extras
                Intent ln = new Intent(AddNoteActivity.this.getApplicationContext(), ListNotesActivity.class);
                ln.putExtra("username",username);
                startActivity(ln);
                return true;

            //Dodavanje slike u listview
            case R.id.item2:

                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.custom_dialog_box);
                dialog.setTitle("Alert Dialog View");
                Button btnExit = (Button) dialog.findViewById(R.id.btnExit);
                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.btnChoosePath)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override public void onClick(View v) {
                                activeGallery();
                            }
                        });
                dialog.findViewById(R.id.btnTakePhoto)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override public void onClick(View v) {
                                activeTakePhoto();
                            }
                        });
                // show dialog on screen
                dialog.show();
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

    private class UploadPhoto extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddNoteActivity.this);
            pDialog.setMessage("Uploading ....");
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            InputStream inputStream = null;
            String result = "";
            String encode = args[0]; //data to post
            String name=args[1];
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


    public class ImageAdapter extends ArrayAdapter<MyImage> {


        ImageView imgIcon;
        VideoView videoSpace;

        public ImageAdapter(Context context, ArrayList<MyImage> images) {
            super(context, 0, images);
        }

        @Override public View getView(int position, View convertView,
                                      ViewGroup parent) {


            MyImage image = getItem(position);

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_video, parent, false);
              videoSpace = (VideoView) convertView.findViewById(R.id.item_videoView);
                imgIcon= (ImageView) convertView.findViewById(R.id.imageView);


                if (image.getBitmap() == null) {

                   videoSpace.setVisibility(convertView.VISIBLE);
                  imgIcon.setVisibility(convertView.INVISIBLE);
                }
                else
                {
                   videoSpace.setVisibility(convertView.INVISIBLE);
                    imgIcon.setVisibility(convertView.VISIBLE);
                }

            final int THUMBSIZE = 724;

            if(image.getBitmap()==null || image.getUri()!=null)
            {
                mediaC=new MediaController(AddNoteActivity.this);
                videoSpace.setVideoURI(image.getUri());
                videoSpace.setMediaController(mediaC);
                mediaC.setAnchorView(videoSpace);
            }
            else{
                imgIcon.setImageBitmap(ThumbnailUtils.extractThumbnail(image.getBitmap(),
                        THUMBSIZE, THUMBSIZE));
            }

            return convertView;
        }
    }


    public class MyImage {
        private String title, path;
        Bitmap bitmap;
        Uri uri;


        public Uri getUri() {
            return uri;
        }

        public void setUri(Uri uri) {
            this.uri = uri;
        }


        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public String getTitle() { return title; }

        public void setTitle(String title) { this.title = title; }


        public void setPath(String path) { this.path = path; }


        public String getPath() { return path; }

    }

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


    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Video.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private byte[] convertVideoToBytes(Uri uri){
        byte[] videoBytes = null;
        try {     ByteArrayOutputStream baos = new ByteArrayOutputStream();

            String s=getRealPathFromURI(uri);
            FileInputStream fis = new FileInputStream(new File(getRealPathFromURI(uri)));

            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);

             videoBytes = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return videoBytes;
    }




}
