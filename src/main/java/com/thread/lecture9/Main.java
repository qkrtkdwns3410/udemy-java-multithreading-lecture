package com.thread.lecture9;

/**
 * packageName    : com.thread.lecture9
 * fileName       : Main
 * author         : Jun
 * date           : 24. 12. 22.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 12. 22.        Jun       최초 생성
 */
public class Main {
    public static void main(String[] args) {
    
    }
    
    public static boolean isShadeOfGray(int red, int green, int blue) {
        //30은 색상 간의 차이를 나타내는 임계값
        // 각각 색상 차이가 30이 넘지 않으면 회색으로 판단
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs(green - blue) < 30;
    }
    
    public static int createRGBFromColors(int red, int green, int blue) {
        //rbg 는 각각의 색상( 빨 초 파) 를 8비트 ( 0 ~ 255) 로 표현함
        // 3가지 색상을 결합해 하나의 32비트 정수로 표현이 가능함.
        // 1. 빨강 : 상위 8비트 ( 16 ~ 23)
        // 2. 초록 : 중간 8비트 ( 8 ~ 15)
        // 3. 파랑 : 하위 8비트 ( 0 ~ 7)
        // 4. 알파 : 8비트 ( 24 ~ 31) 항상 255 (0xFF) 로 설정
        
        // 비트마스킹을 통한 RGB 색상 생성
        int rgb = 0;
        
        rgb |= blue; // 파랑값을 하위 8비트에 할당
        rgb |= green << 8; // 초록값을 중간 8비트에 할당
        rgb |= red << 16; // 빨강값을 상위 8비트에 할당
        
        rgb |= 0xFF000000; // 알파값을 255로 설정
        
        return rgb;
    }
    
    public static int getRed(int rgb) {
        // 비트마스킹을 통한 Red 색상 추출
        return (rgb & 0x00FF0000) >> 16;
    }
    
    public static int getGreen(int rgb) {
        //비스마스킹을 통한 Green 색상 추출
        return (rgb & 0x0000FF00) >> 8;
    }
    
    public static int getBlue(int rgb) {
        return rgb & 0x000000FF;
    }
}
