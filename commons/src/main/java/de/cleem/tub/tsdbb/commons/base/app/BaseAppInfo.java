package de.cleem.tub.tsdbb.commons.base.app;

import de.cleem.tub.tsdbb.commons.base.clazz.BaseClass;
import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseAppInfo extends BaseClass {

    private Date startDate;
    private Date endDate;
    private String appClassName;

}
