package com.gstore.service;

import com.gstore.common.ServerResponse;
import com.gstore.pojo.User;

/**
 * Created by Ocean .
 */
public interface IUserService {
    ServerResponse<User> login(String userName, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str,String type);

    ServerResponse selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username,String question,String answer);

    ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken);

    ServerResponse<String> resetPassword(String passWordOld,String passWordNew,User user);

    ServerResponse<User> updateinfomation(User user);

    ServerResponse<User> getInformation(Integer userId);



    //backend
    ServerResponse checkAdminRole(User user);
}
