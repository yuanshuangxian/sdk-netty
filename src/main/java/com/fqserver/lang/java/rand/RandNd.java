package com.fqserver.lang.java.rand;

import java.util.Random;

public class RandNd {
    final Random rand;
    /** Mean of this distribution. */
    final double mean;
    /** Standard deviation of this distribution. */
    final double standardDeviation;

    public RandNd(double mean, double sd) {
        this(mean, sd, System.currentTimeMillis());
    }

    public RandNd(double mean, double sd, long seed) {
        this.mean = mean;
        this.standardDeviation = sd;
        this.rand = new Random(seed);
    }

    public double next() {
        return this.standardDeviation * rand.nextGaussian() + this.mean;
    }
}