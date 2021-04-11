package com.hpy.distributed.House.controller;

import DTO.HouseDto;
import DTO.HouseInfoDto;
import Query.HouseCardQuery;
import Query.HouseRecordQuery;
import com.hpy.distributed.House.service.HouseInfoService;
import com.hpy.distributed.House.util.OssUtil;
import entity.Message;
import entity.ResponseConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 9:06
 */
@RestController
public class HouseInfoController {

    @Autowired
    private HouseInfoService houseInfoService;
    @Autowired
    private OssUtil ossUtil;

    //图片子目录
    @Value("${file.imgPath}")
    private String imgPath;
    //合同子目录
    @Value("${file.videoPath}")
    private String videoPath;
    //用户子目录
    @Value("${file.housePath}")
    private String housePath;
    /**
     * 修改房屋详细信息
     *
     * @param houseInfoDto 房屋详细信息
     * @return 返回信息
     */
    @PreAuthorize("hasAuthority('user')")
    @PutMapping("/updateHouseInfo")
    public Message updateHouseInfo(@RequestBody HouseInfoDto houseInfoDto) {
        houseInfoService.updateHouseInfo(houseInfoDto);
        return Message.requestSuccess(ResponseConstant.UPDATE_SUCCESS);
    }


    /**
     * 上传房屋图片
     *
     * @param houseInfoId houseInfoId
     */
    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/updateHouseImage/{houseInfoId}")
    public Message updateHouseImage(MultipartRequest files, @PathVariable("houseInfoId") String houseInfoId) {
        MultipartFile file0 = files.getFile("file0");
        MultipartFile file1 = files.getFile("file1");
        MultipartFile file2 = files.getFile("file2");
        String img1,img2,img3;
        List<String> imageList = new ArrayList<>();
        if(file0 != null){
            img1 = ossUtil.upLoadfile(file0, imgPath + housePath, "houseInfo");
            imageList.add(img1);
        }
        if(file1 != null){
            img2 = ossUtil.upLoadfile(file1, imgPath + housePath, "houseInfo");
            imageList.add(img2);
        }
        if(file2 != null){
            img3 = ossUtil.upLoadfile(file2, imgPath + housePath, "houseInfo");
            imageList.add(img3);
        }
        //拿到路径更新数据库
        houseInfoService.updateHouseImage(imageList, houseInfoId);
        return Message.requestSuccess(ResponseConstant.UPDATE_SUCCESS);
    }

    /**
     * 修改房屋视频
     *
     * @param houseInfoId houseInfoId
     */
    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/updateHouseVideo/{houseInfoId}")
    public Message updateHouseVideo(
            MultipartFile file,
            @PathVariable("houseInfoId") String houseInfoId) {
        String filePath = ossUtil.upLoadfile(file, videoPath + housePath, "houseVideo");
        //拿到路径更新数据库
        houseInfoService.updateHouseVideo(filePath, houseInfoId);
        return Message.requestSuccess(ResponseConstant.UPDATE_SUCCESS);
    }

    /**
     * 修改房屋详细信息
     *
     * @param houseCardQuery 房屋房产证号
     * @return 返回信息
     */
    @PreAuthorize("hasAuthority('user')")
    @PutMapping("/updateHouseCardNum")
    public Message updateHouseCardNum(@RequestBody HouseCardQuery houseCardQuery) {
        houseInfoService.updateHouseCardNum(houseCardQuery.getHouseCardNum(),houseCardQuery.getHid());
        return Message.requestSuccess(ResponseConstant.UPDATE_SUCCESS);
    }
    /**
     * 修改房产证图片
     *
     * @param file 图片
     * @param houseInfoId  houseInfoId
     * @return 返回信息
     */
    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/updateHouseCard/{houseInfoId}")
    public Message updateHouseCard(MultipartFile file, @PathVariable("houseInfoId") String houseInfoId) {
        String filePath = ossUtil.upLoadfile(file, imgPath + housePath, "houseCard");
        houseInfoService.updateHouseCard(filePath, houseInfoId);
        return Message.requestSuccess(ResponseConstant.UPDATE_SUCCESS);
    }

    //更新读数
    @PreAuthorize("hasAuthority('user')")
    @PutMapping("/updateHouseRecord")
    public Message updateHouseRecord(@RequestBody HouseRecordQuery houseRecordQuery){
        houseInfoService.updateHouseRecord(
                houseRecordQuery.getWaterRecord(),
                houseRecordQuery.getHouseRecord(),
                houseRecordQuery.getCarRecord(),
                houseRecordQuery.getHouseInfoId(),
                houseRecordQuery.getHid()
        );
        return Message.requestSuccess(null);
    }

    /**
     * 用户查询自己的房屋详细信息
     *
     * @param hid hid
     * @return 返回信息
     */
    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findHouseInfo/{hid}")
    public Message findHouseInfo(@PathVariable("hid") String hid) {
        HouseInfoDto houseInfoDto = houseInfoService.findHouseInfo(hid);
        return Message.requestSuccess(houseInfoDto);

    }
    /**
     * 根据hid查询房屋详细信息 用于查看出租详细信息使用
     *
     * @param hid hid
     * @return 返回信息
     */
    @GetMapping("/findHouse/{hid}")
    public HouseDto findHouseHid(@PathVariable("hid") String hid) {
        return houseInfoService.findHouseByHid(hid);
    }
}
