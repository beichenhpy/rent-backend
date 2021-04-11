package DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author beichen
 *
 * 带密码的用户实体类，用于修改密码
 */
@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
public class UserWithPassDto extends UserDto {
    private String password;
}
