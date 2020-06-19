package com.example.psychapp.wikiapi;

import android.os.AsyncTask;

import com.example.psychapp.wikiapi.QueryObjects.SubstanceObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class APIClient extends AsyncTask<String, Integer, ArrayList<SubstanceObject>> {

    // input : query[0] = query
    // TODO: sort!! try/catches
    @Override
    protected ArrayList<SubstanceObject> doInBackground(String... strings) {
        UncheckedObjectMapper objectMapper = new UncheckedObjectMapper();
        String query = strings[0];

        URL url;
        HttpsURLConnection connection = null;
        try {
            url = new URL("https://api.psychonautwiki.org");
            connection = (HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
        } catch (IOException e) {
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
