package com.datapublica.scraping.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * @author thomas.dudouet@data-publica.com
 */
public class ScrapingIOUtils {

    private static final DecompressingHttpClient CLIENT = new DecompressingHttpClient(new DefaultHttpClient());
    
    /**
     * Return a web resource as a string.
     * 
     * @param url The target URL
     * @return The target URL content as a string
     * @throws IOException If an error occurs fetching URL
     */
    public static synchronized String getResourceAsString(URL url) throws IOException {
        // Prepare request
        final HttpGet get = new HttpGet(url.toExternalForm());
        // Fetch data
        final HttpResponse response = CLIENT.execute(get);
        try {
            // Check status code
            switch (response.getStatusLine().getStatusCode()) {
                case 200:
                    break;
                case 404:
                    throw new FileNotFoundException("404 status code for URL " + url.toExternalForm());
                default:
                    throw new IOException("Unexpected status code " + response.getStatusLine().getStatusCode() + " for URL " + url.toExternalForm());
            }
            // Return copied content to close carefully the client connection
            return EntityUtils.toString(response.getEntity());
        } finally {
            EntityUtils.consumeQuietly(response.getEntity());
        }
    }

    /**
     * Return a web resource as a stream.
     * 
     * @param url The target URL
     * @return The streamed content
     * @throws IOException If an error occurs fetching URL
     */
    public static InputStream getResourceAsStream(URL url) throws IOException {
        return IOUtils.toInputStream(ScrapingIOUtils.getResourceAsString(url));
    }
}
