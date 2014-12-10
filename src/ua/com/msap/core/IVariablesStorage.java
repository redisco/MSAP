package ua.com.msap.core;

import java.util.Set;

/**
 * This interface is declares functions for save and load variables by name.
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public interface IVariablesStorage {
        Set getVariablesNames();
        Variable getVariableByName(String name);
        void addVariableIfNotExists(String name, Variable variable);
        void removeVariable(String name);
        void removeAllVariables();
}
