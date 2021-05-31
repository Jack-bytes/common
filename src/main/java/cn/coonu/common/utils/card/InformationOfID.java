package cn.coonu.common.utils.card;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 *
 * @author Jack wang
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class InformationOfID {

    private String  province;
    private Date    birthday;
    private Integer age;
    /**
     * 男,女
     */
    private String  gender;

}
