package io.alcatraz.f12.socat;

import io.alcatraz.f12.beans.chrome.ChromeObj;

public class Forward {
    private ChromeObj mBrowser;
    private SocatThread mThread;

    public ChromeObj getBrowser() {
        return mBrowser;
    }

    public void setBrowser(ChromeObj mBrowser) {
        this.mBrowser = mBrowser;
    }

    public SocatThread getThread() {
        return mThread;
    }

    public void setThread(SocatThread mThread) {
        this.mThread = mThread;
    }
}
