package com.pyg.manager.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import utils.FastDFSClient;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("http://192.168.25.133/")
//    @Value("${uploadServer}")
    private String uploadSever;

    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file) {

        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            String originalFilename = file.getOriginalFilename();
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String fileUrl = fastDFSClient.uploadFile(file.getBytes(), extName);
            return new Result(true, uploadSever+ fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上传失败");
        }
    }
}
