package spa.lyh.cn.permissionutils.utils.pd.impl

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.annotation.RequiresApi
import spa.lyh.cn.permissionutils.ManifestPro
import spa.lyh.cn.permissionutils.utils.AVersion
import spa.lyh.cn.permissionutils.utils.PUtils

open class PermissionDelegateImplV26: PermissionDelegateImplV23() {

    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.REQUEST_INSTALL_PACKAGES)) {
            if (!AVersion.isAndroid8()) {
                return true
            }
            return isGrantedInstallPermission(context)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.PICTURE_IN_PICTURE)) {
            if (!AVersion.isAndroid8()) {
                return true
            }
            return isGrantedPictureInPicturePermission(context)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.READ_PHONE_NUMBERS)) {
            if (!AVersion.isAndroid6()) {
                return true
            }
            if (!AVersion.isAndroid8()) {
                return PUtils.checkSelfPermission(context, ManifestPro.permission.READ_PHONE_STATE);
            }
            return PUtils.checkSelfPermission(context, permission)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.ANSWER_PHONE_CALLS)) {
            if (!AVersion.isAndroid8()) {
                return true
            }
            return PUtils.checkSelfPermission(context, permission)
        }
        return super.isGrantedPermission(context, permission)
    }

    override fun isDoNotAskAgainPermission(activity: Activity, permission: String): Boolean {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.REQUEST_INSTALL_PACKAGES)) {
            return false
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.PICTURE_IN_PICTURE)) {
            return false
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.READ_PHONE_NUMBERS)) {
            if (!AVersion.isAndroid6()) {
                return false
            }
            if (!AVersion.isAndroid8()) {
                return !PUtils.checkSelfPermission(activity, ManifestPro.permission.READ_PHONE_STATE) &&
                        !PUtils.shouldShowRequestPermissionRationale(activity, ManifestPro.permission.READ_PHONE_STATE)
            }
            return !PUtils.checkSelfPermission(activity, permission) &&
                    !PUtils.shouldShowRequestPermissionRationale(activity, permission)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.ANSWER_PHONE_CALLS)) {
            if (!AVersion.isAndroid8()) {
                return false
            }
            return !PUtils.checkSelfPermission(activity, permission) &&
                    !PUtils.shouldShowRequestPermissionRationale(activity, permission)
        }
        return super.isDoNotAskAgainPermission(activity, permission)
    }

    override fun getPermissionSettingIntent(context: Context, permission: String): Intent? {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.REQUEST_INSTALL_PACKAGES)) {
            if (!AVersion.isAndroid8()) {
                return getApplicationDetailsIntent(context)
            }
            return getInstallPermissionIntent(context)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.PICTURE_IN_PICTURE)) {
            if (!AVersion.isAndroid8()) {
                return getApplicationDetailsIntent(context)
            }
            return getPictureInPicturePermissionIntent(context)
        }
        return super.getPermissionSettingIntent(context, permission)
    }

    /**
     * 是否有安装权限
     */
    @RequiresApi(AVersion.ANDROID_8)
    private fun isGrantedInstallPermission(context:Context):Boolean {
        return context.packageManager.canRequestPackageInstalls()
    }

    /**
     * 是否有画中画权限
     */
    @RequiresApi(AVersion.ANDROID_8)
    private fun isGrantedPictureInPicturePermission(context:Context): Boolean {
        return PUtils.checkOpNoThrow(context, AppOpsManager.OPSTR_PICTURE_IN_PICTURE)
    }

    /**
     * 获取安装权限设置界面意图
     */
    @RequiresApi(AVersion.ANDROID_8)
    private fun getInstallPermissionIntent(context:Context):Intent {
        var intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
        intent.setData(PUtils.getPackageNameUri(context))
        if (!PUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context)
        }
        return intent
    }

    /**
     * 获取画中画权限设置界面意图
     */
    @RequiresApi(AVersion.ANDROID_8)
    private fun getPictureInPicturePermissionIntent(context:Context):Intent {
        // android.provider.Settings.ACTION_PICTURE_IN_PICTURE_SETTINGS
        var intent = Intent("android.settings.PICTURE_IN_PICTURE_SETTINGS")
        intent.setData(PUtils.getPackageNameUri(context))
        if (!PUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context)
        }
        return intent
    }
}