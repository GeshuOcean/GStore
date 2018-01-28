package com.gstore.service.impl;

import com.gstore.common.Const;
import com.gstore.common.ServerResponse;
import com.gstore.dao.UserMapper;
import com.gstore.pojo.User;
import com.gstore.service.IUserService;
import com.gstore.util.MD5Util;
import com.gstore.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by Ocean .
 */
@Service("iUserService")//注入controller
public class UserServiceImpl implements IUserService{
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0 ){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user  = userMapper.selectLogin(username,md5Password);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    public ServerResponse<String> register(User user){
        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if (!validResponse.isSuccess()){
            return validResponse;
        }

        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if (!validResponse.isSuccess()){
            return validResponse;
        }


        user.setRole(Const.Role.ROLE_CUSTOMER);

        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0 ){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return  ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if(StringUtils.isNotBlank(type)){
            //开始校验
            if (Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0){
                    return ServerResponse.createByErrorMessage("用户已存在");
                }
            }
            if (Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0){
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }

        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    @Override
    public ServerResponse selectQuestion(String username) {
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        /**
         *  SUCCESS(0,"SUCCESS"),
         ERROR(1,"ERROR"),
         NEED_LOGIN(10,"NEED_LOGIN"),
         不为零则是失败
         */
        if(validResponse.isSuccess()){
            return  ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNoneBlank(question)){
            return ServerResponse.createBySuccessMessage(question);

        }
        return ServerResponse.createBySuccess("找回密码问题为空");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if (resultCount>0){
            String forgetToken = UUID.randomUUID().toString();
//            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            RedisPoolUtil.setEx(Const.TOKEN_PREFIX + username,forgetToken,60*60*12);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("密保答案错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)){
            return  ServerResponse.createByErrorMessage("参数错误，Token需要传递");
        }

        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            return  ServerResponse.createByErrorMessage("用户不存在");
        }

        String token = RedisPoolUtil.get(Const.TOKEN_PREFIX+username);
//        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或过期");
        }

        if (StringUtils.equals(forgetToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            if (rowCount>0){
                return ServerResponse.createBySuccessMessage("密码修改成功");
            }

        }else{
            return ServerResponse.createByErrorMessage("token错误，请重新获取密码的token");
        }
        return ServerResponse.createByErrorMessage("修改失败");
    }

    @Override
    public ServerResponse<String> resetPassword(String passWordOld, String passWordNew, User user) {
        //防止横向越权，要校验仪一下这个用户的旧密码，一定要指定是这个用户，因为我们会查询一个count（1）如果不指定id，那么结果就是true
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passWordOld),user.getId());
        if (resultCount == 0){
            return  ServerResponse.createByErrorMessage("旧密码错误");

        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passWordNew));
        int updateCount = userMapper.updateByPrimaryKey(user);
        if (updateCount>0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    @Override
    public ServerResponse<User> updateinfomation(User user) {
        //username不能被更新
        //email要进行一个校验，校验这个email是否已存在，并且存在的email如果相同的话，不能是我们当前这个用户的
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if (resultCount>0){
            return  ServerResponse.createByErrorMessage("email已存在，请更换email再尝试更新");

        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount >0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);

        }
        return ServerResponse.createByErrorMessage("更新失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null){
            return ServerResponse.createByErrorMessage("找不到该用户");

        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);

    }

    @Override
    //backend

    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
