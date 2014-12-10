package ua.com.msap.core.links.extended;

import ua.com.msap.core.LinkReader;
import ua.com.msap.core.IVariablesStorage;
import ua.com.msap.core.Variable;
import ua.com.msap.core.Link;
import ua.com.msap.core.links.readers.SaveVariableReader;

/**
 * SaveVariable node used for saving input signal in variable, and 
 * routing the input to the output.
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class SaveVariable extends Link {

    private String variableName = "";
    private double defaultValue = 0;
    private IVariablesStorage storage = null;

    /**
     * Gets the name of the variable in which to record the signal from the input.
     * @return the name of the variable
     */
    public String getVariableName() {
        return variableName;
    }

    /**
     * Sets the name of the variable in which to record the signal from the input.
     * After setting of the new variable name, always call 
     * addToStorageIfNotExists(ISchemaStorage schemaStorage).
     * @param name - new name of the variable
     * @throws IllegalArgumentException if newVariableName is null or empty
     */
    public void setVariableName(String name)
            throws NullPointerException {
        if (name == null || name.trim().isEmpty()) {
            String message = "Попытка присвоения имени переменной нулевого"
                    + " или пустого значения(в блоке SAVE)...";
            throw new IllegalArgumentException(message);
        }
        variableName = name;
    }

    /**
     * Gets the value which will be set to the variable when its added to 
     * the storage (by using the 
     * [addToStorageIfNotExists(ISchemaStorage schemaStorage)]) and when the 
     * variable with the same name is not exists in the storage.
     * @return default value for initializing the variable
     */
    public double getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the value which will be set to the variable when its added to 
     * the storage (by using the 
     * [addToStorageIfNotExists(ISchemaStorage schemaStorage)]) and when the 
     * variable with the same name is not exists in the storage.
     * @param newDefaulValue new default value for initializing the variable
     */
    public void setDefaultValue(double newDefaulValue) {
        defaultValue = newDefaulValue;
    }

    /**
     * If a variable with the same name already exists in the repository, this 
     * function will  bound current object to the variable from repository. If 
     * a variable with the same name does not exist in the repository, this 
     * function will create a new variable with predefined name, initializes it 
     * to the default value and insert it into the repository.
     * @param variablesStorage storage to add variable 
     * @throws IllegalStateException if VariableName does not set
     * @throws IllegalArgumentException if variablesStorage is null
     */
    public void addToStorageIfNotExists(IVariablesStorage variablesStorage) {
        if (this.variableName == null || this.variableName.trim().isEmpty()) {
            String message = "На момент связывания с хранилищем,"
                    + " блок SAVE должен уже содержать имя переменной...";
            throw new IllegalStateException(message);
        }
        if (variablesStorage == null) {
            String message = "Попытка связать блок SAVE"
                    + " с нулевым(не существующим) хранилищем...";
            throw new IllegalArgumentException(message);
        }
        this.storage = variablesStorage;
        Variable variable = this.storage.getVariableByName(variableName);
        if (variable == null) {
            variable = new Variable();
            this.storage.addVariableIfNotExists(variableName, variable);
            variable.setValue(this.defaultValue);
        }
    }

    @Override
    public void setX(double newX) {
        super.setX(newX);
        if (this.variableName == null || this.variableName.trim().isEmpty()) {
            String message = "В блоке SAVE не задано имя переменной...";
            throw new IllegalStateException(message);
        }
        if (this.storage == null) {
            String message = "В блоке SAVE не задано хранилище переменных,"
                    + " невозможно сохранить значение переменной '"
                    + this.variableName + "'...";
            throw new IllegalStateException(message);
        }
        Variable variable = this.storage.getVariableByName(variableName);
        if (variable == null) {
            variable = new Variable();
            this.storage.addVariableIfNotExists(variableName, variable);
        }
        variable.setValue(newX);
        fY = fX;
    }

    @Override
    public String toString() {
        String s = super.toString();
        s += ",VariableName=" + variableName;
        return s;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (super.equals(otherObject) == false) {
            return false;
        }
        //Function super.equals() has already checked that otherObject has 
        //the same class.
        SaveVariable saveLink = (SaveVariable) otherObject;
        //Check that fields have the same values
        return this.variableName.equals(saveLink.variableName)
                && this.storage.equals(saveLink.storage)
                && this.defaultValue == saveLink.defaultValue;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.variableName != null
                ? this.variableName.hashCode() : 0);
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.defaultValue)
                ^ (Double.doubleToLongBits(this.defaultValue) >>> 32));
        hash = 17 * hash + (this.storage != null ? this.storage.hashCode() : 0);
        return hash;
    }

    @Override
    public LinkReader getLinkReader() {
        return (LinkReader) new SaveVariableReader();
    }
}
