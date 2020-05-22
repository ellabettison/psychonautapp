package com.example.psychapp.pillreports;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageRetriever extends AsyncTask<String, Integer, Drawable> {
    @Override
    protected Drawable doInBackground(String... urls) {
        try {
            InputStream is = (InputStream) new URL(urls[0]).getContent();
            Drawable image = Drawable.createFromStream(is, null);
            return image;
        } catch (IOException e){
            return null;
        }
    }
}
