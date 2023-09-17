package de.cleem.tub.tsdbb.apps.worker.adapters;


import de.cleem.tub.tsdbb.api.model.Insert;
import de.cleem.tub.tsdbb.api.model.Select;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InsertResponse {

    private Insert insert;
    private int requestLength;
}
