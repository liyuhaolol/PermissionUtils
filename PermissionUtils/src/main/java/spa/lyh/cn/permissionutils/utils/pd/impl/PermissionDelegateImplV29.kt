package spa.lyh.cn.permissionutils.utils.pd.impl

import android.app.Activity
import android.content.Context
import androidx.annotation.RequiresApi
import spa.lyh.cn.permissionutils.ManifestPro
import spa.lyh.cn.permissionutils.utils.AVersion
import spa.lyh.cn.permissionutils.utils.PUtils

open class PermissionDelegateImplV29: PermissionDelegateImplV28() {

    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.ACCESS_MEDIA_LOCATION)) {
            if (!AVersion.isAndroid6()) {
                return true
            }
            if (!AVersion.isAndroid10()) {
                return PUtils.checkSelfPermission(context, ManifestPro.permission.READ_EXTERNAL_STORAGE)
            }
            return isGrantedReadStoragePermission(context) &&
                    PUtils.checkSelfPermission(context, ManifestPro.permission.ACCESS_MEDIA_LOCATION)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.ACCESS_BACKGROUND_LOCATION)) {
            if (!AVersion.isAndroid6()) {
                return true
            }
            if (!AVersion.isAndroid10()) {
                return PUtils.checkSelfPermission(context, ManifestPro.permission.ACCESS_FINE_LOCATION);
            }
            return PUtils.checkSelfPermission(context, permission)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.ACTIVITY_RECOGNITION)) {
            if (!AVersion.isAndroid10()) {
                return true
            }
            return PUtils.checkSelfPermission(context, permission)
        }
        return super.isGrantedPermission(context, permission)
    }

    override fun isDoNotAskAgainPermission(activity: Activity, permission: String): Boolean {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.ACCESS_BACKGROUND_LOCATION)) {
            if (!AVersion.isAndroid6()) {
                return false
            }
            if (!AVersion.isAndroid10()) {
                return !PUtils.checkSelfPermission(activity, ManifestPro.permission.ACCESS_FINE_LOCATION) &&
                        !PUtils.shouldShowRequestPermissionRationale(activity, ManifestPro.permission.ACCESS_FINE_LOCATION)
            }
            // 先检查前台的定位权限是否拒绝了
            if (!PUtils.checkSelfPermission(activity, ManifestPro.permission.ACCESS_FINE_LOCATION)) {
                // 如果是的话就判断前台的定位权限是否被永久拒绝了
                return !PUtils.shouldShowRequestPermissionRationale(activity, ManifestPro.permission.ACCESS_FINE_LOCATION)
            }
            // 如果不是的话再去判断后台的定位权限是否被拒永久拒绝了
            return !PUtils.checkSelfPermission(activity, permission) &&
                    !PUtils.shouldShowRequestPermissionRationale(activity, permission)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.ACCESS_MEDIA_LOCATION)) {
            if (!AVersion.isAndroid6()) {
                return false
            }
            if (!AVersion.isAndroid10()) {
                return !PUtils.checkSelfPermission(activity, ManifestPro.permission.READ_EXTERNAL_STORAGE) &&
                        !PUtils.shouldShowRequestPermissionRationale(activity, ManifestPro.permission.READ_EXTERNAL_STORAGE);
            }
            return isGrantedReadStoragePermission(activity) &&
                    !PUtils.checkSelfPermission(activity, permission) &&
                    !PUtils.shouldShowRequestPermissionRationale(activity, permission)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.ACTIVITY_RECOGNITION)) {
            if (!AVersion.isAndroid10()) {
                return false
            }
            return !PUtils.checkSelfPermission(activity, permission) &&
                    !PUtils.shouldShowRequestPermissionRationale(activity, permission)
        }
        return super.isDoNotAskAgainPermission(activity, permission)
    }

    /**
     * 判断是否授予了读取文件的权限
     */
    @RequiresApi(AVersion.ANDROID_6)
    private fun isGrantedReadStoragePermission(context:Context): Boolean {
        if (AVersion.isAndroid13() && AVersion.getTargetSdkVersionCode(context) >= AVersion.ANDROID_13) {
            return PUtils.checkSelfPermission(context, ManifestPro.permission.READ_MEDIA_IMAGES) ||
                    isGrantedPermission(context, ManifestPro.permission.MANAGE_EXTERNAL_STORAGE)
        }
        if (AVersion.isAndroid11() && AVersion.getTargetSdkVersionCode(context) >= AVersion.ANDROID_11) {
            return PUtils.checkSelfPermission(context, ManifestPro.permission.READ_EXTERNAL_STORAGE) ||
                    isGrantedPermission(context, ManifestPro.permission.MANAGE_EXTERNAL_STORAGE)
        }
        return PUtils.checkSelfPermission(context, ManifestPro.permission.READ_EXTERNAL_STORAGE)
    }
}