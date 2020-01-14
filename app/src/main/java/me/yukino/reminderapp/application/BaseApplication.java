package me.yukino.reminderapp.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.text.format.Formatter;
import android.util.Log;

import com.tencent.mmkv.MMKV;
import com.tencent.mmkv.MMKVHandler;
import com.tencent.mmkv.MMKVLogLevel;
import com.tencent.mmkv.MMKVRecoverStrategic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.hutool.core.date.DateUtil;
import me.yukino.reminderapp.BuildConfig;
import me.yukino.reminderapp.user.LoginActivity;


/**
 * @author Yukino Yukinoshita
 */

public class BaseApplication extends Application implements Thread.UncaughtExceptionHandler {

    private static final String BASE_DIR = "/me.yukino.reminderapp/";

    @Override
    public void onCreate() {
        super.onCreate();

        // 设置未捕获异常的处理器
        Thread.setDefaultUncaughtExceptionHandler(this);

        // 初始化MMKV
        initMMKV();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        saveExceptionStackTrace(getDeviceInfo(), e);

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Process.killProcess(Process.myPid());
    }

    private void saveExceptionStackTrace(String deviceInfo, Throwable e) {
        String fileName = DateUtil.now()
                .replaceAll(" ", "-")
                .concat(".log");
        String dirName = BASE_DIR + "crash/";
        File file = new File(Environment.getExternalStorageDirectory(), dirName + fileName);
        boolean mkdir = file.getParentFile().mkdirs();
        if (!mkdir) {
            Log.e("BaseApplication#saveExceptionStackTrace", "mkdir fail: dirName = " + dirName + ", fileName = " + fileName);
        }
        if (!file.exists()) {
            try {
                boolean createNewFile = file.createNewFile();
                if (!createNewFile) {
                    Log.e("BaseApplication#saveExceptionStackTrace", "create new file fail: dirName = " + dirName + ", fileName = " + fileName);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        String saveStr = deviceInfo.concat("\n堆栈跟踪：\n").concat(Log.getStackTraceString(e));
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(saveStr.getBytes());
            fileOutputStream.flush();
        } catch (IOException ex) {
            e.printStackTrace();
        }
    }

    private String getDeviceInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("手机厂商：").append(Build.MANUFACTURER).append("\n");
        stringBuilder.append("产品名：").append(Build.PRODUCT).append("\n");
        stringBuilder.append("手机品牌：").append(Build.BRAND).append("\n");
        stringBuilder.append("手机型号：").append(Build.MODEL).append("\n");
        stringBuilder.append("手机主板：").append(Build.BOARD).append("\n");
        stringBuilder.append("设备名：").append(Build.DEVICE).append("\n");
        stringBuilder.append("SDK版本：").append(Build.VERSION.SDK_INT).append("\n");
        stringBuilder.append("应用版本：").append(BuildConfig.VERSION_NAME).append("\n");
        stringBuilder.append("应用版本号：").append(BuildConfig.VERSION_CODE).append("\n");
        stringBuilder.append("Android版本：").append(Build.VERSION.RELEASE).append("\n");

        Context context = getApplicationContext();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return stringBuilder.toString();
        }
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        String availMemory = Formatter.formatFileSize(context, memoryInfo.availMem);
        String totalMemory = Formatter.formatFileSize(context, memoryInfo.totalMem);
        stringBuilder.append("RAM： 可用").append(availMemory).append(" / 总共 ").append(totalMemory);
        stringBuilder.append("\n");

        return stringBuilder.toString();
    }

    /**
     * 初始化 MMKV
     */
    private void initMMKV() {
        MMKV.initialize(this);
        MMKV.registerHandler(new MMKVHandler() {
            @Override
            public MMKVRecoverStrategic onMMKVCRCCheckFail(String s) {
                Log.w("MMKV CRC check fail.", "s = " + s);
                // CRC 校验失败时尝试恢复
                return MMKVRecoverStrategic.OnErrorRecover;
            }

            @Override
            public MMKVRecoverStrategic onMMKVFileLengthError(String s) {
                Log.w("MMKV File length error.", "s = " + s);
                // 文件长度错误时尝试恢复
                return MMKVRecoverStrategic.OnErrorRecover;
            }

            @Override
            public boolean wantLogRedirecting() {
                return false;
            }

            @Override
            public void mmkvLog(MMKVLogLevel mmkvLogLevel, String s, int i, String s1, String s2) {

            }
        });
    }

}
