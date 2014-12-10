package ua.com.msap.core;

/**
 * Wrapper class for storing double value as object,
 * in schema storage or in collection
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class Variable {
    private double value = 0;
    
    public double getValue()
    {
        return this.value;
    }
    public void setValue(double value)
    {
        this.value = value;
    }
    
    @Override
    public boolean equals(Object otherObject){
        //Quick check, if objects are equal
        if(this == otherObject){
            return true;
        }
        //If otherObject is null, then return false
        if(otherObject == null){
            return false;
        }
        //If object classes are not identical, then objects are not equal
        if(this.getClass() != otherObject.getClass()){
            return false;
        }
        //Now we knows that that this object of class Variable
        Variable otherVariable = (Variable)otherObject;
        //Checks whether the fields are stored objects 
        //with identical values
        return this.value == otherVariable.value;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (int) new Double(this.value).hashCode();
        return hash;
    }
}
