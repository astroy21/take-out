package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用controller
 */
@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;
    // result的string是阿里云服务器的网址
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传:{}",file);
        try {
            // 1. 原始文件名
            String originalFilename = file.getOriginalFilename();
            // 2. 截取文件名后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            // 3. 构造新的文件名，随机化以防止重名
            String objecctName = UUID.randomUUID().toString() + extension;
            // 4. 获取返回的路径
            String url = aliOssUtil.upload(file.getBytes(),objecctName);
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败",e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
