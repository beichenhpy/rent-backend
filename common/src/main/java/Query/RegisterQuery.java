package Query;

import lombok.Data;

/**
 * @author: beichenhpy
 * @Date: 2020/4/18 15:32
 */
@Data
public class RegisterQuery {
    private String username;
    private String password;
    private String verCode;
}
