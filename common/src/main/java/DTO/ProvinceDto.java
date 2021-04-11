package DTO;

import lombok.Data;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/4/10 17:51
 *
 * 省 实体类 children是所属的城市
 */
@Data
public class ProvinceDto {
    private String pid;
    private String name;
    private String oldProvinceName;
    private List<CityDto> children;
}
