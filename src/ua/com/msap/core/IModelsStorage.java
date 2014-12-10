package ua.com.msap.core;

import java.util.Set;

/**
 * This interface is declares functions for save and load models by name. 
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public interface IModelsStorage {
        Set getModelsNames();
        Link getModel(String name);
        Link getModel(int pos);
        void addModel(String name, Link model);
        int getCountOfModels();
        void resetAllModels();
        void stepAllModels() throws Exception;
}

