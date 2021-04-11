package com.hpy.distributed.House.service;

import DTO.HouseBasicDto;
import DTO.HouseChecked;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/2/27 21:18
 */

public interface HouseService {

    /**
     * 找到用户的所有房屋
     * 用于用户对自己的房屋进行管理使用
     * 点开具体房屋状态页面，可以看到是否通过核验
     * @param uid uid
     * @return 返回房子集合
     */
    PageInfo<HouseBasicDto> findAllHouseByUid(String uid, int page, int size);

    /**
     * 查询出所有未核验的房屋
     * @return 返回未核验的房屋集合
     */
    PageInfo<HouseBasicDto> findAll(int page, int size);


    /**
     * 添加新房屋
     * 需要将redis总的缓存清除
     * @param houseBasicDto 房屋
     */
    void addHouse(HouseBasicDto houseBasicDto);



    /**
     * 更新修改房屋信息
     * @param houseBasicDto 房屋信息
     *
     */
    void updateHouse(HouseBasicDto houseBasicDto);


    /**
     * 删除房屋 房屋未出租可以删除，删除后同时删除对应出租信息
     * @param hid 房屋编号
     */
    void deleteHouseByHid(String hid);



    /**
     * 根据房屋编号查询房屋的基本信息
     * @param hid 房屋编号
     * @return 返回房屋基本信息
     */
    HouseBasicDto findBasicHouse(String hid);

    /**
     * 修改省市县信息时，同时修改房屋信息的对应信息
     * @param newName 新名字
     * @param oldName 旧名字
     * @param type 类型 province/city/village
     */
    void updateProvinceOrCityOrVillage(String newName,String oldName,String type);


    /**
     * 查询已经核验的房屋信息
     * @param uid 用户查询
     * @return 返回房屋信息简称
     */
    List<HouseChecked> findCheckedHouse(String uid);

    //根据省、市、区查询房屋信息，用于删除省市区时使用
    Boolean findHouseByProvince(String province);
    Boolean findHouseByCity(String city);
    Boolean findHouseByVillage(String village);


}
