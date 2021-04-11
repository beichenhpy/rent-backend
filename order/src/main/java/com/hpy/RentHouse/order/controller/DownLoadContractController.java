package com.hpy.RentHouse.order.controller;

import com.hpy.RentHouse.order.service.DownLoadContractService;
import entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 10:25
 */
@RestController
public class DownLoadContractController {

    @Autowired
    private DownLoadContractService downloadContractRenter;
    //合同下载
    @GetMapping("/downloadContract/{oid}")
    public Message downloadContract(@PathVariable("oid") String oid, @RequestParam("type")String type) {
        String file;
        if("renter".equals(type)){
            file = downloadContractRenter.downloadContractRenter(oid);
        }else {
            file = downloadContractRenter.downloadContractOwner(oid);
        }
        return Message.requestSuccess(file);
    }
}
