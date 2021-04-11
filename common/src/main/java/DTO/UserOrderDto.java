package DTO;

import lombok.Data;

/**
 * @author: beichenhpy
 * @Date: 2020/3/31 20:31
 *
 * 用于签署合同使用的 房东的信息
 */
@Data
public class UserOrderDto {
    private String uid;
    private String phone;
    private String eCard;
    private String profilePhoto;
    private IdCardInfoDto idCardInfo;
}
