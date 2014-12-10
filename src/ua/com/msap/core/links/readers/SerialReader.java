package ua.com.msap.core.links.readers;

import java.util.List;
import ua.com.msap.core.exceptions.InvalidSchemeFormatException;
import org.w3c.dom.Element;
import ua.com.msap.core.LinkReader;
import ua.com.msap.core.IVariablesStorage;
import ua.com.msap.core.Link;
import ua.com.msap.core.XmlSchemeReader;
import ua.com.msap.core.links.Serial;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class SerialReader extends LinkReader {

    @Override
    public Link parse(Element xmlElement,
            IVariablesStorage variablesStorage) throws Exception {
        if (xmlElement == null) {
            String message = "Попытка распознавания Последовательного "
                    + "соединения из нулевого(не существующего) елемента...";
            throw new IllegalArgumentException(message);
        }
        if (!this.hasAttribute(xmlElement, "Forward", "yes", "no")) {
            String message = "У Последовательного соединения  не задан атрибут"
                    + " Forward, отвечающий за положительное направление"
                    + " сигналов(<Serial Forward='yes'>...</Serial>), и может"
                    + " принимать значения 'yes' или 'no'.";
            throw new InvalidSchemeFormatException(message);
        }

        if (xmlElement.getChildNodes().getLength() == 0) {
            String message = "Попытка распознавания Паралельного соединения "
                    + "без звеньев...";
            throw new InvalidSchemeFormatException(message);
        }
        Serial newSerialLink = new Serial();
        String forward = xmlElement.getAttribute("Forward");
        newSerialLink.setDirection(forward.equals("no") ? false : true);

        List<Element> childs = XmlSchemeReader.getChildElements(xmlElement);
        for (int i = 0; i < childs.size(); i++) {
            Link child = XmlSchemeReader.createLinkByXMLElement(childs.get(i),
                    variablesStorage);
            if (child == null) {
                return null;
            }
            newSerialLink.addLink(child);
        }
        if (newSerialLink.getLength() == 0) {
            String message = "В Паралельном соединении не удалось распознать "
                    + "ни одного звена...";
            throw new InvalidSchemeFormatException(message);
        }
        return newSerialLink;
    }
}
