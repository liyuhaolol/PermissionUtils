package spa.lyh.cn.permissionutils.utils

import android.app.Activity
import android.util.Log
import spa.lyh.cn.permissionutils.ManifestPro

object PermissionChecker {
    val TAG = "PermissionChecker"

    fun checkActivityStatus(activity:Activity?): Boolean{
        // 检查当前 Activity 状态是否是正常的，如果不是则不请求权限
        if (activity == null){
            Log.e(TAG,"The instance of the context must be an activity object")
            return false
        }
        if (activity.isFinishing){
            Log.e(TAG,"The activity has been finishing, please manually determine the status of the activity")
            return false
        }
        if (AVersion.isAndroid4_2() && activity.isDestroyed) {
            Log.e(TAG,"The activity has been destroyed, please manually determine the status of the activity")
            return false
        }
        return true
    }
    /**
     * 处理和优化已经过时的权限
     *
     */
    fun optimizeDeprecatedPermission(requestPermissions: MutableList<String>) {
        // 如果本次申请包含了 Android 13 WIFI 权限
        if (!AVersion.isAndroid13()) {
            if (PUtils.containsPermission(requestPermissions, ManifestPro.permission.POST_NOTIFICATIONS) &&
                !PUtils.containsPermission(requestPermissions, ManifestPro.permission.NOTIFICATION_SERVICE)
            ) {
                // 添加旧版的通知权限
                requestPermissions.add(ManifestPro.permission.NOTIFICATION_SERVICE)
            }

            if (PUtils.containsPermission(requestPermissions, ManifestPro.permission.NEARBY_WIFI_DEVICES) &&
                !PUtils.containsPermission(requestPermissions, ManifestPro.permission.ACCESS_FINE_LOCATION)
            ) {
                // 这是 Android 13 之前遗留的问题，使用 WIFI 需要精确定位权限
                requestPermissions.add(ManifestPro.permission.ACCESS_FINE_LOCATION)
            }

            if (PUtils.containsPermission(requestPermissions, ManifestPro.permission.READ_MEDIA_IMAGES) ||
                PUtils.containsPermission(requestPermissions, ManifestPro.permission.READ_MEDIA_VIDEO) ||
                PUtils.containsPermission(requestPermissions, ManifestPro.permission.READ_MEDIA_AUDIO)
            ) {
                // 添加旧版的读取外部存储权限
                if (!PUtils.containsPermission(requestPermissions, ManifestPro.permission.READ_EXTERNAL_STORAGE)) {
                    requestPermissions.add(ManifestPro.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }

        // 如果本次申请包含了 Android 12 蓝牙扫描权限
        if (!AVersion.isAndroid12() &&
            PUtils.containsPermission(requestPermissions, ManifestPro.permission.BLUETOOTH_SCAN) &&
            !PUtils.containsPermission(requestPermissions, ManifestPro.permission.ACCESS_FINE_LOCATION)
        ) {
            // 这是 Android 12 之前遗留的问题，扫描蓝牙需要精确定位权限
            requestPermissions.add(ManifestPro.permission.ACCESS_FINE_LOCATION)
        }

        // 如果本次申请包含了 Android 11 存储权限
        if (PUtils.containsPermission(requestPermissions, ManifestPro.permission.MANAGE_EXTERNAL_STORAGE)) {
            if (!AVersion.isAndroid11()) {
                // 自动添加旧版的存储权限，因为旧版的系统不支持申请新版的存储权限
                if (!PUtils.containsPermission(requestPermissions, ManifestPro.permission.READ_EXTERNAL_STORAGE)) {
                    requestPermissions.add(ManifestPro.permission.READ_EXTERNAL_STORAGE)
                }

                if (!PUtils.containsPermission(requestPermissions, ManifestPro.permission.WRITE_EXTERNAL_STORAGE)) {
                    requestPermissions.add(ManifestPro.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }

        if (!AVersion.isAndroid10() &&
            PUtils.containsPermission(requestPermissions, ManifestPro.permission.ACTIVITY_RECOGNITION) &&
            !PUtils.containsPermission(requestPermissions, ManifestPro.permission.BODY_SENSORS)
        ) {
            // 自动添加传感器权限，因为 ACTIVITY_RECOGNITION 是从 Android 10 开始才从传感器权限中剥离成独立权限
            requestPermissions.add(ManifestPro.permission.BODY_SENSORS)
        }

        if (!AVersion.isAndroid8() &&
            PUtils.containsPermission(requestPermissions, ManifestPro.permission.READ_PHONE_NUMBERS) &&
            !PUtils.containsPermission(requestPermissions, ManifestPro.permission.READ_PHONE_STATE)
        ) {
            // 自动添加旧版的读取电话号码权限，因为旧版的系统不支持申请新版的权限
            requestPermissions.add(ManifestPro.permission.READ_PHONE_STATE)
        }
    }
}