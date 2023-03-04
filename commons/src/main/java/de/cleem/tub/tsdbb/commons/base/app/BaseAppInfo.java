package de.cleem.tub.tsdbb.commons.base.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseAppInfo {

    private Date startDate;
    private Date endDate;
    private String appClassName;

}
