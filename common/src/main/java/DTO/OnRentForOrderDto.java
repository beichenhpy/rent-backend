package DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author: beichenhpy
 * @Date: 2020/3/31 22:03
 *
 * 用于添加订单使用，包含一些订单需要的信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnRentForOrderDto {
    private String hid;
    private String uid;
    private Integer price;
    //月押金
    private Integer deposit;
    //钥匙押金
    private Integer keyDeposit;
    private Integer time;
}
