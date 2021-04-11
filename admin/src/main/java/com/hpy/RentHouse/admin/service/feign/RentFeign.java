package com.hpy.RentHouse.admin.service.feign;

import Query.UpdateProvinceQuery;
import entity.Message;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: beichenhpy
 * @Date: 2020/4/11 22:45
 */
@FeignClient("rent-service")
public interface RentFeign {

    //更新省/市名字 rent服务
    @PutMapping("/updateProvinceOrCity")
    Message updateProvinceOrCity(
            @RequestBody UpdateProvinceQuery query);


}
