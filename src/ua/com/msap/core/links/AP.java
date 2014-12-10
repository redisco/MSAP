package ua.com.msap.core.links;

import ua.com.msap.core.exceptions.NotInitializedVariableException;
import ua.com.msap.core.*;
import ua.com.msap.core.links.readers.APReader;

/**
 * Class of aperiodic link.
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 * @see <a href="http://ait.cs.nstu.ru/tau/book/Sod35.htm">Aperiodical link</a>
 */
public class AP extends Link {

    protected Variable k;
    protected Variable t;
    private double previousY = 0;
    private long timeAterNewX = 0;

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

    public double getT() throws NotInitializedVariableException {
        if (t == null) {
            throw new NotInitializedVariableException("T");
        }
        return t.getValue();
    }

    public void setT(double value) throws NotInitializedVariableException {
        if (t == null) {
            throw new NotInitializedVariableException("T");
        }
        if (value > 0) {
            t.setValue(value);
        } else {
            t.setValue(Double.POSITIVE_INFINITY);
        }
    }

    public void initK(Variable variable) {
        k = variable;
    }

    public void initT(Variable variable) {
        t = variable;
    }

    @Override
    public void reset() {
        super.reset();
        previousY = 0;
        timeAterNewX = 0;
    }

    @Override
    public void setX(double newX) {
        if (this.fX != newX) {
            previousY = fY;
            timeAterNewX = 0;
            super.setX(newX);
        }
    }

    @Override
    public void step() throws Exception {
        super.step();
        ++timeAterNewX;
        double firstPart = fX * this.getK() * 
                (1 - Math.exp(-timeAterNewX / this.getT()));
        double secondPart = previousY * Math.exp(-timeAterNewX / this.getT());
        fY = firstPart + secondPart;
    }

    @Override
    public String toString() {
        String s = super.toString();
        try {
            s += ",K=" + this.getK();
        } catch (NotInitializedVariableException ex) {
            s += ",K='Not initialized'";
        }
        try {
            s += ",T=" + this.getT();
        } catch (NotInitializedVariableException ex) {
            s += ",T='Not initialized'";
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
        AP apLink = (AP) otherObject;
        return this.k == apLink.k
                && this.t == apLink.t
                && this.previousY == apLink.previousY
                && this.timeAterNewX == apLink.timeAterNewX;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.k != null ? this.k.hashCode() : 0);
        hash = 59 * hash + (this.t != null ? this.t.hashCode() : 0);
        hash = 59 * hash + (int) new Double(this.previousY).hashCode();
        hash = 59 * hash + (int) new Long(this.timeAterNewX).hashCode();
        return hash;
    }

    @Override
    public LinkReader getLinkReader() {
        return new APReader();
    }
}
