package spa.lyh.cn.permissionutils.utils.notification

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import spa.lyh.cn.permissionutils.ManifestPro
import spa.lyh.cn.permissionutils.model.AndroidManifestInfo
import spa.lyh.cn.permissionutils.utils.AVersion
import spa.lyh.cn.permissionutils.utils.PIntentManager
import spa.lyh.cn.permissionutils.utils.PUtils

class NotificationListenerPermissionCompat private constructor() {

    companion object {

        /** Settings.Secure.ENABLED_NOTIFICATION_LISTENERS */
        private const val SETTING_ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"

        fun isGrantedPermission(context: Context): Boolean {
            // 经过实践得出，通知监听权限是在 Android 4.3 才出现的，所以前面的版本统一返回 true
            if (!AVersion.isAndroid4_3()) {
                return true
            }
            val enabledNotificationListeners = Settings.Secure.getString(
                context.contentResolver, SETTING_ENABLED_NOTIFICATION_LISTENERS
            )
            if (TextUtils.isEmpty(enabledNotificationListeners)) {
                return false
            }
            // com.hjq.permissions.demo/com.hjq.permissions.demo.NotificationMonitorService:com.huawei.health/com.huawei.bone.ui.setting.NotificationPushListener
            val components = enabledNotificationListeners.split(":")
            for (component in components) {
                val componentName = ComponentName.unflattenFromString(component)
                if (componentName == null) {
                    continue
                }
                if (!TextUtils.equals(componentName.packageName, context.packageName)) {
                    continue
                }

                val className = componentName.className
                try {
                    // 判断这个类有是否存在，如果存在的话，证明是有效的
                    // 如果不存在的话，证明无效的，也是需要重新授权的
                    Class.forName(className)
                    return true
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
            return false
        }

        fun getPermissionIntent(context: Context): Intent {
            var intent: Intent? = null
            if (AVersion.isAndroid11()) {
                val androidManifestInfo = PUtils.getAndroidManifestInfo(context)
                var serviceInfo: AndroidManifestInfo.ServiceInfo? = null
                if (androidManifestInfo != null) {
                    for (info in androidManifestInfo.serviceInfoList) {
                        if (!TextUtils.equals(info.permission, ManifestPro.permission.BIND_NOTIFICATION_LISTENER_SERVICE)) {
                            continue
                        }

                        if (serviceInfo != null) {
                            // 证明有两个这样的 Service，就不跳转到权限详情页了，而是跳转到权限列表页
                            serviceInfo = null
                            break
                        }

                        serviceInfo = info
                    }
                }
                if (serviceInfo != null) {
                    intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS)
                    intent.putExtra(
                        Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME,
                        ComponentName(context, serviceInfo.name).flattenToString()
                    )
                    if (!PUtils.areActivityIntent(context, intent)) {
                        intent = null
                    }
                }
            }

            if (intent == null) {
                intent = if (AVersion.isAndroid5_1()) {
                    Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                } else {
                    // android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
                    Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                }
            }

            if (!PUtils.areActivityIntent(context, intent)) {
                intent = PIntentManager.getApplicationDetailsIntent(context)
            }
            return intent
        }
    }
}