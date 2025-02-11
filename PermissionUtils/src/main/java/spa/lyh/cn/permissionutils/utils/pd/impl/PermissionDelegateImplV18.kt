package spa.lyh.cn.permissionutils.utils.pd.impl

import android.app.Activity
import android.content.Context
import android.content.Intent
import spa.lyh.cn.permissionutils.ManifestPro
import spa.lyh.cn.permissionutils.utils.notification.NotificationListenerPermissionCompat
import spa.lyh.cn.permissionutils.utils.PUtils

open class PermissionDelegateImplV18: PermissionDelegateImplBase() {

    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.BIND_NOTIFICATION_LISTENER_SERVICE)) {
            return NotificationListenerPermissionCompat.isGrantedPermission(context)
        }
        return super.isGrantedPermission(context, permission)
    }

    override fun isDoNotAskAgainPermission(activity: Activity, permission: String): Boolean {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.BIND_NOTIFICATION_LISTENER_SERVICE)) {
            return false
        }
        return super.isDoNotAskAgainPermission(activity, permission)
    }

    override fun getPermissionSettingIntent(context: Context, permission: String): Intent? {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.BIND_NOTIFICATION_LISTENER_SERVICE)) {
            return NotificationListenerPermissionCompat.getPermissionIntent(context);
        }
        return super.getPermissionSettingIntent(context, permission)
    }
}