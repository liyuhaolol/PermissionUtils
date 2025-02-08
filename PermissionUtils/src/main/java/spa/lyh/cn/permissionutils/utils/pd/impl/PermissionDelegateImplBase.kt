package spa.lyh.cn.permissionutils.utils.pd.impl

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.VpnService
import spa.lyh.cn.permissionutils.ManifestPro
import spa.lyh.cn.permissionutils.utils.PApi
import spa.lyh.cn.permissionutils.utils.PUtils
import spa.lyh.cn.permissionutils.utils.pd.PermissionDelegate

class PermissionDelegateImplBase: PermissionDelegate {
    override fun isGrantedPermission(
        context: Context,
        permission: String
    ): Boolean {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.BIND_VPN_SERVICE)) {
            return isGrantedVpnPermission(context)
        }

        return true
    }

    override fun isDoNotAskAgainPermission(
        activity: Activity,
        permission: String
    ): Boolean {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.BIND_VPN_SERVICE)) {
            return false
        }

        return false
    }

    override fun recheckPermissionResult(
        context: Context,
        permission: String,
        grantResult: Boolean
    ): Boolean {
        // 如果这个权限是特殊权限，则需要重新检查权限的状态
/*        if (PApi.isSpecialPermission(permission)) {
            return isGrantedPermission(context, permission);
        }

        if (PermissionHelper.findAndroidVersionByPermission(permission) > AndroidVersion.getAndroidVersionCode()) {
            // 如果是申请了新权限，但却是旧设备上面运行的，会被系统直接拒绝，在这里需要重新检查权限的状态
            return isGrantedPermission(context, permission);
        }*/
        return grantResult
    }

    override fun getPermissionSettingIntent(
        context: Context,
        permission: String
    ): Intent {
        TODO("Not yet implemented")
    }

    private fun isGrantedVpnPermission(context: Context): Boolean{
        return VpnService.prepare(context) == null
    }
}