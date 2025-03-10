package spa.lyh.cn.permissionutils.utils.pd.impl

import android.app.Activity
import android.content.Context
import android.content.Intent
import spa.lyh.cn.permissionutils.ManifestPro
import spa.lyh.cn.permissionutils.utils.AVersion
import spa.lyh.cn.permissionutils.utils.PUtils
import spa.lyh.cn.permissionutils.utils.notification.NotificationPermissionCompat

open class PermissionDelegateImplV33 : PermissionDelegateImplV31(){
    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.BODY_SENSORS_BACKGROUND)) {
            if (!AVersion.isAndroid6()) {
                return true
            }
            if (!AVersion.isAndroid13()) {
                return PUtils.checkSelfPermission(context, ManifestPro.permission.BODY_SENSORS)
            }
            // 有后台传感器权限的前提条件是授予了前台的传感器权限
            return PUtils.checkSelfPermission(context, ManifestPro.permission.BODY_SENSORS) &&
                    PUtils.checkSelfPermission(context, permission)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.POST_NOTIFICATIONS)) {
            if (!AVersion.isAndroid13()) {
                return NotificationPermissionCompat.isGrantedPermission(context)
            }
            return PUtils.checkSelfPermission(context, permission)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.NEARBY_WIFI_DEVICES)) {
            if (!AVersion.isAndroid6()) {
                return true
            }
            if (!AVersion.isAndroid13()) {
                return PUtils.checkSelfPermission(context, ManifestPro.permission.ACCESS_FINE_LOCATION);
            }
            return PUtils.checkSelfPermission(context, permission)
        }

        if (PUtils.containsPermission(arrayListOf(
                ManifestPro.permission.READ_MEDIA_IMAGES,
                ManifestPro.permission.READ_MEDIA_VIDEO,
                ManifestPro.permission.READ_MEDIA_AUDIO), permission)) {
            if (!AVersion.isAndroid6()) {
                return true
            }
            if (!AVersion.isAndroid13()) {
                return PUtils.checkSelfPermission(context, ManifestPro.permission.READ_EXTERNAL_STORAGE)
            }
            return PUtils.checkSelfPermission(context, permission)
        }

        if (AVersion.isAndroid13() && AVersion.getTargetSdkVersionCode(context) >= AVersion.ANDROID_13) {
            // 亲测当这两个条件满足的时候，在 Android 13 不能申请 WRITE_EXTERNAL_STORAGE，会被系统直接拒绝
            // 不会弹出系统授权对话框，框架为了保证不同 Android 版本的回调结果一致性，这里直接返回 true 给到外层
            if (PUtils.equalsPermission(permission, ManifestPro.permission.WRITE_EXTERNAL_STORAGE)) {
                return true
            }

            if (PUtils.equalsPermission(permission, ManifestPro.permission.READ_EXTERNAL_STORAGE)) {
                return PUtils.checkSelfPermission(context, ManifestPro.permission.READ_MEDIA_IMAGES) &&
                        PUtils.checkSelfPermission(context, ManifestPro.permission.READ_MEDIA_VIDEO) &&
                        PUtils.checkSelfPermission(context, ManifestPro.permission.READ_MEDIA_AUDIO)
            }
        }

        return super.isGrantedPermission(context, permission)
    }

    override fun isDoNotAskAgainPermission(activity: Activity, permission: String): Boolean {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.BODY_SENSORS_BACKGROUND)) {
            if (!AVersion.isAndroid6()) {
                return false
            }
            if (!AVersion.isAndroid13()) {
                return !PUtils.checkSelfPermission(activity, ManifestPro.permission.BODY_SENSORS) &&
                        !PUtils.shouldShowRequestPermissionRationale(activity, ManifestPro.permission.BODY_SENSORS)
            }
            // 先检查前台的传感器权限是否拒绝了
            if (!PUtils.checkSelfPermission(activity, ManifestPro.permission.BODY_SENSORS)) {
                // 如果是的话就判断前台的传感器权限是否被永久拒绝了
                return !PUtils.shouldShowRequestPermissionRationale(activity, ManifestPro.permission.BODY_SENSORS)
            }
            // 如果不是的话再去判断后台的传感器权限是否被拒永久拒绝了
            return !PUtils.checkSelfPermission(activity, permission) &&
                    !PUtils.shouldShowRequestPermissionRationale(activity, permission)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.POST_NOTIFICATIONS)) {
            if (!AVersion.isAndroid13()) {
                return false
            }
            return !PUtils.checkSelfPermission(activity, permission) &&
                    !PUtils.shouldShowRequestPermissionRationale(activity, permission)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.NEARBY_WIFI_DEVICES)) {
            if (!AVersion.isAndroid6()) {
                return false
            }
            if (!AVersion.isAndroid13()) {
                return !PUtils.checkSelfPermission(activity, ManifestPro.permission.ACCESS_FINE_LOCATION) &&
                        !PUtils.shouldShowRequestPermissionRationale(activity, ManifestPro.permission.ACCESS_FINE_LOCATION)
            }
            return !PUtils.checkSelfPermission(activity, permission) &&
                    !PUtils.shouldShowRequestPermissionRationale(activity, permission)
        }

        if (PUtils.containsPermission(arrayListOf(
                ManifestPro.permission.READ_MEDIA_IMAGES,
                ManifestPro.permission.READ_MEDIA_VIDEO,
                ManifestPro.permission.READ_MEDIA_AUDIO), permission)) {
            if (!AVersion.isAndroid6()) {
                return false
            }
            if (!AVersion.isAndroid13()) {
                return !PUtils.checkSelfPermission(activity, ManifestPro.permission.READ_EXTERNAL_STORAGE) &&
                        !PUtils.shouldShowRequestPermissionRationale(activity, ManifestPro.permission.READ_EXTERNAL_STORAGE)
            }
            return !PUtils.checkSelfPermission(activity, permission) &&
                    !PUtils.shouldShowRequestPermissionRationale(activity, permission)
        }

        if (AVersion.isAndroid13() && AVersion.getTargetSdkVersionCode(activity) >= AVersion.ANDROID_13) {
            if (PUtils.equalsPermission(permission, ManifestPro.permission.WRITE_EXTERNAL_STORAGE)) {
                return false
            }

            if (PUtils.equalsPermission(permission, ManifestPro.permission.READ_EXTERNAL_STORAGE)) {
                return !PUtils.checkSelfPermission(activity, ManifestPro.permission.READ_MEDIA_IMAGES) &&
                        !PUtils.shouldShowRequestPermissionRationale(activity, ManifestPro.permission.READ_MEDIA_IMAGES) &&
                        !PUtils.checkSelfPermission(activity, ManifestPro.permission.READ_MEDIA_VIDEO) &&
                        !PUtils.shouldShowRequestPermissionRationale(activity, ManifestPro.permission.READ_MEDIA_VIDEO) &&
                        !PUtils.checkSelfPermission(activity, ManifestPro.permission.READ_MEDIA_AUDIO) &&
                        !PUtils.shouldShowRequestPermissionRationale(activity, ManifestPro.permission.READ_MEDIA_AUDIO)
            }
        }

        return super.isDoNotAskAgainPermission(activity, permission)
    }

    override fun recheckPermissionResult(context: Context, permission: String, grantResult: Boolean): Boolean {
        if (AVersion.isAndroid13() && AVersion.getTargetSdkVersionCode(context) >= AVersion.ANDROID_13 &&
            PUtils.equalsPermission(permission, ManifestPro.permission.WRITE_EXTERNAL_STORAGE)) {
            // 在 Android 13 不能申请 WRITE_EXTERNAL_STORAGE，会被系统直接拒绝，在这里需要重新检查权限的状态
            return isGrantedPermission(context, permission)
        }

        return super.recheckPermissionResult(context, permission, grantResult)
    }

    override fun getPermissionSettingIntent(context: Context, permission: String): Intent {
        // Github issue 地址：https://github.com/getActivity/XXPermissions/issues/208
        // POST_NOTIFICATIONS 要跳转到权限设置页和 NOTIFICATION_SERVICE 权限是一样的
        if (PUtils.equalsPermission(permission, ManifestPro.permission.POST_NOTIFICATIONS)) {
            return NotificationPermissionCompat.getPermissionIntent(context)
        }
        return super.getPermissionSettingIntent(context, permission)
    }
}