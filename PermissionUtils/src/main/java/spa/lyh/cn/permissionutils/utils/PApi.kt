package spa.lyh.cn.permissionutils.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import spa.lyh.cn.permissionutils.ManifestPro
import spa.lyh.cn.permissionutils.utils.pd.PermissionDelegate
import spa.lyh.cn.permissionutils.utils.pd.impl.PermissionDelegateImplV34

object PApi {
    private val DELEGATE:PermissionDelegate = PermissionDelegateImplV34()

    fun recheckPermissionResult(context: Context,permission:String,grantResult: Boolean): Boolean{
        return DELEGATE.recheckPermissionResult(context, permission, grantResult)
    }

    /**
     * 获取已授予的权限
     */
    fun getGrantedPermissions(permissions: ArrayList<String>,grantResults: IntArray): ArrayList<String>{
        val grantedPermissions = arrayListOf<String>()
        grantResults.forEachIndexed { index,item ->
            // 把授予过的权限加入到集合中
            if (item == PackageManager.PERMISSION_GRANTED){
                grantedPermissions.add(permissions[index])
            }
        }
        return grantedPermissions
    }

    /**
     * 获取没有授予的权限
     */
    fun getDeniedPermissions(permissions: ArrayList<String>,grantResults: IntArray): ArrayList<String> {
        val deniedPermissions = arrayListOf<String>()
        grantResults.forEachIndexed { index,item ->
            // 把没有授予过的权限加入到集合中
            if (item == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permissions[index])
            }
        }
        return deniedPermissions
    }

    /**
     * 在权限组中检查是否有某个权限是否被永久拒绝
     */
    fun isDoNotAskAgainPermissions(activity:Activity, permissions: ArrayList<String>): Boolean {
        for (permission in permissions) {
            if (isDoNotAskAgainPermission(activity, permission)) {
                return true
            }
        }
        return false
    }

    fun getSmartPermissionIntent(context: Context, permissions: List<String>?): Intent {
        // 如果失败的权限里面不包含特殊权限
        if (permissions == null || permissions.isEmpty()) {
            return PIntentManager.getApplicationDetailsIntent(context)
        }

        // 危险权限统一处理
        if (!PApi.containsSpecialPermission(permissions)) {
            if (permissions.size == 1) {
                return PApi.getPermissionSettingIntent(context, permissions[0])
            }
            return PIntentManager.getApplicationDetailsIntent(context, permissions)
        }

        // 特殊权限统一处理
        when (permissions.size) {
            1 -> {
                // 如果当前只有一个权限被拒绝了
                return PApi.getPermissionSettingIntent(context, permissions[0])
            }
            2 -> {
                if (!AVersion.isAndroid13() &&
                    PUtils.containsPermission(permissions, ManifestPro.permission.NOTIFICATION_SERVICE) &&
                    PUtils.containsPermission(permissions, ManifestPro.permission.POST_NOTIFICATIONS)) {
                    return PApi.getPermissionSettingIntent(context, ManifestPro.permission.NOTIFICATION_SERVICE)
                }
            }
            3 -> {
                if (AVersion.isAndroid11() &&
                    PUtils.containsPermission(permissions, ManifestPro.permission.MANAGE_EXTERNAL_STORAGE) &&
                    PUtils.containsPermission(permissions, ManifestPro.permission.READ_EXTERNAL_STORAGE) &&
                    PUtils.containsPermission(permissions, ManifestPro.permission.WRITE_EXTERNAL_STORAGE)) {
                    return PApi.getPermissionSettingIntent(context, ManifestPro.permission.MANAGE_EXTERNAL_STORAGE)
                }
            }
        }

        return PIntentManager.getApplicationDetailsIntent(context)
    }

    /**
     * 判断某个权限是否授予
     */
    fun isGrantedPermission(context:Context, permission:String): Boolean {
        return DELEGATE.isGrantedPermission(context, permission)
    }

    /**
     * 判断某个权限是否是特殊权限
     */
    fun isSpecialPermission(permission: String): Boolean {
        return PHelper.isSpecialPermission(permission)
    }

    /**
     * 判断某个权限是否被永久拒绝
     */
    fun isDoNotAskAgainPermission(activity:Activity, permission:String): Boolean {
        return DELEGATE.isDoNotAskAgainPermission(activity, permission)
    }

    /**
     * 获取权限设置页的意图
     */
    fun getPermissionSettingIntent(context:Context, permission:String): Intent {
        return DELEGATE.getPermissionSettingIntent(context, permission)
    }

    /**
     * 判断某个权限集合是否包含特殊权限
     */
    fun containsSpecialPermission(permissions:List<String>?): Boolean {
        if (permissions == null || permissions.isEmpty()) {
            return false
        }

        for (permission in permissions) {
            if (isSpecialPermission(permission)) {
                return true
            }
        }
        return false
    }

    /**
     * 判断某些权限是否全部被授予
     */
    fun isGrantedPermissions(context:Context, permissions:List<String>): Boolean {
        if (permissions.isEmpty()) {
            return false
        }

        for (permission in permissions) {
            if (!isGrantedPermission(context, permission)) {
                return false
            }
        }
        return true
    }
}
