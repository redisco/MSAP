package ua.com.msap.core.links;

import ua.com.msap.core.exceptions.NotInitializedVariableException;
import ua.com.msap.core.*;
import ua.com.msap.core.links.readers.PReader;

/**
 * Class of proportional link.
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick 
 * @see <a href="http://ait.cs.nstu.ru/tau/book/Sod32.htm">Proportional link</a>
 */
public class P extends Link {

    private Variable k;

    public double getK() throws NotInitializedVariableException {
        if (k == null) {
            throw new NotInitializedVariableException("K");
        }
        return k.getValue();
    }

    public void setK(double value) throws NotInitializedVariableException {
        if (k == null) {
            throw new NotInitializedVariableException("K");
        }
        k.setValue(value);
    }

    public void initK(Variable variable) {
        k = variable;
    }

    @Override
    public void step() throws Exception {
        super.step();
        fY = this.getK() * fX;
    }

    @Override
    public String toString() {
        String s = super.toString();
        try {
            s += ",K=" + this.getK();
        } catch (NotInitializedVariableException ex) {
            s += ",K='Not initialized'";
        }
        return s;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (super.equals(otherObject) == false) {
            return false;
        }
        //Function super.equals() checks if
        //otherObject belongs to current class
        P p = (P) otherObject;
        return this.k == p.k;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.k != null ? this.k.hashCode() : 0);
        return hash;
    }

    @Override
    public LinkReader getLinkReader() {
        return (LinkReader) new PReader();
    }
}
