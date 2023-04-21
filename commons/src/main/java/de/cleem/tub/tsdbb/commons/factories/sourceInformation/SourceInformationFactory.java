package de.cleem.tub.tsdbb.commons.factories.sourceInformation;

import de.cleem.tub.tsdbb.api.model.SourceInformation;
import de.cleem.tub.tsdbb.commons.base.clazz.BaseClass;

import java.net.URI;

public class SourceInformationFactory extends BaseClass {

    public static SourceInformation getSourceInformation(final URI url){

        final SourceInformation sourceInformation = new SourceInformation();
        sourceInformation.setUrl(url);

        return sourceInformation;
    }
}
