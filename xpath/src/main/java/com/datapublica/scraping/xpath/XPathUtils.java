package com.datapublica.scraping.xpath;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author thomas.dudouet@data-publica.com
 */
public class XPathUtils {

    private static final DomSerializer SERIALIZER;
    static {
        final CleanerProperties props = new CleanerProperties();
        props.setNamespacesAware(false);
        SERIALIZER = new DomSerializer(props);
    }

    private static final DocumentBuilderFactory DOC_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
    private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();

    /**
     * Returns the document object built from an XML resource.
     * 
     * @param stream The XML stream
     * @return The document built from the stream
     * @throws IOException If an error occurs fetching document
     * @throws SAXException If an error occurs parsing XML
     */
    public static Document getDocumentXML(InputStream stream) throws IOException, SAXException {
        try {
            return DOC_BUILDER_FACTORY.newDocumentBuilder().parse(stream);
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Returns the document object built from an HTML resource.
     * 
     * @param stream The HTML stream
     * @param charset The HTML content charset
     * @return The document built from the stream
     * @throws IOException If an error occurs fetching document
     */
    public static Document getDocumentHTML(InputStream stream, String charset) throws IOException {
        try {
            return SERIALIZER.createDOM(new HtmlCleaner().clean(stream, charset));
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Invalid parser configuration", e);
        }
    }

    /**
     * Get a string from the given document, according to the given xpath expression.
     * 
     * @param node The root DOM node
     * @param xpath The xpath expression
     * @return The extracted string
     * @throws XPathExpressionException If the given xpath expression is invalid, or if the xpath expression returned
     *             more than a single text node, or a non text-node
     */
    public static String getString(Node node, String xpath) throws XPathExpressionException {
        // Get node list
        final NodeList nodes = XPathUtils.getNodes(node, xpath);
        // No result
        if (nodes.getLength() == 0) {
            return null;
        }
        // Unique result
        else if (nodes.getLength() == 1) {
            // If this is a text node
            final Node content = nodes.item(0);
            if (content.getNodeType() == Node.TEXT_NODE || content.getNodeType() == Node.ATTRIBUTE_NODE) {
                return XPathUtils.unescape(nodes.item(0).getNodeValue());
            }
            // Invalid node type
            throw new XPathExpressionException("String xpath returned a non text node");
        }
        // More than one result
        throw new XPathExpressionException("String xpath returned " + nodes.getLength() + " nodes");
    }

    /**
     * Returns the DOM nodes associated to a given xpath expression.
     * 
     * @param node The root DOM node
     * @param xpath The xpath expression
     * @return The extracted DOM nodes
     * @throws XPathExpressionException If the given xpath expression is invalid
     */
    public static NodeList getNodes(Node node, String xpath) throws XPathExpressionException {
        return (NodeList) XPATH_FACTORY.newXPath().evaluate(xpath, node, XPathConstants.NODESET);
    }

    /**
     * Unescape an extracted string from special XML or HTML chars.
     * 
     * @param text The extracted text
     * @return The unescaped text
     */
    public static String unescape(String text) {
        return StringUtils.trimToNull(StringEscapeUtils.unescapeHtml(text));
    }
}
