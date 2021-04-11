package DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDo {
    private String uid;
    private String username;
    private String password;
    private String nickName;
    private String phone;
    private String eCard;
    private Integer rid;
    private String sex;
    private Integer success;
    private String profilePhoto;
    private IdCardInfoDo idCardInfo;
}
