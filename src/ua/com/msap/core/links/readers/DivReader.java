package ua.com.msap.core.links.readers;

import ua.com.msap.core.exceptions.InvalidSchemeFormatException;
import org.w3c.dom.Element;
import ua.com.msap.core.LinkReader;
import ua.com.msap.core.IVariablesStorage;
import ua.com.msap.core.Link;
import ua.com.msap.core.Variable;
import ua.com.msap.core.links.extended.Div;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class DivReader extends LinkReader {

    @Override
    public Link parse(Element xmlElement,
            IVariablesStorage variablesStorage) throws Exception {
        if (xmlElement == null) {
            String message = "Попытка распознавания звена деления из нулевого"
                    + "(не существующего) елемента...";
            throw new IllegalArgumentException(message);
        }

        Element kElement = this.findChildElement(xmlElement, "k");
        if (kElement == null || kElement.getTextContent().isEmpty()) {
            String message = "У звена деления не задан параметр 'K' (<K>...</K>).";
            throw new InvalidSchemeFormatException(message);
        }
        Div newDiv = new Div();
        Variable kVariable = this.createVariableFromXmlElement(kElement,
                variablesStorage);
        if (kVariable == null) {
            return null;
        }
        if (kVariable.getValue() == 0) {
            String message = "У звена деления параметр 'K' (<K>...</K>) имеет "
                    + "не допустимое значение(К=0)....";
            throw new InvalidSchemeFormatException(message);
        }
        newDiv.initK(kVariable);

        return newDiv;
    }
}
