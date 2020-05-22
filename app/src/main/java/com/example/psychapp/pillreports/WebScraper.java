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

public class WebScraper extends AsyncTask<Integer, Integer, String> {
    
    String url;
    
    public WebScraper(String url){
        this.url = url;
    }

    // TODO: sort!! try/catches
//    @Override
//    protected String doInBackground(Integer... ints) {
//        String urlToRead = url + "&pnum=" + ints[0];
//        StringBuilder result = new StringBuilder();
//        URL url = null;
//        try {
//            url = new URL(urlToRead);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        HttpURLConnection conn = null;
//        try {
//            conn = (HttpURLConnection) url.openConnection();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            conn.setRequestMethod("GET");
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//        }
//        BufferedReader rd = null;
//        try {
//            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String line;
//        try {
//            while ((line = rd.readLine()) != null) {
//                result.append(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            rd.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result.toString();
//    }

    @Override
    protected String doInBackground(Integer... ints) {
        String urlToRead = url + "&pnum=" + ints[0];
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
