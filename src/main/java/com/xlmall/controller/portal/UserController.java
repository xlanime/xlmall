package com.xlmall.controller.portal;

import com.xlmall.common.Const;
import com.xlmall.common.ResponseCode;
import com.xlmall.common.ServerResponse;
import com.xlmall.pojo.User;
import com.xlmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpSession;

/**
 * 前台用户类
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> userLogin(String username, String password, HttpSession session){
        //调用Service中的登录方法
        ServerResponse<User> response = iUserService.userLogin(username,password);
        //如果登录成功
        if(response.isSuccess()){
            //将用户信息保存到session中。
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * 用户注销
     * @param session
     * @return
     */
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> userLogout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess("注销成功");
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @RequestMapping(value = "regist.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> userRegist(User user){
        return iUserService.userRegist(user);
    }

    /**
     * 用户信息校验。例如用户名校验、邮箱校验。
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    /**
     * 获取用户信息
     * @param session
     * @return
     */
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        //判断用户是否存在
        if(user == null){
            return ServerResponse.createByErrorMessage("用户尚未登陆");
        }
        return ServerResponse.createBySuccess(user);
    }

    /**
     * 根据用户名获取用户密码提示问题
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_get_question",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){

        return iUserService.forgetGetQuestion(username);
    }

    /**
     * 检查用户的问题及答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }

    /**
     * 根据用户名，token更新密码
     * @param username
     * @param passwordNew
     * @param token
     * @return
     */
    @RequestMapping(value = "update_password_by_token.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> updatePasswordByToken(String username,String passwordNew,String token){
        return iUserService.updatePasswordByToken(username,passwordNew,token);
    }

    /**
     * 登陆状态下重置密码
     * @param session
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户尚未登陆");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    /**
     * 登录状态下修改个人信息
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(value="update_infomation.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInfomation(HttpSession session,User user){
        //判断用户是否登录
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByErrorMessage("用户尚未登录");
        }
        //注意传递过来的数据没有ID,如果用户已登录就将id放到传递过来的user中。并进行更新
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse response = iUserService.updateInfomation(user);
        //更新成功
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * 获取用户详细信息并强制登录
     * @param session
     * @return
     */
    @RequestMapping(value = "get_infomation.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInfomation(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户尚未登录");
        }
        return iUserService.getInfomation(user.getId());
    }
}
