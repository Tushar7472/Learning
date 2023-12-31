package com.training.aem.core.models.impl;

import com.training.aem.core.models.HitsCountModel;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;


@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
        adapters = HitsCountModel.class)
public class HitsCountModelImpl implements HitsCountModel{
    @ValueMapValue
    private String message;

    public String getMessage() {
        return message;
    }
}
