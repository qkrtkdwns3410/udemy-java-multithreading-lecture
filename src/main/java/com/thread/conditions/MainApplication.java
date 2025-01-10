package com.thread.conditions;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * packageName    : com.thread.conditions
 * fileName       : MainApplication
 * author         : ipeac
 * date           : 25. 1. 10.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 1. 10.        ipeac       최초 생성
 */
public class MainApplication {
    private static final int N = 10;
    
    public static void main(String[] args) {
    
    }
    
    private static class MatricesMultiplierConsumer extends Thread {
        private ThreadSafeQueue queue;
        private FileWriter fileWriter;
        
        
        public MatricesMultiplierConsumer(FileWriter fileWriter, ThreadSafeQueue queue) {
            this.fileWriter = fileWriter;
            this.queue = queue;
        }
        
        private float[][] multiplyMatrices(float[][] m1, float[][] m2) {
            float[][] result = new float[N][N];
            
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < N; c++) {
                    for (int k = 0; k < N; k++) {
                        result[r][c] += m1[r][k] * m2[k][c];
                    }
                }
            }
        }
    }
    
    private static class MatricsReaderProducer extends Thread {
        
        private Scanner scanner;
        private ThreadSafeQueue queue;
        
        public MatricsReaderProducer(FileReader reader, ThreadSafeQueue queue) {
            this.scanner = new Scanner(reader);
            this.queue = queue;
        }
        
        @Override
        public void run() {
            while (true) {
                float[][] matrix1 = readMatrix();
                float[][] matrix2 = readMatrix();
                
                if (matrix1 == null || matrix2 == null) {
                    queue.terminate();
                    System.out.println("No more matrices to read, Producer is terminating");
                    return;
                }
                
                final MatricesPair matricesPair = new MatricesPair(matrix1, matrix2);
                
                queue.add(matricesPair);
            }
        }
        
        private float[][] readMatrix() {
            float[][] matrix = new float[N][N];
            for (int r = 0; r < N; r++) {
                if (!scanner.hasNext()) {
                    return null;
                }
                
                String[] line = scanner.nextLine().split(", ");
                
                for (int c = 0; c < N; c++) {
                    matrix[r][c] = Float.parseFloat(line[c]);
                }
            }
            
            scanner.nextLine();
            return matrix;
        }
    }
    
    private static class ThreadSafeQueue {
        private Queue<MatricesPair> queue = new LinkedList<>();
        private boolean isEmpty = true;
        private boolean isTerminate = false;
        
        public synchronized void add(MatricesPair matricesPair) {
            queue.add(matricesPair);
            isEmpty = false;
            notify();
        }
        
        public synchronized MatricesPair remove() {
            while (isEmpty && !isTerminate) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            if (queue.size() == 1) {
                isEmpty = true;
            }
            
            if (queue.isEmpty() && isTerminate) {
                return null;
            }
            
            System.out.println("Queue size: " + queue.size());
            
            return queue.remove();
        }
        
        public synchronized void terminate() {
            isTerminate = true;
            notifyAll();
        }
    }
    
    private static class MatricesPair {
        private float[][] matrix1;
        private float[][] matrix2;
        
        public MatricesPair(float[][] matrix1, float[][] matrix2) {
            this.matrix1 = matrix1;
            this.matrix2 = matrix2;
        }
    }
}
