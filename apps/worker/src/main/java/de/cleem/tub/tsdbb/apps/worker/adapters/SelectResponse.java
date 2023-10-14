package de.cleem.tub.tsdbb.apps.worker.adapters;


import de.cleem.tub.tsdbb.api.model.Select;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SelectResponse {

    private Select select;
    private int requestLength;
    private int responseLength;
    private String responseBody;

}
