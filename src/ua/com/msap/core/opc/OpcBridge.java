package ua.com.msap.core.opc;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.Executors;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.opc.dcom.da.OPCSERVERSTATE;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.Group;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.openscada.opc.lib.da.Server;
import ua.com.msap.core.IVariablesStorage;
import ua.com.msap.core.Variable;
import ua.com.msap.core.exceptions.InvalidConfigFormatException;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class OpcBridge {

    private ConnectionInformation connectionInfo = new ConnectionInformation();
    private IVariablesStorage variablesStorage = null;
    private Properties config = null;
    private Properties readTags = null;
    private Properties writeTags = null;
    private Server server = null;
    private HashMap<String, Item> readItems = new HashMap<String, Item>();
    private HashMap<String, Item> writeItems = new HashMap<String, Item>();
    private DecimalFormat formater = null;
    private static final Class opcTagType = double.class;
    
    public OpcBridge(IVariablesStorage variablesStorage, Properties config,
            Properties readTags, Properties writeTags)
            throws Exception {
        this.initDecimalFormat();
        
        this.variablesStorage = variablesStorage;
        this.config = config;
        this.readTags = readTags;
        this.writeTags = writeTags;

        String host = this.config.getProperty("host");
        String domain = this.config.getProperty("domain");
        String user = this.config.getProperty("user");
        String pass = this.config.getProperty("pass");
        String clsid = this.config.getProperty("clsid");

        if (host == null || host.length() == 0) {
            String message = "В файле настроек OPC соединения не укзано поле "
                    + "'host'...";
            throw new InvalidConfigFormatException(message);
        }
        if (domain == null || domain.length() == 0) {
            String message = "В файле настроек OPC соединения не укзано поле "
                    + "'domain'...";
            throw new InvalidConfigFormatException(message);
        }
        if (user == null || user.length() == 0) {
            String message = "В файле настроек OPC соединения не укзано поле "
                    + "'user'...";
            throw new InvalidConfigFormatException(message);
        }
        if (pass == null || pass.length() == 0) {
            String message = "В файле настроек OPC соединения не укзано поле "
                    + "'pass'...";
            throw new InvalidConfigFormatException(message);
        }
        if (clsid == null || clsid.length() == 0) {
            String message = "В файле настроек OPC соединения не укзано поле "
                    + "'clsid'...";
            throw new InvalidConfigFormatException(message);
        }

        this.connectionInfo.setHost(host);
        this.connectionInfo.setDomain(domain);
        this.connectionInfo.setUser(user);
        this.connectionInfo.setPassword(pass);
        this.connectionInfo.setClsid(clsid);

        this.validate();

    }

    public void disconnect() {
        this.server.disconnect();
    }

    private void reconnect() throws Exception {
        try {
            this.server = new Server(connectionInfo,
                    Executors.newSingleThreadScheduledExecutor());
        } catch (Exception ex) {
            String message = "Не удалось связаться с OPC сервером. "
                    + connectionInfo.toString();
            throw new Exception(message);
        }
        // connect to server
        server.connect();

        
        Group group = null;
        Enumeration keyEnumeration = null;
        

        // Add a new group
        group = server.addGroup("outputs");
        keyEnumeration = this.writeTags.keys();
        while (keyEnumeration.hasMoreElements()) {
            String varName = (String) keyEnumeration.nextElement();
            String tagName = this.writeTags.getProperty(varName, "");
            Item item = null;
            try {
                item = group.addItem(tagName);
            } catch (Exception ex) {
                String message = "Не удалось связаться с OPC тегом '"
                        + tagName
                        + "', который соответствует переменной '"
                        + varName
                        + "' текущей XML схемы";
                throw new Exception(message+" "+ex.toString());
            }
            writeItems.put(varName, item);
        }
        
        //this.writeTagsTest();
        
        // Add a new group
        group = server.addGroup("inputs");
        keyEnumeration = readTags.keys();
        while (keyEnumeration.hasMoreElements()) {
            String varName = (String) keyEnumeration.nextElement();
            String tagName = readTags.getProperty(varName, "");

            Item item = null;
            try {
                item = group.addItem(tagName);
                //System.out.println(item.toString());
            } catch (Exception ex) {
                String message = "Не удалось связаться с OPC тегом '"
                        + tagName
                        + "', который соответствует переменной '"
                        + varName
                        + "' текущей XML схемы";
                throw new Exception(message);
            }
            readItems.put(varName, item);
        }
    }

    private void validate() throws Exception {
        validateRead();
        validateWrite();
    }

    private void validateRead() throws Exception {
        Enumeration keyEnumeration = this.readTags.keys();
        while (keyEnumeration.hasMoreElements()) {
            String varName = (String) keyEnumeration.nextElement();
            String tagName = this.readTags.getProperty(varName, "");
            if (tagName.isEmpty()) {
                String message = "Переменной с именем '"
                        + varName
                        + "', в соответствие был поставлен OPC тег(для чтения) "
                        + "с именем в виде пустой строки...";
                throw new InvalidConfigFormatException(message);
            }
            if (this.variablesStorage.getVariableByName(varName) == null) {
                String message = "Переменная с именем '"
                        + varName
                        + "', не найдена в XML схеме, но для нее был "
                        + "определено соответствие с OPC тегом(для чтения) '"
                        + tagName
                        + "'...";
                throw new InvalidConfigFormatException(message);
            }
        }
    }

    private void validateWrite() throws Exception {
        Enumeration keyEnumeration = this.writeTags.keys();
        while (keyEnumeration.hasMoreElements()) {
            String varName = (String) keyEnumeration.nextElement();
            String tagName = this.writeTags.getProperty(varName, "");
            if (tagName.isEmpty()) {
                String message = "Переменной с именем '"
                        + varName
                        + "', в соответствие был поставлен OPC тег(для записи) "
                        + "с именем в виде пустой строки...";
                throw new InvalidConfigFormatException(message);
            }
            if (this.variablesStorage.getVariableByName(varName) == null) {
                String message = "Переменная с именем '"
                        + varName
                        + "', не найдена в XML схеме, но для нее был "
                        + "определено соответствие с OPC тегом(для записи) '"
                        + tagName
                        + "'...";
                throw new InvalidConfigFormatException(message);
            }
        }
    }
    private void initDecimalFormat(){
        this.formater =  new DecimalFormat("#.####");
        DecimalFormatSymbols custom=new DecimalFormatSymbols();
        custom.setDecimalSeparator('.');
        this.formater.setDecimalFormatSymbols(custom);
        this.formater.setGroupingUsed(false);
    }
    public void readTags() throws Exception {
        if (this.server == null
                || !this.server.getServerState().getServerState().equals(
                OPCSERVERSTATE.OPC_STATUS_RUNNING)) {
            this.reconnect();
        }
        if (this.readItems.isEmpty()) {
            return;
        }
        String readMessage = "Read : ";
        for (Entry<String, Item> varItem : readItems.entrySet()) {
            ItemState itemState = varItem.getValue().read(false);
            Variable var = variablesStorage.getVariableByName(varItem.getKey());
            if(var == null){
                String message = "Переменная с именем '"
                        +varItem.getKey()
                        +"' не найдена в хранилише переменных...";
                throw new Exception(message);
            }
            JIVariant v = itemState.getValue();
            //System.out.println(v.toString());
            double newValue = 0;
            if(opcTagType == double.class){
               double doubleValue = v.getObjectAsDouble();
               String strValue =  this.formater.format(doubleValue);
               newValue = Double.parseDouble(strValue);
               var.setValue((double)newValue);
            }
            if(opcTagType == float.class){
                float floatValue = v.getObjectAsFloat();
                String strValue = this.formater.format(floatValue);
                newValue = Float.parseFloat(strValue);
                var.setValue(newValue);
            }
            
            readMessage += varItem.getKey()
                    +"<-["+newValue+"];";
        }
        System.out.println(readMessage);
    }
    
    public void writeTags() throws Exception {
        if (this.server == null
                || !this.server.getServerState().getServerState().equals(
                OPCSERVERSTATE.OPC_STATUS_RUNNING)) {
            this.reconnect();
        }
        if (this.writeItems.isEmpty()) {
            return;
        }
        String writeMessage = "Write : ";
        for (Entry<String, Item> varItem : writeItems.entrySet()) {
            double varValue = this.variablesStorage.getVariableByName(
                    varItem.getKey()).getValue();  
            
            if(opcTagType == double.class){
               String strValue = this.formater.format(varValue);
               double doubleValue = Double.parseDouble(strValue);
               //doubleValue = 31; 
               varItem.getValue().write(new JIVariant(doubleValue));
            }
            if(opcTagType == float.class){
                String strValue =  this.formater.format(varValue);
                float floatValue = Float.parseFloat(strValue);
                //floatValue = 31; 
                varItem.getValue().write(new JIVariant(floatValue));
            }
            
            writeMessage += varItem.getKey() +"->["+varValue+"];";
        }
        System.out.println(writeMessage);
    }
}
