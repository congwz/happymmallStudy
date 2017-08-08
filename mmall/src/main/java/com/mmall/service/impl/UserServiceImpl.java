package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
//import org.apache.commons.lang3.StringUtils;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

//Service注入到controller上，供controller调用。
//注入之前，先声明是个Service，名字为iUserService
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;   //UserMapper为接口对象（Dao层）

    @Override
    public ServerResponse<User> login(String username, String password) {
        //校验用户名存在与否（Dao写接口-->mapper的xml文件中写实现，即SQL查询，最后service调用方法checkUserName即可）
        int resultCount = userMapper.checkUserName(username);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        //todo 注册时，密码登录MD5(不可逆的加密方法)，数据库DB中存储时不是明文而是加密。
        //"todo" 表示程序写完后，提醒程序员还未写的部分，这部分用“todo”标注即可高亮出来，以提醒。程序写完code则可以删除。
        String md5Password = MD5Util.MD5EncodeUtf8(password);

        //检查用户名与密码是否正确
        //User user = userMapper.selectLogin(username,password);
        User user = userMapper.selectLogin(username,md5Password);  //MD5加密后，对密码进行校验
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        //处理返回值的密码，置空
        //org.apache.commons.lang3.StringUtils.EMPTY
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);

    }


    public ServerResponse<String> register(User user){
        /*
        //校验用户名是否存在
        int resultCount = userMapper.checkUserName(user.getUsername());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("用户名已存在");
        }
        */
        //复用代码，优化
        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess()){ //校验不成功，用户名已存在，返回validResponse
            return validResponse;

        }

        /*
        //校验邮箱是否存在
        resultCount = userMapper.checkEmail(user.getEmail());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email已存在");
        }
        */
        //复用代码，优化
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){  //校验不成功，返回validResponse
            return validResponse;

        }


        user.setRole(Const.Role.ROLE_CUSTOMER); //设置默认角色--->在Const常量类中定义
        //MD5加密（非对称加密）
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        /* resultCount = userMapper.insert(user);  //resultCount表示生效的行数 */
        int resultCount = userMapper.insert(user);  //resultCount表示生效的行数
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败"); //可能数据库方面的问题，导致的error
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    //校验email和用户名是否存在。防止恶意用户通过接口调用注册接口（注册功能），实时校验，给前台一个实时反馈
    public ServerResponse<String> checkValid(String str,String type){  //str代表值，type表示在Const类中声明的常量（EMAIL/USERNAME）
        if(org.apache.commons.lang3.StringUtils.isNoneBlank(type)){  //" "对于isNotBlank函数来说为false,对于isNotEmpty来说是true
            //type不空，才开始下面的校验
            //用if-else进行情况选择，如果情况比较多，则选择用switch-cse比较好
            if(Const.USERNAME.equals(type)) {
                //校验用户名是否存在
                int resultCount = userMapper.checkUserName(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)) {
                //校验邮箱是否存在
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }
        }else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse selectQuestion(String username){
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);  //复用checkValid中的检查用户名是否存在的方法
        if(validResponse.isSuccess()){  //0==0,为true
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String question = userMapper.selectQuestionByUsername(username);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(question)){ //问题内容不为空
            return ServerResponse.createBySuccess(question);  //成功，把question放到data中
        }

        return ServerResponse.createByErrorMessage("找回密码的问题是空的");

    }

    /*
    public static void main(String[] args) {
        System.out.print(UUID.randomUUID().toString());

    }*/

    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount > 0) {
            //说明问题及问题答案是这个用户的，并且是正确的
            String forgetToken = UUID.randomUUID().toString();  //UUID.randomUUID().toString()生成类似于这样的字符串：a2f3a643-32c1-4b32-ad0b-946004e84f1e
            //把forgetToken放到本地cache中，并设置其有效期
            //TokenCache.setKey("token_" +  username, forgetToken);
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);  //data中存放forgetToken
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }

    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        if(org.apache.commons.lang3.StringUtils.isBlank(forgetToken)){  //forgetToken参数为空(NULL)--->true，或者空白--->true，则返回参数错误
            return ServerResponse.createByErrorMessage("参数错误，token需要传递");
        }
        //校验用户名
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        //从缓存中获取token
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);  //一般会把"token_"做成常量，在TokenCache中做这个常量。
        if(org.apache.commons.lang3.StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }

        /*
        String a =null;
        if(a.equals("abc")){}  //这样写，就会报空指针异常，需要改成"abc".equals(a),所以，用org.apache.commons.lang3.StringUtils.equals()这个方法就不需要考虑这些，相对安全些
        */
        if(org.apache.commons.lang3.StringUtils.equals(forgetToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            if(rowCount > 0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");

    }

    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
        //防止横向越权，要校验一下这个用户的旧密码，一定要指定这个用户，因为我们会查询一个count(1)，如果不指定id，那么结果就是true啦（即count > 0）。
        int resultCount =userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0) {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createBySuccessMessage("密码更改成功");
        }

        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    public ServerResponse<User> updateInformation(User user){
        //username是不能被更新的
        //email也要进行一个校验，校验新的email是不是已经存在，并且存在的email如果相同的话，不能是我们当前的用户。
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email已存在，请更换email再尝试更新");
        }

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser); //updateByPrimaryKeySelective这个函数是只有字段不是空的时候才会更新。
        if(updateCount > 0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return  ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);  //密码置空
        return ServerResponse.createBySuccess(user);
    }



    //backend

    //校验用户是否为管理员
    public ServerResponse checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();

    }


}

