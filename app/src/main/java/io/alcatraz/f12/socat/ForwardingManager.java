package io.alcatraz.f12.socat;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.alcatraz.f12.LogBuff;
import io.alcatraz.f12.R;
import io.alcatraz.f12.beans.chrome.ChromeObj;
import io.alcatraz.f12.extended.CompatWithPipeActivity;
import io.alcatraz.f12.utils.ShellUtils;
import io.alcatraz.f12.utils.Utils;

public class ForwardingManager {
    private Map<String, Forward> mForwardingThreads = new HashMap<>();
    private int mLastReqPort = 21234;
    private Context mContext;
    private CompatWithPipeActivity mActivity;
    private int progress = 0;

    public ForwardingManager(Context context) {
        ShellUtils.execCommand("killall socat", true);
        mContext = context;
        File socat = getSocatPath(context);
        if (!socat.exists()) {
            Utils.copyAssetsFile(context, "socat", context.getFilesDir().getAbsolutePath());
            ShellUtils.execCommand("chmod 0755 " + socat.getAbsolutePath(), true);
        }
    }

    public static File getSocatPath(Context context) {
        File root = context.getFilesDir();
        return new File(root.getAbsolutePath() + "/socat");
    }

    public int getForwardingCount() {
        return mForwardingThreads.size();
    }

    public synchronized void scanAbsBrowsers(ScanInterface scanInterface, boolean fork) {
        if (fork)
            new Thread(new ScanTask(scanInterface)).start();
        else new ScanTask(scanInterface).run();
    }

    public List<Forward> getChromeList() {
        List<Forward> out = new ArrayList<>(mForwardingThreads.values());
        return out;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    protected synchronized void removeFromThread(String abs_target) {
        if (mForwardingThreads.containsKey(abs_target))
            mForwardingThreads.remove(mForwardingThreads.get(abs_target));
    }

    public synchronized int getNextAvailPort() {
        int currentPort = mLastReqPort++;
        if (checkPortIsAvailable(currentPort)) {
            return currentPort;
        } else {
            return getNextAvailPort();
        }
    }

    private boolean checkPortIsAvailable(int port) {
        LogBuff.I("Checking port(" + port + ")'s availability");
        @SuppressLint("DefaultLocale") ShellUtils.CommandResult result =
                ShellUtils.execCommand(String.format("netstat -tn | grep \":%d\"", port), true);
        if (result.responseMsg.contains(port + "")) {
            LogBuff.I("Port occupied, trying next");
            return false;
        } else {
            LogBuff.I("Port(" + port + ") is available");
            return true;
        }
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public void setActivity(CompatWithPipeActivity mActivity) {
        this.mActivity = mActivity;
    }

    public interface ScanInterface {
        void callProgress(int current, int total);

        void onDone();
    }

    class ScanTask implements Runnable {
        private ScanInterface scanInterface;

        ScanTask(ScanInterface scanInterface) {
            this.scanInterface = scanInterface;
        }

        @Override
        public void run() {
            LogBuff.addDivider();
            LogBuff.I("Start active chrome scan");
            ShellUtils.CommandResult scan_raw = ShellUtils.execCommand("cat /proc/net/unix | grep --text devtools_remote", true);
            if(scan_raw.responseMsg.contains("Usage")){
                scan_raw = ShellUtils.execCommand("cat /proc/net/unix | grep devtools_remote", true);
            }
            if (scan_raw.responseMsg.contains("devtools_remote")) {
                final String[] process_0 = scan_raw.responseMsg.split("\n");

                for (String i : process_0) {
                    if (i.contains("@")) {
                        final String current_abs = i.split("@")[1];

                        //This for scope is for updating existed Threads instead of re-create it
                        if (mForwardingThreads.containsKey(current_abs)) {
                            LogBuff.I("Abs for : " + current_abs + "existed, try update chrome info");
                            final Forward forward = mForwardingThreads.get(current_abs);
                            @SuppressWarnings("ConstantConditions") SocatThread thread = forward.getThread();
                            thread.connectForVersion(thread.getListenPort(), new SocatThread.ChromeInfoUpdateInterface() {
                                @Override
                                public void onReceive(ChromeObj chromeObj) {
                                    forward.setBrowser(chromeObj);
                                    LogBuff.I("Chrome info updated");
                                    scanInterface.callProgress(++progress, process_0.length);
                                    if (progress == process_0.length) {
                                        scanInterface.onDone();
                                        LogBuff.I("Scan task complete");
                                        LogBuff.addDivider();
                                    }
                                }

                                @Override
                                public void onError(String why) {
                                    scanInterface.callProgress(++progress, process_0.length);
                                    if (progress == process_0.length) {
                                        scanInterface.onDone();
                                        LogBuff.I("Scan task complete");
                                        LogBuff.addDivider();
                                    }
                                }
                            });
                            continue;
                        }

                        //Create new thread
                        LogBuff.I("Abs : " + current_abs + " not existed, creating");
                        new SocatThread(ForwardingManager.this, current_abs, getSocatPath(mContext).getParentFile().getAbsolutePath(), new SocatThread.ThreadInterface() {
                            @Override
                            public boolean onRootFailure() {
                                return false;
                            }

                            @Override
                            public void onRetrieveVersionInfo(SocatThread thread, ChromeObj chromeObj) {
                                Forward forward = new Forward();
                                forward.setThread(thread);
                                forward.setBrowser(chromeObj);
                                mForwardingThreads.put(current_abs, forward);
                                LogBuff.I("Thread added(" + thread.getTragetAbstract() + ")");
                            }

                            @Override
                            public void onSocatPermissionDenied() {
                                if (mActivity != null) {
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mActivity.toast(R.string.toast_permission_denied);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onConnectionRefused() {
                                if (mActivity != null) {
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mActivity.toast(R.string.toast_connection_refused);
                                        }
                                    });
                                    LogBuff.I("Scan task complete");
                                    LogBuff.addDivider();
                                }
                            }

                            @Override
                            public void onAddProcessComplete() {
                                scanInterface.callProgress(++progress, process_0.length);
                                if (progress == process_0.length) {
                                    scanInterface.onDone();
                                    LogBuff.I("Scan task complete");
                                    LogBuff.addDivider();
                                }
                            }
                        }).start();
                    }
                }
            }
            LogBuff.I("Scan task all registered");
            scanInterface.onDone();
        }
    }
}
