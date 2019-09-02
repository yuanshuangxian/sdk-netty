package com.fqserver.lang.util.concurrent;

import java.io.Serializable;

public class MutableLong implements Serializable {
    /**
     * Required for serialization support.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 512176391864L;

    /** The mutable value. */
    private long value;

    /**
     * Constructs a new MutableLong with the default value of zero.
     */
    public MutableLong() {
        super();
    }

    /**
     * Constructs a new MutableLong with the specified value.
     * 
     * @param value
     *            the initial value to store
     */
    public MutableLong(long value) {
        super();
        this.value = value;
    }

    public MutableLong incr() {
        ++value;
        return this;
    }

    public MutableLong decr() {
        --value;
        return this;
    }

    public long incrAndGet() {
        return ++value;
    }

    public long getAndIncr() {
        return value++;
    }

    public long decrAndGet() {
        return --value;
    }

    public long getAndDecr() {
        return value--;
    }

    public long get() {
        return value;
    }

    public void set(long newValue) {
        value = newValue;
    }

    public long getAndAdd(int delta) {
        long v = value;
        value += delta;
        return v;
    }

    public long addAndGet(int delta) {
        value += delta;
        return value;
    }

    public MutableLong reset() {
        value = 0;
        return this;
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }
}