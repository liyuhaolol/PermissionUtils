package spa.lyh.cn.permissionutils.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings

object WindowPermissionCompat {

    private const val OP_SYSTEM_ALERT_WINDOW_FIELD_NAME = "OP_SYSTEM_ALERT_WINDOW"
    private const val OP_SYSTEM_ALERT_WINDOW_DEFAULT_VALUE = 24

    fun isGrantedPermission(context: Context): Boolean {
        if (AVersion.isAndroid6()) {
            return Settings.canDrawOverlays(context)
        }

        if (AVersion.isAndroid4_4()) {
            return PUtils.checkOpNoThrow(context, OP_SYSTEM_ALERT_WINDOW_FIELD_NAME, OP_SYSTEM_ALERT_WINDOW_DEFAULT_VALUE)
        }

        return true
    }

    fun getPermissionIntent(context: Context): Intent {
        if (AVersion.isAndroid6()) {
            //这里只是猜测，注释掉了小米的操作。
            /*if (AVersion.isAndroid11() && PRUtils.isMiui() && PRUtils.isMiuiOptimization()) {
                val intent = PIntentManager.getMiuiPermissionPageIntent(context)
                return StartActivityManager.addSubIntentToMainIntent(intent, PIntentManager.getApplicationDetailsIntent(context))!!
            }*/

            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = PUtils.getPackageNameUri(context)

            return if (PUtils.areActivityIntent(context, intent)) {
                intent
            } else {
                PIntentManager.getApplicationDetailsIntent(context)
            }
        }

        return when {
            PRUtils.isEmui() -> {
                val intent = PIntentManager.getEmuiWindowPermissionPageIntent(context)
                StartActivityManager.addSubIntentToMainIntent(intent, PIntentManager.getApplicationDetailsIntent(context))!!
            }
            PRUtils.isMiui() -> {
                var intent: Intent? = null
                if (PRUtils.isMiuiOptimization()) {
                    intent = PIntentManager.getMiuiWindowPermissionPageIntent(context)
                }
                StartActivityManager.addSubIntentToMainIntent(intent, PIntentManager.getApplicationDetailsIntent(context))!!
            }
            PRUtils.isColorOs() -> {
                val intent = PIntentManager.getColorOsWindowPermissionPageIntent(context)
                StartActivityManager.addSubIntentToMainIntent(intent, PIntentManager.getApplicationDetailsIntent(context))!!
            }
            PRUtils.isOriginOs() -> {
                val intent = PIntentManager.getOriginOsWindowPermissionPageIntent(context)
                StartActivityManager.addSubIntentToMainIntent(intent, PIntentManager.getApplicationDetailsIntent(context))!!
            }
            PRUtils.isOneUi() -> {
                val intent = PIntentManager.getOneUiWindowPermissionPageIntent(context)
                StartActivityManager.addSubIntentToMainIntent(intent, PIntentManager.getApplicationDetailsIntent(context))!!
            }
            else -> {
                PIntentManager.getApplicationDetailsIntent(context)
            }
        }
    }
}