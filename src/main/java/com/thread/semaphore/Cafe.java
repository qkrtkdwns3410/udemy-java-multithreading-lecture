package com.thread.semaphore;

import java.util.concurrent.Semaphore;

public class Cafe {
    public static void main(String[] args) {
        // 테이블 5개를 가진 세마포어 생성
        Semaphore tables = new Semaphore(5);
        
        Runnable customer = () -> {
            try {
                // 테이블 예약 시도
                tables.acquire();
                System.out.println(Thread.currentThread().getName() + " 테이블 사용 시작.");
                // 식사 시간 (예: 2초)
                Thread.sleep(2000);
                System.out.println(Thread.currentThread().getName() + " 테이블 사용 종료.");
                // 테이블 반환
                tables.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        
        // 10명의 손님이 동시에 들어옴
        for (int i = 0; i < 10; i++) {
            new Thread(customer).start();
        }
    }
}
