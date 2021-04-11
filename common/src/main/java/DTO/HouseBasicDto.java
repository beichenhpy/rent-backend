package DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: beichenhpy
 * @Date: 2020/3/31 18:27
 * 只包含房屋的基本信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseBasicDto {
    private String hid;
    private String province;
    private String city;
    private String village;
    private String address;
    private String building;
    private String unit;
    private String houseNum;
    private String layout;
    private String forward;
    private Double area;
    private Integer isRented;
    private String uid;
    private Integer isCheck;
    private Integer isOnRent;
}
