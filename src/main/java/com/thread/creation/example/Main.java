package com.thread.creation.example;

/**
 * packageName    : com.thread.creation.example
 * fileName       : Main
 * author         : ipeac
 * date           : 24. 12. 16.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 12. 16.        ipeac       최초 생성
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("Intentional Exception");
            }
        });
        
        //예외 처리 핸들러 설정
        /*스레드 내에서 발생한 예외를 처리하기 위하여 setUncaughtExceptionHandler() 를 사용가능
        *
        * 캐치되지 않은 예외가 발생시 지정된 핸들러가 호출되도록 한다.
        *
        * 왜 쓰냐?
        *
        * 멀티 스레드에서 각 스레드는 독립적으로 실행되는데
        *
        * 특정 스레드 내에서 예외가 발생하고 적절하게 처리하지 않는다면,
        *
        * 해당 스레드는 비정상적으로 종료된다.
        *
        * 예외 처리 핸들러 설정시 스레드가 비정상적으로 종료되더라도, 애플리케이션 전체가 즉시 종료되지 않고, 다른 스레드들이 계속 정상적으로 동작함.
        * */
        
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("A critical error happened in thread " + t.getName()
                        + " the error is " + e.getMessage());
            }
        });
        
        thread.start();
    }
}
