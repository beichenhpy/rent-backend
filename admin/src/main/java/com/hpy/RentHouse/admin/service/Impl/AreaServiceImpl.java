package com.hpy.RentHouse.admin.service.Impl;

import DO.CityDo;
import DO.ProvinceDo;
import DO.VillageDo;
import DTO.CityDto;
import DTO.HouseBasicDto;
import DTO.ProvinceDto;
import DTO.VillageDto;
import Query.UpdateProvinceQuery;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hpy.RentHouse.admin.dao.AreaMapper;
import com.hpy.RentHouse.admin.exception.MyAddException;
import com.hpy.RentHouse.admin.exception.MyDeleteException;
import com.hpy.RentHouse.admin.exception.MyUpdateException;
import com.hpy.RentHouse.admin.exception.UpdateExceptionHouse;
import com.hpy.RentHouse.admin.service.AreaService;
import com.hpy.RentHouse.admin.service.feign.HouseFeign;
import com.hpy.RentHouse.admin.service.feign.RedisFeign;
import com.hpy.RentHouse.admin.service.feign.RentFeign;
import entity.Constant;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author: beichenhpy
 * @Date: 2020/4/10 17:57
 */
@Service
public class AreaServiceImpl implements AreaService {
    @Autowired
    private RedisFeign redisFeign;
    @Autowired
    private HouseFeign houseFeign;
    @Autowired
    private RentFeign rentFeign;
    @Autowired
    private AreaMapper areaMapper;

    private static final Logger logger = LoggerFactory.getLogger(AreaServiceImpl.class);

    private static final String PROVINCES = "provinces";
    private static final String CITIES = "cities";
    /**
     * 查询所有的可以选择的省份和对应的城市
     *
     * @return 返回所有的省市区
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ProvinceDto> findAllProvinces() {
        List<ProvinceDto> provinceDtos = new ArrayList<>();
        List<ProvinceDo> provinceDos;
        Set<String> provincesKeys = redisFeign.keys(PROVINCES + "*");
        if (!provincesKeys.isEmpty()) {
            for (String provincesKey : provincesKeys) {
                //取值
                String provinceDtoJson = redisFeign.get(provincesKey);
                ProvinceDto provinceDto = JSON.parseObject(provinceDtoJson, ProvinceDto.class);
                //拿出cities对应的区，替换
                Set<String> citiesKeys = redisFeign.keys(CITIES + "*");
                List<CityDto> cityDtoList = new ArrayList<>();
                if (!citiesKeys.isEmpty()){
                    for (String citiesKey : citiesKeys) {
                        String cityJson = redisFeign.get(citiesKey);
                        CityDto cityDto = JSON.parseObject(cityJson, CityDto.class);
                        cityDtoList.add(cityDto);
                    }
                }
                provinceDto.setChildren(cityDtoList);
                provinceDtos.add(provinceDto);
            }
        } else {
            provinceDos = areaMapper.findAllProvinceAndCities();
            if (!provinceDos.isEmpty()) {
                for (ProvinceDo provinceDo : provinceDos) {
                    //将cityDo转成cityDto
                    List<CityDo> cityDos = provinceDo.getChildren();
                    List<CityDto> cityDtos = new ArrayList<>();
                    if (!cityDos.isEmpty()){
                        for (CityDo cityDo : cityDos) {
                            //拿出区转换
                            List<VillageDo> villageDos = cityDo.getChildren();
                            List<VillageDto> villageDtos = new ArrayList<>();
                            if (!villageDos.isEmpty()){
                                for (VillageDo villageDo : villageDos) {
                                    VillageDto villageDto = new VillageDto();
                                    BeanUtils.copyProperties(villageDo,villageDto);
                                    villageDtos.add(villageDto);
                                }
                            }
                            CityDto cityDto = new CityDto();
                            BeanUtils.copyProperties(cityDo,cityDto);
                            cityDto.setChildren(villageDtos);
                            cityDtos.add(cityDto);
                            redisFeign.set(CITIES + ":"+cityDto.getCid(),JSON.toJSONString(cityDto));
                        }
                    }
                    ProvinceDto provinceDto = new ProvinceDto();
                    BeanUtils.copyProperties(provinceDo, provinceDto);
                    provinceDto.setChildren(cityDtos);
                    provinceDtos.add(provinceDto);
                    redisFeign.set(PROVINCES + ":" + provinceDto.getPid(), JSON.toJSONString(provinceDto));
                }

            }
        }
        return provinceDtos;
    }



    /**
     * 分页显示省份 城市和区县
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<ProvinceDto> findAllProvinces(int page, int size) {
        List<ProvinceDto> provinceDtoList = new ArrayList<>();
        PageHelper.startPage(page, size);
        List<ProvinceDo> allProvinceDos = areaMapper.findAllProvinceAndCities();
        logger.info("---------{}",allProvinceDos);
        PageInfo<ProvinceDo> provinceDoPageInfo = new PageInfo<>(allProvinceDos);
        if (!allProvinceDos.isEmpty()) {
            //转换省
            for (ProvinceDo allProvinceDo : allProvinceDos) {
                //拿出city转换
                List<CityDto> cityDtoList = new ArrayList<>();
                List<CityDo> cityDos = allProvinceDo.getChildren();
                logger.info("cityDos-----------------{}",cityDos);
                if (!cityDos.isEmpty()) {
                    for (CityDo cityDo : cityDos) {
                        //拿出区转换
                        List<VillageDo> villageDos = cityDo.getChildren();
                        List<VillageDto> villageDtos = new ArrayList<>();
                        if (!villageDos.isEmpty()){
                            for (VillageDo villageDo : villageDos) {
                                VillageDto villageDto = new VillageDto();
                                BeanUtils.copyProperties(villageDo,villageDto);
                                villageDtos.add(villageDto);
                            }
                        }
                        CityDto cityDto = new CityDto();
                        BeanUtils.copyProperties(cityDo, cityDto);
                        cityDto.setChildren(villageDtos);
                        cityDtoList.add(cityDto);

                    }
                }
                ProvinceDto provinceDto = new ProvinceDto();
                BeanUtils.copyProperties(allProvinceDo, provinceDto);
                provinceDto.setChildren(cityDtoList);
                provinceDtoList.add(provinceDto);
            }
        }
        PageInfo<ProvinceDto> provinceDtoPageInfo = new PageInfo<>();
        provinceDtoPageInfo.setList(provinceDtoList);
        provinceDtoPageInfo.setTotal(provinceDoPageInfo.getTotal());
        provinceDtoPageInfo.setPageSize(size);
        provinceDtoPageInfo.setPageNum(page);
        return provinceDtoPageInfo;
    }

    /**
     * 添加省份
     *
     * @param provinceDto
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addProvince(ProvinceDto provinceDto) {
        logger.info("----------------------转换");
        ProvinceDo provinceDo = new ProvinceDo();
        BeanUtils.copyProperties(provinceDto, provinceDo);
        try {
            areaMapper.addProvince(provinceDo);
        } catch (Exception e) {
            throw new MyAddException();
        }
        //清空所有省
        Set<String> keys = redisFeign.keys(PROVINCES + "*");
        if (!keys.isEmpty()){
            for (String key : keys) {
                redisFeign.del(key);
            }
        }
    }

    /**
     * 添加城市
     *
     * @param cityDto
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addCity(CityDto cityDto) {
        logger.info("----------------------转换");
        CityDo cityDo = new CityDo();
        BeanUtils.copyProperties(cityDto, cityDo);
        try {
            areaMapper.addCity(cityDo);

        } catch (Exception e) {
            throw new MyAddException();
        }
        redisFeign.del(PROVINCES + ":"+cityDto.getPid());
    }

    /**
     * 删除城市
     *
     * @param cid
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteCity(String cid, String pid,String city) {
        Boolean houseByCity = houseFeign.findHouseByCity(city);
        if(houseByCity){
            throw new MyDeleteException();
        }
        Integer delete = areaMapper.deleteCity(cid);
        if (delete == 0) {
            throw new MyDeleteException();
        }
        redisFeign.del( PROVINCES + ":"+pid);
        redisFeign.del(CITIES + ":" + cid);
    }

    //更新城市
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCity(CityDto cityDto) {
        Boolean houseByCity = houseFeign.findHouseByCity(cityDto.getOldCityName());
        if(houseByCity){
            throw new UpdateExceptionHouse();
        }
        CityDo cityDo = new CityDo();
        BeanUtils.copyProperties(cityDto, cityDo);
        try {
            areaMapper.updateCity(cityDo);
        }catch (Exception e){
            throw new MyUpdateException();
        }
        //更新条件
        UpdateProvinceQuery query = new UpdateProvinceQuery(
                cityDto.getName(),
                cityDto.getOldCityName(),
                "city"
        );
        //远程更新出租信息
        rentFeign.updateProvinceOrCity(query);
        houseFeign.updateProvinceOrCityOrVillage(query);
        redisFeign.del(PROVINCES + ":"+cityDto.getPid());
        redisFeign.del(CITIES + ":" +cityDto.getCid());
    }

    /**
     * 删除省
     *
     * @param pid
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteProvince(String pid,String province) {
        Boolean houseByProvince = houseFeign.findHouseByProvince(province);
        if(houseByProvince){
            throw new MyDeleteException();
        }
        Integer delete = areaMapper.deleteProvince(pid);
        if (delete == 0) {
            throw new MyDeleteException();
        }
        redisFeign.del(PROVINCES + ":"+pid);
    }

    //更新省份
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateProvince(ProvinceDto provinceDto) {
        Boolean houseByProvince = houseFeign.findHouseByProvince(provinceDto.getOldProvinceName());
        if(houseByProvince){
            throw new UpdateExceptionHouse();
        }
        ProvinceDo provinceDo = new ProvinceDo();
        BeanUtils.copyProperties(provinceDto, provinceDo);
        try {
            areaMapper.updateProvince(provinceDo);
        }catch (Exception e){
            throw new MyUpdateException();
        }
        //更新条件
        UpdateProvinceQuery query = new UpdateProvinceQuery(
                provinceDto.getName(),
                provinceDto.getOldProvinceName(),
                "province"
        );
        //修改出租信息的省份信息
        rentFeign.updateProvinceOrCity(query);
        houseFeign.updateProvinceOrCityOrVillage(query);
        redisFeign.del( PROVINCES + ":" + provinceDto.getPid());
    }

    //添加区
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addVillage(VillageDto villageDto) {
        VillageDo villageDo = new VillageDo();
        BeanUtils.copyProperties(villageDto,villageDo);
       try {
           areaMapper.addVillage(villageDo);
       }catch (Exception e){
           throw new MyAddException();
       }
        redisFeign.del(CITIES + ":" + villageDto.getCid());
    }

    //删除区
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteVillage(String cid, String vid,String village) {
        Boolean houseByVillage = houseFeign.findHouseByVillage(village);
        if(houseByVillage){
            throw new MyDeleteException();
        }
        Integer delete = areaMapper.deleteVillage(vid);
        if (delete == 0){
            throw new MyDeleteException();
        }
        redisFeign.del(CITIES + ":" + cid);
    }

    //更新区名
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateVillage(VillageDto villageDto) {
        Boolean houseByVillage = houseFeign.findHouseByVillage(villageDto.getOldVillageName());
        if(houseByVillage){
            throw new UpdateExceptionHouse();
        }
        VillageDo villageDo = new VillageDo();
        BeanUtils.copyProperties(villageDto,villageDo);
        try {
            areaMapper.updateVillage(villageDo);
        }catch (Exception e){
            throw new MyUpdateException();
        }
        UpdateProvinceQuery query = new UpdateProvinceQuery(
                villageDto.getName(),
                villageDto.getOldVillageName(),
                "village"
        );
        houseFeign.updateProvinceOrCityOrVillage(query);
        redisFeign.del(CITIES + ":" + villageDto.getCid());
    }


}
