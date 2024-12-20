package xyz.funny233.pod_user_info.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import xyz.funny233.pod_user_info.gateway.RPC;
import xyz.funny233.pod_user_info.model.UserModel;
import xyz.funny233.pod_user_info.repository.UserRepository;
import xyz.funny233.pod_user_info.service.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
public class UserController {
    
    @Autowired
    private UserService userService;


    @GetMapping("/userinfo")
    public Response getUserInfo(@RequestHeader(value = "x-forward-id") int id) {
        var user = userService.getUserInfoByUserId(id);
        return new Response(200, "success", user);
    }

    @PostMapping("/userinfo")
    public ResponseEntity<Response> postUserInfo(
        @RequestHeader(value = "x-forward-id") int id,
        @RequestBody() Map params
    ) {
        String nickname = (String)params.get("nickname");
        String msg = userService.updateNicknameByUserId(id, nickname);
        if(msg.equals("success")) {
            return ResponseEntity.ok().body(new Response(200, "success"));
        } else {
            return ResponseEntity.badRequest().body(new Response(400, msg));
        }
    }

    @Value("${file.upload-dir}")
    String uploadDir;

    @PostMapping("/avatar")
    public ResponseEntity<Response> postAvatar(
        @RequestHeader(value = "x-forward-id") int id,
        @RequestParam("avatar") MultipartFile avatar
    ) {
        if (avatar.isEmpty()) {
            return ResponseEntity.badRequest().body(new Response(400, "Please select a file to upload"));
        }
        try {
            // 确保上传目录存在
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            var user = userService.getUserInfoByUserId(id);
            if(user.getAvatar() != null) {
                // 删除原头像
                Path oldAvatarPath = uploadPath.resolve(user.getAvatar().substring(15));
                Files.deleteIfExists(oldAvatarPath);
            }

            // 保存文件
            String fileName = UUID.randomUUID().toString() + ".jpg";
            Path filePath = uploadPath.resolve(fileName);
            avatar.transferTo(filePath);
            userService.updateAvatarByUserId(id, "/static/avatar/" + fileName);
            user = userService.getUserInfoByUserId(id);
            return ResponseEntity.ok().body(new Response(200, "success", user));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Response(400, "Failed to upload file"));
        }
    }

    @GetMapping("/userinfo/all")
    public ResponseEntity<Response> getAllUserInfo(
        @RequestHeader(value = "x-forward-role") int role
    ) {
        // System.out.println(role);
        if(role != 0) {
            return ResponseEntity.badRequest().body(new Response(400, "permission denied"));
        }
        return ResponseEntity.ok().body(new Response(200, "success", userService.getAllUsers()));
    }

}
