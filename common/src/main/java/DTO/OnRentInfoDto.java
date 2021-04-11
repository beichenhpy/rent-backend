package DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: beichenhpy
 * @Date: 2020/2/23 21:30
 *
 * 出租租房信息实体类
 * 点击具体的某个出租信息后显示的
 */
@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
public class OnRentInfoDto extends OnRentDto{
    //房屋的信息
    private HouseDto house;
    //房东用户信息
    private UserRentDto owner;
}
