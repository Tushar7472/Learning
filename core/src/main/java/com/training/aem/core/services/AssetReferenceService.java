package com.training.aem.core.services;

import com.training.aem.core.Beans.ReferenceResultBean;
import org.apache.sling.api.resource.ResourceResolver;

public interface AssetReferenceService {

       String FULLTEXT = "fulltext";
       String PATH = "path";


       ReferenceResultBean getCFReferences(String assetPath, ResourceResolver resolver);
}
