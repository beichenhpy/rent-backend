package com.hpy.RentHouse.admin.service;

import DTO.CityDto;
import DTO.ProvinceDto;
import DTO.VillageDto;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/4/10 17:55
 */

public interface AreaService {

    List<ProvinceDto> findAllProvinces();



    PageInfo<ProvinceDto> findAllProvinces(int page, int size);

    void addProvince(ProvinceDto provinceDto);

    void addCity(CityDto cityDto);

    void deleteCity(String cid,String pid,String city);

    void updateCity(CityDto cityDto);

    void deleteProvince(String pid,String province);

    void updateProvince(ProvinceDto provinceDto);

    void addVillage(VillageDto villageDto);

    void deleteVillage(String cid,String vid,String village);

    void updateVillage(VillageDto villageDto);
}
