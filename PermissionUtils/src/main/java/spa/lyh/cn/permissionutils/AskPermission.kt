package spa.lyh.cn.permissionutils

import android.app.Activity
import androidx.fragment.app.Fragment
import android.content.Context
import android.text.TextUtils
import spa.lyh.cn.permissionutils.utils.PApi
import spa.lyh.cn.permissionutils.utils.PUtils
import spa.lyh.cn.permissionutils.utils.PermissionChecker

open class AskPermission private constructor(private val mContext:Context){
    private val mPermissions: ArrayList<String> = arrayListOf()
    private var mInterceptor:OnPermissionInterceptor? = null

    companion object{
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
            mInterceptor = DefaultPermissionInterceptor()
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
            interceptor.grantedPermissionRequest(activity!!, permissions, permissions, true, callback);
            interceptor.finishPermissionRequest(activity, permissions, true, callback);
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