package com.hpy.rentHouse.distributed.rent.service;

import DTO.OnRentDto;
import DTO.OnRentForOrderDto;
import com.github.pagehelper.PageInfo;
import DTO.OnRentInfoDto;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/2/27 18:57
 */

public interface RentService {

    /**
     * 添加出租信息
     * @param onRentDto 出租信息
     */
    void addOnRent(OnRentDto onRentDto);

    /**
     * 删除出租信息
     * @param hid rid
     */
    void delOnRent(String hid);

    /**
     * 不用更新房屋是否出租，当房屋被删除时删除此出租信息
     * @param hid 出租信息编号
     */
    void delOnRentNoCheck(String hid);

    /**
     * 修改出租信息
     * @param onRentDto 出租信息
     */
    void modifyOnRent(OnRentDto onRentDto);


    /**
     * 查询所有出租信息 根据地区查询
     * @return 返回所有出租信息
     */
    PageInfo<OnRentDto> findAllPushRentInfo(int page, int size,String province,String city);

    /**
     * 冻结该房间的出租信息
     * @param hid 房间编号
     */
    void frozenOnRent(String hid);

    /**
     * 解冻该房间的出租信息
     * @param hid 房间编号
     */
    void unfrozenOnRent(String hid);

    /**
     * 根据hid查询到对应的出租信息
     * @param hid hid
     * @return 返回出租信息
     */
    OnRentForOrderDto findOnRentByHid(String hid);

    /**
     * 找到用户的所有发布的出租信息
     * @param uid 用户编号
     * @return 返回出租信息集合
     */
    PageInfo<OnRentDto> findOnRentInfoByUid(int page,int size,String uid);

    /**
     * 通过房屋编号查询房屋信息和出租信息，通过用户编号查询到房东的一些基本信息
     * @param hid 房屋编号
     * @param uid 用户编号
     * @return 返回信息
     */
    OnRentInfoDto findOnRentInfoAllByHidUid(String hid, String uid);


    /**
     * 修改封面图片信息
     * @param image 图片信息
     * @param hid 房屋编号
     */
    void updateImage(String image,String hid);


    /**
     * 更新省份的名字
     * @param oldName 旧名字
     * @param newName 新名字
     */
    void updateProvinceOrCity(String newName,String oldName,String type);

    OnRentDto findRentByHid(String hid);

    //上架出租信息
    void upRent(String hid);
    //下架出租信息
    void downRent(String hid);

    //更新房屋时同步更新出租信息，要求房屋在未发布和未出租状态
    void updateHouseRent(OnRentDto onRentDto);
}
