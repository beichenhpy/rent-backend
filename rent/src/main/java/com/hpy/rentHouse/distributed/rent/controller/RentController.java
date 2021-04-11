package com.hpy.rentHouse.distributed.rent.controller;

import DTO.OnRentDto;
import DTO.OnRentForOrderDto;
import DTO.OnRentInfoDto;
import Query.UpdateProvinceQuery;
import com.github.pagehelper.PageInfo;
import com.hpy.rentHouse.distributed.rent.service.Feign.HouseFeign;
import com.hpy.rentHouse.distributed.rent.service.RentService;
import com.hpy.rentHouse.distributed.rent.utils.OssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

import entity.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author beichenhpy
 * 房屋租赁
 */
@RestController
public class RentController {
    @Autowired
    private RentService rentService;

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
    private OssUtil ossUtil;
    //图片子目录
    @Value("${file.imgPath}")
    private String imgPath;
    //图片子目录
    @Value("${file.videoPath}")
    private String videoPath;
    //图片/视频子目录下的租房子目录
    @Value("${file.rentPath}")
    private String rentPath;


    //*******************************************GET****************************************

    /**
     * 查询所有出租信息
     *
     * @return 返回信息
     */
    @GetMapping("/findAll")
    public Message findAllPush(@RequestParam("page") int page,
                               @RequestParam("size") int size,
                               @RequestParam("province") String province,
                               @RequestParam("city") String city) {
        PageInfo<OnRentDto> allPushRentInfo =
                rentService.findAllPushRentInfo(page, size,province,city);
        return Message.requestSuccess(allPushRentInfo);
    }

    /**
     * 根据房屋编号和用户编号查询到具体的一个出租信息的详细信息
     *
     * @return 返回信息
     */
    @GetMapping("/findRent")
    public Message findAllRentByHid(@RequestParam("hid") String hid,
                                    @RequestParam("uid") String uid) {
        OnRentInfoDto onRentInfoDto =
                rentService.findOnRentInfoAllByHidUid(hid,uid);
        return Message.requestSuccess(onRentInfoDto);
    }

    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findRentByHid/{hid}")
    public Message findAllRentByHid(@PathVariable("hid") String hid){
        OnRentDto rentByHid = rentService.findRentByHid(hid);
        return Message.requestSuccess(rentByHid);
    }

    /**
     * 查询用户的所有出租信息
     *
     * @return 返回信息
     */
    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findMyRent")
    public Message findMyRent(@RequestParam("page") int page,
                              @RequestParam("size") int size) {
        PageInfo<OnRentDto> allOnRentInfoDto = rentService.findOnRentInfoByUid(page,size,getUserId());
        return Message.requestSuccess(allOnRentInfoDto);
    }

    /**
     * 根据hid查询到对应的出租信息 用于添加订单使用
     *
     * @param hid hid
     * @return 返回出租信息
     */
    @GetMapping("/findForOrder/{hid}")
    public OnRentForOrderDto findOnRentInfoForOrderByHid(@PathVariable("hid") String hid) {
        return rentService.findOnRentByHid(hid);
    }

    //*******************************************POST****************************************

    /**
     * 发布新的出租信息
     * 为了安全，每次的电子签名都不一样，使用完成后删除
     *
     * <p>
     * hid:通过前台选择自己的要出租的房屋
     * uid通过token直接传入
     *
     * @param onRentDto 出租信息
     * @return 信息
     */
    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/putRent")
    public Message putRent(@RequestBody OnRentDto onRentDto) {
        onRentDto.setUid(getUserId());
        rentService.addOnRent(onRentDto);
        return Message.requestSuccess(ResponseConstant.INSERT_SUCCESS);
    }


    //*******************************************PUT****************************************

    /**
     * 修改出租房屋信息
     *
     * @param onRentDto 需要新的出租房屋对象参数
     * @return 返回信息
     */
    @PreAuthorize("hasAuthority('user')")
    @PutMapping("/putRent")
    public Message modifyPullRent(@RequestBody OnRentDto onRentDto) {
        rentService.modifyOnRent(onRentDto);
        return Message.requestSuccess(ResponseConstant.UPDATE_SUCCESS);
    }

    /**
     * 冻结出租信息用于订单
     *
     * @param hid hid
     */
    @PutMapping("/frozenOnRent/{hid}")
    public void frozenOnRent(@PathVariable("hid") String hid) {
        rentService.frozenOnRent(hid);
    }

    /**
     * 解冻出租信息用于订单
     *
     * @param hid hid
     */
    @PutMapping("/unfrozenOnRent/{hid}")
    public void unfrozenOnRent(@PathVariable("hid") String hid) {
        rentService.unfrozenOnRent(hid);
    }

    //发布
    @PreAuthorize("hasAuthority('user')")
    @PutMapping("/upRent/{hid}")
    public Message upRent(@PathVariable("hid") String hid){
        rentService.upRent(hid);
        return Message.requestSuccess(null);
    }

    //下架
    @PreAuthorize("hasAuthority('user')")
    @PutMapping("/downRent/{hid}")
    public Message downRent(@PathVariable("hid") String hid){
        rentService.downRent(hid);
        return Message.requestSuccess(null);
    }

    //同步更新房屋和出租信息
    @PreAuthorize("hasAuthority('user')")
    @PutMapping("/updateHouseRent")
    public void updateHouseRent(@RequestBody OnRentDto onRentDto){
        onRentDto.setUid(getUserId());
        rentService.updateHouseRent(onRentDto);
    }

    /**
     * 上传文件，修改出租信息的封面图
     *
     * @param file 图片信息
     * @param hid  房屋编号
     * @return 返回信息
     */
    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/updateImg/{hid}")
    public Message updateImg(MultipartFile file, @PathVariable("hid") String hid) {
        String filename = ossUtil.upLoadfile(file, imgPath + rentPath, "rentImg");
        rentService.updateImage(filename, hid);
        return Message.requestSuccess(ResponseConstant.UPDATE_SUCCESS);
    }



    //更新省/市名字
    @PutMapping("/updateProvinceOrCity")
    public Message updateProvinceOrCity(@RequestBody UpdateProvinceQuery query){
        rentService.updateProvinceOrCity(query.getNewName(), query.getOldName(),query.getType());
        return Message.requestSuccess(ResponseConstant.UPDATE_SUCCESS);
    }

    //*******************************************DELETE****************************************

    /**
     * 删除出租信息
     *
     * @param hid hid
     * @return 返回信息
     */
    @PreAuthorize("hasAuthority('user')")
    @DeleteMapping("/delRent/{hid}")
    public Message delOnRent(@PathVariable("hid") String hid) {
        rentService.delOnRent(hid);
        return Message.requestSuccess(ResponseConstant.DELETE_SUCCESS);
    }

    /**
     * 用于远程调用 删除房屋时，删除对应的出租信息
     * 不用更新house表
     *
     * @param hid 房屋编号
     */
    @PreAuthorize("hasAuthority('user')")
    @DeleteMapping("/delRentNoCheck/{hid}")
    public void delOnRentNoCheck(@PathVariable("hid") String hid) {
        rentService.delOnRentNoCheck(hid);
    }


}
