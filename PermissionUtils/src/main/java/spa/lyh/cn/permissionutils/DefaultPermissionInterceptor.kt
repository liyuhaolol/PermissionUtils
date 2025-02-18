package spa.lyh.cn.permissionutils

import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import spa.lyh.cn.permissionutils.utils.AVersion

open class DefaultPermissionInterceptor: OnPermissionInterceptor {
    val handler:Handler = Handler(Looper.getMainLooper())

    override fun launchPermissionRequest(activity: Activity, allPermissions: List<String>, callback: OnPermissionCallback?) {
        var showPopupWindow = true
        for (permission in allPermissions) {
            if (!AskPermission.isSpecial(permission)) {
                //不是特殊权限跳过
                continue
            }
            if (AskPermission.isGranted(activity, permission)) {
                //已被授权跳过
                continue
            }
            if (Build.VERSION.SDK_INT < AVersion.ANDROID_11 && TextUtils.equals(ManifestPro.permission.MANAGE_EXTERNAL_STORAGE, permission)) {
                //Android11以下请求了文件管理权限跳过
                continue
            }
            // 如果申请的权限带有特殊权限，并且还没有授予的话
            // 就不用 PopupWindow 对话框来显示，而是用 Dialog 来显示
            showPopupWindow = false
            break
        }
        if (showPopupWindow){
            //要弹悬浮穿
        }else{
            //特殊权限要弹dialog
        }

        super.launchPermissionRequest(activity, allPermissions, callback)
    }
}