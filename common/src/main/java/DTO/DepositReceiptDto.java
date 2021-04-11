package DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: beichenhpy
 * @Date: 2020/5/1 19:42
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class DepositReceiptDto extends DepositInfoDto{
    private String hid;
}
