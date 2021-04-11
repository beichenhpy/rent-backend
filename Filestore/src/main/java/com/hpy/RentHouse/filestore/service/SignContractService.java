package com.hpy.RentHouse.filestore.service;

import DTO.ContractDto;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 8:49
 */

public interface SignContractService {

    void renterSignContract(String oid);

    void ownerSignContract(String oid);

    void addContract(ContractDto contractDto);

    void deleteContract(String oid);
}
