package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import com.sun.corba.se.spi.activation.Server;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/8/4 0004.
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    //public ServerResponse add(Integer userId,Integer productId,Integer count){
    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count){

        //加上校验
        if(productId == null || count ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        //从数据库中查找cart--->首先注入cartMapper---->然后在Dao层中写方法声明---->mapper层写方法实现。
        Cart cart = cartMapper.selectCartByUserIDProductId(userId,productId);
        if(cart == null){
            //这个产品不在购物车里，需要新增一个这个产品的记录
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);   //是否Checked，做成常量哦
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);

            cartMapper.insert(cartItem);

        }else{
            //这个产品已经在购物车里了
            //如果产品已经存在，数量相加
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        /*
        //封装一个方法（购物车逻辑），该方法（getCartVoLimit）是购物车的核心方法。
        CartVo cartVo = this.getCartVoLimit(userId);
        //return null;
        return ServerResponse.createBySuccess(cartVo);
        */
        return this.list(userId);  //调用封装好的list方法
    }

    //更新购物车
    public ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count){
        if(productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIDProductId(userId,productId);
        if(cart != null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart); //更新
        /*
        CartVo cartVo = this.getCartVoLimit(userId);  //调用购物车核心方法getCartVoLimit
        return ServerResponse.createBySuccess(cartVo);
        */
        return this.list(userId);  //调用封装好的list方法
    }

    //购物车中删除产品
    public ServerResponse<CartVo> deleteProduct(Integer userId,String productIds){
        //guava的splitter方法,否则，需要把productId转成数组，然后遍历数组添加到集合中。
        List<String> productList = Splitter.on(",").splitToList(productIds);  //用“,”做分割,productIds是目标字符串
        if(CollectionUtils.isEmpty(productList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());  //从参数错误
        }
        //productList不是空，则需要删除购物车中产品的那条数量--->Dao层声明该方法，mapper中实现该方法
        cartMapper.deleteByUserIdProductIds(userId,productList);
        /*
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
        */
        return this.list(userId);  //调用封装好的list方法
    }

    //查看购物车详情
    public ServerResponse<CartVo> list(Integer userId){
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    /*
    //全选 or 全反选
    public ServerResponse<CartVo> selectOrUnSelectAll(Integer userId,Integer checked){
        cartMapper.checkedOrUnCheckedAllProduct(userId,checked);
        return this.list(userId);
    }
    */

    //全选 or 全反选 or 单独选某个产品 or 单独反选某个产品
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer checked,Integer productId){
        cartMapper.checkedOrUnCheckedProduct(userId,checked,productId);
        return  this.list(userId);
    }

    //查询当前用户的购物车里面的产品数量
    public ServerResponse<Integer> getCartProductCount(Integer userId){
        if(userId == null){
            return ServerResponse.createBySuccess(0);
        }
        //返回购物车产品的总数量--->写Dao层接口，mapper层实现（SQL）
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }


    //封装购物车产品Vo对象：CartProductVo和购物车Vo: CartVo
    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();  //guava中的Lists方法：newArrayList

        BigDecimal cartTotalPrice = new BigDecimal("0");  //购物车总价初始化--->使用String构造器初始化。注意：数字计算的时候，如何避免丢失精度

        if(CollectionUtils.isNotEmpty(cartList)){
            for(Cart cartItem : cartList){
                CartProductVo cartProductVo =new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if(product != null){
                    //继续组装组装CartProductVo
                    cartProductVo.setProductMainImage(product.getMainImage());  //商品主图
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());  //产品库存
                    //判断库存
                    int buyLimitCount = 0;
                    if(product.getStock() >= cartItem.getQuantity()){
                        //产品库存充足时
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMI_NUM_SUCCESS); //和前端有约定，符合库存要求：产品库存大于购物车中的库存
                    }else {
                        //把有效库存放进去
                        buyLimitCount = product.getStock();  //最大值就是产品的库存
                        cartProductVo.setLimitQuantity(Const.Cart.LIMI_NUM_FAIL);  //即超出产品的库存:Fail
                        //购物车中更新有效库存--->更新选择selective这种方法，所以我们只关心要set哪些字段，然后把这些字段更新
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }//else
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算某一个产品的总价--->调用Util工具包下的BigDecimal封装类的方法
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                    //勾选
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }// if(product != null)

                if(cartItem.getChecked() == Const.Cart.CHECKED){
                    //如果已经勾选，增加到整个购物车的总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());  //购物车总价

                    //至此，cartProductVo已经组装好。
                }
                cartProductVoList.add(cartProductVo);
            }//for(Cart cartItem : cartList
        }//if(CollectionUtils.isNotEmpty(cartList))

        //组装cartVo
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));  //是否全选--->写一个私有方法(getAllCheckedStatus)来接受这个返回值
        //把购物车图片的host前缀加上
        //ftp.server.http.prefix=http://img.happymmall.com/
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    //购物车是否全选
    private boolean getAllCheckedStatus(Integer userId){
        if(userId == null){
            return false;
        }
        //若用户不是空（null），则判断一下是不是全选，写SQL的实现。
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;  //cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0若等于0，说明全选了。
    }


}
