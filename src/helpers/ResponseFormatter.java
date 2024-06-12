package helpers;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;


public class ResponseFormatter {

    public enum ContentType {
        JSON,
        XML,
        HTML,
        TEXT // Add more content types if needed
    }
    public static String format(String response) {
        try {

            ContentType contentToBeParse = determineContentType(response);
            switch (contentToBeParse) {
                case JSON:
                    return formatJSON(response);
                case XML:
                    return formatXML(response);
                case HTML:
                    return formatHTML(response);
                default:
                    return response;
            }
        } catch (Exception e) {
            return "Error formatting response: " + e.getMessage();
        }
    }

    private static ContentType determineContentType(String response) {
        if (response.trim().startsWith("{") && response.trim().endsWith("}")) {
            return ResponseFormatter.ContentType.JSON;
        }
        else if ((response.trim().startsWith("<!DOCTYPE html>") ||
                response.trim().startsWith("<html>") ||
                response.trim().startsWith("<!doctype html>"))
                && response.trim().endsWith("</html>")) {
            return ResponseFormatter.ContentType.HTML;
        }
        else if (response.trim().startsWith("<") && response.trim().endsWith(">")) {
            return ResponseFormatter.ContentType.XML;
        }
        else {
            return ResponseFormatter.ContentType.TEXT;
        }
    }
    private static String formatJSON(String jsonString) {
        try {
            // Parse the JSON string and construct an indented JSON string
            StringBuilder formattedJSON = new StringBuilder();
            int indentLevel = 0;
            for (char c : jsonString.toCharArray()) {
                if (c == '{' || c == '[') {
                    indentLevel++;
                    formattedJSON.append(c).append("\n").append(getIndent(indentLevel));
                } else if (c == '}' || c == ']') {
                    indentLevel--;
                    formattedJSON.append("\n").append(getIndent(indentLevel)).append(c);
                } else if (c == ',') {
                    formattedJSON.append(c).append("\n").append(getIndent(indentLevel));
                } else {
                    formattedJSON.append(c);
                }
            }
            return formattedJSON.toString();
        } catch (Exception e) {
            return "Invalid JSON: " + e.getMessage();
        }
    }

    private static String getIndent(int indentLevel) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            indent.append("    ");
        }
        return indent.toString();
    }

    private static void appendIndent(StringBuilder stringBuilder, int indentLevel) {
        for (int i = 0; i < indentLevel; i++) {
            stringBuilder.append("    "); // Use 2 spaces for indentation
        }
    }
    private static String formatHTML(String htmlString) {
        StringBuilder formattedHtml = new StringBuilder();
        int indentLevel = 0;
        boolean insideTag = false;

        for (char c : htmlString.toCharArray()) {
            if (c == '<') {
                if (!insideTag) {
                    appendIndent(formattedHtml, indentLevel);
                    insideTag = true;
                }
                formattedHtml.append(c);
                if (!Character.isWhitespace(c)) {
                    insideTag = true;
                }
            } else if (c == '>') {
                formattedHtml.append(c).append("\n");
                if (!Character.isWhitespace(c)) {
                    insideTag = false;
                }
            } else if (c == '/') {
                formattedHtml.append(c);
                if (insideTag) {
                    formattedHtml.append(" ");
                }
            } else if (c == ' ') {
                formattedHtml.append(c);
                if (!insideTag && c == '\n') {
                    appendIndent(formattedHtml, indentLevel);
                }
            } else if (c == '\n') {
                if (insideTag) {
                    formattedHtml.append(c);
                } else {
                    formattedHtml.append(c);
                    appendIndent(formattedHtml, indentLevel);
                }
            } else {
                formattedHtml.append(c);
            }
        }

        return formattedHtml.toString();
    }



    private static String formatXML(String xmlString) {
        try {
            // Parse the XML string
            Document document = parseXML(xmlString);
            // Format the parsed XML document
            return formatDocument(document);
        } catch (Exception e) {
            return "Invalid XML: " + e.getMessage();
        }
    }

    private static Document parseXML(String xmlString) throws ParserConfigurationException, org.xml.sax.SAXException, java.io.IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xmlString));
        return builder.parse(is);
    }

    private static String formatDocument(Document document) throws Exception {
        // Create a Transformer for formatting
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty("indent", "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        // Convert the Document to a String
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        DOMSource source = new DOMSource(document);
        transformer.transform(source, result);
        return writer.toString();
    }


}
