package com.fqserver.lang.util.concurrent;

import com.fqserver.lang.util.Strings;

public class CountStats extends MutableLong {

    /**
	 * 
	 */
    private static final long serialVersionUID = -5420176719324628046L;

    private long initTime = System.currentTimeMillis();

    /**
     * Constructs a new QPStats with the default value of zero.
     */
    public CountStats() {
        super();
    }

    /**
     * Constructs a new QPStats with the specified value.
     * 
     * @param value
     *            the initial value to store
     */
    public CountStats(long value) {
        super(value);
    }

    public CountStats reset() {
        super.reset();
        initTime = System.currentTimeMillis();
        return this;
    }

    public double getSeconds() {
        long now = System.currentTimeMillis();
        return ((now - initTime) / 1000.0);
    }

    public double getQPS() {
        double base = getSeconds();
        if (base > 0) {
            return this.get() / base;
        }
        return 0;
    }

    @Override
    public String toString() {
        return Strings.format("Num: {0}, Seconds: {1}, QPS: {2}",
                              this.get(),
                              this.getSeconds(),
                              this.getQPS());

    }
}