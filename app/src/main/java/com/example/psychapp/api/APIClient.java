package com.example.psychapp.api;

import android.os.AsyncTask;

import com.example.psychapp.api.QueryObjects.SubstanceObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class APIClient extends AsyncTask<String, Integer, ArrayList<SubstanceObject>> {

//    public ArrayList<SubstanceObject> JSONBodyAsMap(String query) throws IOException {
//        UncheckedObjectMapper objectMapper = new UncheckedObjectMapper();
//
//        URL url = new URL("https://api.psychonautwiki.org");
//        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
//        connection.setRequestMethod("POST");
//        connection.setRequestProperty("content-type", "application/json");
//        connection.setDoOutput(true);
//        byte[] byteQuery = query.getBytes();
//        OutputStream os = connection.getOutputStream();
//        os.write(byteQuery);
//        os.close();
//
//        try(BufferedReader br = new BufferedReader(
//                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
//            StringBuilder response = new StringBuilder();
//            String responseLine;
//            while ((responseLine = br.readLine()) != null) {
//                response.append(responseLine.trim());
//            }
//            return objectMapper.readValue(response.toString());
//        }
//    }

    // TODO: sort!! try/catches
    @Override
    protected ArrayList<SubstanceObject> doInBackground(String... strings) {
        UncheckedObjectMapper objectMapper = new UncheckedObjectMapper();
        String query = strings[0];

        URL url = null;
        try {
            url = new URL("https://api.psychonautwiki.org");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection)url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        connection.setRequestProperty("content-type", "application/json");
        connection.setDoOutput(true);
        byte[] byteQuery = query.getBytes();
        OutputStream os;

        try {
            os = connection.getOutputStream();
            os.write(byteQuery);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return objectMapper.readValue(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    static class UncheckedObjectMapper extends com.fasterxml.jackson.databind.ObjectMapper {
        /**
         * Parses the given JSON string into a SubstanceObject.
         * @return
         */
        ArrayList<SubstanceObject> readValue(String content) throws IOException {
            try {

                // Get substance object
                Map<String, JsonNode> dataMap = this.readValue(content, new TypeReference<Map<String, JsonNode>>() {});
                        String dataObject = this.writeValueAsString(dataMap.get("data"));
                Map<String, JsonNode[]> substanceMap = this.readValue(dataObject, new TypeReference<Map<String, JsonNode[]>>() {});

                JsonNode[] substanceObjects = Objects.requireNonNull(substanceMap.get("substances"));
                ArrayList<SubstanceObject> substances = new ArrayList<>();

                for (JsonNode substance: substanceObjects){
                    substances.add(this.readValue(this.writeValueAsString(substance), SubstanceObject.class));
                }

                // map to SubstanceObject class
                return substances;

            } catch (IOException ioe) {
                throw new IOException(ioe);
            }
        }
    }

}
