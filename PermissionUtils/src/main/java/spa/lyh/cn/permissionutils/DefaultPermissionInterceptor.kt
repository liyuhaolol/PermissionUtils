package spa.lyh.cn.permissionutils

import android.R
import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import spa.lyh.cn.permissionutils.utils.AVersion
import spa.lyh.cn.permissionutils.view.DefaultDialog
import spa.lyh.cn.permissionutils.view.DefaultPermissionPopup
import kotlin.time.Duration
import androidx.core.content.edit

open class DefaultPermissionInterceptor(val context: Context): OnPermissionInterceptor {
    /** 权限申请标记 */
    private var mRequestFlag = false
    /** 权限申请说明 Popup */
    private var mPermissionPopup: DefaultPermissionPopup? = null
    private var mDefaultDialog:DefaultDialog? = null
    private var mDefaultSettingDialog:DefaultDialog? = null

    var forceShowSetting:Boolean = false
    private var interval: Long = 0
    private val FILLNAME = "permission_time"
    private var mSharedPreferences: SharedPreferences = context.getSharedPreferences(FILLNAME, MODE_PRIVATE)
    private var intervalList: ArrayList<String> = arrayListOf()

    val HANDLER:Handler = Handler(Looper.getMainLooper())

    override fun launchPermissionRequest(activity: Activity, allPermissions: MutableList<String>, callback: OnPermissionCallback?) {
        mRequestFlag = true
        intervalList.clear()
        val deniedPermissions: List<String> = AskPermission.getDenied(activity, allPermissions)
        var showPopupWindow = true
        //存在间隔
        if (interval > 0){
            //判断在间隔内的普通权限并移除
            val iterator = allPermissions.iterator()
            while (iterator.hasNext()){
                val item: String = iterator.next()
                val lastRequestTime = mSharedPreferences.getLong(item,0)
                if (lastRequestTime > 0){
                    //存在上次的请求记录
                    val time = System.currentTimeMillis() - lastRequestTime
                    if (time <= interval){
                        //实际请求时间小于间隔
                        intervalList.add(item)
                        iterator.remove()
                    }
                }
            }

        }
        if (allPermissions.isEmpty()){
            //所有权限都在间隔内
            val rightList: ArrayList<String> = arrayListOf()
            rightList.addAll(intervalList)
            deniedPermissionRequest(activity, rightList,intervalList,true,callback)
            return
        }
        //判断是否为特殊权限，权限是否通过
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

    override fun grantedPermissionRequest(activity: Activity, allPermissions: ArrayList<String>, grantedPermissions: ArrayList<String>, allGranted: Boolean, callback: OnPermissionCallback?) {
        //处理允许的权限数据
        for (permission in grantedPermissions){
            if (AskPermission.isSpecial(permission)){
                //不处理特殊权限
                continue
            }
            //移除允许的权限数据
            mSharedPreferences.edit() {
                remove(permission)
            }
        }
        //判断是否有间隔内的请求，这个if块似乎是个不可能触发的逻辑，仅仅为了健壮
        if (intervalList.isNotEmpty()){
            //存在移除请求的间隔内权限
            val iterator = intervalList.iterator()
            while (iterator.hasNext()){
                val item: String = iterator.next()
                if (grantedPermissions.contains(item)){
                    //拒绝的权限已经手动通过了，则应该在列表里去除
                    iterator.remove()
                }
            }
            if (intervalList.isNotEmpty()){
                //依然存在被拒绝的权限
                deniedPermissionRequest(activity,allPermissions,intervalList,true,callback)
            }else{
                super.grantedPermissionRequest(activity, allPermissions, grantedPermissions, allGranted, callback)
            }
        }else{
            super.grantedPermissionRequest(activity, allPermissions, grantedPermissions, allGranted, callback)
        }
    }

    override fun deniedPermissionRequest(activity: Activity, allPermissions: ArrayList<String>, deniedPermissions: ArrayList<String>, doNotAskAgain: Boolean, callback: OnPermissionCallback?) {
        //处理被拒绝的权限数据
        for (permission in deniedPermissions){
            if (AskPermission.isSpecial(permission)){
                //不处理特殊权限
                continue
            }
            val lastRequestTime = mSharedPreferences.getLong(permission,0)
            if (lastRequestTime == 0L){
                //没有缓存对应的请求时间
                mSharedPreferences.edit() {
                    putLong(permission, System.currentTimeMillis())
                }
            }
        }
        //判断权限拒绝后是否要弹窗
        if (forceShowSetting && doNotAskAgain){
            if (mDefaultSettingDialog != null){
                showDeniedDialog(activity,allPermissions,deniedPermissions, doNotAskAgain, callback)
            }else{
                Log.e("PermissionInterceptor","未初始化权限说明Dialog")
                super.deniedPermissionRequest(activity, allPermissions, deniedPermissions, doNotAskAgain, callback)
            }
        }else{
            super.deniedPermissionRequest(activity, allPermissions, deniedPermissions, doNotAskAgain, callback)
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

    private fun showDeniedDialog(activity: Activity, allPermissions: ArrayList<String>, deniedPermissions: ArrayList<String>, doNotAskAgain: Boolean, callback: OnPermissionCallback?){
        mDefaultSettingDialog!!.setPositiveButton(View.OnClickListener {
            mDefaultSettingDialog!!.dismiss()
            AskPermission.startPermissionActivity(activity,deniedPermissions,object :OnPermissionPageCallback{
                override fun onGranted() {
                    callback?.onGranted(allPermissions,true)
                }

                override fun onDenied() {
                    showDeniedDialog(activity,allPermissions, ArrayList(AskPermission.getDenied(activity,allPermissions)),doNotAskAgain, callback)
                }
            })
        })
        mDefaultSettingDialog!!.setNegativeButton(View.OnClickListener {
            mDefaultSettingDialog!!.dismiss()
            super.deniedPermissionRequest(activity, allPermissions, deniedPermissions, doNotAskAgain, callback)
        })
        mDefaultSettingDialog!!.show()
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

    fun setSepcialSettingDialog(dialog:DefaultDialog){
        this.mDefaultDialog = dialog
    }

    fun setGoSettingDialog(dialog:DefaultDialog){
        this.mDefaultSettingDialog = dialog
    }

    //同一个权限请求的时间间隔,为了合规国内某些平台的需要
    fun interval(duration: Duration): DefaultPermissionInterceptor{
        this.interval = duration.inWholeMilliseconds
        return this
    }

    @RequiresApi(26)
    fun interval(duration: java.time.Duration): DefaultPermissionInterceptor{
        this.interval = duration.toMillis()
        return this
    }

    fun interval(milliSeconds: Long): DefaultPermissionInterceptor{
        this.interval = milliSeconds
        return this
    }
}