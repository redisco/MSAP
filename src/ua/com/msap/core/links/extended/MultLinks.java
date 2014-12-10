package ua.com.msap.core.links.extended;

import ua.com.msap.core.Link;
import ua.com.msap.core.LinkReader;
import ua.com.msap.core.links.readers.MultLinksReader;

/**
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class MultLinks extends Link{
    private Link a;
    private Link b;
    private String expression;
    private boolean forHash = false;
    
    public Link getA() {
        return a;
    }

    public void setA(Link value) {
        a = value;
    }

    public Link getB() {
        return b;
    }

    public void setB(Link value) {
        b = value;
    }
    
    public void setExp(String expression) {
        this.expression = expression;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.a.reset();
        this.b.reset();
    }
    
    @Override
    public void setX(double newX) {
        super.setX(newX);
            a.setX(fX);
            b.setX(fX);        
    }
    
    @Override
    public void step() throws Exception {
        super.step();

        a.step();
        b.step();
        
        if (expression.equals("*")) {
                fY=a.getY()*b.getY();
		forHash = true;	    
	}
        if (expression.equals("/")) {
                fY=a.getY()/b.getY();
		forHash = true;	    
	}
        
    }
    
    @Override
    public String toString() {
        String s = super.toString();
        s += ",Exp='";
        s += (this.expression);
        s += "'";
        s += ",A";
        s += "=[";
        s += (this.a != null ? this.a.toString() : "'Not initialized'");
        s += "]";
        s += ",B";
        s += "=[";
        s += (this.b != null ? this.b.toString() : "'Not initialized'");
        s += "]";
        return s;
    }
    
    @Override
    public boolean equals(Object otherObject) {
        if (super.equals(otherObject) == false) {
            return false;
        }
        //Function super.equals() has already checked that otherObject 
        //has the same class
        MultLinks multLink = (MultLinks) otherObject;
        //Check that fields have the same values
        return this.a.equals(multLink.a)
                && this.b.equals(multLink.b)
                && this.expression == multLink.expression;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.a != null ? this.a.hashCode() : 0);
        hash = 59 * hash + (this.b != null ? this.b.hashCode() : 0);
        hash = 59 * hash + (this.forHash ? 1 : 0);
        return hash;
    }

    @Override
    public LinkReader getLinkReader() {
        return (LinkReader) new MultLinksReader();
    }
    
}
