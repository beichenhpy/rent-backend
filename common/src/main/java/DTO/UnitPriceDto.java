package DTO;

import lombok.Data;

/**
 * @author: beichenhpy
 * @Date: 2020/3/25 14:23
 * 电费/水费单价 三个阶梯
 *电费：
 * 广西 0.5283  0.5783  0.8283
 *      230/月   370/月   >370
 *水费：
 * 广西南宁：2.69/吨  3.42/吨  4.14
 *        x<32吨   32<x<48  >48
 */
@Data
public class UnitPriceDto {
    private String priceId;
    private String province;
    private String city;
    //一阶段用量标准 和 单价
    private Integer firstStage;
    private Double firstStagePrice;
    //二阶段用量标准 和 单价
    private Integer secondStage;
    private Double secondStagePrice;
    //三阶段用量标准 和 单价
    private Double thirdStagePrice;
    //类型 水费还是电费
    private String type;
}
