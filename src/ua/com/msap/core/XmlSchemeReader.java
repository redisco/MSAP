package ua.com.msap.core;

import ua.com.msap.core.exceptions.InvalidSchemeFormatException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class XmlSchemeReader {

    public static Scheme parse(File xmlFile, File xsdFile)
            throws Exception {
        if (xsdFile != null) {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(
                    XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema xsdSchema = schemaFactory.newSchema(xsdFile);
            Validator validator = xsdSchema.newValidator();
            validator.validate(new StreamSource(xmlFile));
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);
        factory.setIgnoringComments(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);
        return getSchemeByXMLElement(doc.getDocumentElement());
    }

    protected static Scheme getSchemeByXMLElement(Element xmlElement)
            throws Exception {
        if (xmlElement == null) {
            String message = "Попытка распознать схему из нулевого"
                    + "(не существующего) елемента...";
            throw new IllegalArgumentException(message);
        }
        if (xmlElement.getChildNodes().getLength() == 0) {
            String message = "Попытка распознавания схемы не содержащей "
                    + "ни одного елемента...";
            throw new InvalidSchemeFormatException(message);
        }

        if (!xmlElement.getNodeName().equalsIgnoreCase("schema")) {
            String message = "Корневой елемент схемы должен называться Schema "
                    + "(<Schema>...</Schema>)...";
            throw new InvalidSchemeFormatException(message);
        }

        Scheme scheme = new Scheme();
        for (int i = 0; i < xmlElement.getChildNodes().getLength(); i++) {
            Node childNode = xmlElement.getChildNodes().item(i);
            if (!(childNode instanceof Element)) {
                continue;
            }
            Element childElement = (Element) childNode;
            if (!childElement.getNodeName().equalsIgnoreCase("model")) {
                String message = "Не допустимый елемент '"
                        + childElement.getNodeName()
                        + "'. Дочерними елементами схемы(Schema) могут быть "
                        + "только елементы Model(как минимум 1 елемент) "
                        + "(<Model>...</Model><Model>...</Model>...), "
                        + "которые в свою очередь должны хранить наборы "
                        + "типовых звеньев...";
                throw new InvalidSchemeFormatException(message);
            }
            if (childElement.getAttribute("Name") == null
                    || childElement.getAttribute("Name").trim().length() == 0) {
                String message = "У модели не задан атрибут Name, отвечающий"
                        + " за имя модели(<Model Name='model1'>...</Model>)...";
                throw new InvalidSchemeFormatException(message);
            }
            String modelName = childElement.getAttribute("Name");
            if (childElement.getChildNodes().getLength() == 0) {
                String message = "Модель '" + modelName + "' не содержит ни "
                        + "одного елемента. Moдeль должна состоять из одного "
                        + "корневого елемента(последовательного"
                        + "(<Serial>...</Serial>) или паралельного"
                        + "(<Parallel>...</Parallel>) соединения)...";
                throw new InvalidSchemeFormatException(message);
            }
            if (getChildElements(childElement).size() != 1) {
                String message = "Модель '" + modelName + "' содержит в себе "
                        + "несколько последовательных елементов. Moдeль может "
                        + "состоять только из одного корневого елемента"
                        + "(последовательного(<Serial>...</Serial>) "
                        + "или паралельного(<Parallel>...</Parallel>) соединения)"
                        + "...\nПодсказка:\nОберните содержимое модели '"
                        + modelName + "' в последовательное соединение"
                        + "(<Serial>...</Serial>).";
                throw new InvalidSchemeFormatException(message);
            }
            Link model = createLinkByXMLElement(getFirstChildElement(childElement),
                    (IVariablesStorage) scheme);
            if (model == null) {
                return null;
            }
            scheme.addModel(modelName, model);
        }
        if (scheme.getCountOfModels() == 0) {
            String message = "В схеме не удалось распознать ни одной модели...";
            throw new InvalidSchemeFormatException(message);
        }
        return scheme;
    }
    
    public static Element getFirstChildElement(Element xmlElement){
        if(xmlElement.getChildNodes().getLength() == 0){
            return null;
        }
        NodeList childs = xmlElement.getChildNodes();
        for(int i = 0; i<childs.getLength(); i++){
            Node child = childs.item(i);
            if(child instanceof Element){
                return (Element) child;
            }
        }
        return null;
    }
    public static List<Element> getChildElements(Element xmlElement){
        if(xmlElement.getChildNodes().getLength() == 0){
            return null;
        }
        NodeList childs = xmlElement.getChildNodes();
        List<Element> result = new ArrayList<Element>();
        for(int i = 0; i<childs.getLength(); i++){
            Node child = childs.item(i);
            if(child instanceof Element){
                 result.add((Element)child);
            }
        }
        return result;
    }
    public static Link createLinkByXMLElement(Element xmlElement,
            IVariablesStorage variablesStorage) throws Exception {
        if (xmlElement == null) {
            String message = "Попытка распознать нулевой(не существующий) "
                    + "елемент...";
            throw new IllegalArgumentException(message);
        }
        String[] namespaces = new String[]{"ua.com.msap.core.links",
            "ua.com.msap.core.links.extended"};

        Object linkObject = null;
        for (String namespace : namespaces) {
            try {
                Class linkClass = Class.forName(namespace + "."
                        + xmlElement.getNodeName().trim());
                linkObject = linkClass.newInstance();
                break;
            }
            catch(NoClassDefFoundError err){
                //Do nothing
            }
            catch (Exception ex) {
                //Do nothing
            }
        }
        if (linkObject == null) {
            String message = "Класс соответствующий элементу '"
                    + xmlElement.getNodeName() + "' не найден...";
            throw new ClassNotFoundException(message);
        }
        if (!(linkObject instanceof Link)) {
            String message = "Класс соответствующий элементу '"
                    + xmlElement.getNodeName() + "' не наследует класс Link, "
                    + "базовый для всех звеньев...";
            throw new ClassCastException(message);
        }
        Link link = (Link) linkObject;
        LinkReader linkReader = link.getLinkReader();
        link = linkReader.parse(xmlElement, variablesStorage);
        return link;
    }
}
