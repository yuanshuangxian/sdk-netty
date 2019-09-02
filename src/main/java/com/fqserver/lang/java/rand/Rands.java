package com.fqserver.lang.java.rand;

import java.util.Random;

import com.fqserver.lang.java.HeapQ;
import com.fqserver.lang.util.MathUtils;

public class Rands {
    final int count;
    final Double[] wt;
    final double[] wtp;

    private final HeapQ<RandTuple> heapq;
    final RandNd[] nds;

    public Rands(Double[] itemWeights) {
        this.wt = itemWeights;

        this.count = this.wt.length;

        this.wtp = new double[this.count];

        this.heapq = new HeapQ<RandTuple>(this.count);
        this.nds = new RandNd[this.count];

        double sum = MathUtils.sumD(this.wt);
        Random r = new Random(System.currentTimeMillis());

        for (int i = 0; i < this.count; i++) {
            double p = ((double) this.wt[i]) / sum;
            this.wtp[i] = p;
            this.nds[i] = new RandNd(1.0 / p, 1.0 / p / 3.0, r.nextLong());
            this.getHeapQ().push(new RandTuple(i, this.nds[i].next()));
        }
    }

    public int rand() {
        RandTuple rt = this.getHeapQ().pop();
        rt.value = this.nds[rt.index].next() + rt.value;
        this.getHeapQ().push(rt);
        return rt.index;
    }

    public HeapQ<RandTuple> getHeapQ() {
        return heapq;
    }
}