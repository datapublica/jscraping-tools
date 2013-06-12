package com.datapublica.scraping.example;

import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.datapublica.scraping.io.ScrapingIOUtils;
import com.datapublica.scraping.xpath.XPathUtils;

/**
 * @author thomas.dudouet@data-publica.com
 */
public class Scrapathon implements Runnable {
    
    private static final String BASE_URL = "http://lannuaire.service-public.fr/navigation/gouvernement.html";
    private static final String CHARSET = "UTF-8";
    
    @Override
    public void run() {
        try {
            // On fetch la page de l'annuaire pour récupérer tous les liens vers les pages de ministères
            final URL root = new URL(BASE_URL);
            final Set<URL> urls = new HashSet<>();
            try(InputStream stream = ScrapingIOUtils.getResourceAsStream(root)) {
                final Document document = XPathUtils.getDocumentHTML(stream, CHARSET);
                // On reconstruit chaque lien, et on l'ajoute à la liste
                final NodeList nodes = XPathUtils.getNodes(document, "//div[@class='bloc-contenu']/ul/li/a/@href");
                for(int i=0 ; i<nodes.getLength() ; i++) {
                    final Node node = nodes.item(i);
                    final String href = XPathUtils.unescape(node.getTextContent());
                    urls.add(new URL(root, href));
                }
            }
            // On va aller fetcher chaque page de ministère pour récupérer les informations associées
            for(URL url : urls) {
                try(InputStream stream = ScrapingIOUtils.getResourceAsStream(url)) {
                    final Document document = XPathUtils.getDocumentHTML(stream, CHARSET);
                    final String name = XPathUtils.getString(document, "//h2[@id='titrePage']/text()");
                    final String website = XPathUtils.getString(document, "//h3[text() = 'Site internet']/../../div/p/a/text()");
                    final String phone = XPathUtils.getString(document, "//p[@class='tel']/../p[@class='valeur']/span/text()");
                    final String addrLine1 = XPathUtils.getString(document, "((//p[@class='adresse'])[1]/text())[1]");
                    final String addrLine2 = XPathUtils.getString(document, "((//p[@class='adresse'])[1]/text())[2]");
                    final String addrLine3 = XPathUtils.getString(document, "((//p[@class='adresse'])[1]/text())[3]");
                    final NodeList persons = XPathUtils.getNodes(document, "//dl[@class='vcard']");
                    //
                    System.out.println("--- " + url.toExternalForm() + " ---");
                    System.out.println("NOM         " + name);
                    System.out.println("SITE WEB    " + website);
                    System.out.println("TELEPHONE   " + phone);
                    System.out.println("ADRESSE (CODE POSTAL " + StringUtils.substring(addrLine3 != null ? addrLine3 : addrLine2, 0, 5) + ")");
                    System.out.println("\t" + addrLine1);
                    System.out.println("\t" + addrLine2);
                    if(addrLine3 != null) {
                        System.out.println("\t" + addrLine3);
                    }
                    //
                    System.out.println("RESPONSABLES");
                    for(int i=0 ; i<persons.getLength() ; i++) {
                        final String role = XPathUtils.getString(persons.item(i), ".//dt[@class='role']/text()");
                        final String firstname = XPathUtils.getString(persons.item(i), ".//span[@class='given-name']/text()");
                        final String lastname = XPathUtils.getString(persons.item(i), ".//span[@class='family-name']/text()");
                        System.out.println("\t" + role + " : " + firstname + " " + lastname);
                    }
                }
            }
            //
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void main(String... args) {
        new Scrapathon().run();
    }
}
