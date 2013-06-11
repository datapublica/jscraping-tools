package com.datapublica.scraping.xpath;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author thomas.dudouet@data-publica.com
 */
public class XPathUtils_Test {

    @Test
    public void getDocumentXML() throws Exception {
        try (InputStream stream = XPathUtils_Test.class.getResourceAsStream("test.xml")) {
            final Document document = XPathUtils.getDocumentXML(stream);
            final NodeList nodes = XPathUtils.getNodes(document, "//personne");
            Assert.assertEquals(6, nodes.getLength());
            for (int i = 0; i < nodes.getLength(); i++) {
                final Node node = nodes.item(i);
                switch (i) {
                    case 0:
                        Assert.assertEquals("Obama", XPathUtils.getString(node, ".//nom/text()"));
                        Assert.assertEquals("Barack", XPathUtils.getString(node, ".//prenom/text()"));
                        Assert.assertEquals("Président des Etats-Unis", XPathUtils.getString(node, ".//poste/text()"));
                        Assert.assertEquals("H", XPathUtils.getString(node, ".//sexe/text()"));
                        break;
                    case 5:
                        Assert.assertEquals("Ferrat", XPathUtils.getString(node, ".//nom/text()"));
                        Assert.assertEquals("André", XPathUtils.getString(node, ".//prenom/text()"));
                        Assert.assertEquals("Militant communiste", XPathUtils.getString(node, ".//poste/text()"));
                        Assert.assertEquals("H", XPathUtils.getString(node, ".//sexe/text()"));
                        break;
                }
            }
            Assert.assertEquals(1, XPathUtils.getNodes(document, "//sexe[text() = 'F']/..").getLength());
            Assert.assertEquals("Baez", XPathUtils.getString(document, "//sexe[text() = 'F']/../nom/text()"));
        }
    }
    
    @Test
    public void getDocumentHTML() throws Exception {
        try (InputStream stream = XPathUtils_Test.class.getResourceAsStream("test.html")) {
            final Document document = XPathUtils.getDocumentHTML(stream, "UTF-8");
            Assert.assertEquals("@tdudouet", XPathUtils.getString(document, "//h1/small/text()"));
        }
    }
}
