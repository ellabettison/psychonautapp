package com.example.psychapp.pillreports;

import android.graphics.drawable.Drawable;
import android.media.Image;

import com.example.psychapp.pillreports.Objects.PillObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class WebScraperCaller {

    public ArrayList<PillObject> getPills(String url, int pnum) throws Exception {
        
        String pillReportsUrl = "https://pillreports.net/";

        WebScraper webScraper = new WebScraper(url);
        
        String output = webScraper.getHTML(pnum);
   
        // TODO: simplify
        Document parsed = Jsoup.parse(output);
        Element searchReports = parsed.body().getElementsByClass("container-fluid")
                .select("div[class=row pr]").first().children()
                .get(1).children().first().getElementsByClass("row detail")
                .first().getElementsByClass("row search").first()
                .getElementsByClass("search-reports").first();
        
   
        ArrayList<PillObject> pills = new ArrayList<>();
        
        for (Element element: searchReports.children()){
            Element contents = element.getElementsByIndexEquals(0).first();
            String name = contents.getElementsByIndexEquals(0).first()
                    .getElementsByClass("top_pad").first().text();
            String date = contents.getElementsByAttributeValueContaining("class", "posted-date-search")
                    .first().getElementsByClass("posted-date-search").first().children().first().text();
            Elements bodys = contents.children().select("div[class*=row]");
            
            String location = bodys.first().getElementsByIndexEquals(1).first().text();
            
            String picElem = bodys.select("a").select("img").attr("src");

            InputStream is = (InputStream)  new URL(pillReportsUrl + picElem).getContent();
            Drawable image = Drawable.createFromStream(is, null);
            
            
            Elements leftDetails = bodys.select("ul[class=list-group]").get(0).children();
            Elements rightDetails = bodys.select("ul[class=list-group]").get(1).children();
            
            String suspectContents = leftDetails.get(0).select("p").text();
            String shape = leftDetails.get(3).select("p").text();
            String logo = rightDetails.get(0).select("p").text();
            String colour = rightDetails.get(1).select("p").text();

            PillObject pill = new PillObject(name, location, date, 
                    suspectContents, shape, logo, colour, image);
            pills.add(pill);
        }

        return pills;
        
    }
}
