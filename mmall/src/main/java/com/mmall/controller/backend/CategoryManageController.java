package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategotyService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/6/22 0022.
 */
@Controller
@RequestMapping("/manage/category")
//分类管理模块（属于后台模块，所以路径都打到manage下）
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    //注入CategoryService
    private ICategotyService iCategotyService;

    //添加分类
    /*
    参数session用于验证用户是否为管理员。@RequestParam注解是设置parentId的默认值为0，即分类根节点
    * */
    @RequestMapping("add_category.do")
    @ResponseBody  //使返回值自动使用Jackon的序列化
    public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue = "0") int parentId){
        //判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            //return ServerResponse.createByErrorMessage("用户未登录，请登录");
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验一下登录的用户是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //Success,则为管理员，编写添加分类的逻辑---->创建接口ICategotyService
            return iCategotyService.addCategory(categoryName,parentId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }

    }

    //更新Category name
    @RequestMapping("set_category_name.do")
    @ResponseBody  //使返回值自动使用Jackon的序列化
    public ServerResponse setCategoryName(HttpSession session,Integer categoryId,String categoryName){
        //判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            //return ServerResponse.createByErrorMessage("用户未登录，请登录");
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验一下登录的用户是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //更新category name--->接下来先在service里实现
            return iCategotyService.updateCategoryName(categoryId,categoryName);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    //获取当前category id下面平级节点的信息，无递归
    //若categoryId没有传的话，就用默认值0-->0代表根节点，使用@RequestParam注解可以实现这样的功能
    @RequestMapping("get_category.do")
    @ResponseBody  //使返回值自动使用Jackon的序列化
    public ServerResponse getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        //判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            //return ServerResponse.createByErrorMessage("用户未登录，请登录");
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }

        //校验一下登录的用户是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询子节点的category信息，并且不递归，保持平级
            return iCategotyService.getChildrenParallelCategory(categoryId);

        }else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    //获取当前的category id，并且递归查询其子节点的Category id
    @RequestMapping("get_deep_category.do")
    @ResponseBody  //使返回值自动使用Jackon的序列化
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        //判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            //return ServerResponse.createByErrorMessage("用户未登录，请登录");
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }

        //校验一下登录的用户是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询当前节点的id和递归子节点的id,比如：0->100001->100006
            return  iCategotyService.selectCategoryAndChildrenById(categoryId);

        }else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }
}
