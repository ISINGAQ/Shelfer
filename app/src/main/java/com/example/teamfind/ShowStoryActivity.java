package com.example.teamfind;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.annotations.concurrent.Background;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ShowStoryActivity extends AppCompatActivity {
    TextView content;
    TextView title;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_story);
        imageView = findViewById(R.id.storyImageShowStory);
        title = findViewById(R.id.titleShowStory);
        imageView.setEnabled(false);
        Intent i = getIntent();
        StoryReceiver storyReceiver = (StoryReceiver) i.getParcelableExtra("extra");
        content = findViewById(R.id.textViewShowStory);
        content.setText(storyReceiver.getContent());
        title.setText(storyReceiver.getTitle());

        if(storyReceiver.getImgUrl() != null){
            new Thread(new Runnable(){
                @Override
                public void run() {
                    imageView.setEnabled(true);
                    DownloadImageFromPath(storyReceiver.getImgUrl());
                }
            }).start();
        }


    }


    public void DownloadImageFromPath(String path) {
        InputStream in = null;
        Bitmap bmp = null;
        int responseCode = -1;
        try {
            URL url = new URL(path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.connect();
            responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                in = con.getInputStream();
                bmp = BitmapFactory.decodeStream(in);
                in.close();

            }

        } catch (Exception ex) {
            Log.e("Exception", ex.toString());
        }

        Bitmap finalBmp = bmp;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                imageView.setImageBitmap(finalBmp);

            }
        });

    }
}