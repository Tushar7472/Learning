package com.training.aem.core.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.training.aem.core.services.CreateToolService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;


@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Create Custom Tool",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/createTool"})
public class ToolCreationServlet extends SlingSafeMethodsServlet {

    @Reference
    CreateToolService createToolService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        ResourceResolver resolver = request.getResourceResolver();
        String toolName = request.getParameter("toolName");
        String categoryName = request.getParameter("categoryName");
        String iconName = request.getParameter("iconName");
        String description = request.getParameter("description");
        String pagePath = request.getParameter("pagePath");
        String componentPath = request.getParameter("componentPath");
        String componentName = request.getParameter("componentName");
        JsonObject jsonObject = createToolService.createCustomTool(toolName, categoryName,iconName,description,pagePath,componentName,componentPath,resolver);
        Boolean isSuccess = jsonObject.get("success").getAsBoolean();
        if(!isSuccess) {
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
        } else {
            response.setStatus(SlingHttpServletResponse.SC_OK);
        }
        response.getWriter().write(new Gson().toJson(jsonObject));
    }
}
