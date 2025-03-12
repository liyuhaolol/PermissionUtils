package spa.lyh.cn.permissionutils

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import spa.lyh.cn.permissionutils.utils.PApi
import spa.lyh.cn.permissionutils.utils.PUtils
import spa.lyh.cn.permissionutils.utils.StartActivityManager

class PermissionPageFragment: Fragment(), Runnable {
    companion object{
        /** 请求的权限组  */
        private const val REQUEST_PERMISSIONS: String = "request_permissions"

        /**
         * 开启权限申请
         */
        fun launch(activity: Activity, permissions: List<String>,callback: OnPermissionPageCallback?) {
            val fragment = PermissionPageFragment()
            val bundle = Bundle()
            if (permissions is ArrayList<*>) {
                bundle.putStringArrayList(REQUEST_PERMISSIONS, permissions as ArrayList<String>)
            } else {
                bundle.putStringArrayList(REQUEST_PERMISSIONS, ArrayList(permissions))
            }
            fragment.arguments = bundle
            // 设置保留实例，不会因为屏幕方向或配置变化而重新创建
            fragment.retainInstance = true
            // 设置权限申请标记
            fragment.setRequestFlag(true)
            // 设置权限回调监听
            fragment.setOnPermissionPageCallback(callback)
            // 绑定到 Activity 上面
            fragment.attachByActivity(activity)
        }
    }

    /** 权限回调对象  */
    private var mCallBack: OnPermissionPageCallback? = null

    /** 权限申请标记  */
    private var mRequestFlag = false

    /** 是否申请了权限  */
    private var mStartActivityFlag = false


    /**
     * 绑定 Activity
     */
    fun attachByActivity(activity: Activity) {
        val fragmentManager = activity.fragmentManager ?: return
        fragmentManager.beginTransaction().add(this, this.toString()).commitAllowingStateLoss()
    }

    /**
     * 解绑 Activity
     */
    fun detachByActivity(activity: Activity) {
        val fragmentManager = activity.fragmentManager ?: return
        fragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
    }

    /**
     * 设置权限监听回调监听
     */
    fun setOnPermissionPageCallback(callback: OnPermissionPageCallback?) {
        mCallBack = callback
    }

    /**
     * 权限申请标记（防止系统杀死应用后重新触发请求的问题）
     */
    fun setRequestFlag(flag: Boolean) {
        mRequestFlag = flag
    }

    override fun onResume() {
        super.onResume()

        // 如果当前 Fragment 是通过系统重启应用触发的，则不进行权限申请
        if (!mRequestFlag) {
            detachByActivity(activity)
            return
        }

        if (mStartActivityFlag) {
            return
        }

        mStartActivityFlag = true

        val arguments = arguments
        val activity = activity
        if (arguments == null || activity == null) {
            return
        }
        val permissions: List<String>? = arguments.getStringArrayList(REQUEST_PERMISSIONS)
        StartActivityManager.startActivityForResult(
            this,
            PApi.getSmartPermissionIntent(getActivity(), permissions),
            AskPermission.REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,data: Intent?) {
        if (requestCode != AskPermission.REQUEST_CODE) {
            return
        }

        val activity = activity
        val arguments = arguments
        if (activity == null || arguments == null) {
            return
        }
        val allPermissions = arguments.getStringArrayList(REQUEST_PERMISSIONS)
        if (allPermissions.isNullOrEmpty()) {
            return
        }

        PUtils.postActivityResult(allPermissions, this)
    }

    override fun run() {
        // 如果用户离开太久，会导致 Activity 被回收掉
        // 所以这里要判断当前 Fragment 是否有被添加到 Activity
        // 可在开发者模式中开启不保留活动来复现这个 Bug
        if (!isAdded) {
            return
        }

        val activity = activity ?: return

        val callback = mCallBack
        mCallBack = null

        if (callback == null) {
            detachByActivity(activity)
            return
        }

        val arguments = arguments

        val allPermissions: List<String>? = arguments.getStringArrayList(REQUEST_PERMISSIONS)
        if (allPermissions.isNullOrEmpty()) {
            return
        }

        val grantedPermissions: List<String> =
            PApi.getGrantedPermissions(activity, allPermissions)
        if (grantedPermissions.size == allPermissions.size) {
            callback.onGranted()
        } else {
            callback.onDenied()
        }

        detachByActivity(activity)
    }
}