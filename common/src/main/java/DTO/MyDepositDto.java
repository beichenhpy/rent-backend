package DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: beichenhpy
 * @Date: 2020/5/1 19:50
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class MyDepositDto extends DepositInfoDto{
    private Integer isFinish;
}
