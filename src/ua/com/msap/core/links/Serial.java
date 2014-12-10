package ua.com.msap.core.links;

import ua.com.msap.core.Link;
import java.util.ArrayList;
import ua.com.msap.core.LinkReader;
import ua.com.msap.core.links.readers.SerialReader;

/**
 * Class of serial connection of links.
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 * @see <a href="http://autoworks.com.ua/teoreticheskie-svedeniya/tipovye-svyazi-mezhdu-zvenyami-strukturnyx-sxemax-sistem/#posl">Serial links</a>
 */
public class Serial extends Link {

    private boolean direction = true; /*true=left 2 right. false=right 2 left*/

    private ArrayList<Link> links = new ArrayList<Link>();

    public boolean getDirection() {
        return this.direction;
    }

    public void setDirection(boolean newDirection) {
        this.direction = newDirection;
    }

    public void addLink(Link link) throws NullPointerException {
        if (link == null) {
            throw new IllegalArgumentException();
        }
        links.add(link);
    }

    public Link getLink(int i) {
        if (i >= links.size() || i < 0) {
            return null;
        }
        return links.get(i);
    }

    public int getLength() {
        return links.size();
    }

    @Override
    public void reset() {
        super.reset();
        for (Link link : this.links) {
            link.reset();
        }
    }

    @Override
    public void step() throws Exception {
        super.step();
        double tempX = this.fX;
        if (direction) /*left to right*/ {
            for (Link link : this.links) {
                link.setX(tempX);
                link.step();
                tempX = link.getY();
            }
        } else /*right to left*/ {
            for (int i = links.size() - 1; i >= 0; i--) {
                links.get(i).setX(tempX);
                links.get(i).step();
                tempX = links.get(i).getY();
            }
        }
        fY = tempX;
    }

    @Override
    public String toString() {
        String s = super.toString();
        s += ",links=[";
        for (Link link : this.links) {
            s += link.toString();
            if (link != this.links.get(this.links.size() - 1)) {
                if (this.direction) {
                    s += "-> ";
                } else {
                    s += "<- ";
                }
            }
        }
        s += "]";
        return s;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (super.equals(otherObject) == false) {
            return false;
        }
        //Function super.equals() has already checked that otherObject has 
        //the same class.
        Serial serialLink = (Serial) otherObject;
        //Check that fields have the same values
        return this.direction == serialLink.direction
                && this.links.equals(serialLink.links);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + (this.direction ? 1 : 0);
        hash = 61 * hash + (this.links != null ? this.links.hashCode() : 0);
        return hash;
    }

    @Override
    public LinkReader getLinkReader() {
        return (LinkReader) new SerialReader();
    }
}
