package DTO;

import lombok.Data;


/**
 * @author: beichenhpy
 * @Date: 2020/5/3 23:31
 */
@Data
public class HousePriceStatisticsDto {
    //房间号
    private String houseName;
    //一年的房租和
    private Integer prices;
}
