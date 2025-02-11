package spa.lyh.cn.permissionutils.utils.pd.impl

import android.content.Context
import spa.lyh.cn.permissionutils.ManifestPro
import spa.lyh.cn.permissionutils.utils.AVersion
import spa.lyh.cn.permissionutils.utils.PHelper
import spa.lyh.cn.permissionutils.utils.PUtils

open class PermissionDelegateImplV23: PermissionDelegateImplV21() {
    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        if (!PHelper.isSpecialPermission(permission)) {
            // 读取应用列表权限是比较特殊的危险权限，它和其他危险权限的判断方式不太一样，所以需要放在这里来判断
            if (PUtils.equalsPermission(permission, ManifestPro.permission.GET_INSTALLED_APPS)) {
                return GetInstalledAppsPermissionCompat.isGrantedPermission(context)
            }

            if (!AVersion.isAndroid6()) {
                return true
            }
            return PUtils.checkSelfPermission(context, permission);
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.SYSTEM_ALERT_WINDOW)) {
            return WindowPermissionCompat.isGrantedPermission(context)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.WRITE_SETTINGS)) {
            if (!AVersion.isAndroid6()) {
                return true
            }
            return isGrantedSettingPermission(context)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.ACCESS_NOTIFICATION_POLICY)) {
            if (!AVersion.isAndroid6()) {
                return true
            }
            return isGrantedNotDisturbPermission(context)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)) {
            if (!AVersion.isAndroid6()) {
                return true
            }
            return isGrantedIgnoreBatteryPermission(context)
        }
        return super.isGrantedPermission(context, permission)
    }
}