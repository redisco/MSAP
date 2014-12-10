package ua.com.msap.core.links.extended;

import ua.com.msap.core.exceptions.NotInitializedVariableException;
import ua.com.msap.core.*;
import ua.com.msap.core.Link;
import ua.com.msap.core.links.readers.CreateVariableReader;

/**
 * CreateVariable node used for creating variable in a storage, and 
 * routing the variable value to the output of the node.
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class CreateVariable extends Link {

    private String variableName = "";
    private double defaultValue = 0;
    private IVariablesStorage storage = null;

    /**
     * Gets the name of the variable which will be created & added to a storage.
     * @return the name of the variable
     */
    public String getVariableName() {
        return this.variableName;
    }

    /**
     * Sets the name of the variable which will be created & added to a storage.
     * After setting of the new variable name, always call 
     * addToStorageIfNotExists(ISchemaStorage schemaStorage).
     * @param name - new name of the variable
     * @throws IllegalArgumentException if newVariableName is null or empty
     */
    public void setVariableName(String name) {
        if (name == null || name.trim().isEmpty()) {
            String message = "Попытка присвоения имени переменной нулевого "
                    + "или пустого значения(в блоке CREATE)...";
            throw new IllegalArgumentException(message);
        }
        this.variableName = name;
    }

    /**
     * Gets the value which will be set to the variable when its added to 
     * the storage (by using the 
     * [addToStorageIfNotExists(ISchemaStorage schemaStorage)]) and when the 
     * variable with the same name is not exists in the storage.
     * @return default value for initializing the variable
     */
    public double getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Sets the value which will be set to the variable when its added to 
     * the storage (by using the 
     * [addToStorageIfNotExists(ISchemaStorage schemaStorage)]) and when the 
     * variable with the same name is not exists in the storage.
     * @param newDefaulValue new default value for initializing the variable
     */
    public void setDefaultValue(double value) {
        this.defaultValue = value;
    }

    /**
     * If a variable with the same name already exists in the repository, this 
     * function throw IllegalStateException. If a variable with the same name 
     * does not exist in the repository, this function will create a new 
     * variable with predefined name, initializes it to the default value and 
     * insert it into the repository.
     * @param variablesStorage storage to add variable 
     * @throws IllegalStateException if VariableName does not set or if variable
     * with the same name already exists in the storage
     * @throws IllegalArgumentException if variablesStorage is null
     */
    public void addToStorageIfNotExists(IVariablesStorage variablesStorage) {
        if (this.variableName == null || this.variableName.trim().isEmpty()) {
            String message = "На момент связывания с хранилищем,"
                    + " блок CREATE должен уже содержать имя переменной...";
            throw new IllegalStateException(message);
        }
        if (variablesStorage == null) {
            String message = "Попытка связать блок CREATE"
                    + " с нулевым(не существующим) хранилищем...";
            throw new IllegalArgumentException(message);
        }
        this.storage = variablesStorage;
        Variable variable = this.storage.getVariableByName(variableName);
        if (variable == null) {
            variable = new Variable();
            this.storage.addVariableIfNotExists(variableName, variable);
            variable.setValue(this.defaultValue);
        } else {
            String message = "Невозможно создать в хранилищи переменную '"
                    + this.variableName + "', такая переменная уже существует. "
                    + "Блок CREATE используеться для ввода в схему переменной "
                    + "из вне, на момент чтения блока такой переменной еще не "
                    + "должно быть в хранилищие. Возможно до этого в схеме "
                    + "такая переменная уже используеться в блоках INPUT/SAVE. "
                    + "Если Вам нужно просто загрузить сигнал из переменной "
                    + "используемой в схеме, воспользуйтесь блоком LOAD.";
            throw new IllegalStateException(message);
        }
    }

    @Override
    public void step() throws Exception {
        super.step();
        if (this.variableName == null
                || this.variableName.trim().isEmpty()) {
            String message = "В блоке CREATE не задано имя переменной...";
            throw new IllegalStateException(message);
        }
        if (this.storage == null) {
            String message = "В блоке CREATE не задано хранилище переменных, "
                    + "невозможно сохранить значение переменной '"
                    + this.variableName + "'...";
            throw new IllegalStateException(message);
        }
        Variable variable = this.storage.getVariableByName(this.variableName);
        if (variable == null) {
            String message = "Блок CREATE, не нашел в хранилище, "
                    + "переменную с именем '" + this.variableName + "'...";
            throw new NotInitializedVariableException(message);
        }
        if(this.variableName.equals("zada")){
            fY = variable.getValue();
        }else
        fY = variable.getValue();
    }

    @Override
    public String toString() {
        String s = super.toString();
        s += ",VariableName=" + variableName;
        return s;
    }

    @Override
    public void reset() {
        super.reset();
        if (this.variableName == null || this.variableName.trim().isEmpty()) {
            String message = "На момент связывания с хранилищем,"
                    + " блок CREATE должен уже содержать имя переменной...";
            throw new IllegalStateException(message);
        }
        if (this.storage == null) {
            String message = "Попытка связаться"
                    + " с нулевым(не существующим) хранилищем, "
                    + "при сбросе блока CREATE...";
            throw new IllegalArgumentException(message);
        }
        Variable variable = this.storage.getVariableByName(variableName);
        if (variable == null)  {
            String message = "Не удалось найти в хранилищи переменную '"
                    + this.variableName + "'"
                    + "при сбросе блока CREATE...";
            throw new IllegalStateException(message);
        }
        variable.setValue(this.defaultValue);
    }
        
    @Override
    public boolean equals(Object otherObject) {
        if (super.equals(otherObject) == false) {
            return false;
        }
        //Function super.equals() has already checked that otherObject has 
        //the same class.
        CreateVariable createLink = (CreateVariable) otherObject;
        //Check that fields have the same values
        return this.variableName.equals(createLink.variableName)
                && this.storage.equals(createLink.storage);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.variableName != null ? this.variableName.hashCode() : 0);
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.defaultValue)
                ^ (Double.doubleToLongBits(this.defaultValue) >>> 32));
        hash = 59 * hash + (this.storage != null ? this.storage.hashCode() : 0);
        return hash;
    }

    @Override
    public LinkReader getLinkReader() {
        return (LinkReader) new CreateVariableReader();
    }
}
