package com.example.psychapp.experiencereports;
import android.content.Context;
import android.content.res.AssetManager;

import com.example.psychapp.activities.ExperienceReport;
import com.example.psychapp.experiencereports.Objects.ErowidExperienceName;
import com.example.psychapp.experiencereports.Objects.ExperienceReportObject;
import com.example.psychapp.experiencereports.Objects.ExperienceSectionObject;
import com.example.psychapp.pillreports.WebScraper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

public class ErowidExperienceReportScraper {
    
    private static TreeMap<String, Object> substanceToNumber = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public TreeMap<String, Object> getSubstanceToNumber() {
        return substanceToNumber;
    }

    public void initialiseMap(Context context){
//        File file = new File("substance_numbers.txt");
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open("substance_numbers.txt");
            substanceToNumber = new ObjectMapper().readValue(is, new TypeReference<TreeMap<String, Object>>() {});
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
    public ArrayList<ErowidExperienceName> getExperiences(String substance){
        WebScraper webScraper = new WebScraper();
        int number = Integer.parseInt(Objects.requireNonNull(substanceToNumber.get(substance)).toString());
        String html = "";
        ArrayList<ErowidExperienceName> experiences = new ArrayList<>();
        try {
            html = webScraper.execute("https://www.erowid.org/experiences/exp.cgi?S1="+number+"&S2=-1&C1=-1&Str=").get();
            Elements parsed = Jsoup.parse(html).body().getElementsByClass("exp-list-table");
            Elements reports = parsed.select("tr[class=\"\"]");
            for (Element report: reports){
                experiences.add(new ErowidExperienceName(report.select("a").text(), 
                        report.select("a").attr("href"), 
                        report.select("td").get(3).text(), 
                        report.select("td").get(4).text()));
            }
        } catch (Exception e){
            return null;
        }
        
        return experiences;
    }
    
    public ExperienceReportObject getExperience(ErowidExperienceName substance){
        WebScraper webScraper = new WebScraper();
        try {
            String html = webScraper.execute("https://www.erowid.org/experiences/" + substance.getUrl()).get();
            Elements parsed = Jsoup.parse(html).body().getElementsByClass("report-text-surround");

            ArrayList<ExperienceSectionObject> experienceSections = new ArrayList<>();
            Elements doses = parsed.select("tbody").select("tr");
            if (doses.size() == 1){
                experienceSections.add(new ExperienceSectionObject("Substance:", null));
            } else if (doses.size() > 1){
                experienceSections.add(new ExperienceSectionObject("Substances:", null));
            }

            for (Element dose : doses){
                try {
                    StringBuilder substanceInfo = new StringBuilder();
                    List<TextNode> doseTime = dose.select("td").first()
                            .textNodes();

                    if (!doseTime.isEmpty()){
                        String doseString = doseTime.get(0).toString();
                        if (doseString.contains("&nbsp")){
                            substanceInfo.append(doseString.split("; ")[1]).append(": ");
                        } else if (doseString.equals("DOSE:")){
                            substanceInfo.append(doseTime.get(1).toString()).append(": ");
                        } else {
                            break;
                        }
                    }

                    Element doseSubstance = dose.select("td[class=\"dosechart-substance\"]").first();
                    if (doseSubstance.childrenSize() > 0) {
                        substanceInfo.append(doseSubstance.select("a").text());
                    }

                    Element doseAmount = dose.select("td[class=\"dosechart-amount\"]").first();
                    if (!doseAmount.text().equals("")) {
                        substanceInfo.append(" - ").append(doseAmount.text());
                    }

                    Element doseMethod = dose.select("td[class=\"dosechart-method\"]").first();
                    if (!doseAmount.text().equals("")) {
                        substanceInfo.append(" - ").append(doseMethod.text());
                    }

                    Element doseForm = dose.select("td[class=\"dosechart-form\"]").first();
                    if (doseAmount.childrenSize() > 0) {
                        substanceInfo.append(" - ").append(doseForm.select("b").text());
                    }

                    experienceSections.add(new ExperienceSectionObject(null,
                            substanceInfo.toString()));
                } catch (NullPointerException | IndexOutOfBoundsException ignored){
                }
            }



            Element bodyweight = parsed.select("td[class=\"bodyweight-amount\"]").first();
            if (!bodyweight.text().equals("")){
                experienceSections.add(new ExperienceSectionObject("Form:",
                        bodyweight.text()));
            }

            Elements footdata = parsed.select("table[class=\"footdata\"]");

            for (Element element: footdata.select("tr")){
                String text = element.select("td").first().text();
                if (text.contains("PDF")){
                    break;
                }
                String[] split = text.split(": ");
                experienceSections.add(new ExperienceSectionObject(split[0], split[1]));
            }

            for (TextNode section: parsed.textNodes()){
                if (!section.isBlank()){
                    experienceSections.add(new ExperienceSectionObject(null, section.text()));
                }
            }

            return new ExperienceReportObject(substance.getName(), experienceSections);

        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
}
