package com.example.psychapp.experiencereports;

import android.os.AsyncTask;
import android.util.Log;

import com.example.psychapp.experiencereports.Objects.ExperienceReportObject;
import com.example.psychapp.experiencereports.Objects.ExperienceSectionObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

// Pass in title,
/*    Elements paragraphs = Jsoup.parse(webScraper.execute("https://psychonautwiki.org/" + link[0]).get()).body()
*/
public class ExperienceReportScraper extends AsyncTask<String, Integer, ExperienceReportObject> {

    @Override
    protected ExperienceReportObject doInBackground(String... link) {
        String pillReportsUrl = "https://psychonautwiki.org/wiki/Experience_index";

        Elements paragraphs = Jsoup.parse(link[1]).body()
                .getElementsByClass("mw-parser-output").first()
                .children();

        ArrayList<ExperienceSectionObject> experienceSections = new ArrayList<>();
        for (Element paragraph: paragraphs.subList(1, paragraphs.size())){
            if (paragraph.getElementById("Effects_analysis") != null){
                break;
            }

            experienceSections.addAll(getSection(paragraph) );
        }

        return (new ExperienceReportObject(link[0], experienceSections));
    }

    private ArrayList<ExperienceSectionObject> getSection(Element paragraph){
        String sectionTitle = "";
        ArrayList<ExperienceSectionObject> experienceReports = new ArrayList<>();

        if (paragraph.getElementsByClass("mw-headline").size() != 0) {
            experienceReports.add(new ExperienceSectionObject(paragraph.text().toLowerCase(), null));
            return experienceReports;
        }
        Log.d("SECTION", "getSection: " + paragraph.html() + "\n elems:" + paragraph.getElementsByTag("b").size());
        if (paragraph.tag().toString().equals("ul")) {
            for (Element element: paragraph.children()) {
                experienceReports.addAll(getSection(element));
            }
        } else if (paragraph.getElementsByTag("b").size() > 2){
            for (Element child: paragraph.getElementsByTag("b")){
                experienceReports.add(new ExperienceSectionObject(child.text().toLowerCase(), child.nextSibling().toString().toLowerCase()));
            }
        }
        else {
            Elements titleElement = paragraph.getElementsByTag("b");
            if (titleElement.size() != 0) {
                sectionTitle = titleElement.first().text();
                experienceReports.add(new ExperienceSectionObject(sectionTitle.toLowerCase(), paragraph.after(titleElement.first()).text().toLowerCase()));
            } else {
                experienceReports.add(new ExperienceSectionObject(null, paragraph.text().toLowerCase()));
            }
        }

        return experienceReports;
    }
    
}
