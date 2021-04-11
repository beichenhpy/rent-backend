package com.hpy.RentHouse.filestore.util;

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
     * 上传本地文件
     * @param ossPath 云路径
     * @param localPath 本地路径
     */
    public void uploadLocalFile(String ossPath,String localPath,String type){
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
        try {
            // 创建PutObjectRequest对象。
            ObjectMetadata metadata = new ObjectMetadata();
            if("word".equals(type)){
                metadata.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            }else {
                metadata.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            }
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket ,ossPath, new File(localPath),metadata);
            ossClient.putObject(putObjectRequest);
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            ossClient.shutdown();
        }
    }



    /**
     * 下载文件到指定目录下的指定名下
     *
     * @param fileName 文件名
     */
    public void downLoadFile(String fileName, String ossPath) {
        try {
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
            ossClient.getObject(new GetObjectRequest(bucket, ossPath), new File(fileName));
            ossClient.shutdown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
