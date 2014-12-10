package ua.com.msap.core.links.readers;

import ua.com.msap.core.exceptions.InvalidSchemeFormatException;
import org.w3c.dom.Element;
import ua.com.msap.core.LinkReader;
import ua.com.msap.core.IVariablesStorage;
import ua.com.msap.core.Link;
import ua.com.msap.core.Variable;
import ua.com.msap.core.links.TransportDelay;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class TransportDelayReader extends LinkReader {

    @Override
      public Link parse(Element xmlElement,
            IVariablesStorage variablesStorage) throws Exception{
        
        if (xmlElement == null) {
            String message = "Попытка распознавания звена запаздывания из "
                    + "нулевого (не существующего) елемента...";
            throw new IllegalArgumentException(message);
        }
        Element tElement = this.findChildElement(xmlElement, "t");
        if (tElement == null || tElement.getTextContent().isEmpty()) {
            String message = "У звена запаздывания не задан параметр 'T' "
                    + "(<T>...</T>).";
            throw new InvalidSchemeFormatException(message);
        }
        
        TransportDelay newDelay = new TransportDelay();
        Variable tVariable = this.createVariableFromXmlElement(tElement,
                variablesStorage);
        if (tVariable == null) {
            return null;
        }
        newDelay.initDelay(tVariable);
        return newDelay;
    }
    
}
