package com.hpy.rentHouse.distributed.rent.utils;

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
    @Autowired
    private IdWorker idWorker;

    /**
     * 上传到指定目录
     *
     * @param file          file
     * @param referencePath 相对路径
     */
    public String upLoadfile(MultipartFile file, String referencePath,String filename) {
        // 创建OSSClient实例。
        String filePath;
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
        try {
            //判断是否为空
            if (file.getSize() != 0 && !"".equals(file.getName())) {

                ObjectMetadata metadata = new ObjectMetadata();
                String name = file.getOriginalFilename();
                metadata.setContentType(getContentType(name));
                String fileExtension = name.substring(name.lastIndexOf("."));
                //放入文件夹下 命名方式为 随机id + 文件名
                filePath = referencePath  +idWorker.nextId()+"" + "_" + filename + fileExtension;
                //存入
                ossClient.putObject(bucket, filePath, file.getInputStream(),metadata);
            } else {
                throw new RuntimeException("上传为空");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ossClient.shutdown();
        }
        return filePath;
    }


    /**
     * 通过文件名判断并获取OSS服务文件上传时文件的contentType
     * @param fileName 文件名
     * @return 文件的contentType
     */
    public static  String getContentType(String fileName){
        //文件的后缀名
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        if(".bmp".equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        }
        if(".gif".equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        }
        if(".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension)  || ".png".equalsIgnoreCase(fileExtension) ) {
            return "image/jpeg";
        }
        if(".html".equalsIgnoreCase(fileExtension)) {
            return "text/html";
        }
        if(".txt".equalsIgnoreCase(fileExtension)) {
            return "text/plain";
        }
        if(".vsd".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.visio";
        }
        if(".ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        if(".doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        }
        if(".xml".equalsIgnoreCase(fileExtension)) {
            return "text/xml";
        }
        if (".mp4".equalsIgnoreCase(fileExtension)){
            return "video/mp4";
        }
        //默认返回类型
        return "image/jpeg";
    }

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
