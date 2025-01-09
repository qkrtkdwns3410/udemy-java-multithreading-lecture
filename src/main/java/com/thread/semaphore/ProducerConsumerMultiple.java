package com.thread.semaphore;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * packageName    : com.thread.semaphore
 * fileName       : ProducerConsumerMultiple
 * author         : ipeac
 * date           : 25. 1. 9.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 1. 9.        ipeac       최초 생성
 */
public class ProducerConsumerMultiple {
    private static final int BUFFER_SIZE = 5;
    private static Semaphore full = new Semaphore(0);
    private static Semaphore empty = new Semaphore(BUFFER_SIZE);
    private static Queue<String> buffer = new LinkedList<>();
    private static ReentrantLock lock = new ReentrantLock();
    
    public static void main(String[] args) {
        // 생산자 스레드 3개
        for (int i = 1; i <= 3; i++) {
            int producerId = i;
            new Thread(() -> {
                try {
                    String product = "상품" + producerId;
                    empty.acquire(); // 빈 공간 확보
                    lock.lock(); // 버퍼 접근 제어
                    
                    buffer.add(product);
                    
                    System.out.println("생산자 " + producerId + ": " + product + " 생산");
                    
                    lock.unlock();
                    full.release(); // 소비자에게 알림
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        
        // 소비자 스레드 3개
        for (int i = 1; i <= 3; i++) {
            int consumerId = i;
            new Thread(() -> {
                try {
                    full.acquire(); // 제품 대기
                    lock.lock(); // 버퍼 접근 제어
                    
                    String product = buffer.poll();
                    System.out.println("소비자 " + consumerId + ": " + product + " 소비");
                    
                    lock.unlock();
                    empty.release(); // 생산자에게 알림
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
