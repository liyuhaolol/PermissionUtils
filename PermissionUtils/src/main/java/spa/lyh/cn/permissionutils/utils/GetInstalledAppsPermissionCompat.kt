package spa.lyh.cn.permissionutils.utils

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.provider.Settings
import androidx.annotation.RequiresApi
import spa.lyh.cn.permissionutils.ManifestPro

object GetInstalledAppsPermissionCompat {

    private const val MIUI_OP_GET_INSTALLED_APPS_FIELD_NAME = "OP_GET_INSTALLED_APPS"
    private const val MIUI_OP_GET_INSTALLED_APPS_DEFAULT_VALUE = 10022

    fun isGrantedPermission(context: Context): Boolean {
        if (!AVersion.isAndroid4_4()) {
            return true
        }

        if (AVersion.isAndroid6() && isSupportGetInstalledAppsPermission(context)) {
            return PUtils.checkSelfPermission(context, ManifestPro.permission.GET_INSTALLED_APPS)
        }

        if (PRUtils.isMiui() && isMiuiSupportGetInstalledAppsPermission()) {
            if (!PRUtils.isMiuiOptimization()) {
                return true
            }
            return PUtils.checkOpNoThrow(context, MIUI_OP_GET_INSTALLED_APPS_FIELD_NAME, MIUI_OP_GET_INSTALLED_APPS_DEFAULT_VALUE)
        }

        return true
    }

    fun isDoNotAskAgainPermission(activity: Activity): Boolean {
        if (!AVersion.isAndroid4_4()) {
            return false
        }

        if (AVersion.isAndroid6() && isSupportGetInstalledAppsPermission(activity)) {
            return !PUtils.checkSelfPermission(activity, ManifestPro.permission.GET_INSTALLED_APPS) &&
                    !PUtils.shouldShowRequestPermissionRationale(activity, ManifestPro.permission.GET_INSTALLED_APPS)
        }

        if (PRUtils.isMiui() && isMiuiSupportGetInstalledAppsPermission()) {
            if (!PRUtils.isMiuiOptimization()) {
                return false
            }
            return !isGrantedPermission(activity)
        }

        return false
    }

    fun getPermissionIntent(context: Context): Intent {
        if (PRUtils.isMiui()) {
            var intent: Intent? = null
            if (PRUtils.isMiuiOptimization()) {
                intent = PIntentManager.getMiuiPermissionPageIntent(context)
            }
            intent = StartActivityManager.addSubIntentToMainIntent(intent, PIntentManager.getApplicationDetailsIntent(context))
            return intent
        }

        return PIntentManager.getApplicationDetailsIntent(context)
    }

    @RequiresApi(AVersion.ANDROID_6)
    @Suppress("deprecation")
    private fun isSupportGetInstalledAppsPermission(context: Context): Boolean {
        try {
            val permissionInfo = context.packageManager.getPermissionInfo(ManifestPro.permission.GET_INSTALLED_APPS, 0)
            return if (permissionInfo != null) {
                if (AVersion.isAndroid9()) {
                    permissionInfo.protection == PermissionInfo.PROTECTION_DANGEROUS
                } else {
                    permissionInfo.protectionLevel and PermissionInfo.PROTECTION_MASK_BASE == PermissionInfo.PROTECTION_DANGEROUS
                }
            } else {
                false
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return try {
            Settings.Secure.getInt(context.contentResolver, "oem_installed_apps_runtime_permission_enable") == 1
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    private fun isMiuiSupportGetInstalledAppsPermission(): Boolean {
        if (!AVersion.isAndroid4_4()) {
            return true
        }
        return try {
            val appOpsClass = Class.forName(AppOpsManager::class.java.name)
            appOpsClass.getDeclaredField(MIUI_OP_GET_INSTALLED_APPS_FIELD_NAME)
            true
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            false
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
            false
        }
    }
}