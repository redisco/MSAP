package ua.com.msap.core.links.extended;

import java.util.HashMap;
import java.util.Map;
import ua.com.msap.core.Link;
import ua.com.msap.core.LinkReader;
import ua.com.msap.core.links.readers.MultReader;

/**
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class Mult extends Link{
    
    private boolean sign = true;
    private HashMap<Link, Boolean> branches = new HashMap<Link, Boolean>();
    
    public Mult() {
        super();
    }
    
    public boolean getSign() {
        return this.sign;
    }
    
    public void setSign(boolean newSign) {
        this.sign = newSign;
    }
    
    public int getCountOfBranches() {
        return branches.size();
    }
    
    public void addBranch(Link branch, boolean sign) {
        if (branch == null) {
            String message = "Попытка добавления не существующего звена"
                    + " в блок умножения...";
            throw new IllegalArgumentException(message);
        }
        branches.put(branch, sign);
    }
    
    public int getLength(){
        return this.branches.size();
    }
    
    @Override
    public void reset() {
        super.reset();
        for (Link branch : this.branches.keySet()) {
            branch.reset();
        }
    }
    
    @Override
    public void step() throws Exception {
        super.step();
        double tempY = 1.0;
        if (this.fX!=0.0) tempY=(this.sign ? this.fX : -this.fX);
        
        for (Map.Entry<Link, Boolean> branch : this.branches.entrySet()) {
            Link branchZveno = branch.getKey();
            Boolean branchSign = branch.getValue();
            branchZveno.step();
            tempY *= (branchSign.booleanValue() ? branchZveno.getY() : (1/-branchZveno.getY()));
        }
        fY = tempY;
    }
    
    @Override
    public String toString() {
        String s = super.toString();
        int counter = 1;
        for (Map.Entry<Link, Boolean> branch : this.branches.entrySet()) {
            Link branchLink = branch.getKey();
            String branchSign = (branch.getValue().booleanValue() ? "*" : "/");
            s += "[" + branchSign + " | " + branchLink.toString() + "]";
            if (this.branches.size() > 0 && counter != this.branches.size()) {
                s += ", ";
            }
        }
        return s;
    }
    
    @Override
    public boolean equals(Object otherObject) {
        if (super.equals(otherObject) == false) {
            return false;
        }
        //Function super.equals() has already checked that otherObject has 
        //the same class.
        Mult multLink = (Mult) otherObject;
        //Check that fields have the same values
        return this.branches.equals(multLink.branches)
                && this.sign == multLink.sign;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 81 * hash + (this.sign ? 1 : 0);
        hash = 81 * hash + (this.branches != null ? this.branches.hashCode() : 0);
        return hash;
    }
    
    @Override
    public LinkReader getLinkReader() {
        return (LinkReader) new MultReader();
    }
    
    
}
