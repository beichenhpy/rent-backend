package DTO;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: beichenhpy
 * @Date: 2020/3/5 18:05
 */
@Data
public class LoginModelDto {
    private String grantType;
    private String refreshToken;
    private String username;
    private String password;
}
