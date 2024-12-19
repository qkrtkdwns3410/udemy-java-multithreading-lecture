package com.thread.creation.example.interrupt;

import java.math.BigInteger;

/**
 * packageName    : com.thread.creation.example.interrupt
 * fileName       : Main
 * author         : ipeac
 * date           : 24. 12. 18.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 12. 18.        ipeac       최초 생성
 */
public class Main {
    public static void main(String[] args) {
        Thread thread = new Thread(new LongComputationTask(new BigInteger("20000"), new BigInteger("100000")));
        
        thread.setDaemon(true);
        thread.start();
        thread.interrupt();
    }
    
    private static class LongComputationTask implements Runnable {
        private final BigInteger base;
        private final BigInteger power;
        
        public LongComputationTask(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }
        
        @Override
        public void run() {
            System.out.println(base + "^" + power + " = " + pow(base, power));
        }
        
        private BigInteger pow(BigInteger base, BigInteger power) {
            // 0
            BigInteger result = BigInteger.ONE;
            
            //의미 : power만큼 반복
            for (BigInteger i = BigInteger.ZERO; i.compareTo(power) != 0; i = i.add(BigInteger.ONE)) {
                result = result.multiply(base);
            }
            
            return result;
        }
    }
}
