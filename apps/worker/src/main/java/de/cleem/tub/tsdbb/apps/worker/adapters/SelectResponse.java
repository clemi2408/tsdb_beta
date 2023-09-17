package de.cleem.tub.tsdbb.apps.worker.adapters;


import de.cleem.tub.tsdbb.api.model.Select;
import lombok.Data;

@Data
public class SelectResponse {

    private Select select;
    private int requestLength;
    private int responseLength;

}
