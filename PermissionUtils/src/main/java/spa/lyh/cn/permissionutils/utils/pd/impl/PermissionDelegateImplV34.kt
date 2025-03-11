package spa.lyh.cn.permissionutils.utils.pd.impl

import android.app.Activity
import android.content.Context
import spa.lyh.cn.permissionutils.ManifestPro
import spa.lyh.cn.permissionutils.utils.AVersion
import spa.lyh.cn.permissionutils.utils.PUtils

open class PermissionDelegateImplV34: PermissionDelegateImplV33() {

    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
            if (!AVersion.isAndroid14()) {
                return true;
            }
            return PUtils.checkSelfPermission(context, permission);
        }

        return super.isGrantedPermission(context, permission)
    }

    override fun isDoNotAskAgainPermission(activity: Activity, permission: String): Boolean {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
            if (!AVersion.isAndroid14()) {
                return false
            }
            return !PUtils.checkSelfPermission(activity, permission) &&
                    !PUtils.shouldShowRequestPermissionRationale(activity, permission)
        }

        return super.isDoNotAskAgainPermission(activity, permission)
    }

    override fun recheckPermissionResult(context: Context, permission: String, grantResult: Boolean): Boolean {
        // 如果是在 Android 14 上面，并且是图片权限或者视频权限，则需要重新检查权限的状态
        // 这是因为用户授权部分图片或者视频的时候，READ_MEDIA_VISUAL_USER_SELECTED 权限状态是授予的
        // 但是 READ_MEDIA_IMAGES 和 READ_MEDIA_VIDEO 的权限状态是拒绝的
        if (AVersion.isAndroid14() &&
            PUtils.containsPermission(arrayListOf(
                ManifestPro.permission.READ_MEDIA_IMAGES,
                ManifestPro.permission.READ_MEDIA_VIDEO), permission)) {
            return isGrantedPermission(context, ManifestPro.permission.READ_MEDIA_VISUAL_USER_SELECTED)
        }

        return super.recheckPermissionResult(context, permission, grantResult)
    }
}