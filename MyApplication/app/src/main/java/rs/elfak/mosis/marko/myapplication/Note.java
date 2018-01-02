package rs.elfak.mosis.marko.myapplication;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Marko on 11/3/2017.
 */

public class Note {
    public String title;
    public String text;
    private String dateTime;
    public List<String> slike;
    public boolean enable;

 /*   public Note(String dateInMillis, String mTitle, String mContent,boolean en) {
        dateTime = dateInMillis;
        title = mTitle;
        text = mContent;
        enable=en;
    }*/

 public Note(){

 }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {return text; }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getSlike() {
        return slike;
    }

    public void setSlike(List<String> slike) {
        this.slike = slike;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getDateTimeFormatted(Context context) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"
                , context.getResources().getConfiguration().locale);
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(new Date(dateTime));
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

}
