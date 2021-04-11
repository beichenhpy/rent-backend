package DO;

import lombok.Data;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/4/10 17:51
 */
@Data
public class ProvinceDo {
    private String pid;
    private String name;
    private List<CityDo> children;
}
