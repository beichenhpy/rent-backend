package com.hpy.distributed.House.service;

import DTO.HouseDto;
import DTO.HouseInfoDto;
import Query.HouseRecordQuery;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 9:05
 */

public interface HouseInfoService {
    /**
     * 更新修改房屋的详细信息
     * @param houseInfoDto 房屋详细信息
     */
    void updateHouseInfo(HouseInfoDto houseInfoDto);
    /**
     * 更新房屋的图片路径
     * @param path path
     * @param houseInfoId houseInfoId
     */
    void updateHouseImage(List<String> path, String houseInfoId);

    /**
     * 更新房屋的视频路径
     * @param path 路径
     * @param houseInfoId houseInfoId

     */
    void updateHouseVideo(String path,String houseInfoId);

    /**
     * 更新房产证图片路径
     * @param houseInfoId houseInfoId

     * @param path 路径
     */
    void updateHouseCard(String path,String houseInfoId);


    /**
     * 查询房屋的详细信息
     * @param hid 房屋编号
     * @return 返回房屋详细信息
     */
    HouseInfoDto findHouseInfo(String hid);

    /**
     * 根据hid查询房子的所有信息
     * @param hid hid
     * @return 返回房子信息
     */
    HouseDto findHouseByHid(String hid);

    /**
     * 更新房产证号
     * @param houseCardNum 房产证号
     * @param hid 房屋编号
     */
    void updateHouseCardNum(String houseCardNum,String hid);


    //更新房屋电表读数
    void updateHouseRecord(Integer waterRecord,Integer houseRecord,Integer carRecord,String houseInfoId,String hid);
}
