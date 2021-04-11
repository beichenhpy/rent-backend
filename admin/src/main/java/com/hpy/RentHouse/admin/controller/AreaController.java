package com.hpy.RentHouse.admin.controller;

import DTO.CityDto;
import DTO.ProvinceDto;
import DTO.VillageDto;
import com.github.pagehelper.PageInfo;
import com.hpy.RentHouse.admin.service.AreaService;
import entity.Message;
import entity.ResponseConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import util.IdWorker;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/4/10 18:19
 */
@RestController
public class AreaController {
    @Autowired
    private AreaService areaService;
    @Autowired
    private IdWorker idWorker;

    /**
     * 查询所有省份
     *
     * @return
     */
    @GetMapping("/findProvinces")
    public Message findProvinces() {
        List<ProvinceDto> allProvinces = areaService.findAllProvinces();
        return Message.requestSuccess(allProvinces);
    }

    /**
     * 分页查询显示省份
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/findProvincesPage")
    public Message findProvinces(@RequestParam("page") int page, @RequestParam("size") int size) {
        PageInfo<ProvinceDto> allProvinces = areaService.findAllProvinces(page, size);
        return Message.requestSuccess(allProvinces);
    }



    /**
     * 添加省份
     *
     * @param provinceDto 省份
     * @return 返回体
     */
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/addProvince")
    public Message addProvince(@RequestBody ProvinceDto provinceDto) {
        provinceDto.setPid(idWorker.nextId() + "");
        areaService.addProvince(provinceDto);
        return Message.requestSuccess(ResponseConstant.INSERT_SUCCESS);
    }
    //删除省
    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/delProvince")
    public Message delProvince(@RequestParam("pid") String pid,@RequestParam("province") String province){
        areaService.deleteProvince(pid,province);
        return Message.requestSuccess(ResponseConstant.DELETE_SUCCESS);
    }

    //修改城市
    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/updateProvince")
    public Message updateProvince(@RequestBody ProvinceDto provinceDto){
        areaService.updateProvince(provinceDto);
        return Message.requestSuccess(ResponseConstant.UPDATE_SUCCESS);
    }

    /**
     * 添加城市
     *
     * @param cityDto 城市
     * @return 返回体
     */
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/addCity")
    public Message addCity(@RequestBody CityDto cityDto) {
        cityDto.setCid(idWorker.nextId() + "");
        areaService.addCity(cityDto);
        return Message.requestSuccess(ResponseConstant.INSERT_SUCCESS);
    }
    //删除
    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/delCity")
    public Message delCity(
                @RequestParam("cid") String cid
                ,@RequestParam("pid") String pid,
                @RequestParam("city") String city){
        areaService.deleteCity(cid, pid,city);
        return Message.requestSuccess(ResponseConstant.DELETE_SUCCESS);
    }

    //修改城市
    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/updateCity")
    public Message updateCity(@RequestBody CityDto cityDto){
        areaService.updateCity(cityDto);
        return Message.requestSuccess(ResponseConstant.UPDATE_SUCCESS);
    }
    //添加区
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/addVillage")
    public Message addVillage(@RequestBody VillageDto villageDto){
        villageDto.setVid(idWorker.nextId()+"");
        areaService.addVillage(villageDto);
        return Message.requestSuccess(ResponseConstant.INSERT_SUCCESS);
    }
    //删除区
    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/deleteVillage")
    public Message deleteVillage(@RequestParam("cid") String cid,@RequestParam("vid")String vid,@RequestParam("village")String village){
        areaService.deleteVillage(cid, vid,village);
        return Message.requestSuccess(ResponseConstant.DELETE_SUCCESS);
    }
    //更新区
    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/updateVillage")
    public Message updateVillage(@RequestBody VillageDto villageDto){
        areaService.updateVillage(villageDto);
        return Message.requestSuccess(ResponseConstant.UPDATE_SUCCESS);
    }


    //检查权限使用
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/check")
    public Message check(){
        return Message.requestSuccess(null);
    }
}
