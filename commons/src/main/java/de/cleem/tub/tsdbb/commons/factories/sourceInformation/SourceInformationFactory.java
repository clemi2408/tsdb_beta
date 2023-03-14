package de.cleem.tub.tsdbb.commons.factories.sourceInformation;

import de.cleem.tub.tsdbb.api.model.SourceInformation;

public class SourceInformationFactory {

    public static SourceInformation getSourceInformation(final String url){

        final SourceInformation sourceInformation = new SourceInformation();
        sourceInformation.setUrl(url);

        return sourceInformation;
    }
}
