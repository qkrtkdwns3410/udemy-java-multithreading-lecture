package com.thread.lecture9;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public static final String SOURCE_FILE = "./src/main/resources/many-flowers.jpg";
    public static final String DESTINATION_FILE = "./src/main/resources/many-flowers-gray.jpg";
    
    public static void main(String[] args) throws IOException {
        BufferedImage originalImage = ImageIO.read(new File(SOURCE_FILE));
        BufferedImage grayImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        
        long startTime = System.currentTimeMillis();
        
        //recolorSingleThread(originalImage, grayImage);
        recolorMultiThread(originalImage, grayImage, 8);
        
        long endTime = System.currentTimeMillis();
        
        System.out.println("Multi thread execution time : " + (endTime - startTime));
        
        File outputFile = new File(DESTINATION_FILE);
        ImageIO.write(grayImage, "jpg", outputFile);
    }
    
    public static void recolorSingleThread(BufferedImage originalImage, BufferedImage newImage) {
        recolorImage(originalImage, newImage, 0, 0, originalImage.getWidth(), originalImage.getHeight());
    }
    
    public static void recolorMultiThread(BufferedImage originalImage, BufferedImage newImage, int numberOfThreads) {
        List<Thread> threads = new ArrayList<>();
        
        int width = originalImage.getWidth();
        int height = originalImage.getHeight() / numberOfThreads;
        
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadMultiplier = i;
            
            Thread thread = new Thread(() -> {
                int leftCorner = 0;
                int topCorner = height * threadMultiplier;
                
                recolorImage(originalImage, newImage, leftCorner, topCorner, width, height);
            });
            
            threads.add(thread);
        }
        
        threads.forEach(Thread::start);
        
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        });
    }
    
    public static void recolorImage(BufferedImage originalImage, BufferedImage newImage, int leftCorner, int topCorner, int width, int height) {
        for (int x = leftCorner; x < leftCorner + width && x < originalImage.getWidth(); x++) {
            for (int y = topCorner; y < topCorner + height && y < originalImage.getHeight(); y++) {
                recolorPixel(x, y, originalImage, newImage);
            }
        }
    }
    
    public static void recolorPixel(int x, int y, BufferedImage originalImage, BufferedImage newImage) {
        int rgb = originalImage.getRGB(x, y);
        
        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);
        
        int newRed;
        int newGreen;
        int newBlue;
        
        if (isShadeOfGray(red, green, blue)) {
            newRed = Math.min(255, red + 10);
            newGreen = Math.max(0, green - 80);
            newBlue = Math.max(0, blue - 20);
        } else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }
        
        int newRGB = createRGBFromColors(newRed, newGreen, newBlue);
        setRGB(newImage, x, y, newRGB);
    }
    
    public static void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }
    
    public static boolean isShadeOfGray(int red, int green, int blue) {
        //30은 색상 간의 차이를 나타내는 임계값
        // 각각 색상 차이가 30이 넘지 않으면 회색으로 판단
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs(green - blue) < 30;
    }
    
    public static int createRGBFromColors(int red, int green, int blue) {
        //rbg 는 각각의 색상( 빨 초 파) 를 8비트 (0 ~ 255) 로 표현함
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
