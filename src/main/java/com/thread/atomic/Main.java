package com.thread.atomic;/*
 * Copyright (c) 2019-2023. Michael Pogrebinsky - Top Developer Academy
 * https://topdeveloperacademy.com
 * All rights reserved
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

/**
 * Atomic References, Compare And Set, Lock-Free High Performance Data Structure
 * https://www.udemy.com/java-multithreading-concurrency-performance-optimization
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        //StandardStack<Integer> stack = new StandardStack<>();
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        Random random = new Random();
        
        for (int i = 0; i < 100000; i++) {
            stack.push(random.nextInt());
        }
        
        List<Thread> threads = new ArrayList<>();
        
        int pushingThreads = 2;
        int poppingThreads = 2;
        
        for (int i = 0; i < pushingThreads; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    stack.push(random.nextInt());
                }
            });
            
            thread.setDaemon(true);
            threads.add(thread);
        }
        
        for (int i = 0; i < poppingThreads; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    stack.pop();
                }
            });
            
            thread.setDaemon(true);
            threads.add(thread);
        }
        
        for (Thread thread : threads) {
            thread.start();
        }
        
        Thread.sleep(10000);
        
        System.out.println(String.format("%,d operations were performed in 10 seconds ", stack.getCounter()));
    }
    
    /**
     * 락 - 프리 스레드 세이프 스택 구현
     *
     * @param <T> type of the elements in the stack
     */
    public static class LockFreeStack<T> {
        //스택 헤드 관리용 원자적 레퍼런스
        private AtomicReference<StackNode<T>> head = new AtomicReference<>();
        //수행된 연산 수 카운터
        private AtomicInteger counter = new AtomicInteger(0);
        
        /**
         * 스택에 값을 푸시
         *
         * @param value push할 값
         */
        public void push(T value) {
            StackNode<T> newHeadNode = new StackNode<>(value); //새로운 헤드 노드 생성
            
            while (true) {
                StackNode<T> currentHeadNode = head.get(); // 현재 헤드 노드를 가져온다
                newHeadNode.next = currentHeadNode; // 새 노드의 next 를 현재 헤드로 설정한다.
                
                //헤드를 새 노드로 원자적으로 업데이트를 시도한다.
                if (head.compareAndSet(currentHeadNode, newHeadNode)) {
                    break; //성공한다면 루프를 탈출한다.
                } else {
                    //실패하는 경우 1나노초 대기 후에 재시도를 수행한다.
                    LockSupport.parkNanos(1);
                }
                
                /*왜 위 과정을 거치는가?
                *
                * 비교 및 교환(compare-and-swap, CAS) 연산을 통해 락-프리(lock-free) 방식으로 스택 헤드를 안전하게 업데이트한다.
                * 다중 스레드 환경에서 데이터 일관성 유지, 성능 최적화가 가능해진다.
                *
                *  StackNode<T> currentHeadNode = head.get(); // 현재 헤드 노드를 가져온다
                * newHeadNode.next = currentHeadNode; // 새 노드의 next 를 현재 헤드로 설정한다.
                *
                * 위 2가지 과정에서 스레드 간의 경합 과정에서 예상 값과 달라질수 있기에
                * else 구문이 존재하는 것이다. LockSupport.parkNanos(1) 을 통해 1나노초 대기 후에 재시도를 수행한다.
                * */
            }
            
            counter.incrementAndGet(); // 연산 카운터를 증가시킨다.
        }
        
        public T pop() {
            StackNode<T> currentHeadNode = head.get();
            StackNode<T> newHeadNode;
            
            while (currentHeadNode != null) { // 스택이 비어있다면 pop 을 수행해도 의미가 없기에 비어있지 않은 경우만 순회한다.
                newHeadNode = currentHeadNode.next; // 새로운 헤드는 현재 헤드의 다음 노드가 된다.
                
                if (head.compareAndSet(currentHeadNode, newHeadNode)) {
                    break;
                } else {
                    LockSupport.parkNanos(1);
                    currentHeadNode = head.get();
                }
            }
            
            counter.incrementAndGet();
            return currentHeadNode != null ? currentHeadNode.value : null;
        }
        
        public int getCounter() {
            return counter.get();
        }
    }
    
    public static class StandardStack<T> {
        private StackNode<T> head;
        private int counter = 0;
        
        public synchronized void push(T value) {
            StackNode<T> newHead = new StackNode<>(value);
            newHead.next = head;
            head = newHead;
            counter++;
        }
        
        public synchronized T pop() {
            if (head == null) {
                counter++;
                return null;
            }
            
            T value = head.value;
            head = head.next;
            counter++;
            return value;
        }
        
        public int getCounter() {
            return counter;
        }
    }
    
    private static class StackNode<T> {
        public T value;
        public StackNode<T> next;
        
        public StackNode(T value) {
            this.value = value;
            this.next = next;
        }
    }
}
