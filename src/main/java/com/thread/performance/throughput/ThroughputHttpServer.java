package com.thread.performance.throughput;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * packageName    : com.thread.performance.throughput
 * fileName       : ThroughputHttpServer
 * author         : ipeac
 * date           : 24. 12. 25.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 12. 25.        ipeac       최초 생성
 */
public class ThroughputHttpServer {
    private static final String INPUT_FILE = "./resources/war_and_peace.txt";
    public static final int NUMBER_OF_THREADS = 4;
    
    public static void main(String[] args) throws IOException {
        //책 데이터 로딩 메가바이트 크기임.
        String text = new String(Files.readAllBytes(Paths.get(INPUT_FILE)));
        
        startServer(text);
    }
    
    private static void startServer(String text) throws IOException {
        //backlog 크기(대기열 크기) 0 모든 요청은 스레드 풀 대기열로 전달해야하기에
        // 서버가 들어오는 모든 요청을 즉시 처리하도록 설계된 경우, 백로그 큐를 사용하지 않고 스레드 풀로 전달하는 게 더 효율적임
        // 지연 최소화 -> 요청이 들어오는 즉시 처리되도록 합니다.
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        
        //검색 라우트 uri 인 경우 WordCountHandler를 호출
        server.createContext("/search", new WordCountHandler(text));
        
        Executor executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        executor.execute(server::start);
    }
    
    private static class WordCountHandler implements HttpHandler {
        private String text;
        
        public WordCountHandler(String text) {
            this.text = text;
        }
        
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String[] keyValue = query.split("=");
            String action = keyValue[0];
            String word = keyValue[1];
            
            if (!"word".equals(action)) {
                exchange.sendResponseHeaders(400, 0);
                return;
            }
            
            long count = countWord(word);
            
            byte[] response = Long.toString(count).getBytes();
            
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        }
        
        private long countWord(String word) {
            long count = 0;
            int index = 0;
            while (index >= 0) {
                //단어가 발견되지 않으면 -1을 반환
                index = text.indexOf(word, index);
                
                //단어가 발견되면 count 와 index를 증가시킴
                if (index >= 0) {
                    count++;
                    index++;
                }
            }
            
            return count;
        }
        
    }
}
