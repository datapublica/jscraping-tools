package com.datapublica.scraping.io;

import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author thomas.dudouet@data-publica.com
 */
public class ScrapingIOUtils_Test {

    @Test
    @Ignore
    public void getResource() throws Exception {
        final URL url = new URL("http://www.data-publica.com/");
        System.out.println(ScrapingIOUtils.getResourceAsString(url));
    }
}
