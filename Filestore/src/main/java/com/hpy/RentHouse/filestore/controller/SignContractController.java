package com.hpy.RentHouse.filestore.controller;

import DTO.ContractDto;
import Query.SignQuery;
import com.hpy.RentHouse.filestore.service.SignContractService;
import entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: beichenhpy
 * @Date: 2020/3/12 18:35
 */
@RestController
public class SignContractController {
    @Autowired
    private SignContractService signContractService;


    /**
     * 添加合同信息到数据库 order远程调用
     * @param ContractDto
     * @return
     */
    @PostMapping("/addContract")
    public void ContractRenter(@RequestBody ContractDto ContractDto){
        signContractService.addContract(ContractDto);
    }


    //签合同

    @PostMapping("/signContract")
    public Message signContract(@RequestBody SignQuery signQuery){
        if("renter".equals(signQuery.getType())){
            signContractService.renterSignContract(signQuery.getOid());
        }else{
            signContractService.ownerSignContract(signQuery.getOid());
        }
        return Message.requestSuccess(null);
    }






    @DeleteMapping("/deleteContract/{oid}")
    public void delete(@PathVariable("oid") String oid){
        signContractService.deleteContract(oid);
    }
}
