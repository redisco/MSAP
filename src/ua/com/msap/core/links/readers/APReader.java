package ua.com.msap.core.links.readers;


import org.w3c.dom.Element;
import ua.com.msap.core.LinkReader;
import ua.com.msap.core.IVariablesStorage;
import ua.com.msap.core.Link;
import ua.com.msap.core.Variable;
import ua.com.msap.core.exceptions.InvalidSchemeFormatException;
import ua.com.msap.core.links.AP;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class APReader extends LinkReader {

    @Override
      public Link parse(Element xmlElement,
            IVariablesStorage variablesStorage) throws Exception{
                 if (xmlElement == null) {
            String message = "Попытка распознавания АП-звена из нулевого"
                    + "(не существующего) елемента...";
            throw new IllegalArgumentException(message);
        }
                 Element kElement = this.findChildElement(xmlElement, "k");
        if (kElement == null || kElement.getTextContent().isEmpty()) {
            String message = "У АП-звена не задан параметр 'K' (<K>...</K>).";
            throw new InvalidSchemeFormatException(message);
        }
        Element tElement = this.findChildElement(xmlElement, "t");
        if (tElement == null || tElement.getTextContent().isEmpty()) {
            String message = "У АП-звена не задан параметр 'T' (<T>...</T>).";
            throw new InvalidSchemeFormatException(message);
        }
        
        AP newAP = new AP();
        Variable kVariable = this.createVariableFromXmlElement(kElement,
                variablesStorage);
        if (kVariable == null) {
            return null;
        }
        newAP.initK(kVariable);
        Variable tVariable = this.createVariableFromXmlElement(tElement,
                variablesStorage);
        if (tVariable == null) {
            return null;
        }
        newAP.initT(tVariable);
        return newAP;
    }

    
}
