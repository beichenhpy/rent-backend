package DTO;


import lombok.*;

/**
 * @author: beichenhpy
 * @Date: 2020/2/21 20:41
 *
 * 房屋实体类 包含所有信息
 */
@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
public class HouseDto extends HouseBasicDto{
    /**
     * houseInfoDo 1对1 房屋详细信息
     *
     */
    private HouseInfoDto houseInfo;
}
