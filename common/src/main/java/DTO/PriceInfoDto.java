package DTO;

import lombok.Data;

/**
 * @author: beichenhpy
 * @Date: 2020/3/12 0:54
 *
 * 费用详情
 */
@Data
public class PriceInfoDto {
    private String priceId;
    private String bid;
    private String water;
    private String elect;
    private Integer basic;

}
