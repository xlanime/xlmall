package com.xlmall.service.impl;

import com.xlmall.common.Const;
import com.xlmall.common.ServerResponse;
import com.xlmall.common.TokenCache;
import com.xlmall.dao.UserMapper;
import com.xlmall.pojo.User;
import com.xlmall.service.IUserService;
import com.xlmall.util.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> userLogin(String username, String password) {
        //判断用户名是否存在
        int rowCount = userMapper.checkUsername(username);

        if(rowCount == 0){
            //用户名不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //密码登录MD5加密
        password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,password);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功！",user);
    }

    @Override
    public ServerResponse<String> userRegist(User user){
        ServerResponse serverResponse = checkValid(user.getUsername(),Const.USERNAME);
        //校验是否可用。返回值是true则表明用户名可用。
        if(!serverResponse.isSuccess()){
            return ServerResponse.createByErrorMessage(serverResponse.getMsg());
        }
        serverResponse = checkValid(user.getEmail(),Const.EMAIL);
        if(!serverResponse.isSuccess()){
            return ServerResponse.createByErrorMessage(serverResponse.getMsg());
        }
        //设置用户权限为普通用户
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //密码MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int rowCount = userMapper.insert(user);
        if(rowCount>0){
            return ServerResponse.createBySuccess("注册成功");
        }
        return ServerResponse.createByErrorMessage("注册失败");
    }

    @Override
    public ServerResponse<String> checkValid(String str,String type){
        //首先判断type是否为空
        if(StringUtils.isNotBlank(type)){
            //校验用户名
            if(type.equals(Const.USERNAME)){
                int rowCount = userMapper.checkUsername(str);
                if(rowCount>0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
                return ServerResponse.createBySuccess("用户名校验成功");
            }

            //校验邮箱
            if(type.equals(Const.EMAIL)){
                int rowCount = userMapper.checkEmail(str);
                if(rowCount>0){
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
                return ServerResponse.createBySuccess("邮箱校验成功");
            }
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createByErrorMessage("参数不能为空");
    }

    @Override
    public ServerResponse<String> forgetGetQuestion(String username) {
        //校验用户名是否存在
        ServerResponse response = this.checkValid(username,Const.USERNAME);
        if(response.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("获取用户问题为空");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        ServerResponse response = this.checkValid(username,Const.USERNAME);
        if(response.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        int rowCount = userMapper.checkAnswer(username,question,answer);
        if(rowCount>0){
            String tokenUUID = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,tokenUUID);
            return ServerResponse.createBySuccess("答案验证成功",tokenUUID);
        }
        return ServerResponse.createByErrorMessage("答案验证错误");
    }

    @Override
    public ServerResponse<String> updatePasswordByToken(String username,String passwordNew,String token){
        ServerResponse response = this.checkValid(username,Const.USERNAME);
        if(response.isSuccess()){
           return ServerResponse.createByErrorMessage("用户名不存在");
        }

        //判断token是否存在
        if(!StringUtils.isNotBlank(token)){
            return ServerResponse.createByErrorMessage("参数错误：token不能为空");
        }

        String tokenCache = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.equals(tokenCache,token)){
            String MD5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,MD5Password);
            if(rowCount > 0) {
                return ServerResponse.createBySuccess("密码更新成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token过期或不正确");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
        int rowCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(rowCount == 0){
            return ServerResponse.createByErrorMessage("旧密码验证失败");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createBySuccessMessage("更新密码成功");
        }
        return ServerResponse.createByErrorMessage("更新密码失败");
    }

    @Override
    public ServerResponse<User> updateInfomation(User user){
        //首先需要校验邮箱
        ServerResponse response = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMessage("邮箱已被使用");
        }
        //这里设置一个updateUser的原因是，尽量少更新字段。
        //因为如果不设置，这里的udpateUser，则还会更新Name等字段。虽然它是没变的。
//        User updateUser = new User();
//        updateUser.setId(user.getId());
//        updateUser.setPassword(user.getPassword());
//        updateUser.setEmail(user.getEmail());
//        updateUser.setPhone(user.getPhone());
//        updateUser.setQuestion(user.getQuestion());
//        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return ServerResponse.createBySuccess("用户信息更新成功",user);
        }
        return ServerResponse.createByErrorMessage("用户信息更新失败");
    }

    @Override
    public ServerResponse<User> getInfomation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("未找到当前用户");
        }
        //如果找到了将密码置为空再返回
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("获取用户信息成功",user);
    }


    /**
     * 校验用户是否为管理员
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
