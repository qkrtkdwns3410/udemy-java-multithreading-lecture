package com.thread.atomic.reference;

import java.util.concurrent.atomic.AtomicReference;

/**
 * packageName    : com.thread.atomic.reference
 * fileName       : Main
 * author         : ipeac
 * date           : 25. 1. 15.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 1. 15.        ipeac       최초 생성
 */
public class Main {
    public static void main(String[] args) {
        String oldName = "old name";
        String newName = "new name";
        
        AtomicReference<String> atomicReference = new AtomicReference<>(oldName);
        
        if(atomicReference.compareAndSet(oldName, newName)) {
            System.out.println("New value is " + atomicReference.get());
        } else {
            System.out.println("Nothing changed");
        }
    }
    
}
