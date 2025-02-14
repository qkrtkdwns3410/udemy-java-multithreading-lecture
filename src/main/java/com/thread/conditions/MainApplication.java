package com.thread.conditions;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringJoiner;

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
    private static final String INPUT_FILE = "./out/matrices";
    private static final String OUTPUT_FILE = "./out/matrices_results.txt";
    private static final int N = 10;
    
    public static void main(String[] args) throws IOException {
        ThreadSafeQueue queue = new ThreadSafeQueue();
        
        File inputFile = new File(INPUT_FILE);
        File outputFile = new File(OUTPUT_FILE);
        
        MatricsReaderProducer matricesReader = new MatricsReaderProducer(new FileReader(inputFile), queue);
        MatricesMultiplierConsumer matricesMultiplier = new MatricesMultiplierConsumer(new FileWriter(outputFile), queue);
        
        matricesReader.start();
        matricesMultiplier.start();
    }
    
    private static class MatricesMultiplierConsumer extends Thread {
        private ThreadSafeQueue queue;
        private FileWriter fileWriter;
        
        
        public MatricesMultiplierConsumer(FileWriter fileWriter, ThreadSafeQueue queue) {
            this.fileWriter = fileWriter;
            this.queue = queue;
        }
        
        @Override
        public void run() {
            while (true) {
                final MatricesPair matricesPair = queue.remove();
                if (matricesPair == null) {
                    System.out.println("No more matrices to multiply, Consumer is terminating");
                    break;
                }
                
                float[][] result = multiplyMatrices(matricesPair.matrix1, matricesPair.matrix2);
                
                try {
                    saveMatrixToFile(fileWriter, result);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void saveMatrixToFile(FileWriter fileWriter, float[][] matrix) throws IOException {
            for (int r = 0; r < N; r++) {
                StringJoiner stringJoiner = new StringJoiner(", ");
                for (int c = 0; c < N; c++) {
                    stringJoiner.add(String.format("%.2f", matrix[r][c]));
                }
                
                fileWriter.write(stringJoiner.toString());
                fileWriter.write('\n');
            }
            
            fileWriter.write('\n');
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
            
            return result;
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
        
        //백프레셔 구현
        private static final int CAPACITY = 5;
        
        public synchronized void add(MatricesPair matricesPair) {
            //while 을 통해 백프레셔 구현 => queue가 꽉 찼을 때는 wait()을 통해 대기
            while (queue.size() == CAPACITY) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
            
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
            
            final MatricesPair removed = queue.remove();
            
            if(queue.size() == CAPACITY - 1) {
                notifyAll();
            }
            
            return removed;
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
