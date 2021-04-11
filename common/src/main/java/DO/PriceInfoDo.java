package DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: beichenhpy
 * @Date: 2020/3/12 0:54
 */
@Data
public class PriceInfoDo {
    private String priceId;
    private String bid;
    private Double water;
    private Double elect;
    private Integer basic;

}
