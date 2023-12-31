package com.training.aem.core.services;

import com.google.gson.JsonObject;
import org.apache.sling.api.resource.ResourceResolver;

public interface CreateToolService {

    String FORWARD_SLASH = "/";

    String PAGE_PATH = "pagePath";

    String MESSAGE = "message";

    String SUCCESS = "success";

    String SLING_RESOURCE_TYPE = "sling:resourceType";

    String CATEGORIES = "categories";

    String CQ_COMPONENT = "cq:Component";

    String CQ_CLIENT_LIBRARY_FOLDER = "cq:ClientLibraryFolder";

    String NT_FILE = "nt:file";

    String NT_RESOURCE = "nt:resource";

    String ICON = "icon";

    String ID = "id";

    String TOOL = "-tool";

    String JCR_DESCRIPTION = "jcr:description";

    String HREF = "href";

    String HTML = ".html";

    String ALLOW_PROXY = "allowProxy";

    String JCR_CONTENT = "/jcr:content";

    String CLIENTLIBS = "/clientlibs";

    String CONTENT = "/content";

    String TITLE = "/title";
    String JCR_TITLE = "jcr:title";
    String PATH_TO_LIBS = "/apps/cq/core/content/nav/tools/";

    JsonObject createCustomTool(String toolName, String categoryName, String iconName, String description,
                                String pagePath, String componentName, String componentPath, ResourceResolver resolver);
}
