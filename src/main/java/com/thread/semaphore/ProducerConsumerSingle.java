package com.thread.semaphore;

import java.util.concurrent.Semaphore;

/**
 * packageName    : com.thread.semaphore
 * fileName       : ProducerConsumerSingle
 * author         : ipeac
 * date           : 25. 1. 9.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 1. 9.        ipeac       최초 생성
 */
public class ProducerConsumerSingle {
    private static Semaphore full = new Semaphore(0);
    private static Semaphore empty = new Semaphore(1);
    private static String product = null;
    
    public static void main(String[] args) {
        //생산자 스레드
        Thread producer = new Thread(() -> {
            try {
                empty.acquire(); // 빈 공간을 확보한다.
                product = "상품"; // 상품을 생산한다고 친다.
                System.out.println("생산자: " + product + "생산");
                
                full.release(); // 소비자에게 소비를 하라고 알리는 역할이다.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        //소비자 스레드
        Thread consumer = new Thread(() -> {
            try {
                full.acquire(); // 제품을 대기한다.
                System.out.println("소비자: " + product + "소비");
                
                product = null; // 제품을 소비한다.
                
                empty.release(); // 소비를 했으니 빈 공간을 알린다.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        producer.start();
        consumer.start();
    }
}
