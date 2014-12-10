package ua.com.msap.core.links.readers;

import ua.com.msap.core.exceptions.InvalidSchemeFormatException;
import org.w3c.dom.Element;
import ua.com.msap.core.LinkReader;
import ua.com.msap.core.IVariablesStorage;
import ua.com.msap.core.Link;
import ua.com.msap.core.links.extended.LoadVariable;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class LoadVariableReader extends LinkReader {

    @Override
    public Link parse(Element xmlElement,
            IVariablesStorage variablesStorage) throws Exception {

        if (xmlElement == null) {
            String message = "Попытка распознавания звена загрузки переменной"
                    + " из нулевого(не существующего) елемента...";
            throw new IllegalArgumentException(message);
        }
        if (!this.hasAttribute(xmlElement, "VariableName", (String[]) null)) {
            String message = "У звена загрузки переменной не задан атрибут"
                    + " VariableName, отвечающий за имя переменной из которой"
                    + " будет загружаться сигнал"
                    + "(<LoadVariable VariableName='v1'>..."
                    + "</LoadVariable>)...";
            throw new InvalidSchemeFormatException(message);
        }
        String variableName = xmlElement.getAttribute("VariableName");
        if (variableName.isEmpty()) {
            String message = "Атрибут 'VariableName' у елемента '"
                    + xmlElement.getNodeName()
                    + "' имеет не верный формат...\n"
                    + "Атрибут 'VariableName' должен содержать имя переменной"
                    + " из которой будет загружаться сигнал"
                    + "(<LoadVariable VariableName='v1'>...</LoadVariable>)...";
            throw new InvalidSchemeFormatException(message);
        }

        LoadVariable newLoadLink = new LoadVariable();
        newLoadLink.setVariableName(variableName);
        newLoadLink.setStorage(variablesStorage);
        return newLoadLink;
    }
}
