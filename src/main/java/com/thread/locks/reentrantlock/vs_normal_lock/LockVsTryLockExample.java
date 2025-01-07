package com.thread.locks.reentrantlock.vs_normal_lock;

import java.util.concurrent.locks.ReentrantLock;

public class LockVsTryLockExample {
    private final ReentrantLock lock = new ReentrantLock();

    public void doTaskWithLock(String threadName) {
        System.out.println(threadName + " - lock() 시도");
        lock.lock(); // 여기서 락을 얻을 때까지 대기
        try {
            System.out.println(threadName + " - 락 획득, 작업 수행 시작");
            // 오래 걸리는 작업을 가정
            Thread.sleep(2000);
            System.out.println(threadName + " - 작업 수행 완료");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            System.out.println(threadName + " - 락 반환");
        }
    }

    public void doTaskWithTryLock(String threadName) {
        System.out.println(threadName + " - tryLock() 시도");
        
        // tryLock()은 락을 즉시 시도하고, 실패 시 대기하지 않고 false를 반환
        if (lock.tryLock()) {
            try {
                System.out.println(threadName + " - 락 획득, 작업 수행 시작");
                Thread.sleep(2000);
                System.out.println(threadName + " - 작업 수행 완료");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                System.out.println(threadName + " - 락 반환");
            }
        } else {
            System.out.println(threadName + " - 락을 얻지 못해 바로 종료(다른 작업으로 넘어감)");
        }
    }

    public static void main(String[] args) {
        LockVsTryLockExample example = new LockVsTryLockExample();

        // Thread-1: 무조건 lock()을 사용 -> 락을 얻을 때까지 블로킹
        Thread t1 = new Thread(() -> {
            example.doTaskWithLock("Thread-1");
        });

        // Thread-2: tryLock()을 사용 -> 락 획득 실패 시 바로 종료
        Thread t2 = new Thread(() -> {
            example.doTaskWithTryLock("Thread-2");
        });

        t1.start();
        // 살짝 시간차를 두어 t1이 먼저 락을 잡게 만듦
        try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
        t2.start();
    }
}
