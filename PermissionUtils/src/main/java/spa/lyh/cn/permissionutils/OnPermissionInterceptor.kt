package spa.lyh.cn.permissionutils

import android.app.Activity

interface OnPermissionInterceptor {

    fun launchPermissionRequest(activity: Activity, allPermissions: List<String>, callback: OnPermissionCallback?) {
        PermissionFragment.launch(activity, allPermissions, this, callback)
    }
}