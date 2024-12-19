package com.thread.coordination.join;

import java.math.BigInteger;
import java.util.List;

public class ComplexCalculation {
    public BigInteger calculateResult(BigInteger base1, BigInteger power1, BigInteger base2, BigInteger power2) {
        BigInteger result;
        /*
            Calculate result = ( base1 ^ power1 ) + (base2 ^ power2).
            Where each calculation in (..) is calculated on a different thread
        */
        List<Thread> threads = List.of(
                new PowerCalculatingThread(base1, power1),
                new PowerCalculatingThread(base2, power2)
        );
        
        threads.forEach(thread -> {
            thread.setDaemon(true);
            thread.start();
        });
        
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        result = ((PowerCalculatingThread) threads.get(0)).getResult().add(((PowerCalculatingThread) threads.get(1)).getResult());
        
        return result;
    }
    
    private static class PowerCalculatingThread extends Thread {
        private BigInteger result = BigInteger.ONE;
        private final BigInteger base;
        private final BigInteger power;
        
        public PowerCalculatingThread(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }
        
        @Override
        public void run() {
            this.calculateResult();
        }
        
        private void calculateResult() {
            this.result = this.base.pow(this.power.intValue());
        }
        
        public BigInteger getResult() {
            return result;
        }
    }
}
