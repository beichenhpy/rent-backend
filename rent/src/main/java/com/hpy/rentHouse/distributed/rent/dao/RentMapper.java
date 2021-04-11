package com.hpy.rentHouse.distributed.rent.dao;

import DO.OnRentInfoDo;
import DTO.OnRentInfoDto;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/2/27 18:59
 * 出租信息持久层
 */
@Repository
public interface RentMapper {


    /**
     * 添加出租信息
     * @param onRentInfoDo 出租信息
     */
    @Insert("insert into onrentinfo (title, price, deposit,keyDeposit, time, rule, hid, uid,image,province,city,createTime,detailPosition,introduction,leastTime) " +
            "values (#{title},#{price},#{deposit},#{keyDeposit},#{time},#{rule},#{hid},#{uid},#{image},#{province},#{city},#{createTime},#{detailPosition},#{introduction},#{leastTime})")
    void pushRent(OnRentInfoDo onRentInfoDo);


    /**
     * 删除出租信息 未出租可以删除出租信息
     * @param hid hid
     * @return 影响行数
     */
    @Delete("delete from onrentinfo where hid = #{hid} and isFrozen = 0")
    Integer delRent(String hid);

    /** 未出租和未发布时才可以修改
     * 修改出租信息
     * @param onRentInfoDo 出租信息
     * @return 影响行数
     */
    @Update("update onrentinfo set title=#{title},price=#{price},deposit=#{deposit},keyDeposit=#{keyDeposit},time=#{time},leastTime = #{leastTime},rule=#{rule},introduction = #{introduction} where hid = #{hid} and isFrozen = 0 and isOnRent = 0")
    Integer modifyPushRent(OnRentInfoDo onRentInfoDo);

    /**
     * 修改出租信息的图片封面 要求未出租且未发布
     * @param hid 房屋编号
     * @param image 图片信息
     * @return 影响行数
     */
    @Update("update onrentinfo set image = #{image} where hid = #{hid} and isFrozen = 0 and isOnRent = 0")
    Integer modifyRentImage(String hid,String image);

    /**
     * 悲观锁 删除出租信息使用 不用管是否发布都可已删除
     * 根据房屋hid 因为hid唯一
     * 查询出租信息表中的房主的uid
     * @param hid hid
     * @return 返回出租信息
     */
    @Select("select * from onrentinfo where hid = #{hid} and isFrozen = 0")
    OnRentInfoDo findOnRentInfoByHid(String hid);


    /**
     *
     *
     * 查询出租信息表中的房主的uid
     * @param uid rid
     * @return 返回出租信息
     */
    @Select("select * from onrentinfo where uid = #{uid}")
    List<OnRentInfoDo> findOnRentInfoByUid(String uid);

    /**
     * 冻结出租房屋的信息，房屋出租后冻结
     * @param hid hid
     * @return 返回影响行数
     */
    @Update("update onrentinfo set isFrozen = 1 where hid = #{hid} and isFrozen = 0")
    Integer frozenPushRent(String hid);


    /**
     * 解冻出租房屋的信息，房屋退租后解冻
     * @param hid hid
     * @return 返回影响行数
     */
    @Update("update onrentinfo set isFrozen = 0 where hid = #{hid} and isFrozen = 1")
    Integer unfrozenPushRent(String hid);


    //上架出租信息
    @Update("update onrentinfo set isOnRent = 1 where hid = #{hid} and isOnRent = 0")
    Integer upOnRent(String hid);

    //下架出租信息
    @Update("update onrentinfo set isOnRent = 0 where hid = #{hid} and isOnRent = 1")
    Integer downOnRent(String hid);

    /**
     * 找到所有出租房屋 根据地区查询
     * 未被冻结的，在用户下单时，被置为1 表示冻结状态
     *
     * @return 返回出租房屋信息
     */
    @Select("select * from onrentinfo where isFrozen = 0 and isOnRent = 1 and province=#{province} and city = #{city}")
    List<OnRentInfoDo> findAllPushRent(String province,String city);

    /**
     * 更新省份的名字
     * @param oldProvince 旧名字
     * @param newProvince 新名字
     */
    @Update("update onrentinfo set province = #{newProvince} where province = #{oldProvince}")
    void updateProvince(String newProvince,String oldProvince);

    /**
     * 更新城市的名字
     * @param oldCity 旧名字
     * @param newCity 新名字
     */
    @Update("update onrentinfo set city = #{newCity} where city = #{oldCity}")
    void updateCity(String newCity,String oldCity);

    @Update("update onrentinfo set province = #{province},city = #{city},detailPosition = #{detailPosition} where hid = #{hid} and isOnRent = 0 and isFrozen = 0")
    void updateHouseRent(OnRentInfoDo onRentInfoDo);
}
