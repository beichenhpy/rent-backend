package Query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: beichenhpy
 * @Date: 2020/4/15 19:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProvinceQuery {
    private String newName;
    private String oldName;
    private String type;
}
