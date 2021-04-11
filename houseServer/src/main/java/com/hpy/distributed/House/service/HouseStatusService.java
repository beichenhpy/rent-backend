package com.hpy.distributed.House.service;

/**
 * @author: beichenhpy
 * @Date: 2020/5/4 21:50
 */

public interface HouseStatusService {
    /**
     * 更改房屋是否出租状态
     * @param hid hid
     */
    void isRented(String hid);

    //更新为未出租
    void unIsRented(String hid);

    /**
     * 更改check为已核验
     * 用于admin 验证房屋是否真实
     * @param hid hid
     */
    void updateCheck(String hid);

    /**
     * 更改为未通过核验
     * @param hid
     */
    void updateCheckToUnSuccess(String hid);
    /**
     * 更新为已经发布到出租信息中
     * @param hid hid
     */
    void isOnRent(String hid);

    //更新未未发布
    void unisOnRent(String hid);
}
