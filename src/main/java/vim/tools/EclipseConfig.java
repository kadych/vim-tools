package vim.tools;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EclipseConfig {
    Document loadDocument() throws IOException, ParserConfigurationException,
        SAXException {
        try (InputStream is =
            new BufferedInputStream(getClass().getResourceAsStream("/eclipse.xml"))) {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        }
    }

    Element loadProfile(Document document) {
        Objects.requireNonNull(document);
        NodeList nodes = document.getElementsByTagName("profile");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) node;
            }
        }
        return null;
    }

    int loadSettings(Element profile, Properties options) {
        Objects.requireNonNull(profile);
        Objects.requireNonNull(options);
        int count = 0;
        NodeList nodes = profile.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element setting = (Element) node;
                options.put(setting.getAttribute("id"), setting.getAttribute("value"));
                ++count;
            }
        }
        return count;
    }

    public void load(Properties options) throws IOException {
        try {
            Document document = loadDocument();
            loadSettings(loadProfile(document), options);
        } catch (SAXException | ParserConfigurationException e) {
            throw new RuntimeException(e.getCause());
        }
    }
}
