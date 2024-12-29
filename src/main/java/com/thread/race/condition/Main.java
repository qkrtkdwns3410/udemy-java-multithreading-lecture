package com.thread.race.condition;

public class Main {
    public static void main(String[] args) {
        InventoryCounter inventoryCounter = new InventoryCounter();
        
        IncrementingThread incrementingThread = new IncrementingThread(inventoryCounter);
        DecrementingThread decrementingThread = new DecrementingThread(inventoryCounter);
        
        incrementingThread.start();
        decrementingThread.start();
        
        try {
            incrementingThread.join();
            decrementingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("The number of items in the inventory is " + inventoryCounter.getItems());
    }
    
    public static class IncrementingThread extends Thread {
        private final InventoryCounter inventoryCounter;
        
        public IncrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }
        
        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                inventoryCounter.increment();
            }
        }
    }
    
    public static class DecrementingThread extends Thread {
        private final InventoryCounter inventoryCounter;
        
        public DecrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }
        
        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                inventoryCounter.decrement();
            }
        }
    }
    
    private static class InventoryCounter {
        private int items = 0;
        
        final Object lock = new Object();
        
        public void increment() {
            synchronized (this.lock) {
                items++;
            }
        }
        
        public void decrement() {
            synchronized (this.lock) {
                items--;
            }
        }
        
        public int getItems() {
            synchronized (this.lock) {
                return items;
            }
        }
    }
}
