package ua.com.msap.core.links.extended;

import ua.com.msap.core.exceptions.NotInitializedVariableException;
import ua.com.msap.core.*;
import ua.com.msap.core.Link;
import ua.com.msap.core.links.readers.LoadVariableReader;

/**
 * LoadVariable node is being used for load a variable from connected 
 * repository, and routing the variable value to the output.
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class LoadVariable extends Link {

    private String variableName = "";
    private IVariablesStorage storage = null;

    /**
     * Gets the name of the variable from which the signal will be loaded.
     * @return the name of the variable
     */
    public String getVariableName() {
        return this.variableName;
    }

    /**
     * Sets the name of the variable from which the signal will be loaded.
     * @param name - new name of the variable
     * @throws IllegalArgumentException if newVariableName is null or empty
     */
    public void setVariableName(String name) {
        if (name == null || name.trim().isEmpty()) {
            String message = "Попытка присвоения имени переменной нулевого или"
                    + " пустого значения(в блоке LOAD)...";
            throw new IllegalArgumentException(message);
        }
        this.variableName = name;
    }

    /**
     * Sets the storage from which the variable will be loaded and routed to 
     * the output (after each call of step())
     * @param variablesStorage storage to add variable 
     * @throws IllegalStateException if VariableName does not set
     * @throws IllegalArgumentException if variablesStorage is null
     */
    public void setStorage(IVariablesStorage variablesStorage)
            throws NotInitializedVariableException {
        if (this.variableName == null
                || this.variableName.trim().isEmpty()) {
            String message = "На момент связывания с хранилищем, блок LOAD "
                    + "должен уже содержать имя переменной...";
            throw new IllegalStateException(message);
        }
        if (variablesStorage == null) {
            String message = "Попытка связать блок LOAD с нулевым(не "
                    + "существующим) хранилищем...";
            throw new IllegalArgumentException(message);
        }
        this.storage = variablesStorage;
    }

    @Override
    public void step() throws Exception {
        super.step();
        if (this.variableName == null
                || this.variableName.trim().isEmpty()) {
            String message = "В блоке LOAD не задано имя переменной...";
            throw new IllegalStateException(message);
        }
        if (this.storage == null) {
            String message = "В блоке LOAD не задано хранилище переменных, "
                    + "невозможно сохранить значение переменной '"
                    + this.variableName + "'...";
            throw new IllegalStateException(message);
        }
        Variable variable = this.storage.getVariableByName(this.variableName);
        if (variable == null) {
            String message = "Блок LOAD, не нашел в хранилище, "
                    + "переменную с именем '" + this.variableName + "'...";
            throw new NotInitializedVariableException(message);
        }
        fY = variable.getValue();
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
        LoadVariable loadLink = (LoadVariable) otherObject;
        //Check that fields have the same values
        return this.variableName.equals(loadLink.variableName)
                && this.storage.equals(loadLink.storage);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.variableName != null
                ? this.variableName.hashCode() : 0);
        hash = 79 * hash + (this.storage != null
                ? this.storage.hashCode() : 0);
        return hash;
    }

    @Override
    public LinkReader getLinkReader() {
        return (LinkReader) new LoadVariableReader();
    }
}
