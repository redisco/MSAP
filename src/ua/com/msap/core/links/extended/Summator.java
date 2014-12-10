package ua.com.msap.core.links.extended;

import java.util.HashMap;
import java.util.Map.Entry;
import ua.com.msap.core.LinkReader;
import ua.com.msap.core.Link;
import ua.com.msap.core.links.readers.SummatorReader;

/**
 * This class is used for implementation of the summator of a signals from 
 * a different nodes. Each branch of the summator is an object of class Link.
 * Signals from the branches are added to the signal from the main input of 
 * the summator.
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class Summator extends Link {

    private boolean sign = true;
    private HashMap<Link, Boolean> branches = new HashMap<Link, Boolean>();

    public Summator() {
        super();
    }

    /**
     * Gets the sign of the main input of the summator.
     * @return the sign of the main input of the summator.
     */
    public boolean getSign() {
        return this.sign;
    }

    /**
     * Sets the sign of the main input of the summator.
     * @param newSign the new sign of the main input of the summator.
     */
    public void setSign(boolean newSign) {
        this.sign = newSign;
    }

    /**
     * Gets the sign of the main input of the summator.
     * @return the sign of the main input of the summator.
     */
    public int getCountOfBranches() {
        return branches.size();
    }

    /**
     * Add new branch to the summator with defined sign.
     * @param branch - the link that corresponds to the branch.
     * @param sign - the sign of the branch.
     */
    public void addBranch(Link branch, boolean sign) {
        if (branch == null) {
            String message = "Попытка добавления не существующего звена"
                    + " в сумматор...";
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
        double tempY = (this.sign ? this.fX : -this.fX);

        for (Entry<Link, Boolean> branch : this.branches.entrySet()) {
            Link branchZveno = branch.getKey();
            Boolean branchSign = branch.getValue();
            branchZveno.step();
            tempY += (branchSign.booleanValue() ? branchZveno.getY() : -branchZveno.getY());
        }
        fY = tempY;
    }

    @Override
    public String toString() {
        String s = super.toString();
        int counter = 1;
        for (Entry<Link, Boolean> branch : this.branches.entrySet()) {
            Link branchLink = branch.getKey();
            String branchSign = (branch.getValue().booleanValue() ? "+" : "-");
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
        Summator summatorLink = (Summator) otherObject;
        //Check that fields have the same values
        return this.branches.equals(summatorLink.branches)
                && this.sign == summatorLink.sign;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (this.sign ? 1 : 0);
        hash = 73 * hash + (this.branches != null ? this.branches.hashCode() : 0);
        return hash;
    }

    @Override
    public LinkReader getLinkReader() {
        return (LinkReader) new SummatorReader();
    }
}
