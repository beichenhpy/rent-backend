package com.hpy.RentHouse.admin.controller;

import DTO.UnitPriceDto;
import com.github.pagehelper.PageInfo;
import com.hpy.RentHouse.admin.service.UnitPriceService;
import entity.Message;
import entity.ResponseConstant;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/4/11 19:38
 */
@RestController
public class UnitPriceController {

    @Autowired
    private UnitPriceService unitPriceService;

    /**
     * 管理员添加电费水费单价
     *
     * @param unitPriceDto 单价
     * @return 返回信息
     */
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/addUnitPrice")
    public Message addUnitPrice(@RequestBody UnitPriceDto unitPriceDto) {
        unitPriceService.addUnitPrice(unitPriceDto);
        return Message.requestSuccess(ResponseConstant.INSERT_SUCCESS);
    }

    /**
     * 管理员查询所有电费水费单价
     *
     * @param page 当前页面
     * @param size 每页显示个数
     * @return
     */
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/findUnitPrice")
    public Message findUnitPrice(@RequestParam("page") int page,
                                 @RequestParam("size") int size) {
        PageInfo<UnitPriceDto> allUnitPrice = unitPriceService.findAllUnitPrice(page, size);
        return Message.requestSuccess(allUnitPrice);
    }

    /**
     * 账单服务调用查询对应单价
     *
     * @param province
     * @param city
     * @return
     */
    @GetMapping("/findUnitPriceByProvince")
    public List<UnitPriceDto> findUnitPrice(@RequestParam("province") String province,
                                            @RequestParam("city") String city) {
        return unitPriceService.findUnitPriceByProvinceAndCity(province, city);
    }

    /**
     * 删除对应的单价
     *
     * @param  priceId province city 为了清空缓存
     * @return 结果集
     */
    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/deleteUnitPrice")
    public Message deleteUnitPrice(@RequestParam String priceId,
                                   @RequestParam String province,
                                   @RequestParam String city) {
        unitPriceService.deleteUnitPrice(
                priceId,
                province,
                city
        );
        return Message.requestSuccess(ResponseConstant.DELETE_SUCCESS);
    }


    /**
     * 更新单价信息
     *
     * @param unitPriceDto 单价实体
     * @return 返回结果
     */
    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/updateUnitPrice")
    public Message updateUnitPrice(@RequestBody UnitPriceDto unitPriceDto) {
        unitPriceService.updateUnitPrice(unitPriceDto);
        return Message.requestSuccess(ResponseConstant.UPDATE_SUCCESS);
    }
}
