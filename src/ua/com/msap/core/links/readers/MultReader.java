package ua.com.msap.core.links.readers;

import java.util.List;
import org.w3c.dom.Element;
import ua.com.msap.core.IVariablesStorage;
import ua.com.msap.core.Link;
import ua.com.msap.core.LinkReader;
import ua.com.msap.core.XmlSchemeReader;
import ua.com.msap.core.exceptions.InvalidSchemeFormatException;
import ua.com.msap.core.links.extended.Mult;


/**
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class MultReader extends LinkReader{
    
    @Override
    public Link parse(Element xmlElement,
            IVariablesStorage variablesStorage) throws Exception {
        if (xmlElement == null) {
            String message = "Попытка распознавания блока умножения из нулевого"
                    + "(не существующего) елемента...";
            throw new IllegalArgumentException(message);
        }
        if (!this.hasAttribute(xmlElement, "Sign", "*", "/")) {
            String message = "У блока умножения не задан атрибут Sign, отвечающий "
                    + "за знак с которым основной сигнал поступает на блок умножения"
                    + "(<Mult Sign='*'>...</Mult>), и может принимать"
                    + " значения '*' или '/'.";
            throw new InvalidSchemeFormatException(message);
        }
        if (xmlElement.getChildNodes().getLength() == 0) {
            String message = "Попытка распознавания блока умножения без веток..."
                    + " Дочерними елементами блока умножения(Mult) могут быть "
                    + "только елементы Branch(как минимум 1 елемент) "
                    + "(<Branch>...</Branch><Branch>...</Branch>...), "
                    + "которые в свою очередь должны хранить наборы "
                    + "типовых звеньев...";
            throw new InvalidSchemeFormatException(message);
        }
        Mult newMult = new Mult();
        String sign = xmlElement.getAttribute("Sign");
        newMult.setSign(sign.equals("-") ? false : true);
        List<Element> childs = XmlSchemeReader.getChildElements(xmlElement);
        for (int i = 0; i < childs.size(); i++) {
            Element childElement = childs.get(i);
            if (!childElement.getNodeName().equalsIgnoreCase("branch")) {
                String message = "Не допустимый елемент '"
                        + childElement.getNodeName() + "'. Дочерними елементами"
                        + " блока умножения(Mult) могут быть только елементы "
                        + "Branch(как минимум 1 елемент) "
                        + "(<Branch>...</Branch><Branch>...</Branch>...), "
                        + "которые в свою очередь должны хранить наборы "
                        + "типовых звеньев...";
                throw new InvalidSchemeFormatException(message);
            }
            if (!this.hasAttribute(childElement, "Sign", "*", "/")) {
                String message = "У ветки(Branch)  не задан атрибут Sign, "
                        + "отвечающий за знак с которым сигнал поступает на "
                        + "блок умножения(<Branch Sign='*'>...</Branch>), и может "
                        + "принимать значения '*' или '/'.";
                throw new InvalidSchemeFormatException(message);
            }
            String brunchAttribute = childElement.getAttribute("Sign");
            boolean brunchSign = (brunchAttribute.equals("/") ? false : true);
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
            newMult.addBranch(brunchLink, brunchSign);
        }
        if (newMult.getLength() == 0) {
            String message = "В блоке умножения не удалось распознать ни одной "
                    + "ветки...";
            throw new InvalidSchemeFormatException(message);
        }
        return newMult;
    }
    
}
