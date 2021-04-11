package DTO;

import lombok.Data;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/4/10 17:53
        */
@Data
public class CityDto {
    private String name;
    private String pid;
    private String cid;
    private String oldCityName;
    private List<VillageDto> children;
}
