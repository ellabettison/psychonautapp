package com.example.psychapp.pillreports;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class WebScraper extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... urls) {
        String urlToRead = urls[0];
        String result = null;
        int resCode;
        InputStream in;
        try {
            URL url = new URL(urlToRead);
            Log.d("url", "urlToRead: "+urlToRead);
            URLConnection urlConn = url.openConnection();

            HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
            httpsConn.setAllowUserInteraction(false);
            httpsConn.setInstanceFollowRedirects(true);
            httpsConn.setRequestMethod("GET");
            httpsConn.connect();
            resCode = httpsConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpsConn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        in, StandardCharsets.ISO_8859_1), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                in.close();
                result = sb.toString();
            } else{
                Log.d("TAG", "doInBackground: ERR NO HTTPOK");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
