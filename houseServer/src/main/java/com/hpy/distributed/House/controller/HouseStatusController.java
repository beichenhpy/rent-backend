package com.hpy.distributed.House.controller;

import com.hpy.distributed.House.service.HouseStatusService;
import entity.Message;
import entity.ResponseConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: beichenhpy
 * @Date: 2020/5/4 21:53
 */
@RestController
public class HouseStatusController {
    @Autowired
    private HouseStatusService houseStatusService;
    /**
     * 更改房屋的状态为 已租状态
     *
     * @param hid hid
     */
    @PutMapping("/isRented/{hid}")
    public void isRented(@PathVariable("hid") String hid) {
        houseStatusService.isRented(hid);
    }

    /**
     * 更改房屋的状态为 未租状态
     *
     * @param hid hid
     */
    @PutMapping("/unisRented/{hid}")
    public void unisRented(@PathVariable("hid") String hid) {
        houseStatusService.unIsRented(hid);
    }

    /**
     * 更新房屋状态 已发布
     *
     * @param hid hid
     */
    @PutMapping("/updateOnRent/{hid}")
    public void updateOnRent(@PathVariable("hid") String hid) {
        houseStatusService.isOnRent(hid);
    }

    /**
     * 更新房屋状态 未发布
     *
     * @param hid hid
     */
    @PutMapping("/updateUnOnRent/{hid}")
    public void updateUnOnRent(@PathVariable("hid") String hid) {
        houseStatusService.unisOnRent(hid);
    }

    /******************************************************admin****************************
     * 修改确认的房屋，为已经核验
     * @return 返回状态等信息
     */
    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/checkHouse/{hid}")
    public Message checkHouse(@PathVariable("hid") String hid) {
        houseStatusService.updateCheck(hid);
        return Message.requestSuccess(ResponseConstant.UPDATE_SUCCESS);
    }

    //未通过审核
    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/noOkHouse/{hid}")
    public Message noOkHouse(@PathVariable("hid") String hid) {
        houseStatusService.updateCheckToUnSuccess(hid);
        return Message.requestSuccess(ResponseConstant.UPDATE_SUCCESS);
    }
}
