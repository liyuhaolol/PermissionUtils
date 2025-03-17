package spa.lyh.cn.permissionutils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import spa.lyh.cn.permissionutils.utils.AVersion
import spa.lyh.cn.permissionutils.utils.PApi
import spa.lyh.cn.permissionutils.utils.PIntentManager
import spa.lyh.cn.permissionutils.utils.PUtils
import spa.lyh.cn.permissionutils.utils.PermissionChecker
import spa.lyh.cn.permissionutils.utils.StartActivityManager
import kotlin.time.Duration

open class AskPermission private constructor(private val mContext:Context){
    private val mPermissions: ArrayList<String> = arrayListOf()
    private var mInterceptor:OnPermissionInterceptor? = null

    companion object{
        /** 权限设置页跳转请求码  */
        const val REQUEST_CODE: Int = 1024 + 1
        @JvmStatic
        fun with(context: Context): AskPermission{
            return AskPermission(context)
        }
        @JvmStatic
        fun with(fragment: Fragment): AskPermission{
            return AskPermission(fragment.requireActivity())
        }
        @JvmStatic
        fun with(fragment: android.app.Fragment): AskPermission{
            return AskPermission(fragment.activity)
        }
        @JvmStatic
        fun isSpecial(permission:String): Boolean {
            return PApi.isSpecialPermission(permission)
        }

        @JvmStatic
        fun isGranted(context:Context, vararg permissions:String):Boolean {
            return isGranted(context, permissions.toMutableList())
        }

        @JvmStatic
        fun isGranted(context:Context, permissions:List<String>): Boolean {
            return PApi.isGrantedPermissions(context, permissions)
        }

        /**
         * 获取没有授予的权限
         */
        @JvmStatic
        fun getDenied(context: Context, vararg permissions: String
        ): List<String> {
            return getDenied(context, arrayListOf(*permissions))
        }

        @JvmStatic
        fun getDenied(context: Context,permissions:List<String>): List<String> {
            return PApi.getDeniedPermissions(context, permissions)
        }


        /* android.content.Context */
        @JvmStatic
        fun startPermissionActivity(context: Context) {
            startPermissionActivity(context, ArrayList<String>(0))
        }

        @JvmStatic
        fun startPermissionActivity(context: Context,vararg permissions: String) {
            startPermissionActivity(context, PUtils.asArrayList(permissions) as ArrayList<String>)
        }

        @JvmStatic
        fun startPermissionActivity(context: Context, vararg permissions: Array<String>) {
            startPermissionActivity(context, PUtils.asArrayLists(permissions) as ArrayList<String>)
        }

        /**
         * 跳转到应用权限设置页
         *
         * @param permissions           没有授予或者被拒绝的权限组
         */
        @JvmStatic
        fun startPermissionActivity(context: Context,permissions: List<String>) {
            val activity: Activity? = PUtils.findActivity(context)
            if (activity != null) {
                startPermissionActivity(activity, permissions)
                return
            }
            val intent: Intent = PApi.getSmartPermissionIntent(context, permissions)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            StartActivityManager.startActivity(context, intent)
        }


         //android.app.Activity
        @JvmStatic
        fun startPermissionActivity(activity: Activity) {
            startPermissionActivity(activity, ArrayList<String>(0))
        }

        @JvmStatic
        fun startPermissionActivity(activity: Activity,vararg permissions: String) {
            startPermissionActivity(activity, PUtils.asArrayList(permissions) as ArrayList<String>)
        }

        @JvmStatic
        fun startPermissionActivity(activity: Activity,vararg permissions: Array<String>) {
            startPermissionActivity(activity, PUtils.asArrayLists(permissions) as ArrayList<String>)
        }

        @JvmStatic
        fun startPermissionActivity(activity: Activity,permissions: List<String>) {
            startPermissionActivity(activity, permissions, REQUEST_CODE)
        }

        @JvmStatic
        fun startPermissionActivity(activity: Activity,permissions: List<String>, requestCode: Int) {
            val intent: Intent = PApi.getSmartPermissionIntent(activity, permissions)
            StartActivityManager.startActivityForResult(activity, intent, requestCode)
        }

        @JvmStatic
        fun startPermissionActivity(activity: Activity,permission: String,callback: OnPermissionPageCallback?) {
            startPermissionActivity(activity, PUtils.asArrayList(permission), callback)
        }

        @JvmStatic
        fun startPermissionActivity(activity: Activity,permissions: Array<String>,callback: OnPermissionPageCallback?) {
            startPermissionActivity(activity, PUtils.asArrayLists(permissions), callback)
        }

        @JvmStatic
        fun startPermissionActivity(activity: Activity,permissions: List<String>,callback: OnPermissionPageCallback?) {
            if (permissions.isEmpty()) {
                StartActivityManager.startActivity(
                    activity,
                    PIntentManager.getApplicationDetailsIntent(activity)
                )
                return
            }
            PermissionPageFragment.launch(activity, permissions, callback)
        }


         //android.app.Fragment
        @JvmStatic
        fun startPermissionActivity(fragment: android.app.Fragment) {
            startPermissionActivity(fragment, java.util.ArrayList<String>(0))
        }
        @JvmStatic
        fun startPermissionActivity(fragment: android.app.Fragment,vararg permissions: String) {
            startPermissionActivity(fragment, PUtils.asArrayList(permissions) as ArrayList<String>)
        }
        @JvmStatic
        fun startPermissionActivity(fragment: android.app.Fragment,vararg permissions: Array<String>) {
            startPermissionActivity(fragment, PUtils.asArrayLists(permissions) as ArrayList<String>)
        }
        @JvmStatic
        fun startPermissionActivity(fragment: android.app.Fragment,permissions: List<String>) {
            startPermissionActivity(
                fragment,
                permissions,
                REQUEST_CODE
            )
        }
        @JvmStatic
        fun startPermissionActivity(fragment: android.app.Fragment,permissions: List<String>,
            requestCode: Int
        ) {
            val activity = fragment.activity ?: return
            if (permissions.isEmpty()) {
                StartActivityManager.startActivity(
                    fragment,
                    PIntentManager.getApplicationDetailsIntent(activity)
                )
                return
            }
            val intent: Intent = PApi.getSmartPermissionIntent(activity, permissions)
            StartActivityManager.startActivityForResult(fragment, intent, requestCode)
        }
        @JvmStatic
        fun startPermissionActivity(fragment: android.app.Fragment,permission: String,callback: OnPermissionPageCallback?) {
            startPermissionActivity(fragment, PUtils.asArrayList(permission), callback)
        }
        @JvmStatic
        fun startPermissionActivity(fragment: android.app.Fragment,permissions: Array<String>, callback: OnPermissionPageCallback?) {
            startPermissionActivity(fragment, PUtils.asArrayLists(permissions), callback)
        }
        @JvmStatic
        fun startPermissionActivity(fragment: android.app.Fragment,permissions: List<String>, callback: OnPermissionPageCallback?) {
            val activity = fragment.activity
            if (activity == null || activity.isFinishing) {
                return
            }
            if (AVersion.isAndroid4_2() && activity.isDestroyed) {
                return
            }
            if (permissions.isEmpty()) {
                StartActivityManager.startActivity(
                    fragment,
                    PIntentManager.getApplicationDetailsIntent(activity)
                )
                return
            }
            PermissionPageFragment.launch(activity, permissions, callback)
        }
    }


    fun permission(vararg permissions: String): AskPermission{
        val list = permissions.toMutableList()
        return permission(list)
    }

    fun permission(permissions: MutableList<String>): AskPermission{
        PUtils.removeExitsPermission(mPermissions, permissions)
        if (permissions.isEmpty()){
            return this
        }
        mPermissions.addAll(permissions)
        return this
    }

    fun request(callback:OnPermissionCallback?){
        if (mInterceptor == null){
            mInterceptor = DefaultPermissionInterceptor(this.mContext)
        }
        val context = this.mContext
        val interceptor:OnPermissionInterceptor = mInterceptor!!
        val permissions = ArrayList(mPermissions)
        // 检查当前 Activity 状态是否是正常的，如果不是则不请求权限
        val activity:Activity? = PUtils.findActivity(context)
        if (!PermissionChecker.checkActivityStatus(activity)) {
            return
        }
        // 优化所申请的权限列表
        PermissionChecker.optimizeDeprecatedPermission(permissions)

        if (PApi.isGrantedPermissions(context, permissions)) {
            // 证明这些权限已经全部授予过，直接回调成功
            interceptor.grantedPermissionRequest(activity!!, permissions, permissions, true, callback)
            interceptor.finishPermissionRequest(activity, permissions, true, callback)
            return
        }
        // 申请没有授予过的权限
        interceptor.launchPermissionRequest(activity!!,permissions,callback)
    }

    fun interceptor(interceptor:OnPermissionInterceptor?): AskPermission{
        mInterceptor = interceptor
        return this
    }


}