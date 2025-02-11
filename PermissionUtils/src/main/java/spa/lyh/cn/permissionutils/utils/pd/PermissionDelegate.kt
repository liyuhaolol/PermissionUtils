package spa.lyh.cn.permissionutils.utils.pd

import android.app.Activity
import android.content.Context
import android.content.Intent

interface PermissionDelegate {

    /**
     * 判断某个权限是否授予了
     */
    fun isGrantedPermission(context:Context,permission: String): Boolean

    /**
     * 判断某个权限是否勾选了不再询问
     */
    fun isDoNotAskAgainPermission(activity:Activity,permission: String): Boolean

    /**
     * 重新检查权限回调的结果
     */
    fun recheckPermissionResult(context: Context,permission:String,grantResult: Boolean): Boolean

    /**
     * 获取权限设置页的意图
     */
    fun getPermissionSettingIntent(context: Context,permission:String):Intent?
}