package spa.lyh.cn.permissionutils

import android.os.Build
import androidx.annotation.RequiresApi

object ManifestPro {
    object permission{
        /** 闹钟权限（特殊权限，Android 12 新增的权限）*/
        const val SCHEDULE_EXACT_ALARM = "android.permission.SCHEDULE_EXACT_ALARM"
        /**
         * 文件管理权限（特殊权限，Android 11 新增的权限）
         *
         * 如果你的应用需要上架 GooglePlay，那么需要详细阅读谷歌应用商店的政策：
         * https://support.google.com/googleplay/android-developer/answer/9956427
         */
        const val MANAGE_EXTERNAL_STORAGE = "android.permission.MANAGE_EXTERNAL_STORAGE"
        /**
         * 安装应用权限（特殊权限，Android 8.0 新增的权限）
         *
         * Android 11 特性调整，安装外部来源应用需要重启 App：https://cloud.tencent.com/developer/news/637591
         * 经过实践，Android 12 已经修复了此问题，授权或者取消授权后应用并不会重启
         */
        const val REQUEST_INSTALL_PACKAGES = "android.permission.REQUEST_INSTALL_PACKAGES"
        /**
         * 画中画权限（特殊权限，Android 8.0 新增的权限，注意此权限不需要在清单文件中注册也能申请）
         *
         * 需要注意的是：这个权限和其他特殊权限不同的是，默认已经是授予状态，用户也可以手动撤销授权
         */
        const val PICTURE_IN_PICTURE = "android.permission.PICTURE_IN_PICTURE"
        /**
         * 悬浮窗权限（特殊权限，Android 6.0 新增的权限）
         *
         * 在 Android 10 及之前的版本能跳转到应用悬浮窗设置页面，而在 Android 11 及之后的版本只能跳转到系统设置悬浮窗管理列表了
         * 官方解释：https://developer.android.google.cn/reference/android/provider/Settings#ACTION_MANAGE_OVERLAY_PERMISSION
         */
        const val SYSTEM_ALERT_WINDOW = "android.permission.SYSTEM_ALERT_WINDOW"
        /** 系统设置权限（特殊权限，Android 6.0 新增的权限） */
        const val WRITE_SETTINGS = "android.permission.WRITE_SETTINGS"
        /** 勿扰权限，可控制手机响铃模式【静音，震动】（特殊权限，Android 6.0 新增的权限）*/
        const val ACCESS_NOTIFICATION_POLICY = "android.permission.ACCESS_NOTIFICATION_POLICY"
        /** 请求忽略电池优化选项权限（特殊权限，Android 6.0 新增的权限）*/
        const val REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"
        /** 查看应用使用情况权限，简称使用统计权限（特殊权限，Android 5.0 新增的权限） */
        const val PACKAGE_USAGE_STATS = "android.permission.PACKAGE_USAGE_STATS"
        /** 通知栏权限（特殊权限，只有 Android 4.4 及以上设备才能判断到权限状态，注意此权限不需要在清单文件中注册也能申请） */
        const val NOTIFICATION_SERVICE = "android.permission.NOTIFICATION_SERVICE"
        /** 通知栏监听权限（特殊权限，Android 4.3 新增的权限，注意此权限不需要在清单文件中注册也能申请） */
        const val BIND_NOTIFICATION_LISTENER_SERVICE = "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE"
        /** VPN 权限（特殊权限，Android 4.0 新增的权限，注意此权限不需要在清单文件中注册也能申请） */
        const val BIND_VPN_SERVICE = "android.permission.BIND_VPN_SERVICE"

        //pt
        /**
         * WIFI 权限（Android 13.0 新增的权限）
         *
         * 需要在清单文件中加入 android:usesPermissionFlags="neverForLocation" 属性（表示不推导设备地理位置）
         * 否则就会导致在没有定位权限的情况下扫描不到附近的 WIFI 设备，这个是经过测试的，下面是清单权限注册案例，请参考以下进行注册
         * <uses-permission android:name="android.permission.NEARBY_WIFI_DEVICES" android:usesPermissionFlags="neverForLocation" tools:targetApi="s" />
         *
         * 为了兼容 Android 13 以下版本，需要清单文件中注册 {@link #ACCESS_FINE_LOCATION} 权限
         * 还有 Android 13 以下设备，使用 WIFI 需要 {@link #ACCESS_FINE_LOCATION} 权限，框架会自动在旧的安卓设备上自动添加此权限进行动态申请
         */
        const val NEARBY_WIFI_DEVICES = "android.permission.NEARBY_WIFI_DEVICES"
        /**
         * 后台传感器权限（Android 13.0 新增的权限）
         *
         * 需要注意的是：
         * 1. 一旦你申请了该权限，在授权的时候，需要选择《始终允许》，而不能选择《仅在使用中允许》
         * 2. 如果你的 App 只在前台状态下使用传感器功能，请不要申请该权限（后台传感器权限）
         */
        const val BODY_SENSORS_BACKGROUND = "android.permission.BODY_SENSORS_BACKGROUND"
        /**
         * 蓝牙扫描权限（Android 12.0 新增的权限）
         *
         * 需要在清单文件中加入 android:usesPermissionFlags="neverForLocation" 属性（表示不推导设备地理位置）
         * 否则就会导致在没有定位权限的情况下扫描不到附近的蓝牙设备，这个是经过测试的，下面是清单权限注册案例，请参考以下进行注册
         * <uses-permission android:name="android.permission.BLUETOOTH_SCAN" android:usesPermissionFlags="neverForLocation" tools:targetApi="s" />
         *
         * 为了兼容 Android 12 以下版本，需要清单文件中注册 {@link Manifest.permission#BLUETOOTH_ADMIN} 权限
         * 还有 Android 12 以下设备，获取蓝牙扫描结果需要 {@link #ACCESS_FINE_LOCATION} 权限，框架会自动在旧的安卓设备上自动添加此权限进行动态申请
         */
        const val BLUETOOTH_SCAN = "android.permission.BLUETOOTH_SCAN"
        /**
         * 蓝牙连接权限（Android 12.0 新增的权限）
         *
         * 为了兼容 Android 12 以下版本，需要在清单文件中注册 {@link Manifest.permission#BLUETOOTH} 权限
         */
        const val BLUETOOTH_CONNECT = "android.permission.BLUETOOTH_CONNECT"
        /**
         * 蓝牙广播权限（Android 12.0 新增的权限）
         *
         * 将当前设备的蓝牙进行广播，供其他设备扫描时需要用到该权限
         * 为了兼容 Android 12 以下版本，需要在清单文件中注册 {@link Manifest.permission#BLUETOOTH_ADMIN} 权限
         */
        const val BLUETOOTH_ADVERTISE = "android.permission.BLUETOOTH_ADVERTISE"
        /**
         * 在后台获取位置（Android 10.0 新增的权限）
         *
         * 需要注意的是：
         * 1. 一旦你申请了该权限，在授权的时候，需要选择《始终允许》，而不能选择《仅在使用中允许》
         * 2. 如果你的 App 只在前台状态下使用定位功能，没有在后台使用的场景，请不要申请该权限
         */
        const val ACCESS_BACKGROUND_LOCATION = "android.permission.ACCESS_BACKGROUND_LOCATION"
        /**
         * 获取活动步数（Android 10.0 新增的权限）
         *
         * 需要注意的是：Android 10 以下不需要传感器（BODY_SENSORS）权限也能获取到步数
         */
        const val ACTIVITY_RECOGNITION = "android.permission.ACTIVITY_RECOGNITION"
        /**
         * 读取照片中的地理位置（Android 10.0 新增的权限）
         *
         * 需要注意的是：如果这个权限申请成功了但是不能正常读取照片的地理信息，那么需要先申请存储权限，具体可分别下面两种情况：
         *
         * 1. 如果适配了分区存储的情况下：
         *     1) 如果项目 targetSdkVersion <= 32 需要申请 {@link Permission#READ_EXTERNAL_STORAGE}
         *     2) 如果项目 targetSdkVersion >= 33 需要申请 {@link Permission#READ_MEDIA_IMAGES}
         *
         * 2. 如果没有适配分区存储的情况下：
         *     1) 如果项目 targetSdkVersion <= 29 需要申请 {@link Permission#READ_EXTERNAL_STORAGE}
         *     2) 如果项目 targetSdkVersion >= 30 需要申请 {@link Permission#MANAGE_EXTERNAL_STORAGE}
         */
        const val ACCESS_MEDIA_LOCATION = "android.permission.ACCESS_MEDIA_LOCATION"
        /** 允许呼叫应用继续在另一个应用中启动的呼叫（Android 9.0 新增的权限） */
        const val ACCEPT_HANDOVER = "android.permission.ACCEPT_HANDOVER"
        /**
         * 接听电话（Android 8.0 新增的权限，Android 8.0 以下可以采用模拟耳机按键事件来实现接听电话，这种方式不需要权限）
         */
        const val ANSWER_PHONE_CALLS = "android.permission.ANSWER_PHONE_CALLS"
        /**
         * 读取手机号码（Android 8.0 新增的权限）
         *
         * 为了兼容 Android 8.0 以下版本，需要在清单文件中注册 {@link #READ_PHONE_STATE} 权限
         */
        const val READ_PHONE_NUMBERS = "android.permission.READ_PHONE_NUMBERS"
        /**
         * 读取应用列表权限（危险权限，电信终端产业协会联合各大中国手机厂商搞的一个权限）
         *
         * Github issue 地址：https://github.com/getActivity/XXPermissions/issues/175
         * 移动终端应用软件列表权限实施指南：http://www.taf.org.cn/StdDetail.aspx?uid=3A7D6656-43B8-4C46-8871-E379A3EA1D48&stdType=TAF
         *
         * 需要注意的是：
         *   1. 需要在清单文件中注册 QUERY_ALL_PACKAGES 权限，否则在 Android 11 上面就算申请成功也是获取不到第三方安装列表信息的
         *   2. 这个权限在有的手机上面是授予状态，在有的手机上面是还没有授予，在有的手机上面是无法申请，能支持申请该权限的的厂商系统版本有：
         *      华为：Harmony 3.0.0 及以上版本，Harmony 2.0.1 实测不行
         *      荣耀：Magic UI 6.0 及以上版本，Magic UI 5.0 实测不行
         *      小米：Miui 13 及以上版本，Miui 12 实测不行，经过验证 miui 上面默认会授予此权限
         *      OPPO：(ColorOs 12 及以上版本 && Android 11+) || (ColorOs 11.1 及以上版本 && Android 12+)
         *      VIVO：虽然没有申请这个权限的通道，但是读取已安装第三方应用列表是没有问题的，没有任何限制
         *      真我：realme UI 3.0 及以上版本，realme UI 2.0 实测不行
         */
        const val GET_INSTALLED_APPS = "com.android.permission.GET_INSTALLED_APPS"

        //***************************************分割线**************************************
        const val READ_CALENDAR = "android.permission.READ_CALENDAR"
        const val WRITE_CALENDAR = "android.permission.WRITE_CALENDAR"
        const val CAMERA = "android.permission.CAMERA"
        const val READ_CONTACTS = "android.permission.READ_CONTACTS"
        const val WRITE_CONTACTS = "android.permission.WRITE_CONTACTS"
        const val GET_ACCOUNTS = "android.permission.GET_ACCOUNTS"
        const val ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION"
        const val ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION"
        const val RECORD_AUDIO = "android.permission.RECORD_AUDIO"
        const val READ_PHONE_STATE = "android.permission.READ_PHONE_STATE"
        const val READ_PHONE_STATE_BLOW_ANDROID_9 = "android.permission.READ_PHONE_STATE_BLOW_ANDROID_9"
        const val CALL_PHONE = "android.permission.CALL_PHONE"
        const val READ_CALL_LOG = "android.permission.READ_CALL_LOG"
        const val WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG"
        const val ADD_VOICEMAIL = "com.android.voicemail.permission.ADD_VOICEMAIL"
        const val USE_SIP = "android.permission.USE_SIP"
        const val PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS"
        const val BODY_SENSORS = "android.permission.BODY_SENSORS"
        const val SEND_SMS = "android.permission.SEND_SMS"
        const val RECEIVE_SMS = "android.permission.RECEIVE_SMS"
        const val READ_SMS = "android.permission.READ_SMS"
        const val RECEIVE_WAP_PUSH = "android.permission.RECEIVE_WAP_PUSH"
        const val RECEIVE_MMS = "android.permission.RECEIVE_MMS"
        const val READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"
        const val READ_EXTERNAL_STORAGE_BLOW_ANDROID_13 = "android.permission.READ_EXTERNAL_STORAGE_BLOW_ANDROID_13"
        const val WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE"
        const val WRITE_EXTERNAL_STORAGE_BLOW_ANDROID_9 = "android.permission.WRITE_EXTERNAL_STORAGE_BLOW_ANDROID_9"
        //Android13新引入权限
        const val POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS"
        const val READ_MEDIA_IMAGES = "android.permission.READ_MEDIA_IMAGES"
        const val READ_MEDIA_VIDEO = "android.permission.READ_MEDIA_VIDEO"
        const val READ_MEDIA_AUDIO = "android.permission.READ_MEDIA_AUDIO"
        //Android14新引入权限
        const val READ_MEDIA_VISUAL_USER_SELECTED = "android.permission.READ_MEDIA_VISUAL_USER_SELECTED"
    }
}