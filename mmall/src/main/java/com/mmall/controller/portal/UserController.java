package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/6/5 0005.
 */
@Controller
@RequestMapping("/user/")  //请求路径都打到/user/下。
public class UserController {

    //把Service注入进来
    @Autowired
    private IUserService iUserService;  //iUserService与@Service("iUserService")中的iUserService与保持一致，这样才会注入。
    /**
     * 用户登录
     * */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody  //MVC返回值通过ResponseBody这个注解实现自动序列化成json的功能
    //public Object login(String username, String password, HttpSession session){
    //service-->mybatis-->dao
    public ServerResponse<User> login(String username, String password, HttpSession session){

        ServerResponse<User> response = iUserService.login(username,password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());  //常量类Const中定义了key
        }
        return response;
    }

    //@RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    //登出功能
    public ServerResponse<String> logout(HttpSession session){

        session.removeAttribute(Const.CURRENT_USER);  //删除session key即删除session
        return ServerResponse.createBySuccess();

    }

    //@RequestMapping(value = "register.do", method = RequestMethod.GET)
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    //注册
    public ServerResponse<String> register(User user){
        //调用service
        return iUserService.register(user);
    }

    //@RequestMapping(value = "check_valid.do", method = RequestMethod.GET)
    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    //校验
    public ServerResponse<String> checkValid(String str,String type){

        return  iUserService.checkValid(str,type);
    }

    //@RequestMapping(value = "get_user_info.do", method = RequestMethod.GET)
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    //获取用户登录信息
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
    }

    //@RequestMapping(value = "forget_get_question.do", method = RequestMethod.GET)
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    //忘记密码，返回要找回密码提示的问题
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);  //返回找回密码的问题
    }


    //@RequestMapping(value = "forget_check_answer.do", method = RequestMethod.GET)
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    //校验问题的答案是否正确,使用本地缓存。
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);

    }

    //@RequestMapping(value = "forget_reset_password.do", method = RequestMethod.GET)
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    //忘记密码的重置密码功能
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);

    }

    //@RequestMapping(value = "reset_password.do", method = RequestMethod.GET)
    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    //登录状态下的重置密码功能
    public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    //@RequestMapping(value = "update_information.do", method = RequestMethod.GET)
    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    //更新用户个人信息
    public ServerResponse<User> update_information(HttpSession session,User user){
        //只有在登录状态下才能更新用户个人信息
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        //传过来的参数中没有user的id，所以需要从之前的user里获取id，然后赋给新的user
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());    //username是不能被更新的
        //Service层逻辑
        ServerResponse<User> response = iUserService.updateInformation(user);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    //@RequestMapping(value = "get_information.do", method = RequestMethod.GET)
    @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
    @ResponseBody
    //获取用户详细信息
    public ServerResponse<User> get_information(HttpSession session){
        //检查用户是否登录，若没有登录，则强制登录
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            //return ServerResponse.createByErrorMessage("用户未登录");
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录status=10");
        }

        //调用Service
        return iUserService.getInformation(currentUser.getId()); // 当前登录用户的id
    }

}
