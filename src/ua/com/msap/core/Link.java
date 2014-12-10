package ua.com.msap.core;

/**
 * Base class of all links 
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public abstract class Link {

    protected double fX;
    protected double fY;
    protected long fTime;

    public Link() {
        fTime = 0;
    }

    public abstract LinkReader getLinkReader();
    
    public void reset() {
        fTime = 0;
        fY = 0;
        fX = 0;
    }

    public long getTime() {
        return fTime;
    }

    public void setX(double x) {
        fX = x;
    }

    public void step() throws Exception {
        ++fTime;
    }

    public double getY() {
        return fY;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + ": X=" + fX + ", Y" + fY + ", time=" + fTime + "";
    }

    @Override
    public boolean equals(Object otherObject) {
        //Quick check, if objects equal
        if (this == otherObject) {
            return true;
        }
        //If parameter otherObject is null, then return false
        if (otherObject == null) {
            return false;
        }
        //If classes are not identical, then objects are not equal
        if (this.getClass() != otherObject.getClass()) {
            return false;
        }
        //Now we knows that this object of class Link
        Link other = (Link) otherObject;
        //Checks whether the fields are stored objects 
        //with identical values
        return fX == other.fX
                && fY == other.fY
                && fTime == other.fTime;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.fX) ^ (Double.doubleToLongBits(this.fX) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.fY) ^ (Double.doubleToLongBits(this.fY) >>> 32));
        hash = 67 * hash + (int) (this.fTime ^ (this.fTime >>> 32));
        return hash;
    }
}
