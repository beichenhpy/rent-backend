package com.hpy.distributed.House.dao;

import DO.HouseDo;
import DO.StatisticsDo;
import DTO.HouseDto;
import DO.HouseInfoDo;
import DTO.HouseInfoDto;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/2/27 21:18
 */
@Repository
public interface HouseMapper {

    /**
     * 根据hid查询房屋信息
     *
     * 用于查看出租房屋信息的，具体的房子的信息
     * 最好加上条件，是否检验完成
     * @param hid hid
     * @return 房屋信息
     */
    @Select("select * from house where hid = #{hid} for update ")
    @Results(id = "houseMap",value = {
            @Result(id = true,column = "hid",property = "hid"),
            @Result(column = "hid",property = "houseInfo" ,
                    one = @One(select = "com.hpy.distributed.House.dao.HouseInfoMapper.findHouseInfo"))
    })
    HouseDo findHouseByHid(String hid);

    /**
     * 查询房屋基本信息 不包含houseInfo do
     * @param hid 房屋编号
     * @return 房屋信息
     */
    @Select("select * from house where hid = #{hid}")
    HouseDo findHouseBasicByHid(String hid);


    /**
     * 根据uid查这个人所有的房屋信息
     *
     * 后台会进行校验房子信息的真实性，
     * 如果未通过，则会提示用户房子信息无效
     * 如果通过，则可以正常出租
     * 如果审核未完成，前台显示未完成
     * @param uid uid
     * @return 返回房屋集合
     */
    @Select("select * from house where uid = #{uid}")
    List<HouseDo> findHouseByUid(String uid);

    /**
     * 查询所有未核验的房屋
     * @return 返回房屋集合
     */
    @Select("select * from house where isCheck = 0")
    List<HouseDo> findAll();
    /**
     * 查询所有已核验的房屋
     * @return 返回房屋集合
     */
    @Select("select * from house where isCheck = 1 and uid = #{uid} and isOnRent = 0 and isRented = 0")
    List<HouseDo> findAllChecked(String uid);
    /**
     * 添加房屋
     *
     * 添加房屋 默认isCheck = 0 ,为未核验
     * 默认 isOnRent = 0 为未发布到出租信息
     * 后台核验后会修改状态，即可以出租
     * @param houseDo 房屋
     */
    @Insert("insert into house (hid, province, city, village, address, building, unit, houseNum, layout, forward, area, uid) VALUES " +
            "(#{hid},#{province},#{city},#{village},#{address},#{building},#{unit},#{houseNum},#{layout},#{forward},#{area},#{uid})")
    void addHouse(HouseDo houseDo);


    /**
     * 更新基本房屋信息 更新后变为未核验
     * @param  houseDo houseDto
     * @return 返回影响行数
     */
    @Update("update house set province = #{province},city = #{city}" +
            ",village = #{village} ,building = #{building},unit = #{unit}," +
            "houseNum = #{houseNum},address=#{address}," +
            "layout = #{layout},forward=#{forward},area=#{area}" +
            ",isCheck = 0 where hid = #{hid} and isRented = 0 and isOnRent = 0")
    Integer updateHouse(HouseDo houseDo);


    /**
     * 删除房屋 房屋未出租可以删除
     * @param hid 房屋编号
     * @return 返回影响行数
     */
    @Delete("delete from house where hid = #{hid} and isRented = 0")
    Integer deleteHouse(String hid);

    /*------------------------用于修改省市县名字时使用------------同步信息*/
    /**
     * 修改省的名字使用
     * @param newProvince 新名字
     * @param oldProvince 旧名字
     */
    @Update("update house set province = #{newProvince} where province = #{oldProvince}")
    void updateProvince(String newProvince,String oldProvince);

    /**
     * 修改城市的名字使用
     * @param newCity 新名字
     * @param oldCity 旧名字
     */
    @Update("update house set city = #{newCity} where city = #{oldCity}")
    void updateCity(String newCity,String oldCity);

    /**
     * 修改区的名字
     * @param newVillage 新的区的名字
     * @param oldVillage 旧的区的名字
     */
    @Update("update house set village = #{newVillage} where village = #{oldVillage}")
    void updateVillage(String newVillage,String oldVillage);

    //查询已经出租或已经发布的房屋信息
    @Select("select count(*) from house where province = #{province} and (isRented = 1 or isOnRent = 1)")
    Integer findHouseByProvince(String province);

    @Select("select count(*) from house where city = #{city} and (isRented = 1 or isOnRent = 1)")
    Integer findHouseByCity(String city);

    @Select("select count(*) from house where village = #{village} and (isRented = 1 or isOnRent = 1)")
    Integer findHouseByVillage(String village);

}
