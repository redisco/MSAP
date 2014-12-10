package ua.com.msap.core.links;

import ua.com.msap.core.LinkReader;
import ua.com.msap.core.Link;
import ua.com.msap.core.links.readers.ParallelReader;

/**
 * Class of parallel connection of the links.
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 * @see <a href="http://autoworks.com.ua/teoreticheskie-svedeniya/tipovye-svyazi-mezhdu-zvenyami-strukturnyx-sxemax-sistem/#paral">Parallel connection(direct)</a>
 * @see <a href="http://autoworks.com.ua/teoreticheskie-svedeniya/tipovye-svyazi-mezhdu-zvenyami-strukturnyx-sxemax-sistem/#os">Parallel connection(reverse, with feedback)</a>
 */
public class Parallel extends Link {

    private Link a;
    private Link b;
    private boolean signA = true;
    private boolean signB = true;
    private boolean forward = true;

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

    public boolean getSignA() {
        return signA;
    }

    public void setSignA(boolean sign) {
        signA = sign;
    }

    public boolean getSignB() {
        return signB;
    }

    public void setSignB(boolean sign) {
        signB = sign;
    }

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean isForward) {
        forward = isForward;
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
        if (forward) /*If it is direct connection*/ {
            a.setX(fX);
            b.setX(fX);
        } else /*If it is reverse connection*/ {
            double tempX = 0;
            //Since the input signal of the parallel connection has changed,
            //recalculate input of А-node.
            //Input of A(reverse connection) = output of B-node * sign of 
            //B-node + the input signal of parallel connection.
            tempX = (signA ? fX : -fX);
            tempX += (signB ? b.getY() : -b.getY());
            a.setX(tempX);
            //Input of B-node will changes only after next step
        }
    }

    @Override
    public void step() throws Exception {
        super.step();

        if (forward) /*If it is direct connection*/ {
            a.step();
            b.step();
            fY = (signA ? a.getY() : -a.getY())
                    + (signB ? b.getY() : -b.getY());
        } else /*If it is reverse connection*/ {
            a.step();
            fY = a.getY();

            //Сalculate output of B-node.
            b.setX(fY);
            b.step();

            //Recalculate input of А-node, for using on the next step.
            //Input of A(reverse connection) = output of B-node * sign of 
            //B-node + input signal of paralel connection.
            double tempX = 0;
            tempX = (signA ? fX : -fX);
            tempX += (signB ? b.getY() : -b.getY());
            a.setX(tempX);
        }
    }

    @Override
    public String toString() {
        String s = super.toString();
        s += ",Forward='";
        s += (this.forward == true ? "+" : "-");
        s += "'";
        s += ",A";
        s += (this.signA == true ? "+" : "-");
        s += "=[";
        s += (this.a != null ? this.a.toString() : "'Not initialized'");
        s += "]";
        s += ",B";
        s += (this.signB == true ? "+" : "-");
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
        Parallel paralelLink = (Parallel) otherObject;
        //Check that fields have the same values
        return this.a.equals(paralelLink.a)
                && this.b.equals(paralelLink.b)
                && this.signA == paralelLink.signA
                && this.signB == paralelLink.signB
                && this.forward == paralelLink.forward;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.a != null ? this.a.hashCode() : 0);
        hash = 53 * hash + (this.b != null ? this.b.hashCode() : 0);
        hash = 53 * hash + (this.signA ? 1 : 0);
        hash = 53 * hash + (this.signB ? 1 : 0);
        hash = 53 * hash + (this.forward ? 1 : 0);
        return hash;
    }

    @Override
    public LinkReader getLinkReader() {
        return (LinkReader) new ParallelReader();
    }
}
