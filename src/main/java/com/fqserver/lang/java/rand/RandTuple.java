package com.fqserver.lang.java.rand;

public class RandTuple implements Comparable<RandTuple> {
    public int index;
    public double value;

    public RandTuple(int i, double d) {
        index = i;
        value = d;
    }

    @Override
    public int compareTo(RandTuple rt) {
        return Double.compare(this.value, rt.value);
    }

    @Override
    public String toString() {
        return "(" + index + ":" + value + ")";
    }
}