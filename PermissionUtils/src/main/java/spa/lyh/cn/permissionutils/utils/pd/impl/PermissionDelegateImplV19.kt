package spa.lyh.cn.permissionutils.utils.pd.impl

import android.app.Activity
import android.content.Context
import android.content.Intent
import spa.lyh.cn.permissionutils.ManifestPro
import spa.lyh.cn.permissionutils.utils.PUtils
import spa.lyh.cn.permissionutils.utils.notification.NotificationPermissionCompat

open class PermissionDelegateImplV19: PermissionDelegateImplV18() {
    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.NOTIFICATION_SERVICE)) {
            return NotificationPermissionCompat.isGrantedPermission(context)
        }
        return super.isGrantedPermission(context, permission)
    }

    override fun isDoNotAskAgainPermission(activity: Activity, permission: String): Boolean {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.NOTIFICATION_SERVICE)) {
            return false
        }
        return super.isDoNotAskAgainPermission(activity, permission)
    }

    override fun getPermissionSettingIntent(context: Context, permission: String): Intent? {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.NOTIFICATION_SERVICE)) {
            return NotificationPermissionCompat.getPermissionIntent(context)
        }
        return super.getPermissionSettingIntent(context, permission)
    }
}