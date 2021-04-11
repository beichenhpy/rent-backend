package com.hpy.distributed.House.dao;

import DO.HouseInfoDo;
import Query.HouseRecordQuery;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 14:28
 */
@Repository
public interface HouseInfoMapper {
    /**
     * 根据hid查询 房屋详细信息
     * @param houseInfoId houseInfoId
     * @return 返回房屋信息
     */
    @Select("select * from houseInfo where houseInfoId = #{houseInfoId}")
    HouseInfoDo findHouseInfoByHouseInfoId(String houseInfoId);


    /**
     * 根据hid查询 房屋详细信息
     * @param hid hid
     * @return 返回房屋信息
     */
    @Select("select * from houseInfo where hid = #{hid}")
    HouseInfoDo findHouseInfo(String hid);
    /**
     * 添加房屋详细信息
     * @param houseInfoDo 房屋详细信息
     */
    @Insert("insert into houseInfo (houseInfoId,isWash, isAir, isRobe, isFridge, isWarm, isBed, hid,houseCardNum) VALUES " +
            "(#{houseInfoId},#{isWash},#{isAir},#{isRobe},#{isFridge},#{isWarm},#{isBed},#{hid},#{houseCardNum})")
    void addHouseInfo(HouseInfoDo houseInfoDo);

    /**
     * 上传房产证图片使用
     * @param  houseCard houseCard
     */
    @Update("update houseinfo set houseCard = #{houseCard} where houseInfoId = #{houseInfoId}")
    Integer updateHouseCard(String houseCard,String houseInfoId);

    /**
     * 更新图片路径
     * @param images 图片
     */
    @Update("update houseinfo set images = #{images} where houseInfoId = #{houseInfoId}")
    Integer updateHouseInfoImg(String images,String houseInfoId);

    /**
     * 更新视频路径
     * @param video 视频
     */
    @Update("update houseinfo set video = #{video} where houseInfoId = #{houseInfoId}")
    Integer updateHouseInfoVideo(String video,String houseInfoId);

    /**
     * 更新详细信息 除了图片和视频的路径
     * @param houseInfoDo 房屋的详细信息
     */
    @Update("update houseinfo set isAir = #{isAir},isBed = #{isBed},isFridge = #{isFridge}," +
            "isRobe = #{isRobe},isWarm = #{isWarm},isWash = #{isWash} where houseInfoId = #{houseInfoId}")
    void updateHouseInfo(HouseInfoDo houseInfoDo);

    //更新房产证号
    @Update("update houseinfo set houseCardNum = #{houseCardNum} where hid = #{hid}")
    void updateHouseCardNum(String houseCardNum,String hid);

    //更新记录
    @Update("update houseinfo set waterRecord = #{waterRecord},houseRecord = #{houseRecord},carRecord = #{carRecord} where houseInfoId = #{houseInfoId}")
    void updateRecord(Integer waterRecord,Integer houseRecord,Integer carRecord,String houseInfoId);
}
