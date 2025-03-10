package spa.lyh.cn.permissionutils

import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.ViewGroup
import android.widget.PopupWindow
import spa.lyh.cn.permissionutils.utils.AVersion

open class DefaultPermissionInterceptor: OnPermissionInterceptor {
    /** 权限申请标记 */
    private var mRequestFlag = false
    /** 权限申请说明 Popup */
    var mPermissionPopup:PopupWindow? = null

    val HANDLER:Handler = Handler(Looper.getMainLooper())

    override fun launchPermissionRequest(activity: Activity, allPermissions: List<String>, callback: OnPermissionCallback?) {
        mRequestFlag = true
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
            super.launchPermissionRequest(activity, allPermissions, callback)
            HANDLER.postDelayed({
                if (!mRequestFlag) {
                    return@postDelayed
                }
                if (activity.isFinishing ||
                    (Build.VERSION.SDK_INT >= AVersion.ANDROID_4_2 && activity.isDestroyed)) {
                    return@postDelayed
                }
                showPopupWindow(activity.window.decorView as ViewGroup)
            },300)
        }else{
            //特殊权限要弹dialog
        }
    }

    private fun showPopupWindow(decorView: ViewGroup){
        if (mPermissionPopup != null){
            //不为空才弹出
        }
    }
}