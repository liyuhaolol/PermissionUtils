package spa.lyh.cn.permissionutils.utils.pd.impl

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.annotation.RequiresApi
import spa.lyh.cn.permissionutils.ManifestPro
import spa.lyh.cn.permissionutils.utils.AVersion
import spa.lyh.cn.permissionutils.utils.PIntentManager.getApplicationDetailsIntent
import spa.lyh.cn.permissionutils.utils.PUtils

open class PermissionDelegateImplV21 : PermissionDelegateImplV19(){
    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.PACKAGE_USAGE_STATS)) {
            if (!AVersion.isAndroid5()) {
                return true
            }
            return isGrantedPackagePermission(context)
        }
        return super.isGrantedPermission(context, permission)
    }

    override fun isDoNotAskAgainPermission(activity: Activity, permission: String): Boolean {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.PACKAGE_USAGE_STATS)) {
            return false
        }
        return super.isDoNotAskAgainPermission(activity, permission)
    }

    override fun getPermissionSettingIntent(context: Context, permission: String): Intent {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.PACKAGE_USAGE_STATS)) {
            if (!AVersion.isAndroid5()) {
                return getApplicationDetailsIntent(context)
            }
            return getPackagePermissionIntent(context);
        }

        return super.getPermissionSettingIntent(context, permission)
    }



    /**
     * 是否有使用统计权限
     */
    @RequiresApi(AVersion.ANDROID_5)
    private fun isGrantedPackagePermission(context:Context): Boolean {
        return PUtils.checkOpNoThrow(context, AppOpsManager.OPSTR_GET_USAGE_STATS)
    }

    /**
     * 获取使用统计权限设置界面意图
     */
    @RequiresApi(AVersion.ANDROID_5)
    private fun getPackagePermissionIntent(context:Context): Intent{
        var intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        if (AVersion.isAndroid10()) {
            // 经过测试，只有在 Android 10 及以上加包名才有效果
            // 如果在 Android 10 以下加包名会导致无法跳转
            intent.setData(PUtils.getPackageNameUri(context));
        }
        if (!PUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context)
        }
        return intent
    }
}