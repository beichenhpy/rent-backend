package com.hpy.RentHouse.admin.service;

import DTO.UnitPriceDto;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/4/11 19:35
 */

public interface UnitPriceService {
    /**
     * 添加电费水费单价
     * @param unitPriceDto 单价
     */
    void addUnitPrice(UnitPriceDto unitPriceDto);

    /**
     * 根据province和city查询单价
     * @param province 省份
     * @param city 城市
     * @return 返回单价
     */
    List<UnitPriceDto> findUnitPriceByProvinceAndCity(String province, String city);

    /**
     * 查询所有单价
     * @return
     */
    PageInfo<UnitPriceDto> findAllUnitPrice(int page, int size);

    /**
     * 删除对应单价
     * @param priceId 单价编号
     */
    void deleteUnitPrice(String priceId,String province,String city);

    /**
     * 更新单价信息
     * @param unitPriceDto 单价
     */
    void updateUnitPrice(UnitPriceDto unitPriceDto);
}
