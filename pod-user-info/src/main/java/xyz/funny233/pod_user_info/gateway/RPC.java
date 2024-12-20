package xyz.funny233.pod_user_info.gateway;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import xyz.funny233.pod_user_info.model.UserModel;

@Component
public class RPC {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public boolean checkUserValid(int id) {
        try {
            // Step 1: 调用第一个服务，获取 server 地址
            String serverResponse = fetchServer();
            if (serverResponse == null) {
                return false;
            }

            // 解析 JSON 响应，提取 server 字段
            JsonNode jsonNode = objectMapper.readTree(serverResponse);
            String serverUrl = jsonNode.get("server").asText();

            // Step 2: 调用第二个服务，验证用户
            return checkUser(serverUrl, id);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String fetchServer() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3100/server/select?path=/auth"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body();
        }
        return null;
    }

    private boolean checkUser(String serverUrl, int id) throws IOException, InterruptedException {
        String authUrl = serverUrl + "/checkUser?id=" + id;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(authUrl))
                .GET()
                .build();

        HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        // System.out.println(response);
        return response.statusCode() == 200;
    }

    public static class UserAuth extends UserModel {
        public String username;
        public int role;
        public int id;

        public UserAuth(String username, int role, int id) {
            this.username = username;
            this.role = role;
            this.id = id;
        }

    }

    private static final HttpClient client = HttpClient.newHttpClient();

    public UserAuth[] fetchUserAuths() throws IOException, InterruptedException {
        // Step 1: 获取 server 地址
        String firstUrl = "http://localhost:3100/server/select?path=/auth";
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(firstUrl))
                .GET()
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        JsonNode serverResponse = objectMapper.readTree(response1.body());
        String serverUrl = serverResponse.get("server").asText();

        // Step 2: 使用 server 地址请求用户列表
        String secondUrl = serverUrl + "/getAllUsers";
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(secondUrl))
                .GET()
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        JsonNode usersResponse = objectMapper.readTree(response2.body());

        // Step 3: 解析用户数据
        JsonNode data = usersResponse.get("data");
        // System.out.println(usersResponse);
        List<UserAuth> userAuths = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode userNode : data) {
                String username = userNode.get("username").asText();
                int role = userNode.get("role").asInt();
                int id = userNode.get("id").asInt();
                userAuths.add(new UserAuth(username, role, id));
            }
        }

        return userAuths.toArray(new UserAuth[0]);
    }

}
