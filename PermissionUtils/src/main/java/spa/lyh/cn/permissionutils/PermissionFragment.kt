package spa.lyh.cn.permissionutils

import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import spa.lyh.cn.permissionutils.utils.PUtils
import java.util.Random
import kotlin.math.pow

class PermissionFragment: Fragment(), Runnable {
    companion object{
        private val REQUEST_CODE_ARRAY = arrayListOf<Int>()
        private const val REQUEST_CODE = "request_code"
        private const val REQUEST_PERMISSIONS = "request_permissions"
        fun launch(activity:Activity,permissions: List<String>,interceptor:OnPermissionInterceptor,callback: OnPermissionCallback?){
            val fragment: PermissionFragment = PermissionFragment()
            val random = Random()
            var requestCode: Int
            do {
                // 新版本的 Support 库限制请求码必须小于 65536
                // 旧版本的 Support 库限制请求码必须小于 256
                requestCode = random.nextInt(2.0.pow(8.0).toInt())
            }while (REQUEST_CODE_ARRAY.contains(requestCode))
            // 标记这个请求码已经被占用
            REQUEST_CODE_ARRAY.add(requestCode)
            val bundle = Bundle()
            bundle.putInt(REQUEST_CODE,requestCode)
            if (permissions is ArrayList){
                bundle.putStringArrayList(REQUEST_PERMISSIONS,permissions)
            }else{
                bundle.putStringArrayList(REQUEST_PERMISSIONS, ArrayList(permissions))
            }
            fragment.setArguments(bundle)
            // 设置保留实例，不会因为屏幕方向或配置变化而重新创建
            fragment.setRetainInstance(true)
            // 设置权限申请标记
            fragment.setRequestFlag(true)
            // 设置权限回调监听
            fragment.setOnPermissionCallback(callback)
            // 设置权限请求拦截器
            fragment.setOnPermissionInterceptor(interceptor)
            // 绑定到 Activity 上面
            fragment.attachByActivity(activity)
        }
    }
    private var mRequestFlag: Boolean = false
    private var mCallBack:OnPermissionCallback? = null
    private lateinit var mInterceptor:OnPermissionInterceptor
    private var mScreenOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    fun setRequestFlag(flag: Boolean){
        mRequestFlag = flag
    }
    fun setOnPermissionCallback(callback:OnPermissionCallback?){
        mCallBack = callback
    }
    fun setOnPermissionInterceptor(interceptor:OnPermissionInterceptor){
        mInterceptor = interceptor
    }
    fun attachByActivity(activity: Activity){
        val fragmentManager = activity.fragmentManager
        if (fragmentManager == null){
            return
        }
        fragmentManager.beginTransaction().add(this,this.toString()).commitAllowingStateLoss()
    }

    fun detachByActivity(activity: Activity){
        val fragmentManager = activity.getFragmentManager();
        if (fragmentManager == null) {
            return
        }
        fragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
    }

    fun requestDangerousPermission(){}

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (activity == null){
            return
        }
        // 如果当前没有锁定屏幕方向就获取当前屏幕方向并进行锁定
        mScreenOrientation = activity.getRequestedOrientation()
        if (mScreenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            return
        }
        // 锁定当前 Activity 方向
        PUtils.lockActivityOrientation(activity);
    }

    override fun onDetach() {
        super.onDetach()
        if (activity == null || mScreenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED ||
            activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            return
        }
        // 为什么这里不用跟上面一样 try catch ？因为这里是把 Activity 方向取消固定，只有设置横屏或竖屏的时候才可能触发 crash
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
    }

    override fun onDestroy() {
        super.onDestroy()
        mCallBack = null
    }

    override fun onResume() {
        super.onResume()
        // 如果当前 Fragment 是通过系统重启应用触发的，则不进行权限申请
        if (!mRequestFlag) {
            detachByActivity(activity)
            return
        }
        requestDangerousPermission()
    }

    override fun run() {
        // 如果用户离开太久，会导致 Activity 被回收掉
        // 所以这里要判断当前 Fragment 是否有被添加到 Activity
        // 可在开发者模式中开启不保留活动来复现这个 Bug
        if (!isAdded) {
            return;
        }
        requestDangerousPermission()
    }
}