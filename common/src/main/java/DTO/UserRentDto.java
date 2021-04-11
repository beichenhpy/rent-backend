package DTO;

import lombok.Data;

/**
 * @author: beichenhpy
 * @Date: 2020/3/31 19:57
 *
 * 用于出租的详细信息中的房东的一些信息
 */
@Data
public class UserRentDto {
    private String uid;
    private String nickName;
    private String phone;
    private String sex;
    private String profilePhoto;
}
