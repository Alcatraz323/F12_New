package io.alcatraz.f12.socat;

import android.annotation.SuppressLint;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

import io.alcatraz.f12.AsyncInterface;
import io.alcatraz.f12.LogBuff;
import io.alcatraz.f12.beans.chrome.ChromeObj;
import io.alcatraz.f12.beans.chrome.InspectorInfo;
import io.alcatraz.f12.utils.ShellUtils;
import io.alcatraz.f12.utils.Utils;

public class SocatThread extends Thread {
    private int mListenPort;
    private String mTragetAbstract;
    private String mSocatCommand = "exec %s/socat -d -d TCP4-LISTEN:%d,reuseaddr,fork ABSTRACT-CONNECT:%s | grep 'refused|denied'";
    private ThreadInterface mInterface;
    private DataOutputStream dos;
    private String mSocatPath;

    private boolean mAlive = true;
    private Process mSocatProcess;
    private WeakReference<ForwardingManager> mManager;

    SocatThread(ForwardingManager manager, String targetAbs, String socatpath, ThreadInterface threadInterface) {
        mManager = new WeakReference<>(manager);
        mTragetAbstract = targetAbs;
        mInterface = threadInterface;
        mSocatPath = socatpath;
    }

    @Override
    public void run() {
        super.run();
        if (requirementCheck()) {
            Looper.prepare();
            prepareParameters();
            try {
                mSocatProcess = Runtime.getRuntime().exec("su");
                dos = new DataOutputStream(mSocatProcess.getOutputStream());
                dos.writeBytes(mSocatCommand + "\n");
                dos.flush();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mSocatProcess.getInputStream()));
                String line;

                sleep(300);
                connectForVersion(mListenPort);

                while ((line = bufferedReader.readLine()) != null && mAlive) {
                    if (line.contains("refused")) {
                        LogBuff.E("Target socket connection refused. Check whether the target browser's remote debugging is enabled or can be accessed by outside, or you may restarted Chrome, and it's not ready");
                        mInterface.onConnectionRefused();
                    } else if (line.contains("denied")) {
                        LogBuff.E("Permission denied to connect the socket, check your root and SeLinux status,");
                        mAlive = false;
                        mInterface.onSocatPermissionDenied();
                    } else {
                        mAlive = false;
                        LogBuff.E("Unknown Exception:" + line);
                    }

                }

                dos.close();
                kill();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException ignored) {

            }
        }
        LogBuff.E("Requirement not meet!");
        kill();
    }

    public void kill() {
        kill(true);
    }

    public void kill(boolean printLog) {
        if (printLog)
            LogBuff.I("killing Thread for tcp:" + mListenPort + " abstract:" + mTragetAbstract);
        mAlive = false;
        mSocatProcess.destroy();
        if (printLog)
            LogBuff.I("Sub process destroyed");
        mManager.get().removeFromThread(mTragetAbstract);
        if (printLog)
            LogBuff.I("Removed from forward map");
    }

    @Override
    protected void finalize() throws Throwable {
        kill(false);
        super.finalize();
    }

    public void connectForVersion(final int port, final ChromeInfoUpdateInterface infoUpdateInterface) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder content = new StringBuilder();
                String line;
                try {
                    @SuppressLint("DefaultLocale") String url_str = String.format("http://localhost:%d/json/version", port);
                    URL url = new URL(url_str);
                    URLConnection conn = url.openConnection();
                    conn.setDoInput(true);
                    conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Accept-Language", "zh-CN,en-US;q=0.8");
                    conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
                    conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
                    InputStreamReader is = new InputStreamReader(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(is);
                    while ((line = reader.readLine()) != null) {
                        content.append("\n");
                        content.append(line);
                    }
                    conn.connect();
                    try {
                        ChromeObj chromeObj =
                                Utils.json2Object(content.toString().replaceAll("-", ""), ChromeObj.class);
                        LogBuff.I("Got target Abs info, package=" + chromeObj.getAndroidPackage() +
                                " devtools_protocol=" + chromeObj.getProtocolVersion());
                        infoUpdateInterface.onReceive(chromeObj);
                    } catch (Exception e) {
                        infoUpdateInterface.onError("Convert error");
                        LogBuff.E("Error when try convert : " + content.toString());
                    }
                } catch (Exception e) {
                    infoUpdateInterface.onError(e.getMessage());
                    LogBuff.E(e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void connectForPages(final AsyncInterface<InspectorInfo> asyncInterface) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder content = new StringBuilder();
                String line;
                try {
                    @SuppressLint("DefaultLocale") String url_str = String.format("http://localhost:%d/json/list", mListenPort);
                    URL url = new URL(url_str);
                    URLConnection conn = url.openConnection();
                    conn.setDoInput(true);
                    conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Accept-Language", "zh-CN,en-US;q=0.8");
                    conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
                    conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
                    InputStreamReader is = new InputStreamReader(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(is);
                    while ((line = reader.readLine()) != null) {
                        content.append("\n");
                        content.append(line);
                    }
                    conn.connect();
                    try {
                        InspectorInfo inspectorInfo =
                                Utils.json2Object("{\"page\":" + content.toString() + "}", InspectorInfo.class);
                        LogBuff.I("Got target Abs info, size=" + inspectorInfo.getPage().size());
                        asyncInterface.onDone(inspectorInfo);
                    } catch (Exception e) {
                        asyncInterface.onFailure("Convert error");
                        LogBuff.E("Error when try convert : " + content.toString());
                    }
                } catch (Exception e) {
                    asyncInterface.onFailure(e.getMessage());
                    LogBuff.E(e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void prepareParameters() {
        mListenPort = mManager.get().getNextAvailPort();
        mSocatCommand = String.format(mSocatCommand, mSocatPath, mListenPort, mTragetAbstract);
    }

    private boolean requirementCheck() {
        if (mInterface != null) {
            if (ShellUtils.hasRootPermission()) {
                return true;
            } else {
                return mInterface.onRootFailure();
            }
        } else {
            return false;
        }
    }

    private void connectForVersion(final int port) {
        connectForVersion(port, new ChromeInfoUpdateInterface() {
            @Override
            public void onReceive(ChromeObj chromeObj) {
                mInterface.onRetrieveVersionInfo(SocatThread.this, chromeObj);
                mInterface.onAddProcessComplete();
            }

            @Override
            public void onError(String why) {
                mInterface.onAddProcessComplete();
            }
        });
    }

    public int getListenPort() {
        return mListenPort;
    }

    public String getTragetAbstract() {
        return mTragetAbstract;
    }

    public String getSocatCommand() {
        return mSocatCommand;
    }

    public boolean isLoopAlive() {
        return mAlive;
    }

    public Process getSocatProcess() {
        return mSocatProcess;
    }

    public interface ThreadInterface {
        boolean onRootFailure();

        void onRetrieveVersionInfo(SocatThread thread, ChromeObj chromeObj);

        void onSocatPermissionDenied();

        void onConnectionRefused();

        void onAddProcessComplete();
    }

    public interface ChromeInfoUpdateInterface {
        void onReceive(ChromeObj chromeObj);

        void onError(String why);
    }
}
