package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/8/4 0004.
 */

@Controller
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    private ICartService iCartService;

    //添加到购物车
    //public ServerResponse add(HttpSession session,Integer count,Integer productId){
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpSession session, Integer count, Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());  //需要强制登录
        }
        //编写添加购物车逻辑--->创建Service
        return iCartService.add(user.getId(),productId,count);
    }

    //更新购物车
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpSession session, Integer count, Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());  //需要强制登录
        }
        //编写添加Service逻辑
        return iCartService.update(user.getId(),productId,count);
    }

    //在购物车中删除产品
    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(HttpSession session,String productIds){  //删除产品的时候可能删除多个，所以和前端约定，用“,”来分割productId
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());  //需要强制登录
        }
        //编写添加Service逻辑
        return iCartService.deleteProduct(user.getId(),productIds);
    }

    //查看购物车详情
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());  //需要强制登录
        }
        //编写添加Service逻辑
        return iCartService.list(user.getId());
    }

    //全选
    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());  //需要强制登录
        }
        //编写添加Service逻辑
        //return iCartService.selectOrUnSelectAll(user.getId(), Const.Cart.CHECKED);
        return iCartService.selectOrUnSelect(user.getId(), Const.Cart.CHECKED,null);
    }

    //全反选
    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());  //需要强制登录
        }
        //编写添加Service逻辑
        //return iCartService.selectOrUnSelectAll(user.getId(), Const.Cart.UN_CHECKED);
        return iCartService.selectOrUnSelect(user.getId(), Const.Cart.UN_CHECKED,null);
    }


    //单独反选某个产品
    @RequestMapping("un_select.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelect(HttpSession session,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());  //需要强制登录
        }
        //编写添加Service逻辑
        //return iCartService.selectOrUnSelectAll(user.getId(), Const.Cart.UN_CHECKED);
        return iCartService.selectOrUnSelect(user.getId(), Const.Cart.UN_CHECKED,productId);
    }

    //单独选某个产品
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<CartVo> Select(HttpSession session,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());  //需要强制登录
        }
        //编写添加Service逻辑
        //return iCartService.selectOrUnSelectAll(user.getId(), Const.Cart.UN_CHECKED);
        return iCartService.selectOrUnSelect(user.getId(), Const.Cart.CHECKED,productId);
    }

    //查询当前用户的购物车里面的产品数量。原则：如果一个产品有10个，那么数量就是10
    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createBySuccess(0);  //没有登录的话，返回数量是0
        }
        //编写添加Service逻辑
        return iCartService.getCartProductCount(user.getId());
    }
}
