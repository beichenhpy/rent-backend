package com.hpy.RentHouse.admin.service.Impl;

import DO.UnitPriceDo;
import DTO.UnitPriceDto;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hpy.RentHouse.admin.dao.UnitPriceMapper;
import com.hpy.RentHouse.admin.exception.MyAddException;
import com.hpy.RentHouse.admin.exception.MyDeleteException;
import com.hpy.RentHouse.admin.exception.MyUpdateException;
import com.hpy.RentHouse.admin.service.UnitPriceService;
import com.hpy.RentHouse.admin.service.feign.HouseFeign;
import com.hpy.RentHouse.admin.service.feign.RedisFeign;
import entity.Constant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/4/11 19:36
 */
@Service
public class UnitPriceServiceImpl implements UnitPriceService {
    @Autowired
    private UnitPriceMapper unitPriceMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisFeign redisFeign;
    @Autowired
    private HouseFeign houseFeign;

    private static final Logger logger = LoggerFactory.getLogger(UnitPriceServiceImpl.class);
    /**
     * 添加电费水费单价
     *
     * @param unitPriceDto 单价
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addUnitPrice(UnitPriceDto unitPriceDto) {
        unitPriceDto.setPriceId(idWorker.nextId() + "");
        UnitPriceDo unitPriceDo = new UnitPriceDo();
        BeanUtils.copyProperties(unitPriceDto,unitPriceDo);
        try {
            unitPriceMapper.addUnitPrice(unitPriceDo);
        }catch (Exception e){
            throw new MyAddException();
        }
        logger.info("-----------------redis---------------------");
        redisFeign.del(unitPriceDto.getProvince() + ":" + unitPriceDto.getCity()+":"+ Constant.UNITPRICE);
    }

    /**
     * 根据province和city查询单价
     * 将电费和水费放在一起
     * @param province 省份
     * @param city     城市
     * @return 返回单价
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<UnitPriceDto> findUnitPriceByProvinceAndCity(String province, String city) {
        List<UnitPriceDo> unitPricesDos;
        List<UnitPriceDto> unitPriceDtos = new ArrayList<>();
        String unitPrinceJson = redisFeign.get(province + ":" + city + ":" + Constant.UNITPRICE);
        if (StringUtils.isNotEmpty(unitPrinceJson)) {
            unitPricesDos = JSON.parseArray(unitPrinceJson, UnitPriceDo.class);

        } else {
            unitPricesDos = unitPriceMapper.findUnitPriceByProvinceAndCity(province, city);
            if (!unitPricesDos.isEmpty()){
                redisFeign.set(province + ":" + city + ":" + Constant.UNITPRICE,JSON.toJSONString(unitPricesDos));
            }
        }
        if (!unitPricesDos.isEmpty()){
            for (UnitPriceDo unitPricesDo : unitPricesDos) {
                UnitPriceDto unitPriceDto = new UnitPriceDto();
                BeanUtils.copyProperties(unitPricesDo,unitPriceDto);
                unitPriceDtos.add(unitPriceDto);
            }
        }
        return unitPriceDtos;
    }

    /**
     * 分页查询费用单价
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PageInfo<UnitPriceDto> findAllUnitPrice(int page,int size) {
        PageHelper.startPage(page,size);
        List<UnitPriceDo> allUnitPrice = unitPriceMapper.findAllUnitPrice();
        PageInfo<UnitPriceDo> unitPriceDoPageInfo = new PageInfo<>(allUnitPrice);
        PageInfo<UnitPriceDto> unitPriceDtoPageInfo = new PageInfo<>();
        List<UnitPriceDto> unitPriceDtos = new ArrayList<>();
        if (!allUnitPrice.isEmpty()){
            for (UnitPriceDo unitPriceDo : allUnitPrice) {
                UnitPriceDto unitPriceDto = new UnitPriceDto();
                BeanUtils.copyProperties(unitPriceDo,unitPriceDto);
                unitPriceDtos.add(unitPriceDto);
            }
            unitPriceDtoPageInfo.setList(unitPriceDtos);
            unitPriceDtoPageInfo.setTotal(unitPriceDoPageInfo.getTotal());
            unitPriceDtoPageInfo.setPageNum(page);
            unitPriceDtoPageInfo.setPageSize(size);
        }
        return unitPriceDtoPageInfo;
    }

    /**
     * 删除对应编号的单价信息，并删除缓存
     * @param priceId 单价编号
     * @param city 城市信息
     * @param province  省份信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteUnitPrice(String priceId,String province,String city) {
        Boolean houseByProvince = houseFeign.findHouseByProvince(province);
        if(houseByProvince){
            throw new MyDeleteException();
        }
        Integer delete = unitPriceMapper.deleteUnitPrice(priceId);
        if (delete == 0){
            throw new MyDeleteException();
        }
        //清空对应缓存
        redisFeign.del(province + ":" + city + ":" + Constant.UNITPRICE);
    }

    /**
     * 更新单价信息，并清空缓存
     * @param unitPriceDto 单价实体
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUnitPrice(UnitPriceDto unitPriceDto) {
        UnitPriceDo unitPriceDo = new UnitPriceDo();
        BeanUtils.copyProperties(unitPriceDto,unitPriceDo);
        try {
            Integer update = unitPriceMapper.updateUnitPrice(unitPriceDo);
            if (update == 0){
                throw new MyUpdateException();
            }
        }catch (Exception e){
            throw new MyUpdateException();
        }
        //清空缓存
        redisFeign.del(unitPriceDto.getProvince()+ ":" + unitPriceDto.getCity() + ":" + Constant.UNITPRICE);
    }
}
