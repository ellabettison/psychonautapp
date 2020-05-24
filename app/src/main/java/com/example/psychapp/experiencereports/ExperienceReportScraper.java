package com.example.psychapp.experiencereports;

import android.os.AsyncTask;

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
            if (paragraph.getElementsContainingText("Effects analysis").size() != 0){
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
            experienceReports.add(new ExperienceSectionObject(paragraph.text(), null));
            return experienceReports;
        }
        if (paragraph.tag().toString().equals("ul")) {
            for (Element element: paragraph.children()) {
                experienceReports.addAll(getSection(element));
            }
        } else {
            Elements titleElement = paragraph.getElementsByTag("b");
            if (titleElement.size() != 0) {
                sectionTitle = titleElement.first().text();
                experienceReports.add(new ExperienceSectionObject(sectionTitle, paragraph.after(titleElement.first()).text()));
            } else {
                experienceReports.add(new ExperienceSectionObject(null, paragraph.text()));
            }
        }

        return experienceReports;
    }
    
}
