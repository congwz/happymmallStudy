package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Administrator on 2017/7/29 0029.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    //编写服务：上传文件
    public String upload(MultipartFile file,String path){
        //获取文件原始名
        String fileName = file.getOriginalFilename();
        //获取文件的扩展名，不要.(即.之后的名字)  文件名如: abc.jpg，abc.abc.abc.jpg
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        //定义上传文件名，使用UUID是为了防止其他人上传的文件名和自己上传的一样，这样就会被覆盖，用了UUID，则不会被覆盖。
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件，上传文件的文件名：{},上传的路径：{},新文件名：{}",fileName,path,uploadFileName);  //使用{}来占位。

        //创建目录
        File fileDir = new File(path);
        if(!fileDir.exists()){ //不存在,进入if条件，进行创建目录
            fileDir.setWritable(true);  //赋予写权限
            fileDir.mkdirs(); //可创建层级文件夹(目录)
        }
        //创建文件
        File targetFile = new File(path,uploadFileName);  //视频教程这么写的，但我觉得应该这么写吧： File targetFile = new File(fileDir,uploadFileName);

        try {
            file.transferTo(targetFile); //至此，文件上传已经OK了。

            //todo 将targetFile上传到我们的FTP服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));  //至此，已经上传到ftp服务器上,可以删除todo一行的注释提醒

            //todo 上传完之后，删除uplaod文件夹(upload文件夹在该项目mmall的webapp文件下,与WEB_INF文件夹同级)下的文件
            targetFile.delete();

        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        return targetFile.getName();  //返回上传的文件的文件名
    }

}
