package ua.com.msap.core.links.readers;

import ua.com.msap.core.exceptions.InvalidSchemeFormatException;
import org.w3c.dom.Element;
import ua.com.msap.core.LinkReader;
import ua.com.msap.core.IVariablesStorage;
import ua.com.msap.core.Link;
import ua.com.msap.core.XmlSchemeReader;
import ua.com.msap.core.links.Parallel;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class ParallelReader extends LinkReader {

    @Override
    public Link parse(Element xmlElement,
            IVariablesStorage variablesStorage) throws Exception {
        if (xmlElement == null) {
            String message = "Попытка распознавания Паралельного соединения"
                    + " из нулевого(не существующего) елемента...";
            throw new IllegalArgumentException(message);
        }

        if (!this.hasAttribute(xmlElement, "Forward", "yes", "no")) {
            String message = "У Паралельного соединения  не задан атрибут "
                    + "Forward, отвечающий за положительное направление "
                    + "сигналов(<Parallel Forward='yes'>...</Parallel>), и "
                    + "может принимать значения 'yes' или 'no'...";
            throw new InvalidSchemeFormatException(message);
        }

        Element aElement = this.findChildElement(xmlElement, "a");
        if (aElement == null) {
            String message = "У Паралельного соединения не задан елемент 'A' "
                    + "(<A>...</A>).";
            throw new InvalidSchemeFormatException(message);
        }
        if (XmlSchemeReader.getChildElements(aElement).isEmpty()) {
            String message = "У Паралельного соединения не задано содержание "
                    + "елемента 'A' (пример: <A><P>...<P></A>), елемент должен"
                    + " содержать звено одной из веток соединения.";
            throw new InvalidSchemeFormatException(message);
        }
        if (!this.hasAttribute(aElement, "Sign", "+", "-")) {
            String message = "У Паралельного соединения в елементе 'A' не "
                    + "задан атрибут Sign, отвечающий за знак "
                    + "соответствующей ветки на сумматоре"
                    + "(<A Sign='+'>...</A>), и может принимать значения "
                    + "+ или -.";
            throw new InvalidSchemeFormatException(message);
        }

                
        Element bElement = this.findChildElement(xmlElement, "b");
        if (bElement == null) {
            String message = "У Паралельного соединения не задан елемент 'B' "
                    + "(<B>...</B>), который должен содержать звено одной из "
                    + "веток соединения.";
            throw new InvalidSchemeFormatException(message);
        }
        if (XmlSchemeReader.getChildElements(bElement).isEmpty()) {
            String message = "У Паралельного соединения не задано содержание "
                    + "елемента 'B' (пример: <B><P>...<P></B>), елемент должен"
                    + " содержать звено одной из веток соединения.";
            throw new InvalidSchemeFormatException(message);
        }
        if (!this.hasAttribute(bElement, "Sign", "+", "-")) {
            String message = "У Паралельного соединения в елементе 'B' не "
                    + "задан атрибут Sign, отвечающий за знак соответствующей "
                    + "ветки на сумматоре(<B Sign='+'>...</B>), и может "
                    + "принимать значения + или -.";
            throw new InvalidSchemeFormatException(message);
        }
        
        Parallel newParalelLink = new Parallel();
        String aSign = aElement.getAttribute("Sign").trim();
        String bSign = bElement.getAttribute("Sign").trim();
        String forward = xmlElement.getAttribute("Forward").trim();
        newParalelLink.setSignA(aSign.equals("-") ? false : true);
        newParalelLink.setSignB(bSign.equals("-") ? false : true);
        newParalelLink.setForward(forward.equals("no") ? false : true);

        Link a = XmlSchemeReader.createLinkByXMLElement(
                XmlSchemeReader.getFirstChildElement(aElement),
                variablesStorage);
        if (a == null) {
            return null;
        }
        newParalelLink.setA(a);

        Link b = XmlSchemeReader.createLinkByXMLElement(
                XmlSchemeReader.getFirstChildElement(bElement),
                variablesStorage);
        if (b == null) {
            return null;
        }
        newParalelLink.setB(b);

        return newParalelLink;
    }
}
