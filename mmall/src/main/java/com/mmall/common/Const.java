package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by Administrator on 2017/6/7 0007.
 */
public class Const {
    public  static  final  String CURRENT_USER = "currentUser";

    public  static  final  String EMAIL = "email";
    public  static  final  String USERNAME = "username";

    //前端商品动态排序用的一个常量。以在Service（ProductServiceImpl.java）中调用
    public interface ProductListOrderBy{
        //price_desc为Set中的第一个元素，price_asc为Set中的第二个元素
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");  //降序    升序
    }

    //购物车相关常量
    public interface Cart{
        int CHECKED = 1; //即购物车选中状态
        int UN_CHECKED = 0; //购物车中未选中状态

        String LIMI_NUM_FAIL = "LIMI_NUM_FAIL";
        String LIMI_NUM_SUCCESS = "LIMI_NUM_SUCCESS";

    }


    //普通用户和管理员是一个组的，用内部接口类把常量进行分组，而不用枚举
    public interface Role{
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1; //管理员
    }

    //枚举--->供“前端商品详情功能”中service实现方法中使用
    public enum ProductStatusEnum{

        ON_SALE(1,"在线");

        private String value;
        private int code;
        //构造器
        ProductStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        //get方法
        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }
}
