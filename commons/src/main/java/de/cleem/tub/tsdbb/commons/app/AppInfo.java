package de.cleem.tub.tsdbb.commons.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppInfo {

    private Date startDate;
    private Date endDate;
    private String appClassName;

}
