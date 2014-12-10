package ua.com.msap.core.links;

import ua.com.msap.core.Link;
import java.util.LinkedList;
import ua.com.msap.core.LinkReader;
import ua.com.msap.core.Variable;
import ua.com.msap.core.links.readers.TransportDelayReader;

/**
 * Class of transport delay.The Transport Delay link, delays the input by a 
 * specified amount of time. You can use this block to simulate a time delay. 
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class TransportDelay extends Link {

    LinkedList<Double> queueOfOutputValues = new LinkedList<Double>();
    protected Variable delay;//запаздывание в квантах.

    public int getDelay() {
        return (int) this.delay.getValue();
    }

    public void setDelay(int newDelay) {
        this.delay.setValue((double) Math.abs(newDelay));
        //Clear the queue of output values
        this.queueOfOutputValues.clear();
        //Set delay as queue size, and fill the queue with zero values.
        for (int i = 0; i < this.getDelay(); i++) {
            this.queueOfOutputValues.addLast((double)0);
        }
    }

    public void initDelay(Variable variable) {
        this.delay = variable;
        //Clear the queue of output values
        this.queueOfOutputValues.clear();
        //Set delay as queue size, and fill the queue with zero values.
        for (int i = 0; i < this.delay.getValue(); i++) {
            this.queueOfOutputValues.addLast((double)0);
        }
    }

    @Override
    public void reset() {
        super.reset();
        //Clear the queue of output values
        this.queueOfOutputValues.clear();
        //Set delay as queue size, and fill the queue with zero values.
        for (int i = 0; i < this.getDelay(); i++) {
            this.queueOfOutputValues.addLast((double)0);
        }
    }

    @Override
    public void step() throws Exception {
        super.step();
        //Put current input value to the end of the queue of output values.
        this.queueOfOutputValues.addLast(fX);
        //Set the oldest value from queue as the output value of the link, 
        //and remove this value from the queue.
        fY = (double) this.queueOfOutputValues.removeFirst();
    }

    @Override
    public String toString() {
        String s = super.toString();
        s += ",T=" + this.delay.toString();
        return s;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (super.equals(otherObject) == false) {
            return false;
        }
        //Function super.equals() has already checked that otherObject has 
        //the same class.
        TransportDelay delayLink = (TransportDelay) otherObject;
        //Check that fields have the same values
        return this.delay == delayLink.delay
                && queueOfOutputValues.equals(delayLink.queueOfOutputValues);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.queueOfOutputValues != null
                ? this.queueOfOutputValues.hashCode() : 0);
        hash = 59 * hash + (this.delay != null
                ? this.delay.hashCode() : 0);
        return hash;
    }

    @Override
    public LinkReader getLinkReader() {
        return (LinkReader) new TransportDelayReader();
    }
}
