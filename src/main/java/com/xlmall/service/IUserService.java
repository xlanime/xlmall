package com.xlmall.service;

import com.xlmall.common.ServerResponse;
import com.xlmall.pojo.User;

public interface IUserService {

    ServerResponse<User> userLogin(String username,String password);

    ServerResponse<String> userRegist(User user);

    ServerResponse<String> checkValid(String str,String type);

    ServerResponse<String> forgetGetQuestion(String username);

    ServerResponse<String> checkAnswer(String username,String question,String answer);

    ServerResponse<String> updatePasswordByToken(String username,String passwordNew,String token);

    ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user);

    ServerResponse<User> updateInfomation(User user);

    ServerResponse<User> getInfomation(Integer userId);

    ServerResponse<String> checkAdminRole(User user);
}
