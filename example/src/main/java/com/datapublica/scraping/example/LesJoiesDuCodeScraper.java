package com.datapublica.scraping.example;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import com.datapublica.scraping.io.ScrapingIOUtils;

/**
 * @author thomas.dudouet@data-publica.com
 */
public class LesJoiesDuCodeScraper implements Runnable {

    private static final String SEARCH_URL = "http://lesjoiesducode.tumblr.com/";

    private static final Pattern REGEX_PUBLICATION = Pattern.compile("<h3><a.+href=\"(.+?)\".*>(.+?)</a></h3>\\s*<div class=\"bodytype\">\\s*<p><p class=\"c1\"><img.+src=\"(.+?)\".*/></p>");
    private static final Pattern REGEX_NEXT_PAGE = Pattern.compile("<a href=\"(\\/page\\/[0-9]+)\">pagePrecedente");
    
    @Override
    public void run() {
        try {
            // Root url
            URL url = new URL(SEARCH_URL);
            //
            do {
                // Get webpage content
                final String content = ScrapingIOUtils.getResourceAsString(url);
                // For each publication
                final Matcher pub = REGEX_PUBLICATION.matcher(content);
                while(pub.find()) {
                    // Extract data
                    final String href = StringEscapeUtils.unescapeHtml(pub.group(1));
                    final String title = StringEscapeUtils.unescapeHtml(pub.group(2));
                    final String img = StringEscapeUtils.unescapeHtml(pub.group(3));
                    //
                    System.out.println("---");
                    System.out.println(title);
                    System.out.println(new URL(url, href));
                    System.out.println(new URL(url, img));
                }
                // Extract next search page url
                final Matcher next = REGEX_NEXT_PAGE.matcher(content);
                url = (next.find()) ? new URL(url, StringEscapeUtils.unescapeHtml(next.group(1))) : null;
                //
            } while (url != null);
            //
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String... args) {
        new LesJoiesDuCodeScraper().run();
    }
}
