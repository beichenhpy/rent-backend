package DTO;

import lombok.Data;

/**
 * @author: beichenhpy
 * @Date: 2020/4/30 22:03
 */
@Data
public class ElectWaterUsageYearDto {
    private String electCount;
    private String waterCount;
    private String electPrice;
    private String waterPrice;
    private String year;
}
