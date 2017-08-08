package com.mmall.dao;

import com.mmall.pojo.Cart;
import com.sun.javafx.image.IntPixelGetter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);   //没有字段的空判断

    int insertSelective(Cart record);  //有字段的空判断

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record); //有字段的空判断

    int updateByPrimaryKey(Cart record);  //没有字段的空判断

    //根据用户id,产品Id去查询购物车
    Cart selectCartByUserIDProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    //根据用户Id查询购物车
    List<Cart> selectCartByUserId(Integer userId);

    int selectCartProductCheckedStatusByUserId(Integer userId);

    int deleteByUserIdProductIds(@Param("userId") Integer userId,@Param("productList") List<String> productList);

    //int checkedOrUnCheckedAllProduct(@Param("userId") Integer userId,@Param("checked") Integer checked);
    //全选、全反选、单选、单反选都可以实现
    int checkedOrUnCheckedProduct(@Param("userId") Integer userId,@Param("checked") Integer checked,@Param("productId")Integer productId);

    int selectCartProductCount(Integer userId);


}