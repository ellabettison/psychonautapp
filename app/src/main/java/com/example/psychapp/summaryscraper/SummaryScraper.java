package com.example.psychapp.summaryscraper;

import android.content.Context;

import com.example.psychapp.pillreports.WebScraper;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import static android.content.Context.MODE_PRIVATE;

public class SummaryScraper {

    private String getSummaryFromWiki(String name) {
        String substanceUrl = "https://psychonautwiki.org/wiki/"+name;
        WebScraper webScraper = new WebScraper();
        String output = "";

        try {
            output = webScraper.execute(substanceUrl).get();
//            output = webScraper.getHTML(substanceUrl);
        } catch (Exception e){
            return null;
        }

        Document parsed = Jsoup.parse(output);
        Elements content = parsed.body().getElementsByClass("mw-parser-output").first()
                .children().select("p").select("p:contains("+name+")");
        Element elem = content.first();

        StringBuilder description = new StringBuilder();

        while (!elem.id().equals("toc")) {
            description.append(elem.text()).append("\n\n");
            elem = elem.nextElementSibling();
        }
        description.setLength(description.length()-2);
        String result = description.toString().replaceAll("\\[.]", "");
        result = result.replaceAll("\\[.*]", "");

        return result;
    }

    public String getSummary(String category, String substance, Context context){
        JSONObject substancesJson = getJson(category, context);
        if (substancesJson.has(substance)){
            try{
                return substancesJson.getString(substance);
            } catch (JSONException e){
                e.printStackTrace();
            }
        } else {
            SummaryScraper summaryScraper = new SummaryScraper();
            String summary = summaryScraper.getSummaryFromWiki(substance);
            if (summary != null) {
                try {
                    substancesJson.put(substance, summary);
                    FileOutputStream fOut = context.openFileOutput(category + ".txt",
                            MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);
                    // Write the string to the file
                    osw.write(substancesJson.toString());

                    /* ensure that everything is
                     * really written out and close */
                    osw.flush();
                    osw.close();

//                    FileWriter writer = new FileWriter(category + ".txt");
//                    writer.write(substancesJson.toString());
//                    writer.close();
                } catch (IOException | JSONException e){
                    e.printStackTrace();
                }
                return summary;
            }
        }
        return null;
    }


    private JSONObject getJson(String category, Context context) {
        JSONObject jsonObject = new JSONObject();
        StringBuilder fileContents = new StringBuilder();
        File file = new File("data/data/pychapp/" + category + ".txt");
        try {
//            Scanner scanner = new Scanner(file);
//            while (scanner.hasNextLine()){
//                fileContents.append(scanner.nextLine());
//            }
            FileInputStream fIn = context.openFileInput(category + ".txt");
            InputStreamReader isr = new InputStreamReader(fIn);

            /* Prepare a char-Array that will
             * hold the chars we read back in. */
            char[] inputBuffer = new char[500];

            // Fill the Buffer with data from the file
            isr.read(inputBuffer);

            // Transform the chars to a String
            String readString = new String(inputBuffer);
            try {
                jsonObject = new JSONObject(fileContents.toString());
            } catch (Exception e){
                return jsonObject;
            }

        } catch (FileNotFoundException e){
            try {
                if(!file.createNewFile()){
                    System.out.print("\nCOULD NOT CREATE NEW FILE\n");
                    throw new IOException();
                } else{
                    System.out.println("FILE CRESTED");
                }
            } catch (IOException o){
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

}
