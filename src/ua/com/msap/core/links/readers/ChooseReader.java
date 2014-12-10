package ua.com.msap.core.links.readers;

import java.util.List;
import ua.com.msap.core.exceptions.InvalidSchemeFormatException;
import org.w3c.dom.Element;
import ua.com.msap.core.LinkReader;
import ua.com.msap.core.IVariablesStorage;
import ua.com.msap.core.Link;
import ua.com.msap.core.XmlSchemeReader;
import ua.com.msap.core.links.extended.Choose;
import ua.com.msap.core.links.extended.Choose.IfVariable;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class ChooseReader extends LinkReader {

    @Override
    public Link parse(Element xmlElement,
            IVariablesStorage variablesStorage) throws Exception {
        if (xmlElement == null) {
            String message = "Попытка распознавания элемента "
                    + "выбора из нулевого(не существующего) елемента...";
            throw new IllegalArgumentException(message);
        }

        if (xmlElement.getChildNodes().getLength() == 0) {
            String message = "Попытка распознавания элемента выбора "
                    + "без звеньев...";
            throw new InvalidSchemeFormatException(message);
        }
        Choose newChooseLink = new Choose();

        List<Element> childs = XmlSchemeReader.getChildElements(xmlElement);
        for (Element element : childs) {
            if (element.getNodeName().equalsIgnoreCase("if")) {

                if (!this.hasAttribute(element, "IfType", (String[]) null)) {
                    String message = "Атрибут 'IfType' у елемента '"
                            + element.getNodeName()
                            + "' имеет не верный формат...\n"
                            + "Атрибут 'IfType'  должен содержать "
                            + "текстовое значение с типом проверки."
                            + "Допустимые значения equal|default "
                            + "(<If IfType='equel'>..."
                            + "</If>)...";
                    throw new InvalidSchemeFormatException(message);
                }
                String ifTypeString = element.getAttribute("IfType");
                Choose.IfType ifType = Choose.IfType.valueOf(
                        ifTypeString.toUpperCase());
                
                double value = 0;
                if (ifType != Choose.IfType.DEFAULT) {
                    if (!this.hasAttribute(element, "Value", (String[]) null)) {
                        String message = "Атрибут 'Value' у елемента '"
                                + element.getNodeName()
                                + "' имеет не верный формат...\n"
                                + "Атрибут 'Value'  должен содержать "
                                + "числовое значение с которым сравниваеться сигнал."
                                + "(<If IfType='equal' Value='100'>..."
                                + "</If>)...";
                        throw new InvalidSchemeFormatException(message);
                    }
                    String valueString = element.getAttribute("Value");

                    try {
                        value = Double.parseDouble(valueString);
                    } catch (Exception ex) {
                        String message = "Атрибут  'value' звена '"
                                + element.getNodeName()
                                + "' имеет не верный формат ["
                                + element.getNodeValue()
                                + "]...";
                        throw new InvalidSchemeFormatException(message);
                    }
                }

                if (XmlSchemeReader.getChildElements(element).isEmpty()) {
                    String message = "У элемента IF не задано содержание, "
                            + " елемент должен содержать хотябы одно звено"
                            + "(пример: <IF><P>...<P></IF>).";
                    throw new InvalidSchemeFormatException(message);
                }
                Link link = XmlSchemeReader.createLinkByXMLElement(
                        XmlSchemeReader.getFirstChildElement(element),
                        variablesStorage);
                if (link == null) {
                    return null;
                }




                IfVariable ifv = newChooseLink.new IfVariable(
                        ifType, value, link);
                newChooseLink.addIf(ifv);
            }

        }
        if (newChooseLink.getSize() == 0) {
            String message = "В элементе выбора не удалось распознать "
                    + "ни одного звена...";
            throw new InvalidSchemeFormatException(message);
        }
        return newChooseLink;
    }
}
