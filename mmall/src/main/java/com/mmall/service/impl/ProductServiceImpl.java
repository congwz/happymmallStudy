package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategotyService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/2 0002.
 */

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    //注入ProductMapper
    @Autowired
    private ProductMapper productMapper;

    //注入CategoryMapper
    @Autowired
    private CategoryMapper categoryMapper;

    //注入CategoryService
    @Autowired
    private ICategotyService iCategotyService;

    //保存（新增）与更新产品
    public ServerResponse saveOrUpdateProduct(Product product) {
        if (product != null) {
            //判断子图是否为空
            if (StringUtils.isNotBlank(product.getSubImages())) {  //子图不空 -->true
                //取子图的第一个图片赋给主图
                String[] subImagesArray = product.getSubImages().split(",");  //与前端约定好用逗号作分割
                if (subImagesArray.length > 0) {
                    product.setMainImage(subImagesArray[0]);
                }
            }

            if (product.getId() != null) {  //更新产品时，需要传Id(这与前端商量好)
                //productMapper.updateByPrimaryKey(product);
                int rowCount = productMapper.updateByPrimaryKey(product);  //更新产品的数量
                if (rowCount > 0) {
                    return ServerResponse.createBySuccess("更新产品成功");
                }
                return ServerResponse.createBySuccess("更新产品失败");
            } else {  //id为空，则新增
                int rowCount = productMapper.insert(product);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccess("新增产品成功");
                }
                return ServerResponse.createBySuccess("新增产品失败");
            }
        }
        return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }

    //更新产品销售状态
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }

    //获取商品详情的业务逻辑方法（后台）
    //泛型使用VO进行填充
    //public ServerResponse<Object> manageProductDetail(Integer productId){
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());  //返回参数错误
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        //若product不为空，返回VO对象(value Object)
        //若复杂些，可以做成POJO->BO(business object：业务层对象)->VO(View Object)
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);

    }

    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());  //富文本
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());


        //imageHost  需要从一个配置文件中获取，配置和代码分离，更好的维护
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));

        //parentCategotyId
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            productDetailVo.setParentCategotyId(0); //默认为根节点
        } else {
            productDetailVo.setParentCategotyId(category.getParentId());
        }

        //createTime
        //updateTime
        //createTime和updateTime从DB中取出来是毫秒---》MyBatis的时间存储，所以需要进行时间转换--->编写工具类：DateTimeUtil
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }


    //public ServerResponse getProductList(int pageNum,int pageSize){
    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
        //Mybatis的pageHelper插件的使用步骤:如下
        //1. startPage--开始
        //2. 填充自己的Sql逻辑
        //3. pageHelper--收尾

        //1. startPage--开始
        PageHelper.startPage(pageNum, pageSize);

        //2. 填充自己的Sql逻辑
        List<Product> productList = productMapper.selectList();  //调用查询List的方法(在mapper中写该方法)
        //但是List不需要Product那么多信息,因此需要创建一个ProductListVo类以及组装ProductListVo的方法

        /*
        for(Product productItem : productList){
            ProductListVo productListVo = assambelProductListVo(productItem);
        }
        */

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductListVo productListVo = assambelProductListVo(productItem);
            productListVoList.add(productListVo);
        }

        //3. pageHelper--收尾
        PageInfo pageResult = new PageInfo(productList);
        //重置List
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);  //这里T data就是pageResult
    }

    private ProductListVo assambelProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());

        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));

        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());

        return productListVo;

    }

    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
        //Mybatis的pageHelper插件的使用步骤:如下
        //1. startPage--开始
        //2. 填充自己的Sql逻辑
        //3. pageHelper--收尾
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(productName)) {
            productName = new StringBuilder().append("%").append(productName).append("%").toString();  //模糊查询的productNname拼接
        }

        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);
        //Product转换成ProductListVo对象
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductListVo productListVo = assambelProductListVo(productItem);
            productListVoList.add(productListVo);
        }

        //分页
        PageInfo pageResult = new PageInfo(productList);
        //重置List
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);  //这里T data就是pageResult
    }

    //前台商品详情
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());  //返回参数错误
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        //前台商品详情功能------>类似于后台商品详情功能，不同之处在于：前台查看产品详情的时候，需要看下产品状态是否为在线。
        //判断product的status是否为上线状态----------------->前后台商品详情功能的不同code之处
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        //若product不为空，返回VO对象(value Object)
        //若复杂些，可以做成POJO->BO(business object：业务层对象)->VO(View Object)
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    //前端用户搜索商品的功能（即返回List，其中会对List进行分页）
    //public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize){
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy) {
        //判断参数
        if (StringUtils.isBlank(keyword) && categoryId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        //集合--->用于存储分类与子分类，子子分类的categoryId(涉及递归)
        List<Integer> categoryIdList = new ArrayList<Integer>();

        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                //没有该分类，并且还没有关键字。所以需要返回一个空结果集，而不是报错提示信息。
                //这个空结果集也要进行分页
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }

            //需要注入CategoryService，以调用其中的递归算法,(平级调用)
            categoryIdList = iCategotyService.selectCategoryAndChildrenById(category.getId()).getData();
        }//if

        if (StringUtils.isNotBlank(keyword)) {  //关键字keyword(即productName)不是空，则进入if里的语句
            //拼接字符串，供SQL查询
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        //分页
        PageHelper.startPage(pageNum, pageSize);
        //排序处理
        if (StringUtils.isNotBlank(orderBy)) {
            //动态排序（按价格排序）
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {  //使用Set集合是因为contains方法的时间复杂度是O(1),而List的是O(n)
                String[] orderByArray = orderBy.split("_");
                //PageHelper.orderBy("price desc");  --------------------------->这是PageHelper中的orderBy方法参数格式：price desc
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }
        //搜索Product----->在ProductMapper.xml中写SQL语句
        //List<Product> productList = productMapper.selectByNameAndCategoryIds(keyword,categoryIdList);
        //三元运算(真:假)简单对两个参数: keyword, categoryIdList  判断一下。
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword) ? null : keyword, categoryIdList.size() == 0 ? null : categoryIdList);

        //构建成ProductListVo对象
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo productListVo = assambelProductListVo(product);
            productListVoList.add(productListVo);
        }
        //分页
        PageInfo pageInfo = new PageInfo(productList);  //SQL的执行结果：productList
        pageInfo.setList(productListVoList);

        return ServerResponse.createBySuccess(pageInfo);

    }

}
