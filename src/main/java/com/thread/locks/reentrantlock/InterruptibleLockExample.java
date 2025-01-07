package com.thread.locks.reentrantlock;

import java.util.concurrent.locks.ReentrantLock;

public class InterruptibleLockExample {
    private final ReentrantLock lock = new ReentrantLock();

    public void doLongTask() throws InterruptedException {
        // lockInterruptibly()를 사용하면,
        // 락 대기 중에 인터럽트 발생 시 InterruptedException을 던집니다.
        lock.lockInterruptibly();
        try {
            System.out.println(Thread.currentThread().getName() + " - 락 획 득성공, 작업 시작.");
            
            // 여기서 오래 걸리는 작업을 가정
            Thread.sleep(10000); // 예: 10초 걸리는 작업
            
            System.out.println(Thread.currentThread().getName() + " - 작업 완료.");
        } finally {
            lock.unlock();
            System.out.println(Thread.currentThread().getName() + " - 락 반환.");
        }
    }

    public static void main(String[] args) {
        InterruptibleLockExample example = new InterruptibleLockExample();

        Runnable task = () -> {
            try {
                example.doLongTask();
            } catch (InterruptedException e) {
                // 대기 도중에 인터럽트가 걸리면 여기로 빠집니다.
                System.out.println(Thread.currentThread().getName() + " - 작업 중 인터럽트 발생, 작업 중단.");
            }
        };

        Thread t1 = new Thread(task, "Thread-1");
        Thread t2 = new Thread(task, "Thread-2");

        t1.start();
        t2.start();

        // Thread-2가 대기 중일 때, 인터럽트를 걸어보자.
        try {
            // 잠시 대기 후, 두 번째 스레드에 인터럽트
            Thread.sleep(2000);
            System.out.println("메인 스레드에서 " + t2.getName() + "에 인터럽트 전달.");
            t2.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
