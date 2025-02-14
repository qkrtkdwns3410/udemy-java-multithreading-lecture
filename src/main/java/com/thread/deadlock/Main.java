package com.thread.deadlock;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Intersection intersection = new Intersection();
        
        Thread trainAThread = new Thread(new TrainA(intersection));
        Thread trainBThread = new Thread(new TrainB(intersection));
        
        trainAThread.start();
        trainBThread.start();
    }
    
    public static class TrainA implements Runnable {
        private Intersection intersection;
        private Random random = new Random();
        
        public TrainA(Intersection intersection) {
            this.intersection = intersection;
        }
        
        @Override
        public void run() {
            while (true) {
                long sleepingTime = random.nextInt(5);
                
                try {
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                intersection.takeRoadA();
            }
        }
    }
    
    public static class TrainB implements Runnable {
        private Intersection intersection;
        private Random random = new Random();
        
        public TrainB(Intersection intersection) {
            this.intersection = intersection;
        }
        
        @Override
        public void run() {
            while (true) {
                long sleepingTime = random.nextInt(5);
                
                try {
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                intersection.takeRoadB();
            }
        }
    }
    
    private static class Intersection {
        private final Object roadA = new Object();
        private final Object roadB = new Object();
        
        public void takeRoadA() {
            synchronized (roadA) {
                System.out.println("Road A is locked by thread: " + Thread.currentThread().getName());
                
                synchronized (roadB) {
                    System.out.println("Train is passing through road A");
                    stopTrain();
                }
            }
        }
        
        public void takeRoadB() {
            synchronized (roadA) {
                System.out.println("Road A is locked by thread: " + Thread.currentThread().getName());
                
                synchronized (roadB) {
                    System.out.println("Train is passing through road B");
                    stopTrain();
                }
            }
        }
        
        private void stopTrain() {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
