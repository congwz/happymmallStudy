package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String username);

    int checkEmail(String email);

    //Mybatis在声明多个参数时，需要用到Param注解，这样在写SQL的时候就对应注解里面的string内容
    User selectLogin(@Param("username") String username, @Param("password") String password);

    String selectQuestionByUsername(String username);

    //多个参数，记得加上注解@Param()
    int checkAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);

    //根据用户名更新密码
    int updatePasswordByUsername(@Param("username")String username,@Param("passwordNew")String passwordNew);

    //校验密码
    //int checkPassword(@Param("password")String password,@Param("userId")Integer userId);
    int checkPassword(@Param(value="password")String password,@Param("userId")Integer userId); //写法的作用同上一句code

    int checkEmailByUserId(@Param("email")String email,@Param("userId")Integer userId);

}