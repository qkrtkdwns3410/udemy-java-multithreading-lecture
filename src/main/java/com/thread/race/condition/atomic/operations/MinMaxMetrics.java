package com.thread.race.condition.atomic.operations;

public class MinMaxMetrics {
    
    private volatile long minMetric;
    private volatile long maxMetric;
    
    public MinMaxMetrics() {
        this.minMetric = Long.MAX_VALUE;
        this.maxMetric = Long.MIN_VALUE;
    }
    
    /**
     * Adds a new sample to our metrics.
     */
    public void addSample(long newSample) {
        // Add code here
        synchronized (this) {
            this.minMetric = Math.min(this.minMetric, newSample);
            this.maxMetric = Math.max(this.maxMetric, newSample);
        }
    }
    
    /**
     * Returns the smallest sample we've seen so far.
     */
    public long getMin() {
        return this.minMetric;
    }
    
    /**
     * Returns the biggest sample we've seen so far.
     */
    public long getMax() {
        return this.maxMetric;
    }
}
