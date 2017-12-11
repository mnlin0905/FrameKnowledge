package com.knowledge.mnlin.frame.util;

/**
 * Created by Administrator on 17-1-6.
 */
public class Const {
    /**
     * 闹铃服务发送的广播
     * pendingIntent对应的requestCode
     */
    public static final String ALARM_BROADCAST = "com.mnlin.hotchpotch.alarm";
    public static final int ALARM_REQUESTCODE = 1001;

    /**
     * 显示toast
     */
    public static final int SHOW_TOAST = 2001;
    public static final int SHOW_PROGRESS = SHOW_TOAST + 1;
    public static final int HIDDEN_PROGRESS = SHOW_TOAST + 2;
    public static final int REQUEST_OPEN_BLUETOOTH = SHOW_TOAST + 3;
    public static final int INIT_FLV_SCAN_HAVEBEMATCHEDDEVICE = SHOW_TOAST + 4;

    /**
     * preference存储信息的字段
     */
    public static final String PREFERENCE_APP_THEME="app_theme";

}
