package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Administrator on 2017/7/29 0029.
 */
public interface IFileService {

    String upload(MultipartFile file, String path);
}
