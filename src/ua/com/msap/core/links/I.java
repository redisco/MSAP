package ua.com.msap.core.links;

import ua.com.msap.core.exceptions.NotInitializedVariableException;
import ua.com.msap.core.*;
import ua.com.msap.core.links.readers.IReader;

/**
 * Class of inertial link
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 * @see <a href="http://ait.cs.nstu.ru/tau/book/Sod34.htm">Integral link</a>
 */
public class I extends Link {

    protected Variable t;
    private double previousY = 0;
    private long timeAterNewX = 0;

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

    public void initT(Variable variable) {
        this.t = variable;
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
        fY = previousY + fX * (timeAterNewX / this.getT());
    }

    @Override
    public String toString() {
        String s = super.toString();
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
        I iLink = (I) otherObject;
        return this.t == iLink.t
                && this.previousY == iLink.previousY
                && this.timeAterNewX == iLink.timeAterNewX;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.t != null ? this.t.hashCode() : 0);
        hash = 11 * hash + (int) new Double(this.previousY).hashCode();
        hash = 11 * hash + (int) new Long(this.timeAterNewX).hashCode();
        return hash;
    }

    @Override
    public LinkReader getLinkReader() {
        return (LinkReader) new IReader();
    }
}
