package com.xlmall.controller.backend;

import com.xlmall.common.Const;
import com.xlmall.common.ServerResponse;
import com.xlmall.pojo.User;
import com.xlmall.service.IUserService;
import net.sf.jsqlparser.schema.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * 后台用户管理类
 */
@Controller
@RequestMapping("/manage/user/")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    /**
     * 管理员账户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login")
    public ServerResponse<User> userLogin(String username, String password, HttpSession session){
        ServerResponse<User> response = iUserService.userLogin(username,password);
        if(response.isSuccess()){
            User user = response.getData();
            if(user.getRole() == Const.Role.ROLE_ADMIN){
                return ServerResponse.createBySuccess("管理员登录成功",user);
            }
            return ServerResponse.createByErrorMessage("不是管理员账户");
        }
        return response;
    }
}
