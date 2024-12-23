package xyz.funny233.pod_user_info.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.stereotype.Component;

import xyz.funny233.pod_user_info.gateway.RPC;
import xyz.funny233.pod_user_info.gateway.RPC.UserAuth;
import xyz.funny233.pod_user_info.model.UserModel;
import xyz.funny233.pod_user_info.repository.UserRepository;

@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;
    public UserModel getUserInfoByUserId(int userId) {
        if(rpc.checkUserValid(userId) == false) {
            var user = userRepository.findByUserId(userId);
            if(user != null) {
                userRepository.delete(user);
            }
            return null;
        }
        var user = userRepository.findByUserId(userId);
        if(user == null) {
            user = new UserModel();
            user.setUserId(userId);
            user.setNickname(String.valueOf(userId));
            userRepository.save(user);
            return user;
        }
        // System.out.println(user.getNickname().equals("") || user.getNickname() == null);
        if(user.getNickname() == null || user.getNickname().equals("")) {
            user.setNickname(String.valueOf(userId));
            userRepository.save(user);
        }
        return userRepository.findByUserId(userId);
    }

    public String updateNicknameByUserId(int userId, String nickname) {
        if(rpc.checkUserValid(userId) == false) {
            var user = userRepository.findByUserId(userId);
            if(user != null) {
                userRepository.delete(user);
            }
            return null;
        }
        if(nickname == null || nickname.length() == 0) {
            return "nickname is empty";
        }
        if(nickname.length() > 20) {
            return "nickname is too long";
        }
        var user = userRepository.findByUserId(userId);
        if(user == null) {
            user = new UserModel();
            user.setUserId(userId);
            userRepository.save(user);
        }
        user.setNickname(nickname);
        userRepository.save(user);
        return "success";
    }

    public String updateAvatarByUserId(int userId, String avatar) {
        if(rpc.checkUserValid(userId) == false) {
            var user = userRepository.findByUserId(userId);
            if(user != null) {
                userRepository.delete(user);
            }
            return null;
        }
        if(avatar == null || avatar.length() == 0) {
            return "avatar is empty";
        }
        var user = userRepository.findByUserId(userId);
        if (user == null) {
            user = new UserModel();
            user.setUserId(userId);
            userRepository.save(user);
        }
        user.setAvatar(avatar);
        userRepository.save(user);
        return "success";
    }

    RPC rpc = new RPC();

    public Object[] getAllUsers() {
        try {
            var usersAuth = rpc.fetchUserAuths();
            var users = userRepository.findAll().toArray(new UserModel[0]);
            for(int i = 0; i < usersAuth.length; i++){
                for(int j = 0; j < users.length; j++){
                    if(usersAuth[i].id == users[j].getUserId()){
                        usersAuth[i].setNickname(users[j].getNickname());
                        usersAuth[i].setAvatar(users[j].getAvatar());
                    }
                }
            }
            return usersAuth;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
