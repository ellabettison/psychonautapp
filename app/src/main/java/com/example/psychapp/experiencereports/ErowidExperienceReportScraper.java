package com.example.psychapp.experiencereports;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.example.psychapp.activities.ExperienceReport;
import com.example.psychapp.experiencereports.Objects.ErowidExperienceName;
import com.example.psychapp.experiencereports.Objects.ExperienceReportObject;
import com.example.psychapp.experiencereports.Objects.ExperienceSectionObject;
import com.example.psychapp.pillreports.WebScraper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.validator.GenericValidator;
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
                        Log.d("IFNEIF", "e\ne\ngetExperience: "+doseTime+"\ne\ne");
                        if (doseString.contains("&nbsp") && doseTime.size()==2){
                            substanceInfo.append(doseString.split("; ")[1]).append(": ");
                        } else if (doseString.equals("DOSE:") && doseTime.size()==2){
                            substanceInfo.append(doseTime.get(1).toString()).append(": ");
                        }
                    }

                    Element doseSubstance = dose.select("td[class=\"dosechart-substance\"]").first();
                    if (doseSubstance != null && doseSubstance.childrenSize() > 0) {
                        substanceInfo.append(doseSubstance.select("a").text());
                    } else {
                        continue;
                    }

                    Element doseAmount = dose.select("td[class=\"dosechart-amount\"]").first();
                    if (doseAmount != null && !doseAmount.text().equals("")) {
                        substanceInfo.append(" - ").append(doseAmount.text());
                    }

                    Element doseMethod = dose.select("td[class=\"dosechart-method\"]").first();
                    if (doseMethod != null && !doseMethod.text().equals("")) {
                        substanceInfo.append(" - ").append(doseMethod.text());
                    }

                    Element doseForm = dose.select("td[class=\"dosechart-form\"]").first();
                    if (doseForm != null && doseForm.childrenSize() > 0) {
                        substanceInfo.append(" - ").append(doseForm.select("b").text());
                    }

                    Log.d("INI", "\ng\ne\ntExperience: " + substanceInfo.toString() + "\ne\ne");

                    experienceSections.add(new ExperienceSectionObject(null,
                            substanceInfo.toString()));
                } catch (NullPointerException | IndexOutOfBoundsException e){
                    e.printStackTrace();
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
                    if (isTitle(section.toString())){
                        experienceSections.add(new ExperienceSectionObject(section.text(), null));
                    } else {
                        experienceSections.add(new ExperienceSectionObject(null, section.text()));
                    }
                }
            }

            return new ExperienceReportObject(substance.getName(), experienceSections);

        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isTitle(String section){
        if (section.length() < 100){
            if (section.contains("--")){
                return true;
            } else if (section.contains("T+") || section.contains("T +")
                    || section.contains("t+") || section.contains("t +")){
                return true;
            } else {
                if (!GenericValidator.isDate(section, "dd MMM yyyy", false)){
                    if(!GenericValidator.isDate(section, "dd MM yyyy", false)){
                        if(!GenericValidator.isDate(section, "MM dd yyyy", false)){
                            return !GenericValidator.isDate(section, "MMM dd yyyy", false);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
    
}
