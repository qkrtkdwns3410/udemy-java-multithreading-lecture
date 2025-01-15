package com.thread.conditions.test;

public class SimpleCountDownLatch {
    private int count;
    
    public SimpleCountDownLatch(int count) {
        this.count = count;
        
        if (count < 0) {
            throw new IllegalArgumentException("count cannot be negative");
        }
    }
    
    /**
     * Causes the current thread to wait until the latch has counted down to zero.<br>
     * If the current count is already zero then this method returns immediately.
     */
    public void await() throws InterruptedException {
        if(this.count == 0) {
            return;
        }
        
        synchronized (this) {
            while(this.count > 0) {
                wait();
            }
        }
    }
    
    /**
     * Decrements the count of the latch, releasing all waiting threads when the count reaches zero.
     * If the current count already equals zero then nothing happens.
     */
    public void countDown() {
        if(this.count == 0) {
            return;
        }
        
        this.count--;
        
        if(this.count == 0) {
            synchronized (this) {
                // 모든 스레드들에게 알림
                notifyAll();
            }
        }
    }
    
    /**
     * Returns the current count.
     */
    public int getCount() {
        return this.count;
    }
}
