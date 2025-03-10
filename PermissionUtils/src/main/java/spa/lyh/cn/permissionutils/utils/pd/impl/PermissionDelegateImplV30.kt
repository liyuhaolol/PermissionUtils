package spa.lyh.cn.permissionutils.utils.pd.impl

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import spa.lyh.cn.permissionutils.ManifestPro
import spa.lyh.cn.permissionutils.utils.AVersion
import spa.lyh.cn.permissionutils.utils.PUtils

open class PermissionDelegateImplV30: PermissionDelegateImplV29() {

    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.MANAGE_EXTERNAL_STORAGE)) {
            if (!AVersion.isAndroid6()) {
                return true
            }
            if (!AVersion.isAndroid11()) {
                // 这个是 Android 10 上面的历史遗留问题，假设申请的是 MANAGE_EXTERNAL_STORAGE 权限
                // 必须要在 AndroidManifest.xml 中注册 android:requestLegacyExternalStorage="true"
                if (AVersion.isAndroid10() && !isUseDeprecationExternalStorage()) {
                    return false
                }
                return PUtils.checkSelfPermission(context, ManifestPro.permission.READ_EXTERNAL_STORAGE) &&
                        PUtils.checkSelfPermission(context, ManifestPro.permission.WRITE_EXTERNAL_STORAGE)
            }
            return isGrantedManageStoragePermission()
        }
        return super.isGrantedPermission(context, permission)
    }

    override fun isDoNotAskAgainPermission(activity: Activity, permission: String): Boolean {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.MANAGE_EXTERNAL_STORAGE)) {
            return false
        }
        return super.isDoNotAskAgainPermission(activity, permission)
    }

    override fun getPermissionSettingIntent(context: Context, permission: String): Intent {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.MANAGE_EXTERNAL_STORAGE)) {
            if (!AVersion.isAndroid11()) {
                return getApplicationDetailsIntent(context)
            }
            return getManageStoragePermissionIntent(context)
        }
        return super.getPermissionSettingIntent(context, permission)
    }

    /**
     * 是否采用的是非分区存储的模式
     */
    @RequiresApi(AVersion.ANDROID_10)
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private fun isUseDeprecationExternalStorage(): Boolean {
        return Environment.isExternalStorageLegacy()
    }

    /**
     * 是否有所有文件的管理权限
     */
    @RequiresApi(AVersion.ANDROID_11)
    private fun isGrantedManageStoragePermission(): Boolean {
        return Environment.isExternalStorageManager()
    }

    /**
     * 获取所有文件的管理权限设置界面意图
     */
    @RequiresApi(AVersion.ANDROID_11)
    private fun getManageStoragePermissionIntent(context:Context):Intent {
        var intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        intent.setData(PUtils.getPackageNameUri(context))

        if (!PUtils.areActivityIntent(context, intent)) {
            intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
        }

        if (!PUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context)
        }
        return intent
    }
}