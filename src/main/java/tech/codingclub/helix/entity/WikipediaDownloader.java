package tech.codingclub.helix.entity;
import  tech.codingclub.helix.entity.TaskManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Date;

public class gitWikipediaDownloader {

    private String keyword;
    private String result;

    public WikipediaDownloader() {

    }

    public WikipediaDownloader(String keyword) {
        this.keyword = keyword;
    }

    public WikiResult getResult() {

        //1. Get clean keyword !
        //2. Get the url for wikipedia
        //3. Make a GET request to wikipedia !
        //4. Parsing the useful results using jsoup
        //5. Showing results !

        if (this.keyword == null || this.keyword.length() == 0) {
            return null;
        }

        //STEP 1
        this.keyword = this.keyword.trim().replaceAll("[ ]+", "_");

        //STEP 2
        String wikiUrl = getWikipediaUrlForQuery(this.keyword);
        String response = "";
        String imageUrl = null;

        try {

            //STEP 3
            String wikipediaResponseHTML = tech.codingclub.helix.global.HttpURLConnectionExample.sendGet(wikiUrl);
            // System.out.println(wikipediaResponseHTML);

            //STEP 4
            Document document = Jsoup.parse(wikipediaResponseHTML, "https://en.wikipedia.org");

            Elements childElements = document.body().select(".mw-parser-output > *");

            int state = 0;
            for (Element childElement : childElements) {
                if (state == 0) {
                    if (childElement.tagName().equals("table")) {
                        state = 1;
                    }
                } else if (state == 1) {
                    if (childElement.tagName().equals("p")) {
                        state = 2;
                        response = childElement.text();
                        break;
                    }
                }
            }
            try {
                imageUrl = document.body().select(".infobox img").get(0).attr("src");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        tech.codingclub.helix.entity.WikiResult wikiResult = new tech.codingclub.helix.entity.WikiResult(this.keyword, response, imageUrl);
        // PUSH RESULT INTO DATABASE

        return wikiResult;


    }


    private String getWikipediaUrlForQuery(String cleanKeyword) {
        return "https://en.wikipedia.org/wiki/" + cleanKeyword;
    }
//    public static void main(String[] args) {
//        TaskManager taskManager = new TaskManager(20);
//
//        String arr[] = {"India", "United States"};
//
//        System.out.println("This side is Harshita Jain");
//        System.out.println("Running Wikipedia Downloader at " + new Date().getTime());
//
//        for (String keyword : arr) {
//            WikipediaDownloader wikipediaDownloader = new WikipediaDownloader(keyword);
//            taskManager.waitTillQueueIsFreeAndAddTask( wikipediaDownloader);
//        }
//    }
}

