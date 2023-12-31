package com.training.aem.core.services.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.JsonObject;
import com.training.aem.core.services.CreateToolService;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

import javax.jcr.*;


@Component(service = CreateToolService.class, immediate = true)
public class CreateToolServiceImpl implements CreateToolService {

    public static final String NT_FOLDER = "nt:folder";

    @Override
    public JsonObject createCustomTool(String toolName, String categoryName, String iconName, String description,
                                       String pagePath, String componentName, String componentPath, ResourceResolver resolver) {
        JsonObject jsonObject = new JsonObject();
        try {
            categoryName = JcrUtil.createValidName(categoryName, JcrUtil.HYPHEN_LABEL_CHAR_MAPPING, "_");
            toolName = JcrUtil.createValidName(toolName, JcrUtil.HYPHEN_LABEL_CHAR_MAPPING, "_");
            componentName = JcrUtil.createValidName(componentName, JcrUtil.HYPHEN_LABEL_CHAR_MAPPING, "_");
            Session session = resolver.adaptTo(Session.class);
            overlayCQAndCreateSchema(session, categoryName);
            Page page = createPage(categoryName, resolver, session, toolName, pagePath);
            Node componentNode = createComponent(componentName, componentPath, session, toolName);
            createContentNode(page.getContentResource(), session, componentNode.getPath());
            createToolNode(categoryName, toolName, description, page.getPath(), iconName, session);
            jsonObject.addProperty(PAGE_PATH, page.getPath());
            jsonObject.addProperty(MESSAGE, "Tool Created Successfully");
            jsonObject.addProperty(SUCCESS, true);
        } catch (Exception e) {
            jsonObject.addProperty(MESSAGE, e.getMessage());
            jsonObject.addProperty(SUCCESS, false);
        }
        return jsonObject;
    }

    private void overlayCQAndCreateSchema(Session session, String categoryName) {
        try {
            Node node = JcrUtils.getOrCreateByPath(PATH_TO_LIBS, JcrConstants.NT_UNSTRUCTURED, JcrConstants.NT_UNSTRUCTURED, session, true);
            Node category = JcrUtils.getOrCreateByPath(node.getPath() + FORWARD_SLASH + categoryName, JcrConstants.NT_UNSTRUCTURED, JcrConstants.NT_UNSTRUCTURED, session, false);
            category.setProperty(JCR_TITLE, categoryName);
            session.save();
        }  catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    private Page createPage(String categoryName, ResourceResolver resolver, Session session, String toolName, String pagePath) {
       try {
           Node pageFolderNode = JcrUtils.getOrCreateByPath(pagePath.concat(FORWARD_SLASH + categoryName), JcrConstants.NT_FOLDER, JcrConstants.NT_FOLDER, session, true);
           PageManager pm = resolver.adaptTo(PageManager.class);
           boolean isAlreadyExists = session.itemExists(pageFolderNode.getPath() + FORWARD_SLASH + toolName);
           if(!isAlreadyExists) {
               Page page = pm.create(pageFolderNode.getPath(), toolName, "granite/ui/components/shell/page", toolName, true);
               Resource resource = page.getContentResource();
               Node jcrPageNode = resource.adaptTo(Node.class);
               jcrPageNode.setProperty(SLING_RESOURCE_TYPE, "granite/ui/components/shell/page");
               session.save();
               createHeadNode(resource,session, toolName);
               createTitleNode(resource,session, toolName);
               return page;
           } else {
               throw new ItemExistsException("Page is Already Exists");
           }
       } catch (Exception e) {
           throw new RuntimeException(e.getMessage());
       }
    }

    private void createHeadNode(Resource jcrResource, Session session, String toolName){
        try {
            Node headClientNode = JcrUtils.getOrCreateByPath(jcrResource.getPath().concat("/head/clientlibs"), JcrConstants.NT_UNSTRUCTURED, JcrConstants.NT_UNSTRUCTURED, session, true);
            headClientNode.setProperty(SLING_RESOURCE_TYPE, "granite/ui/components/coral/foundation/includeclientlibs");
            headClientNode.setProperty(CATEGORIES, new String[]{toolName});
            session.save();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTitleNode(Resource jcrResource, Session session, String toolName) {
        try {
            Node titleNode = JcrUtils.getOrCreateByPath(jcrResource.getPath().concat(TITLE), JcrConstants.NT_UNSTRUCTURED, JcrConstants.NT_UNSTRUCTURED, session, true);
            titleNode.setProperty(SLING_RESOURCE_TYPE, "granite/ui/components/shell/title");
            titleNode.setProperty(JCR_TITLE, toolName);
            session.save();
        }  catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    private void createContentNode(Resource jcrResource, Session session, String componentPath) {
        try {
            Node contentNode = JcrUtils.getOrCreateByPath(jcrResource.getPath().concat(CONTENT), JcrConstants.NT_UNSTRUCTURED, JcrConstants.NT_UNSTRUCTURED, session, true);
            contentNode.setProperty(SLING_RESOURCE_TYPE, componentPath);
            session.save();
        }  catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    private Node createComponent(String componentName, String componentPath, Session session, String toolName) {
        try {
            if(!session.itemExists(componentPath  + "/" + componentName)) {
                Node componentNode = JcrUtils.getOrCreateByPath(componentPath.concat(FORWARD_SLASH + componentName), JcrConstants.NT_FOLDER, CQ_COMPONENT, session, true);
                Node componentClientLibs = JcrUtils.getOrCreateByPath(componentNode.getPath() + CLIENTLIBS, CQ_COMPONENT, CQ_CLIENT_LIBRARY_FOLDER, session, true);
                componentClientLibs.setProperty(CATEGORIES, new String[]{toolName});
                componentClientLibs.setProperty(ALLOW_PROXY, true);

                createJsClientLibsFile(componentClientLibs, session);

                Node htmlFileNode = JcrUtils.getOrCreateByPath(componentNode.getPath() + FORWARD_SLASH + componentName + HTML, CQ_COMPONENT, NT_FILE, session, false);
                Node jcrNode = JcrUtils.getOrCreateByPath(htmlFileNode.getPath().concat(JCR_CONTENT), NT_FILE, NT_RESOURCE, session, false);
                jcrNode.setProperty(JcrConstants.JCR_MIMETYPE, "text/html");
                String htmlContent = "<div class=\"intial-content\">\n" +
                        "    <h1>Welcome to your custom tool which is developed using Custom Tool </h1>" +
                        "</div>";
                jcrNode.setProperty(JcrConstants.JCR_DATA, htmlContent);
                session.save();
                return componentNode;
            } else {
                throw new ItemExistsException("Component Is Already Exists");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void createJsClientLibsFile(Node componentClientLibs, Session session) {
        try {
            Node jsFileNode = JcrUtils.getOrCreateByPath(componentClientLibs.getPath() + FORWARD_SLASH + "js.txt", CQ_CLIENT_LIBRARY_FOLDER, NT_FILE, session, false);
            Node jcrNode = JcrUtils.getOrCreateByPath(jsFileNode.getPath().concat(JCR_CONTENT), NT_FILE, NT_RESOURCE, session, false);
            jcrNode.setProperty(JcrConstants.JCR_MIMETYPE, "text/plain");
            String data = "#base=js\n" +
                    "script.js";
            jcrNode.setProperty(JcrConstants.JCR_DATA, data);
            session.save();

            Node jsFolderNode = JcrUtils.getOrCreateByPath(componentClientLibs.getPath() + FORWARD_SLASH + "js", CQ_CLIENT_LIBRARY_FOLDER, NT_FOLDER, session, true);
            Node scriptFileNode = JcrUtils.getOrCreateByPath(jsFolderNode.getPath() + FORWARD_SLASH + "script.js", NT_FOLDER, NT_FILE,session,false);
            Node jsJcrNode = JcrUtils.getOrCreateByPath(scriptFileNode.getPath().concat(JCR_CONTENT), NT_FILE, NT_RESOURCE, session, false);
            jsJcrNode.setProperty(JcrConstants.JCR_MIMETYPE, "application/javascript");
            String jsData = "console.log('Js is called')";
            jsJcrNode.setProperty(JcrConstants.JCR_DATA, jsData);
            session.save();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    private void createToolNode(String categoryName, String toolName, String description, String pagePath, String iconName, Session session) {
        try {
            Node toolNode = JcrUtils.getOrCreateByPath(PATH_TO_LIBS.concat(categoryName).concat(FORWARD_SLASH + toolName), JcrConstants.NT_UNSTRUCTURED, JcrConstants.NT_UNSTRUCTURED, session, true);
            toolNode.setProperty(ICON, iconName);
            toolNode.setProperty(JCR_TITLE, toolName);
            toolNode.setProperty(ID, toolName.concat(TOOL));
            toolNode.setProperty(JCR_DESCRIPTION, description);
            toolNode.setProperty(HREF, pagePath.concat(HTML));
            session.save();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
}
