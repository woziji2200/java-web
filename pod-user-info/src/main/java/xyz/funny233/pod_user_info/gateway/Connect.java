package xyz.funny233.pod_user_info.gateway;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@EnableScheduling
public class Connect {

    RestTemplate restTemplate = new RestTemplate();

    class ConnectResponse {
        String message;
    }

    class ConnectRequest {
        String path = "/userinfo";
        boolean auth = true;
        Server server = new Server();

        class Server {
            String name = "pod-user-info";
            String host = "http://localhost:3002";
            String usage = "1";
            boolean available = true;
        }
    }

    @Scheduled(fixedRate = 3000)
    public void run () throws Exception {
        try {
                   HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3100/server/join"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"path\":\"/userinfo\",\"auth\":true,\"server\":{\"name\":\"pod-user-info\",\"host\":\"http://localhost:3002\",\"usage\":\"1\",\"available\":true}}"))
                .build();
        // 发送请求并获取响应
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
 
        } catch (Exception e) {
            System.out.println("拒绝加入节点");
        }

        // 打印响应
        // System.out.println(response.body());
    }
}
