package com.gstore.controller.backend;

import com.gstore.common.Const;
import com.gstore.common.ServerResponse;
import com.gstore.pojo.User;
import com.gstore.service.IUserService;
import com.gstore.util.CookieUtil;
import com.gstore.util.JsonUtil;
import com.gstore.util.RedisShardedPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by Ocean .
 */
@Controller
@RequestMapping("/manage/user")
public class UserManageController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse){
        ServerResponse<User> response = iUserService.login(username,password);
        if(response.isSuccess()){
            User user = response.getData();
            if (user.getRole() == Const.Role.ROLE_ADMIN){
                //说明登录是管理员
//                session.setAttribute(Const.CURRENT_USER,user);

                //新增redis共享cookie，session 的方式
                CookieUtil.writeLoginToken(httpServletResponse,session.getId());
                RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);

                return response;

            }else {
                return ServerResponse.createByErrorMessage("不是管理员用户，无法登录");
            }
        }
        return response;
    }
}
