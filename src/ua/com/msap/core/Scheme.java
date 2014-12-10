package ua.com.msap.core;

import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class Scheme implements IVariablesStorage, IModelsStorage {

    LinkedHashMap<String, Link> modelsStorage = new LinkedHashMap<String, Link>();
    HashMap<String, Variable> variablesStorage = new HashMap<String, Variable>();

    @Override
    public Set getModelsNames() {
        HashSet<String> names = new HashSet<String>();
        for (String modelName : this.modelsStorage.keySet()) {
            names.add(modelName);
        }
        return names;
    }

    @Override
    public Link getModel(String name) {
        if (!this.modelsStorage.keySet().contains(name)) {
            return null;
        }
        return this.modelsStorage.get(name);
    }
    @Override
    public Link getModel(int pos) {
        
        if (this.modelsStorage.values().size() > pos) {
            return (Link)this.modelsStorage.values().toArray()[pos];
        }
        return null;
    }
    @Override
    public void addModel(String name, Link model) {
        this.modelsStorage.put(name, model);
    }

    @Override
    public int getCountOfModels() {
        return this.modelsStorage.size();
    }

    @Override
    public Set getVariablesNames() {
        HashSet<String> names = new HashSet<String>();
        for (String modelName : this.variablesStorage.keySet()) {
            names.add(modelName);
        }
        return names;
    }

    @Override
    public Variable getVariableByName(String name) {
        if (!this.variablesStorage.keySet().contains(name)) {
            return null;
        }
        return this.variablesStorage.get(name);
    }

    @Override
    public void addVariableIfNotExists(String name, Variable variable) {
        if (!this.variablesStorage.keySet().contains(name)) {
            this.variablesStorage.put(name, variable);
        }
    }

    @Override
    public void removeVariable(String name) {
        if (this.variablesStorage.keySet().contains(name)) {
            this.variablesStorage.remove(name);
        }
    }

    @Override
    public void removeAllVariables() {
        this.variablesStorage.clear();
    }
    
    @Override
    public void resetAllModels(){
        for(Link model: this.modelsStorage.values()){
            model.reset();
        }
    }
    
    @Override
    public void stepAllModels() throws Exception{
        for(Link model: this.modelsStorage.values()){
            model.step();
        }
    }
}
