package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

//通用的数据端响应对象
//泛型来声明这个类，泛型代表响应里要封装的数据的类型。
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)  //value值为null的不需要封装在json中
//保证序列化json的时候，如果null的对象，key也会消失
public class ServerResponse<T> implements Serializable {

    private int status;
    private String msg;
    private T data;  //泛型的好处：在返回的时候，可以指定泛型里面的内容，也可以不指定泛型的类型。

    //私有构造方法
    private ServerResponse(int status){
        this.status = status;
    }

    private ServerResponse(int status,T data){
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status,String msg,T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ServerResponse(int status,String msg){
        this.status = status;
        this.msg = msg;
    }

    //确认响应是不是一个正确的（成功的）响应
    @JsonIgnore  //加上这个注解是为了将这个字段在序列化时不会显示在json中。
    //使之不在json序列化结果中
    public boolean isSuccess(){
       //return this.status == 0;
       return this.status == ResponseCode.SUCCESS.getCode();  //返回true或者false
    } //0-true，响应成功，非0-false

    public  int getStatus(){
        return status;
    }
    public T getData(){
        return  data;
    }
    public String getMsg(){
        return msg;
    }

    //静态方法
    public static <T> ServerResponse<T> createBySuccess(){

        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode()); //调用私有构造方法：ServerResponse，返回status:0

    }
    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);

    }
    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }
    //错误时，返回提示
    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }
    //code做成变量，根据返回码code,提示不同信息的提示，如需要登录，密码错误等信息
    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMessage){
        return new ServerResponse<T>(errorCode,errorMessage);
    }


}
