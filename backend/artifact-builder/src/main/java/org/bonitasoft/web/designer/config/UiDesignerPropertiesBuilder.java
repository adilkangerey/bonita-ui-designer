package org.bonitasoft.web.designer.config;

import org.bonitasoft.web.designer.Version;

import java.nio.file.Path;

public class UiDesignerPropertiesBuilder {

    private boolean experimental = false;

    private final UiDesignerProperties.BonitaProperties bonita = new UiDesignerProperties.BonitaProperties();
    private final WorkspaceProperties workspace = new WorkspaceProperties();
    private final WorkspaceUidProperties workspaceUid = new WorkspaceUidProperties();

    private String widgetsFolderName = "widgets";
    private String fragmentsFolderName = "fragments";
    private String pagesFolderName = "pages";

    public UiDesignerPropertiesBuilder experimental(boolean experimental) {
        this.experimental = experimental;
        return this;
    }

    public UiDesignerPropertiesBuilder portal(String url, String user, String password) {
        this.bonita.getPortal().setUrl(url);
        this.bonita.getPortal().setUser(user);
        this.bonita.getPortal().setPassword(password);
        return this;
    }

    public UiDesignerPropertiesBuilder bdm(String url) {
        this.bonita.getBdm().setUrl(url);
        return this;
    }

    public UiDesignerPropertiesBuilder studioUrl(String url) {
        this.workspace.setApiUrl(url);
        return this;
    }

    public UiDesignerPropertiesBuilder workspaceUidPath(Path path) {
        this.workspaceUid.setPath(path);
        return this;
    }

    public UiDesignerPropertiesBuilder workspacePath(Path path) {
        this.workspace.setPath(path);
        return this;
    }

    public UiDesignerPropertiesBuilder fragmentsFolderName(String fragmentsFolderName) {
        this.fragmentsFolderName = fragmentsFolderName;
        return this;
    }

    public UiDesignerPropertiesBuilder pagesFolderName(String pagesFolderName) {
        this.pagesFolderName = pagesFolderName;
        return this;
    }

    public UiDesignerPropertiesBuilder widgetsFolderName(String widgetsFolderName) {
        this.widgetsFolderName = widgetsFolderName;
        return this;
    }

    public UiDesignerProperties build() {
        var properties = new UiDesignerProperties();

        properties.setVersion(Version.VERSION);
        properties.setEdition(Version.EDITION);
        properties.setModelVersion(Version.MODEL_VERSION);
        properties.setModelVersionLegacy(Version.MODEL_VERSION_LEGACY);

        properties.setExperimental(experimental);
        properties.setBonita(bonita);
        properties.setWorkspaceUid(workspaceUid);
        properties.setWorkspace(workspace);

        Path workspacePath = properties.getWorkspace().getPath();
        if (workspacePath != null) {
            properties.getWorkspace().getWidgets().setDir(workspacePath.resolve(widgetsFolderName));
            properties.getWorkspace().getFragments().setDir(workspacePath.resolve(fragmentsFolderName));
            properties.getWorkspace().getPages().setDir(workspacePath.resolve(pagesFolderName));
        }

        Path workspaceUidPath = properties.getWorkspaceUid().getPath();
        if (workspaceUidPath != null) {
            properties.getWorkspaceUid().setExtractPath(workspaceUidPath.resolve("extract"));
        }

        return properties;
    }

}
