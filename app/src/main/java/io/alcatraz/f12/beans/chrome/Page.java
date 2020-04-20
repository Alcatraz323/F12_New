package io.alcatraz.f12.beans.chrome;

public class Page {

    private String description;
    private String devtoolsFrontendUrl;
    private String id;
    private String title;
    private String type;
    private String url;
    private String webSocketDebuggerUrl;
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }

    public void setDevtoolsFrontendUrl(String devtoolsFrontendUrl) {
        this.devtoolsFrontendUrl = devtoolsFrontendUrl;
    }
    public String getDevtoolsFrontendUrl() {
        return devtoolsFrontendUrl;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }

    public void setWebSocketDebuggerUrl(String webSocketDebuggerUrl) {
        this.webSocketDebuggerUrl = webSocketDebuggerUrl;
    }
    public String getWebSocketDebuggerUrl() {
        return webSocketDebuggerUrl;
    }

}