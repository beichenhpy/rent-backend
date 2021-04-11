package com.hpy.distributed.House.dao;

import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * @author: beichenhpy
 * @Date: 2020/5/4 21:49
 */
@Repository
public interface HouseStatusMapper {
    /** admin
     * 更新check为1 变成已经核验
     * 用于admin核验房屋信息
     * @param hid hid
     * @return 返回影响行数
     */
    @Update("update house set isCheck = 1 where hid = #{hid} and isCheck = 0")
    Integer updateCheck(String hid);

    /** admin
     * 更新check为2 变成审核未通过
     * 用于admin核验房屋信息
     * @param hid hid
     * @return 返回影响行数
     */
    @Update("update house set isCheck = 2 where hid = #{hid} and isCheck = 0")
    Integer updateCheckToUnSuccess(String hid);

    /*
     * 更新check为0 变成未核验
     * 用于admin核验房屋信息
     * @param hid hid
     * @return 返回影响行数
     */
    @Update("update house set isCheck = 0 where hid = #{hid}")
    void updateCheckToUnCheck(String hid);



    /**
     * rented
     * 更改房屋的是否出租的信息为 1 代表已出租 不可使用
     * isCheck是是否通过核验
     * 乐观锁
     * @param hid hid
     * @return 返回影响行数
     */
    @Update("update house set isRented = 1 where hid = #{hid} and isRented = 0 and isCheck = 1 and isOnRent = 1")
    Integer updateUnRentedToRented(String hid);


    /**
     * 更改房屋的是否出租的信息为 0 代表未出租 可以使用
     * isCheck是是否通过核验
     * 乐观锁
     * @param hid hid
     * @return 返回影响行数
     */
    @Update("update house set isRented = 0 where hid = #{hid} and isRented = 1 and isCheck = 1 and isOnRent = 1")
    Integer updateRentedToUnRented(String hid);


    /**
     * 更新房屋的状态为已经发布到出租信息 1 当这个房子已经通过核验并且还未出租
     * @param hid hid
     * @return 影响行数
     */
    @Update("update house set isOnRent = 1 where hid = #{hid}")
    Integer updateUnOnRentToOnRent(String hid);



    /**
     * 更新房屋的状态为未发布到出租信息 0 当这个房子已经通过核验，出不出租都可以
     * @param hid hid
     * @return 影响行数
     */
    @Update("update house set isOnRent = 0 where hid = #{hid}")
    Integer updateOnRentToUnOnRent(String hid);



}
