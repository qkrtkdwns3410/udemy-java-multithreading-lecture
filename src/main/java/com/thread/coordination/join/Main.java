package com.thread.coordination.join;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        List<Long> inputNumbers = List.of(100000000000L, 3435L, 2324L, 554L, 34L, 23525L, 542L, 245L, 245L);
        
        List<FactorialThread> threads = new ArrayList<>();
        
        for (Long inputNumber : inputNumbers) {
            threads.add(new FactorialThread(inputNumber));
        }
        
        for (Thread thread : threads) {
            thread.setDaemon(true);
            thread.start();
        }
        
        for (Thread thread : threads) {
            thread.join(2000);
        }
        
        for (int i = 0; i < inputNumbers.size(); i++) {
            FactorialThread thread = threads.get(i);
            
            if (thread.isFinished()) {
                System.out.println("Factorial of " + inputNumbers.get(i) + " is " + thread.getResult());
            } else {
                System.out.println("The calculation for the factorial of " + inputNumbers.get(i) + " is still in progress.");
            }
        }
    }
    
    public static class FactorialThread extends Thread {
        private final long inputNumber;
        private BigInteger result = BigInteger.ZERO;
        private boolean isFinished = false;
        
        public FactorialThread(long inputNumber) {
            this.inputNumber = inputNumber;
        }
        
        @Override
        public void run() {
            this.result = factorial(inputNumber);
            this.isFinished = true;
        }
        
        public BigInteger factorial(long n) {
            BigInteger tempResult = BigInteger.ONE;
            
            for (long i = n; i > 0; i--) {
                tempResult = tempResult.multiply(new BigInteger(Long.toString(i)));
            }
            
            return tempResult;
        }
        
        public boolean isFinished() {
            return isFinished;
        }
        
        public BigInteger getResult() {
            return result;
        }
    }
}
