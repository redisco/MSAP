package ua.com.msap.core;

import java.text.NumberFormat;
import java.util.Locale;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ua.com.msap.core.exceptions.InvalidSchemeFormatException;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public abstract class LinkReader {

    public abstract Link parse(Element xmlElement,
            IVariablesStorage variablesStorage) throws Exception;

    protected Element findChildElement(Element xmlElement, String elementName) {
        NodeList childNodes = xmlElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeName().equalsIgnoreCase(elementName)
                    && childNode instanceof Element) {
                return (Element) childNode;
            }
        }
        return null;
    }

    protected Variable createVariableFromXmlElement(Element fieldElement,
            IVariablesStorage variablesStorage)
            throws InvalidSchemeFormatException {
        Variable newVariable = null;

        if (fieldElement.hasAttribute("VariableName")) {
            String variableName = fieldElement.getAttribute("VariableName");
            if (variableName.isEmpty()) {
                String message = "Атрибут 'VariableName' у елемента '"
                        + fieldElement.getNodeName()
                        + "' имеет не верный формат...\n"
                        + "Если атрибут 'VariableName' задан, то должен "
                        + "содержать имя переменной/параметра. ";
                throw new InvalidSchemeFormatException(message);
            }
            newVariable = variablesStorage.getVariableByName(variableName);
            if (newVariable == null) {
                newVariable = new Variable();
            }
            variablesStorage.addVariableIfNotExists(variableName,
                    newVariable);
        } else {
            newVariable = new Variable();
        }

        try {
            NumberFormat format = NumberFormat.getInstance(
                    Locale.getDefault());
            newVariable.setValue(
                    format.parse(fieldElement.getTextContent()).doubleValue());
        } catch (Exception ex) {
            String message = "Параметр '"
                    + fieldElement.getNodeName()
                    + "' имеет не верный формат...";
            throw new InvalidSchemeFormatException(message);
        }

        return newVariable;
    }

    protected boolean hasAttribute(Element fieldElement, String attributeName,
            String... legalValues) {
        if (!fieldElement.hasAttribute(attributeName)) {
            return false;
        }
        if (legalValues == null || legalValues.length == 0) {
            return true;
        }
        String attributeValue = fieldElement.getAttribute(attributeName);
        for (String legalValue : legalValues) {
            if (attributeValue.equals(legalValue)) {
                return true;
            }
        }
        return false;
    }
}
