package io.alcatraz.f12.beans.chrome;

public class ChromeObj {

    private String AndroidPackage;
    private String Browser;
    private String ProtocolVersion;
    private String UserAgent;
    private String V8Version;
    private String WebKitVersion;
    private String webSocketDebuggerUrl;
    public void setAndroidPackage(String AndroidPackage) {
        this.AndroidPackage = AndroidPackage;
    }
    public String getAndroidPackage() {
        return AndroidPackage;
    }

    public void setBrowser(String Browser) {
        this.Browser = Browser;
    }
    public String getBrowser() {
        return Browser;
    }

    public void setProtocolVersion(String ProtocolVersion) {
        this.ProtocolVersion = ProtocolVersion;
    }
    public String getProtocolVersion() {
        return ProtocolVersion;
    }

    public void setUserAgent(String UserAgent) {
        this.UserAgent = UserAgent;
    }
    public String getUserAgent() {
        return UserAgent;
    }

    public void setV8Version(String V8Version) {
        this.V8Version = V8Version;
    }
    public String getV8Version() {
        return V8Version;
    }

    public void setWebKitVersion(String WebKitVersion) {
        this.WebKitVersion = WebKitVersion;
    }
    public String getWebKitVersion() {
        return WebKitVersion;
    }

    public void setWebSocketDebuggerUrl(String webSocketDebuggerUrl) {
        this.webSocketDebuggerUrl = webSocketDebuggerUrl;
    }
    public String getWebSocketDebuggerUrl() {
        return webSocketDebuggerUrl;
    }

}