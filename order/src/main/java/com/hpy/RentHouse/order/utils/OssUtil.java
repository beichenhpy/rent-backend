package com.hpy.RentHouse.order.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import util.IdWorker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/3/20 17:32
 */
@Component
public class OssUtil {

    @Value("${aliyun.accessId}")
    private String accessId;
    @Value("${aliyun.accessKey}")
    private String accessKey;
    @Value("${aliyun.bucket}")
    private String bucket;
    @Value("${aliyun.endpoint}")
    private String endpoint;


    /**
     * 删除单个文件
     * @param filename 文件名
     */
    public void deleteFile(String filename){
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
       try {
           // 删除文件。如需删除文件夹，请将ObjectName设置为对应的文件夹名称。如果文件夹非空，则需要将文件夹下的所有object删除后才能删除该文件夹。
           ossClient.deleteObject(bucket, filename);
       }catch (Exception e){
           throw new RuntimeException(e);
       }finally {
           ossClient.shutdown();
       }
    }



}
