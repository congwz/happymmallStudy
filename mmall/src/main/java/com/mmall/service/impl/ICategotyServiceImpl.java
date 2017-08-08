package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategotyService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.util.resources.cldr.tg.CalendarData_tg_Cyrl_TJ;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/6/22 0022.
 */
@Service("iCategotyService")
public class ICategotyServiceImpl implements ICategotyService {

    private Logger logger = LoggerFactory.getLogger(ICategotyServiceImpl.class);

    //注入CategoryMapper
    @Autowired
    private CategoryMapper categoryMapper;

    //添加分类
    public ServerResponse addCategory(String categoryName,Integer parentId){

        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加品类的参数错误");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);  //这个分类是可用的

        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("添加品类成功");
        }

        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    //更新分类名字
    public ServerResponse updateCategoryName(Integer categoryId,String categoryName){
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("更新品类的参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);  //updateByPrimaryKeySelective为有选择性的更新（即通过主键id进行更新）
        if(rowCount > 0){
            return ServerResponse.createBySuccess("更新品类名字成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名字失败");

    }

    //查询子节点的category信息，并且不递归，保持平级
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){  //泛型为集合：List<Category>
        //调用dao层--->调用mapper: CategoryMapper--->根据parent id获取孩子节点的categoty信息
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            //打印日志
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);  //返回集合，T data的data就是categoryList
    }

    //查询当前节点的id和递归子节点的id,比如：0->100001->100006   递归即子节点的子节点的子节点都要遍历到
    //递归查询本节点的id和孩子节点的id
    //public ServerResponse selectCategoryAndChildrenById(Integer categoryId){
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();  //guava中的Sets方法
        findChildCategory(categorySet,categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();  //guava中的Lists方法
        if(categoryId != null){
            for(Category categoryItem : categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);  //返回id的集合
    }

    //递归函数（递归算法就是自己调用自己，算出子节点）
    //这里需要重写Category的hashcode和equals方法并且保证这两个方法中的判断因子是一样的，以实现排重
    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            categorySet.add(category);
        }
        //查找子节点，递归算法一定要有个退出的条件
        //这里的退出条件是子节点是否为空，空则退出
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for(Category categoryItem : categoryList){
            findChildCategory(categorySet,categoryItem.getId()); //调用自己:findChildCategory
        }
        return categorySet;
    }
}
