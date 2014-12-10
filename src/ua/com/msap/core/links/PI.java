package ua.com.msap.core.links;

import ua.com.msap.core.exceptions.NotInitializedVariableException;
import ua.com.msap.core.*;
import ua.com.msap.core.links.readers.PIReader;

/**
 * Class of PI regulator
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 * @see <a href="http://autoworks.com.ua/teoreticheskie-svedeniya/pi-regulyator/">PI regulator(proportional-integral)</a>
 */
public class PI extends Link {

    protected I iLink;
    protected P pLink;

    public PI() {
        pLink = new P();
        iLink = new I();
    }

    public double getK() throws NotInitializedVariableException {
        return pLink.getK();
    }

    public void setK(double value) throws NotInitializedVariableException {
        pLink.setK(value);
    }

    public double getT() throws NotInitializedVariableException {
        return iLink.getT();
    }

    public void setT(double value) throws NotInitializedVariableException {
        iLink.setT(value);
    }

    public void initK(Variable variable) {
        this.pLink.initK(variable);
    }

    public void initT(Variable variable) {
        this.iLink.initT(variable);
    }

    @Override
    public void reset() {
        super.reset();
        iLink.reset();
        pLink.reset();
    }

    @Override
    public void setX(double newX) {
        super.setX(newX);
        pLink.setX(newX);
    }

    @Override
    public void step() throws Exception {
        super.step();
        pLink.step();
        iLink.setX(pLink.getY());
        iLink.step();
        fY = pLink.getY() + iLink.getY();
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
        PI piRegulator = (PI) otherObject;
        return this.pLink.equals(piRegulator.pLink)
                && this.iLink.equals(piRegulator.iLink);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + (this.iLink != null ? this.iLink.hashCode() : 0);
        hash = 31 * hash + (this.pLink != null ? this.pLink.hashCode() : 0);
        return hash;
    }

    @Override
    public LinkReader getLinkReader() {
        return (LinkReader) new PIReader();
    }
}
