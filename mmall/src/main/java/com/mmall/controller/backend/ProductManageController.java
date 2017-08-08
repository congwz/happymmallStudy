package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.Response;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/2 0002.
 */

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    //注入UserService
    @Autowired
    private IUserService iUserService;
    //注入ProductService
    @Autowired
    private IProductService iProductService;
    //注入FileService
    @Autowired
    private IFileService iFileService;

    //保存商品（新增or更新商品--->新增则参数里没有id，更新则参数里带有id）
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        //校验用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员"); //强制登录
        }
        //校验用户是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充我们增加产品的业务逻辑
            return iProductService.saveOrUpdateProduct(product);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    //产品上下架（即更新产品status）
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId,Integer status){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        //校验用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员"); //强制登录
        }
        //校验用户是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充ProductService业务逻辑
            return iProductService.setSaleStatus(productId,status);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    //获取商品详情
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        //校验用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员"); //强制登录
        }
        //校验用户是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充ProductService中关于获取商品详情的业务逻辑
            return iProductService.manageProductDetail(productId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    //产品List，包含动态分页功能--->Mybatis的pagehelper插件
    @RequestMapping("List.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){  //pageNum表示第几页，pageSize表示页面容量
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        //校验用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员"); //强制登录
        }
        //校验用户是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充ProductService中产品List的分页业务逻辑
            return iProductService.getProductList(pageNum,pageSize);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    //后台商品搜索功能
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session,String productNamw,Integer productId, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){  //pageNum表示第几页，pageSize表示页面容量
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        //校验用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员"); //强制登录
        }
        //校验用户是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务逻辑
            return iProductService.searchProduct(productNamw,productId,pageNum,pageSize);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    //后台商品图片的springmvc上传
    //产品图片上传到服务器上
    @RequestMapping("upload.do")
    @ResponseBody
    //public ServerResponse upload(MultipartFile file,HttpServletRequest request){   //MultipartFile为springmvc的文件上传中支持的文件类型。HttpServletRequest根据servlet上下文动态创建一个相对路径。
    public ServerResponse upload(HttpSession session,@RequestParam(value = "upload_file",required = false) MultipartFile file,HttpServletRequest request){   //MultipartFile为springmvc的文件上传中支持的文件类型。HttpServletRequest根据servlet上下文动态创建一个相对路径。
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        //校验用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员"); //强制登录
        }
        //校验用户是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务逻辑
            String path = request.getSession().getServletContext().getRealPath("upload"); //通过session中的getServletContext方法拿到servlet的上下文，然后上传的文件的文件夹名叫upload。
            String targetFileName = iFileService.upload(file,path);
            //ftp.server.http.prefix=http://img.happymmall.com/
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName; //根据和前端的约定，url需要拼接起来，直接访问。

            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    //富文本上传
    //富文本中的图片上传到服务器上
    //富文本中对于返回值有自己的要求，我们使用的是simditor这个插件，所以按照simditor的要求进行返回。返回格式如下：
    /*{
         "success"：true/false,
         "msg": "error message" ,#optional （这个属性可选）
         "file_path": "[real file path]"
       }
    * */
    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    //public ServerResponse upload(MultipartFile file,HttpServletRequest request){   //MultipartFile为springmvc的文件上传中支持的文件类型。HttpServletRequest根据servlet上下文动态创建一个相对路径。
    //public Map richtextImgUpload(HttpSession session,@RequestParam(value = "upload_file",required = false) MultipartFile file,HttpServletRequest request){   //MultipartFile为springmvc的文件上传中支持的文件类型。HttpServletRequest根据servlet上下文动态创建一个相对路径。
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){   //MultipartFile为springmvc的文件上传中支持的文件类型。HttpServletRequest根据servlet上下文动态创建一个相对路径。
        Map resultMap = Maps.newHashMap();

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        //校验用户是否登录
        if(user == null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
            //return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员"); //强制登录
        }
        //校验用户是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务逻辑
            String path = request.getSession().getServletContext().getRealPath("upload"); //通过session中的getServletContext方法拿到servlet的上下文，然后上传的文件的文件夹名叫upload。
            String targetFileName = iFileService.upload(file,path);
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }

            //上传成功
            //ftp.server.http.prefix=http://img.happymmall.com/
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName; //根据和前端的约定，url需要拼接起来，直接访问。

            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);

            //处理response的Header，这是所以按照simditor的文档要求
            response.addHeader("Access-Controller-Allow-Headers","X-File-Name");  //这是和前端的约定。很多插件，前后端的返回都会有约定。
           return resultMap;
        }else {
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
           //return ServerResponse.createByErrorMessage("无权限操作");
        }
    }
}
