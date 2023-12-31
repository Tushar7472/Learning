package com.training.aem.core.servlets;

import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Find Referred Assets",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/getToolCategory"})
public class GetToolCategoryServlet extends SlingAllMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(GetToolCategoryServlet.class);

    String rootPath = "/apps/cq/core/content/nav/tools";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

        ResourceResolver resolver = request.getResourceResolver();
        List<String> toolCategory = new ArrayList<>();
        Session session = resolver.adaptTo(Session.class);
        try {
            if (session.itemExists(rootPath)) {
                Resource resource = resolver.getResource(rootPath);
                for (Resource childResource : resource.getChildren()) {
                    toolCategory.add(childResource.getName());
                }
            }
            response.setContentType("application/json");
            response.getWriter().write(new Gson().toJson(toolCategory));
        } catch (RepositoryException e) {
            response.getWriter().write(new Gson().toJson(toolCategory));
        }
    }
}
