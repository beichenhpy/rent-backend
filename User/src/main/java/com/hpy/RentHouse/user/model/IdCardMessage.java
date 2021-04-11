package com.hpy.RentHouse.user.model;

import lombok.Data;

/**
 * @author: beichenhpy
 * @Date: 2020/3/15 16:57
 */
@Data
public class IdCardMessage {
    private String address;
    private String name;
    private String nationality;
    private String num;
    private String sex;
    private String birth;
    private Boolean success;
}
