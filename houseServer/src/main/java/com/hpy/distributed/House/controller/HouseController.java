package com.hpy.distributed.House.controller;


import DTO.HouseBasicDto;
import DTO.HouseChecked;
import Query.UpdateProvinceQuery;
import com.github.pagehelper.PageInfo;
import com.hpy.distributed.House.service.HouseService;
import entity.Message;
import entity.ResponseConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import util.IdWorker;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/2/25 21:18
 * <p>
 * <p>
 * 房产证照片 必须添加 否则在管理员验证房屋信息时不通过
 */
@RestController
public class HouseController {
    /**
     * 获得上下文中的用户信息
     */
    private String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2Authentication)) {
            return null;
        }
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
        Authentication userAuthentication = oAuth2Authentication.getUserAuthentication();
        return userAuthentication.getName();
    }
    @Autowired
    private HouseService houseService;
    @Autowired
    private IdWorker idWorker;

    //*******************************************GET****************************************

    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findChecked")
    public Message findChecked(){
        List<HouseChecked> checkedHouse = houseService.findCheckedHouse(getUserId());
        return Message.requestSuccess(checkedHouse);
    }

    /**
     * 显示用户所有的房屋信息
     *
     * @return 信息
     */
    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findAll")
    public Message showHouse(@RequestParam("page") int page, @RequestParam("size") int size) {
        PageInfo<HouseBasicDto> allHouseByUid =
                houseService.findAllHouseByUid(
                        getUserId(),
                        page,
                        size);
        return Message.requestSuccess(allHouseByUid);


    }



    /**
     * 找到房屋编号的对应的房屋基本信息，远程调用使用
     */
    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findBasicHouse/{hid}")
    public Message findBasicHouse(@PathVariable("hid") String hid) {
        return Message.requestSuccess(houseService.findBasicHouse(hid));
    }

    /**
     * 找到房屋编号的对应的房屋基本信息，远程调用使用
     */
    @GetMapping("/findHouseByProvince")
    public Boolean findHouseByProvince(@RequestParam("province")String province) {
        return houseService.findHouseByProvince(province);
    }
    /**
     * 找到房屋编号的对应的房屋基本信息，远程调用使用
     */
    @GetMapping("/findHouseByCity")
    public Boolean findHouseByCity(@RequestParam("city")String city) {
        return houseService.findHouseByCity(city);
    }
    /**
     * 找到房屋编号的对应的房屋基本信息，远程调用使用
     */
    @GetMapping("/findHouseByVillage")
    public Boolean findHouseByVillage(@RequestParam("village")String village) {
        return houseService.findHouseByVillage(village);
    }



    /******admin*******
     * 显示所有的未核验的房屋
     * 用于admin服务使用
     * @return 返回房屋的信息和是否成功
     */
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/showHouseUncheck")
    public Message showHouseUncheck(@RequestParam("page") int page, @RequestParam("size") int size) {
        PageInfo<HouseBasicDto> all =
                houseService.findAll(
                        page,
                        size);
        return Message.requestSuccess(all);

    }




    //*******************************************POST****************************************

    /**
     * 添加新房屋
     * 进入到验证阶段，验证未通过不能发布出租信息
     *
     * @param houseBasicDto 房屋
     * @return 信息
     */
    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/modifyHouse")
    public Message addHouse(@RequestBody HouseBasicDto houseBasicDto) {
        houseBasicDto.setUid(getUserId());
        houseBasicDto.setHid(idWorker.nextId() + "");
        houseService.addHouse(houseBasicDto);
        return Message.requestSuccess(ResponseConstant.INSERT_SUCCESS);

    }




    //*******************************************PUT****************************************



    /**
     * 修改省市县的名字时同时更新
     * @param query 内容
     * @return 结果集
     */
    @PutMapping("/updateProvinceOrCityOrVillage")
    public Message updateProvinceOrCityOrVillage(@RequestBody UpdateProvinceQuery query){
        houseService.updateProvinceOrCityOrVillage(query.getNewName(),query.getOldName(),query.getType());
        return Message.requestSuccess(ResponseConstant.UPDATE_SUCCESS);
    }
    /**
     * 修改房屋基本信息
     *
     * @param houseBasicDto 房屋基本信息
     * @return 返回信息
     */
    @PreAuthorize("hasAuthority('user')")
    @PutMapping("/updateHouse")
    public Message updateHouse(@RequestBody HouseBasicDto houseBasicDto) {
        houseBasicDto.setUid(getUserId());
        houseService.updateHouse(houseBasicDto);
        return Message.requestSuccess(ResponseConstant.UPDATE_SUCCESS);
    }

    /****************************************************DELETE***********************************
     * 删除房屋 房屋未出租可以删除，删除后同时删除对应出租信息
     *
     * 已租信息由于在退租后就会删除对应订单 所以不用删除
     * @param hid 房屋编号
     */
    @PreAuthorize("hasAuthority('user')")
    @DeleteMapping("/delete/{hid}")
    public Message deleteHouse(@PathVariable("hid") String hid) {
        houseService.deleteHouseByHid(hid);
        return Message.requestSuccess(ResponseConstant.DELETE_SUCCESS);
    }




}
