package spa.lyh.cn.permissionutils

import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import spa.lyh.cn.permissionutils.utils.AVersion
import spa.lyh.cn.permissionutils.view.DefaultDialog
import spa.lyh.cn.permissionutils.view.DefaultPermissionPopup

open class DefaultPermissionInterceptor: OnPermissionInterceptor {
    /** 权限申请标记 */
    private var mRequestFlag = false
    /** 权限申请说明 Popup */
    private var mPermissionPopup: DefaultPermissionPopup? = null
    private var mDefaultDialog:DefaultDialog? = null

    val HANDLER:Handler = Handler(Looper.getMainLooper())

    override fun launchPermissionRequest(activity: Activity, allPermissions: List<String>, callback: OnPermissionCallback?) {
        mRequestFlag = true
        val deniedPermissions: List<String> = AskPermission.getDenied(activity, allPermissions)
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
            if (mDefaultDialog != null){
                mDefaultDialog!!.setPositiveButton(View.OnClickListener {
                    mDefaultDialog!!.dismiss()
                    super.launchPermissionRequest(activity, allPermissions, callback)
                })
                mDefaultDialog!!.setNegativeButton(View.OnClickListener {
                    mDefaultDialog!!.dismiss()
                    callback?.onDenied(deniedPermissions, false)
                })
                mDefaultDialog!!.show()
            }else{
                super.launchPermissionRequest(activity, allPermissions, callback)
            }
        }
    }

    override fun finishPermissionRequest(
        activity: Activity,
        allPermissions: ArrayList<String>,
        skipRequest: Boolean,
        callback: OnPermissionCallback?
    ) {
        mRequestFlag = false
        dismissPopupWindow()
    }

    private fun showPopupWindow(decorView: ViewGroup){
        mPermissionPopup?.showAtLocation(decorView, Gravity.TOP, 0, 0)
    }

    private fun dismissPopupWindow() {
        if (mPermissionPopup == null) {
            return
        }
        if (!mPermissionPopup!!.isShowing) {
            return
        }
        mPermissionPopup!!.dismiss()
    }

    fun setPopUpWindow(pop:DefaultPermissionPopup){
        this.mPermissionPopup = pop
    }

    fun setPopSettingDialog(dialog:DefaultDialog){
        this.mDefaultDialog = dialog;
    }
}