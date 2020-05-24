package com.example.psychapp.experiencereports;

import android.os.AsyncTask;
import android.util.Pair;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

// First param: html of page, Second: substance name
public class ExperienceNameScraper extends AsyncTask<String, Integer, Pair<ArrayList<String>, ArrayList<String>>> {

    @Override
    protected Pair<ArrayList<String>, ArrayList<String>> doInBackground(String... output) {

        Document parsed = Jsoup.parse(output[0]);
        Elements searchReports = parsed.body().getElementsByClass("mw-headline")
                .select(String.format("span[id=%s]", output[1])).first()
                .parent().parent().getElementsByTag("li")
                .select("a[href*=Experience]");

        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> urls = new ArrayList<>();

        for (
                Element report: searchReports)

        {
            Element a = report.getElementsByTag("a").first();
            String link = a.attr("href");
            String title = a.text();
            names.add(title);
            urls.add(link);
            System.out.printf("name: %s\nurl: %s\n\n", title, link);
        }

        return new Pair<>(names, urls);
    }
}
