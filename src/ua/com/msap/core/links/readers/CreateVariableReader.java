package ua.com.msap.core.links.readers;

import ua.com.msap.core.exceptions.InvalidSchemeFormatException;
import org.w3c.dom.Element;
import ua.com.msap.core.LinkReader;
import ua.com.msap.core.IVariablesStorage;
import ua.com.msap.core.Link;
import ua.com.msap.core.links.extended.CreateVariable;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class CreateVariableReader extends LinkReader {

    @Override
      public Link parse(Element xmlElement,
            IVariablesStorage variablesStorage) throws Exception{
                if (xmlElement == null) {
            String message = "Попытка распознавания звена 'Создания/ввода"
                    + " переменной' из нулевого(не существующего) елемента...";
            throw new IllegalArgumentException(message);
        }
        if (!this.hasAttribute(xmlElement, "VariableName", (String[]) null)) {
            String message = "У звена 'создания переменной' не задан атрибут "
                    + "VariableName, отвечающий за имя переменной через "
                    + "которую в схему будет передаваться новый сигнал"
                    + "(<CreateVariable VariableName='v1'>..."
                    + "</CreateVariable>)...";
            throw new InvalidSchemeFormatException(message);
        }
        String variableName = xmlElement.getAttribute("VariableName");
        if (variableName.isEmpty()) {
            String message = "Атрибут 'VariableName' у елемента '"
                    + xmlElement.getNodeName()
                    + "' имеет не верный формат...\nАтрибут 'VariableName'"
                    + " должен содержать имя переменной через которую в схему"
                    + " будет передаваться новый сигнал "
                    + "(<CreateVariable VariableName='v1'>..."
                    + "</CreateVariable>)... ";
            throw new InvalidSchemeFormatException(message);
        }
        if (!this.hasAttribute(xmlElement, "DefaultValue", (String[]) null)) {
            String message = "Атрибут 'DefaultValue' у елемента '"
                    + xmlElement.getNodeName()
                    + "' имеет не верный формат...\n"
                    + "Атрибут 'DefaultValue'  должен содержать "
                    + "числовое значение по умолчанию, которое будет присвоено"
                    + " переменной при первом ее создании, если такая "
                    + "переменная была ранее создана другим звеном, то "
                    + "значение по умолчанию не присваиваеться"
                    + "(<CreateVariable DefaultValue='0'>..."
                    + "</CreateVariable>)...";
            throw new InvalidSchemeFormatException(message);
        }
        String defaultValueStr = xmlElement.getAttribute("DefaultValue");
        double defaultValue = 0;
        try {
            defaultValue = Double.parseDouble(defaultValueStr);
        } catch (Exception ex) {
            String message = "Атрибут  'DefaultValue' звена '"
                    + xmlElement.getNodeName()
                    + "' имеет не верный формат ["
                    + xmlElement.getNodeValue()
                    + "]...";
            throw new InvalidSchemeFormatException(message);
        }

        CreateVariable newCreate = new CreateVariable();
        newCreate.setVariableName(variableName);
        newCreate.setDefaultValue(defaultValue);
        newCreate.addToStorageIfNotExists(variablesStorage);
        return newCreate;
    }
    
}
