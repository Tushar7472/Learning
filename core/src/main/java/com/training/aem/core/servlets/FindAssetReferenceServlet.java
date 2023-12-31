package com.training.aem.core.servlets;

import com.training.aem.core.Beans.ReferenceResultBean;
import com.google.gson.Gson;
import com.training.aem.core.services.AssetReferenceService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Find Referred Assets",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/manageReferences"})
public class FindAssetReferenceServlet extends SlingAllMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(FindAssetReferenceServlet.class);

    private static final String PATH = "assetPath";
    private static final String ROOT_PATH = "rootPath";
    public static final String TYPE = "type";
    @Reference
    transient AssetReferenceService assetReferenceService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        try {
            String assetPath = request.getParameter(PATH);
            response.setContentType("application/json");
            if(StringUtils.isNotEmpty(assetPath)) {
                ReferenceResultBean referredAsset = assetReferenceService.getCFReferences(assetPath, request.getResourceResolver());
                log.info("List of references : {} ", referredAsset);
                response.getWriter().write(new Gson().toJson(referredAsset));
            } else {
                log.error("Asset Path : {} or Root Path :: {} is getting empty", assetPath);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Not able to find the references : {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
        }
    }

    @Override
    protected void doDelete(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        String assetPath = request.getParameter(PATH);
        String destinationPath = request.getParameter("destinationPath");
        String type = request.getParameter(TYPE);
        response.setContentType("application/json");
        try {
            if (type.equals("remove")) {

            } else if (type.equals("replace")) {
                Session session = request.getResourceResolver().adaptTo(Session.class);
                session.move(assetPath, destinationPath);
                session.save();
            }
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
}
