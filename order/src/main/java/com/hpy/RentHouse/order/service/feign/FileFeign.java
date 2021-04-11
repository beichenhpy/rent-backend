package com.hpy.RentHouse.order.service.feign;

import DTO.ContractDto;
import Query.SignQuery;
import entity.Message;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author: beichenhpy
 * @Date: 2020/3/25 21:35
 */
@FeignClient("file-service")
public interface FileFeign {

    //添加合同
    @PostMapping("/addContract")
    void addContract(@RequestBody ContractDto contractDto);

    //签合同
    @PostMapping("/signContract")
    public Message signContract(@RequestBody SignQuery signQuery);
}
