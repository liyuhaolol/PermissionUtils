package spa.lyh.cn.permissionutils.utils.notification

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import spa.lyh.cn.permissionutils.utils.AVersion
import spa.lyh.cn.permissionutils.utils.PIntentManager
import spa.lyh.cn.permissionutils.utils.PUtils

object NotificationPermissionCompat {

    private const val OP_POST_NOTIFICATION_FIELD_NAME = "OP_POST_NOTIFICATION"
    private const val OP_POST_NOTIFICATION_DEFAULT_VALUE = 11

    fun isGrantedPermission(context: Context): Boolean {
        if (AVersion.isAndroid7()) {
            return context.getSystemService(NotificationManager::class.java).areNotificationsEnabled()
        }

        if (AVersion.isAndroid4_4()) {
            return PUtils.checkOpNoThrow(context, OP_POST_NOTIFICATION_FIELD_NAME, OP_POST_NOTIFICATION_DEFAULT_VALUE)
        }
        return true
    }

    fun getPermissionIntent(context: Context): Intent? {
        var intent: Intent? = null
        if (AVersion.isAndroid8()) {
            intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            //intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.applicationInfo.uid);
        } else if (AVersion.isAndroid5()) {
            intent = Intent()
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo.uid)
        }
        if (!PUtils.areActivityIntent(context, intent)) {
            intent = PIntentManager.getApplicationDetailsIntent(context)
        }
        return intent
    }
}