package ua.com.msap.core.links.readers;

import org.w3c.dom.Element;
import ua.com.msap.core.IVariablesStorage;
import ua.com.msap.core.Link;
import ua.com.msap.core.LinkReader;
import ua.com.msap.core.XmlSchemeReader;
import ua.com.msap.core.exceptions.InvalidSchemeFormatException;
import ua.com.msap.core.links.extended.Select;

/**
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class SelectReader extends LinkReader{
    
    @Override
    public Link parse(Element xmlElement,
            IVariablesStorage variablesStorage) throws Exception {
        if (xmlElement == null) {
            String message = "Попытка распознавания Select"
                    + " из нулевого(не существующего) елемента...";
            throw new IllegalArgumentException(message);
        }

        if (!this.hasAttribute(xmlElement, "Exp", "=", "<", "<=", ">", ">=", "!=")) {
            String message = "У Select не задан атрибут "
                    + "Exp, отвечающий за логическое выражение "
                    + "сигналов(<Select Exp='=>'>...</Select>), и "
                    + "может принимать значения '=', '!=', '>', '>=', '<', '<='...";
            throw new InvalidSchemeFormatException(message);
        }

        Element aElement = this.findChildElement(xmlElement, "a");
        if (aElement == null) {
            String message = "У соединения не задан елемент 'A' "
                    + "(<A>...</A>).";
            throw new InvalidSchemeFormatException(message);
        }
        if (XmlSchemeReader.getChildElements(aElement).isEmpty()) {
            String message = "У соединения не задано содержание "
                    + "елемента 'A' (пример: <A><K>...</K></A>), елемент должен"
                    + " содержать звено одной из веток соединения.";
            throw new InvalidSchemeFormatException(message);
        }
                        
        Element bElement = this.findChildElement(xmlElement, "b");
        if (bElement == null) {
            String message = "У соединения не задан елемент 'B' "
                    + "(<B>...</B>), который должен содержать звено одной из "
                    + "веток соединения.";
            throw new InvalidSchemeFormatException(message);
        }
        if (XmlSchemeReader.getChildElements(bElement).isEmpty()) {
            String message = "У соединения не задано содержание "
                    + "елемента 'B' (пример: <B><K>...<K></B>), елемент должен"
                    + " содержать звено одной из веток соединения.";
            throw new InvalidSchemeFormatException(message);
        }
        
        Element thenElement = this.findChildElement(xmlElement, "then");
        if (thenElement == null) {
            String message = "У соединения не задан елемент 'Then' "
                    + "(<Then>...</Then>), который должен содержать условие "
                    + "с чем сравнивать А.";
            throw new InvalidSchemeFormatException(message);
        }
        if (XmlSchemeReader.getChildElements(thenElement).isEmpty()) {
            String message = "У соединения не задано содержание "
                    + "елемента 'Then' (пример: <Then><K>...<K></Then>), елемент должен"
                    + " содержать звено одной из веток соединения.";
            throw new InvalidSchemeFormatException(message);
        }
                
        Select newSelectLink = new Select();
        String exp = xmlElement.getAttribute("Exp").trim();
        newSelectLink.setExp(exp);

        Link a = XmlSchemeReader.createLinkByXMLElement(
                XmlSchemeReader.getFirstChildElement(aElement),
                variablesStorage);
        if (a == null) {
            return null;
        }
        newSelectLink.setA(a);

        Link b = XmlSchemeReader.createLinkByXMLElement(
                XmlSchemeReader.getFirstChildElement(bElement),
                variablesStorage);
        if (b == null) {
            return null;
        }
        newSelectLink.setB(b);
        
        Link then = XmlSchemeReader.createLinkByXMLElement(
                XmlSchemeReader.getFirstChildElement(thenElement),
                variablesStorage);
        if (then == null) {
            return null;
        }
        newSelectLink.setThen(then);

        return newSelectLink;
    }
}
