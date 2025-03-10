package spa.lyh.cn.permissionutils

import android.app.Activity

interface OnPermissionInterceptor {

    fun launchPermissionRequest(activity: Activity, allPermissions: List<String>, callback: OnPermissionCallback?) {
        PermissionFragment.launch(activity, allPermissions, this, callback)
    }

    /**
     * 用户授予了权限（注意需要在此处回调 {@link OnPermissionCallback#onGranted(List, boolean)}）
     *
     */
    fun grantedPermissionRequest(activity:Activity, allPermissions: ArrayList<String>, grantedPermissions:ArrayList<String>, allGranted: Boolean,callback:OnPermissionCallback?) {
        if (callback == null) {
            return
        }
        callback.onGranted(grantedPermissions, allGranted)
    }

    /**
     * 用户拒绝了权限（注意需要在此处回调 {@link OnPermissionCallback#onDenied(List, boolean)}）
     */
    fun deniedPermissionRequest(activity:Activity, allPermissions: ArrayList<String>, deniedPermissions: ArrayList<String>, doNotAskAgain: Boolean, callback:OnPermissionCallback?) {
        if (callback == null) {
            return
        }
        callback.onDenied(deniedPermissions, doNotAskAgain)
    }

    /**
     * 权限请求完成
     *
     */
    fun finishPermissionRequest(activity:Activity, allPermissions: ArrayList<String>, skipRequest: Boolean,callback:OnPermissionCallback?) {
        //参考代码这里就是空的
    }
}