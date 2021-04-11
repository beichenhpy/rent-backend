package DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: beichenhpy
 * @Date: 2020/3/31 18:13
 *
 * 首页的出租信息，用于展示
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnRentDto {
    private String hid;
    private String uid;
    private String title;
    private Integer price;
    private Integer deposit;
    private Integer keyDeposit;
    private Integer time;
    private String rule;
    private String image;
    //城市和省份 拓展查询方便
    private String province;
    private String city;
    private String createTime;
    private String detailPosition;
    private String introduction;
    private Integer leastTime;
    private Integer isFrozen;
    private Integer isOnRent;
}
