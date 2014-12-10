package ua.com.msap.core.links.readers;

import java.util.List;
import ua.com.msap.core.exceptions.InvalidSchemeFormatException;
import org.w3c.dom.Element;
import ua.com.msap.core.LinkReader;
import ua.com.msap.core.IVariablesStorage;
import ua.com.msap.core.Link;
import ua.com.msap.core.XmlSchemeReader;
import ua.com.msap.core.links.extended.Summator;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class SummatorReader extends LinkReader {

    @Override
    public Link parse(Element xmlElement,
            IVariablesStorage variablesStorage) throws Exception {
        if (xmlElement == null) {
            String message = "Попытка распознавания сумматора из нулевого"
                    + "(не существующего) елемента...";
            throw new IllegalArgumentException(message);
        }
        if (!this.hasAttribute(xmlElement, "Sign", "+", "-")) {
            String message = "У сумматора  не задан атрибут Sign, отвечающий "
                    + "за знак с которым основной сигнал поступает на сумматор"
                    + "(<Summator Sign='+'>...</Summator>), и может принимать"
                    + " значения '+' или '-'.";
            throw new InvalidSchemeFormatException(message);
        }
        if (xmlElement.getChildNodes().getLength() == 0) {
            String message = "Попытка распознавания сумматора без веток..."
                    + " Дочерними елементами сумматора(Summator) могут быть "
                    + "только елементы Branch(как минимум 1 елемент) "
                    + "(<Branch>...</Branch><Branch>...</Branch>...), "
                    + "которые в свою очередь должны хранить наборы "
                    + "типовых звеньев...";
            throw new InvalidSchemeFormatException(message);
        }
        Summator newSummator = new Summator();
        String sign = xmlElement.getAttribute("Sign");
        newSummator.setSign(sign.equals("-") ? false : true);
        List<Element> childs = XmlSchemeReader.getChildElements(xmlElement);
        for (int i = 0; i < childs.size(); i++) {
            Element childElement = childs.get(i);
            if (!childElement.getNodeName().equalsIgnoreCase("branch")) {
                String message = "Не допустимый елемент '"
                        + childElement.getNodeName() + "'. Дочерними елементами"
                        + " сумматора(Summator) могут быть только елементы "
                        + "Branch(как минимум 1 елемент) "
                        + "(<Branch>...</Branch><Branch>...</Branch>...), "
                        + "которые в свою очередь должны хранить наборы "
                        + "типовых звеньев...";
                throw new InvalidSchemeFormatException(message);
            }
            if (!this.hasAttribute(childElement, "Sign", "+", "-")) {
                String message = "У ветки(Branch)  не задан атрибут Sign, "
                        + "отвечающий за знак с которым сигнал поступает на "
                        + "сумматор(<Branch Sign='+'>...</Branch>), и может "
                        + "принимать значения '+' или '-'.";
                throw new InvalidSchemeFormatException(message);
            }
            String brunchAttribute = childElement.getAttribute("Sign");
            boolean brunchSign = (brunchAttribute.equals("-") ? false : true);
            if (childElement.getChildNodes().getLength() == 0) {
                String message = "Ветка(Branch) не содержит ни одного "
                        + "елемента. Ветка должна состоять из одного "
                        + "корневого елемента(последовательного"
                        + "(<Serial>...</Serial>) или паралельного"
                        + "(<Parallel>...</Parallel>) соединения)...";
                throw new InvalidSchemeFormatException(message);
            }
            if (XmlSchemeReader.getChildElements(childElement).size() > 1) {
                String message = "Ветка(Branch) содержит в себе несколько "
                        + "последовательных елементов. Ветка может состоять "
                        + "только из одного корневого елемента"
                        + "(последовательного(<Serial>...</Serial>) или "
                        + "паралельного(<Parallel>...</Parallel>) соединения)..."
                        + "\nПодсказка:\nОберните содержимое ветки в "
                        + "последовательное соединение(<Serial>...</Serial>).";
                throw new InvalidSchemeFormatException(message);
            }
            Link brunchLink = XmlSchemeReader.createLinkByXMLElement(
                    XmlSchemeReader.getFirstChildElement(childElement), 
                    variablesStorage);
            if (brunchLink == null) {
                return null;
            }
            newSummator.addBranch(brunchLink, brunchSign);
        }
        if (newSummator.getLength() == 0) {
            String message = "В сумматоре не удалось распознать ни одной "
                    + "ветки...";
            throw new InvalidSchemeFormatException(message);
        }
        return newSummator;
    }
}
