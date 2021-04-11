package DTO;

import lombok.Data;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/4/30 20:41
 */
@Data
public class ElectWaterUsageDto {
    private List<String> date;
    private List<String> electCount;
    private List<String> waterCount;
    private List<String> electPrice;
    private List<String> waterPrice;
}
