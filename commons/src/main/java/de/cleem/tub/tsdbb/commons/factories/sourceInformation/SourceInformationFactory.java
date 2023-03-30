package de.cleem.tub.tsdbb.commons.factories.sourceInformation;

import de.cleem.tub.tsdbb.api.model.SourceInformation;
import de.cleem.tub.tsdbb.commons.base.clazz.BaseClass;

public class SourceInformationFactory extends BaseClass {

    public static SourceInformation getSourceInformation(final String url){

        final SourceInformation sourceInformation = new SourceInformation();
        sourceInformation.setUrl(url);

        return sourceInformation;
    }
}
