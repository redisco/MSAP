package ua.com.msap.core.links.extended;

import ua.com.msap.core.Link;
import java.util.ArrayList;
import ua.com.msap.core.LinkReader;
import ua.com.msap.core.links.readers.ChooseReader;

/**
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
  */
public class Choose extends Link {
    
    private ArrayList<IfVariable> ifs = new ArrayList<IfVariable>();

    public void addIf(IfVariable ifVariable) throws IllegalArgumentException{
        if (ifVariable == null) {
            throw new IllegalArgumentException();
        }
        ifs.add(ifVariable);
    }

    public IfVariable getIf(int i) {
        if (i >= ifs.size() || i < 0) {
            return null;
        }
        return ifs.get(i);
    }

    public int getSize(){
        return this.ifs.size();
    }

    @Override
    public void reset() {
        super.reset();
        for (IfVariable ifVariable : this.ifs) {
            Link link = ifVariable.getLink();
            if(link != null){
                link.reset();
            }
        }
    }

    @Override
    public void step() throws Exception {
        super.step();
        double tempX = this.fX;
        for (IfVariable ifVariable : this.ifs) {
            if(ifVariable.isTrue(tempX)){
                Link link = ifVariable.getLink();
                if(link != null){
                    link.setX(tempX);
                    link.step();
                    tempX = link.getY();
                }
                break;
            }            
        }
        fY = tempX;
    }

    @Override
    public String toString() {
        String s = super.toString();
        s += ",ifs=[";
        for (IfVariable ifVariable : this.ifs) {
            s += ifVariable.toString();
            if (ifVariable != this.ifs.get(this.ifs.size() - 1)) {
                    s += ", ";
            }
        }
        s += "]";
        return s;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Choose other = (Choose) obj;
        if (this.ifs != other.ifs
                && (this.ifs == null || !this.ifs.equals(other.ifs))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + (this.ifs != null ? this.ifs.hashCode() : 0);
        return hash;
    }

    @Override
    public LinkReader getLinkReader() {
        return (LinkReader) new ChooseReader();
    }
    
    public enum IfType { EQUAL, DEFAULT  }
    public class IfVariable{
       
        private Link link;
        private double value;
        private IfType ifType;
        
        public IfVariable(IfType ifType, double value, Link link){
            this.ifType = ifType;
            this.value = value;
            this.link = link;
        }
        public boolean isTrue(double input){
            switch(this.ifType){
                case DEFAULT:
                    return true;
                case EQUAL:
                    if(Math.abs(input - value) < 0.000001){
                        return true;
                    } else {
                        return false;
                    }
            }
            return false;
        }

        /**
         * @return the link
         */
        public Link getLink() {
            return link;
        }

        /**
         * @param link the link to set
         */
        public void setLink(Link link) {
            this.link = link;
        }

        /**
         * @return the value
         */
        public double getValue() {
            return value;
        }

        /**
         * @param value the value to set
         */
        public void setValue(double value) {
            this.value = value;
        }

        /**
         * @return the ifType
         */
        public IfType getIfType() {
            return ifType;
        }

        /**
         * @param ifType the ifType to set
         */
        public void setIfType(IfType ifType) {
            this.ifType = ifType;
        }
        
        @Override
        public String toString() {
                    String s = super.toString();
        s += ", IfType="+this.ifType+", Value=" + this.value 
                + ", Link=["+this.link+"]";
        return s;
        }
              
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final IfVariable other = (IfVariable) obj;
            if (this.link != other.link && (this.link == null || !this.link.equals(other.link))) {
                return false;
            }
            if (Double.doubleToLongBits(this.value) != Double.doubleToLongBits(other.value)) {
                return false;
            }
            if (this.ifType != other.ifType) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + (this.link != null ? this.link.hashCode() : 0);
            hash = 41 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
            hash = 41 * hash + (this.ifType != null ? this.ifType.hashCode() : 0);
            return hash;
        }
    }
}
