package com.training.aem.core.services.impl;

import com.training.aem.core.Beans.ReferenceResultBean;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.PageManager;
import com.training.aem.core.services.AssetReferenceService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.*;

@Component(service = AssetReferenceService.class, immediate = true)
public class AssetReferenceServiceImpl implements AssetReferenceService {

    private static final Logger log = LoggerFactory.getLogger(AssetReferenceServiceImpl.class);
    private static final String ROOT_PATH = "/content";

    public static final String TYPE = "type";

    @Reference
    QueryBuilder queryBuilder;

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Override
    public ReferenceResultBean getCFReferences(String assetPath, ResourceResolver resolver) {
        ReferenceResultBean referredPath = new ReferenceResultBean();
        Set<String> cfReference = new HashSet<>();
        Set<String> pageReference = new HashSet<>();
        Set<String> otherReference = new HashSet<>();
            Session session = resolver.adaptTo(Session.class);
            Map<String, String> map = new HashMap<>();
            map.put(PATH, ROOT_PATH);
            map.put(FULLTEXT, assetPath);
            Query query = queryBuilder.createQuery(PredicateGroup.create(map), session);
            SearchResult result = query.getResult();
            Iterator<Resource> it = result.getResources();
            boolean flag = false;
            while (it.hasNext()) {
                Resource resource = it.next();
                String curPath = resource.getPath();
                log.info("Parent resource : {} ", resource.getPath());
                PageManager pageManager = resolver.adaptTo(PageManager.class);
                if (!curPath.equals(assetPath) && resource.adaptTo(ContentFragment.class) != null) {
                    cfReference.add(curPath);
                    flag = true;
                } else if(!curPath.equals(assetPath) && pageManager.getContainingPage(resource) != null) {
                    pageReference.add(pageManager.getContainingPage(resource).getPath());
                    flag = true;
                } else {
                    otherReference.add(curPath);
                    flag = true;
                }
            }
            referredPath.setCfReference(cfReference);
            referredPath.setPageReference(pageReference);
            referredPath.setAssetPath(assetPath);
            referredPath.setOtherReference(otherReference);
            referredPath.setHasReferences(flag);
            return referredPath;
    }
}
