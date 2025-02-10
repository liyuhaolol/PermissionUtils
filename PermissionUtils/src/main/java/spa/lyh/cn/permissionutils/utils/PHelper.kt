package spa.lyh.cn.permissionutils.utils

import spa.lyh.cn.permissionutils.ManifestPro

object PHelper {
    /** 特殊权限列表 */
    private val SPECIAL_PERMISSION_LIST = mutableListOf<String>().apply {
        add(ManifestPro.permission.SCHEDULE_EXACT_ALARM)
        add(ManifestPro.permission.MANAGE_EXTERNAL_STORAGE)
        add(ManifestPro.permission.REQUEST_INSTALL_PACKAGES)
        add(ManifestPro.permission.PICTURE_IN_PICTURE)
        add(ManifestPro.permission.SYSTEM_ALERT_WINDOW)
        add(ManifestPro.permission.WRITE_SETTINGS)
        add(ManifestPro.permission.ACCESS_NOTIFICATION_POLICY)
        add(ManifestPro.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        add(ManifestPro.permission.PACKAGE_USAGE_STATS)
        add(ManifestPro.permission.NOTIFICATION_SERVICE)
        add(ManifestPro.permission.BIND_NOTIFICATION_LISTENER_SERVICE)
        add(ManifestPro.permission.BIND_VPN_SERVICE)
    }

    /** 权限和 Android 版本对应的集合 */
    private val PERMISSION_VERSION_MAP = mutableMapOf<String, Int>().apply {
        put(ManifestPro.permission.SCHEDULE_EXACT_ALARM, AVersion.ANDROID_12)
        put(ManifestPro.permission.MANAGE_EXTERNAL_STORAGE, AVersion.ANDROID_11)
        put(ManifestPro.permission.REQUEST_INSTALL_PACKAGES, AVersion.ANDROID_8)
        put(ManifestPro.permission.PICTURE_IN_PICTURE, AVersion.ANDROID_8)
        put(ManifestPro.permission.SYSTEM_ALERT_WINDOW, AVersion.ANDROID_6)
        put(ManifestPro.permission.WRITE_SETTINGS, AVersion.ANDROID_6)
        put(ManifestPro.permission.ACCESS_NOTIFICATION_POLICY, AVersion.ANDROID_6)
        put(ManifestPro.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, AVersion.ANDROID_6)
        put(ManifestPro.permission.PACKAGE_USAGE_STATS, AVersion.ANDROID_5)
        put(ManifestPro.permission.NOTIFICATION_SERVICE, AVersion.ANDROID_4_4)
        put(ManifestPro.permission.BIND_NOTIFICATION_LISTENER_SERVICE, AVersion.ANDROID_4_3)
        put(ManifestPro.permission.BIND_VPN_SERVICE, AVersion.ANDROID_4_0)

        put(ManifestPro.permission.READ_MEDIA_VISUAL_USER_SELECTED, AVersion.ANDROID_14)
        put(ManifestPro.permission.POST_NOTIFICATIONS, AVersion.ANDROID_13)
        put(ManifestPro.permission.NEARBY_WIFI_DEVICES, AVersion.ANDROID_13)
        put(ManifestPro.permission.BODY_SENSORS_BACKGROUND, AVersion.ANDROID_13)
        put(ManifestPro.permission.READ_MEDIA_IMAGES, AVersion.ANDROID_13)
        put(ManifestPro.permission.READ_MEDIA_VIDEO, AVersion.ANDROID_13)
        put(ManifestPro.permission.READ_MEDIA_AUDIO, AVersion.ANDROID_13)
        put(ManifestPro.permission.BLUETOOTH_SCAN, AVersion.ANDROID_12)
        put(ManifestPro.permission.BLUETOOTH_CONNECT, AVersion.ANDROID_12)
        put(ManifestPro.permission.BLUETOOTH_ADVERTISE, AVersion.ANDROID_12)
        put(ManifestPro.permission.ACCESS_BACKGROUND_LOCATION, AVersion.ANDROID_10)
        put(ManifestPro.permission.ACTIVITY_RECOGNITION, AVersion.ANDROID_10)
        put(ManifestPro.permission.ACCESS_MEDIA_LOCATION, AVersion.ANDROID_10)
        put(ManifestPro.permission.ACCEPT_HANDOVER, AVersion.ANDROID_9)
        put(ManifestPro.permission.ANSWER_PHONE_CALLS, AVersion.ANDROID_8)
        put(ManifestPro.permission.READ_PHONE_NUMBERS, AVersion.ANDROID_8)
        put(ManifestPro.permission.GET_INSTALLED_APPS, AVersion.ANDROID_6)
        put(ManifestPro.permission.READ_EXTERNAL_STORAGE, AVersion.ANDROID_6)
        put(ManifestPro.permission.WRITE_EXTERNAL_STORAGE, AVersion.ANDROID_6)
        put(ManifestPro.permission.CAMERA, AVersion.ANDROID_6)
        put(ManifestPro.permission.RECORD_AUDIO, AVersion.ANDROID_6)
        put(ManifestPro.permission.ACCESS_FINE_LOCATION, AVersion.ANDROID_6)
        put(ManifestPro.permission.ACCESS_COARSE_LOCATION, AVersion.ANDROID_6)
        put(ManifestPro.permission.READ_CONTACTS, AVersion.ANDROID_6)
        put(ManifestPro.permission.WRITE_CONTACTS, AVersion.ANDROID_6)
        put(ManifestPro.permission.GET_ACCOUNTS, AVersion.ANDROID_6)
        put(ManifestPro.permission.READ_CALENDAR, AVersion.ANDROID_6)
        put(ManifestPro.permission.WRITE_CALENDAR, AVersion.ANDROID_6)
        put(ManifestPro.permission.READ_PHONE_STATE, AVersion.ANDROID_6)
        put(ManifestPro.permission.CALL_PHONE, AVersion.ANDROID_6)
        put(ManifestPro.permission.READ_CALL_LOG, AVersion.ANDROID_6)
        put(ManifestPro.permission.WRITE_CALL_LOG, AVersion.ANDROID_6)
        put(ManifestPro.permission.ADD_VOICEMAIL, AVersion.ANDROID_6)
        put(ManifestPro.permission.USE_SIP, AVersion.ANDROID_6)
        put(ManifestPro.permission.PROCESS_OUTGOING_CALLS, AVersion.ANDROID_6)
        put(ManifestPro.permission.BODY_SENSORS, AVersion.ANDROID_6)
        put(ManifestPro.permission.SEND_SMS, AVersion.ANDROID_6)
        put(ManifestPro.permission.RECEIVE_SMS, AVersion.ANDROID_6)
        put(ManifestPro.permission.READ_SMS, AVersion.ANDROID_6)
        put(ManifestPro.permission.RECEIVE_WAP_PUSH, AVersion.ANDROID_6)
        put(ManifestPro.permission.RECEIVE_MMS, AVersion.ANDROID_6)
    }

    /** 框架自己虚拟出来的权限列表（此类权限不需要清单文件中静态注册也能动态申请） */
    private val VIRTUAL_PERMISSION_LIST = mutableListOf<String>().apply {
        add(ManifestPro.permission.NOTIFICATION_SERVICE)
        add(ManifestPro.permission.BIND_NOTIFICATION_LISTENER_SERVICE)
        add(ManifestPro.permission.BIND_VPN_SERVICE)
        add(ManifestPro.permission.PICTURE_IN_PICTURE)
    }

    /**
     * 判断某个权限是否是特殊权限
     */
    fun isSpecialPermission(permission: String): Boolean {
        return SPECIAL_PERMISSION_LIST.contains(permission)
    }

    /**
     * 获取权限是从哪个 Android 版本新增的
     */
    fun findAndroidVersionByPermission(permission: String): Int {
        return PERMISSION_VERSION_MAP[permission] ?: 0
    }

    /**
     * 判断权限是否为框架自己虚拟出来的
     */
    fun isVirtualPermission(permission: String): Boolean {
        return VIRTUAL_PERMISSION_LIST.contains(permission)
    }
}