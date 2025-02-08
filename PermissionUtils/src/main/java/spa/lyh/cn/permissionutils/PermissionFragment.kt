package spa.lyh.cn.permissionutils

import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import spa.lyh.cn.permissionutils.utils.PApi
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
    private var mInterceptor:OnPermissionInterceptor? = null
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

    fun requestDangerousPermission(){
        if (activity == null || arguments == null){
            return
        }
        val requestCode = arguments.getInt(REQUEST_CODE)
        val allPermissions = arguments.getStringArrayList(REQUEST_PERMISSIONS)

        if (allPermissions == null || allPermissions.isEmpty) {
            return
        }
        // Android 13 传感器策略发生改变，申请后台传感器权限的前提是要有前台传感器权限
        // Android 10 定位策略发生改变，申请后台定位权限的前提是要有前台定位权限（授予了精确或者模糊任一权限）
        // 必须要有文件读取权限才能申请获取媒体位置权限
        // 发起权限请求
        requestPermissions(allPermissions.toTypedArray(), requestCode);
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (activity == null || arguments == null || mInterceptor == null || requestCode != arguments.getInt(REQUEST_CODE)) {
            return
        }
        val callback: OnPermissionCallback? = mCallBack
        // 释放监听对象的引用
        mCallBack = null
        val interceptor:OnPermissionInterceptor? = mInterceptor
        // 释放拦截器对象的引用
        mInterceptor = null
        // 释放对这个请求码的占用
        REQUEST_CODE_ARRAY.remove(requestCode)

        if (permissions.isEmpty() || grantResults.isEmpty()) {
            return
        }
        //这里是重新检查权限，范例由于历史原因过于复杂，暂时不管
/*        permissions.forEachIndexed { index,item ->
            grantResults[index] = if (PApi.recheckPermissionResult(activity, item.toString(), grantResults[index] == PackageManager.PERMISSION_GRANTED)){
                PackageManager.PERMISSION_GRANTED
            }else{
                PackageManager.PERMISSION_DENIED
            }
        }*/
        // 将数组转换成 ArrayList
        val allPermissions: ArrayList<String> = PUtils.asArrayList(*permissions.filterNotNull().toTypedArray())
        // 将 Fragment 从 Activity 移除
        detachByActivity(activity)

        // 获取已授予的权限
        val grantedPermissions: ArrayList<String> = PApi.getGrantedPermissions(allPermissions,grantResults)

        // 如果请求成功的权限集合大小和请求的数组一样大时证明权限已经全部授予
        if (grantedPermissions.size == allPermissions.size) {
            // 代表申请的所有的权限都授予了
            interceptor?.grantedPermissionRequest(activity, allPermissions, grantedPermissions, true, callback);
            // 权限申请结束
            interceptor?.finishPermissionRequest(activity, allPermissions, false, callback)
            return
        }
        // 获取被拒绝的权限
        val deniedPermissions = PApi.getDeniedPermissions(allPermissions, grantResults)

        // 代表申请的权限中有不同意授予的，如果有某个权限被永久拒绝就返回 true 给开发人员，让开发者引导用户去设置界面开启权限
        interceptor?.deniedPermissionRequest(
            activity,
            allPermissions,
            deniedPermissions,
            PApi.isDoNotAskAgainPermissions(activity, deniedPermissions),
            callback
        )

        // 证明还有一部分权限被成功授予，回调成功接口
        if (grantedPermissions.isNotEmpty()) {
            interceptor?.grantedPermissionRequest(activity, allPermissions, grantedPermissions, false, callback)
        }

        // 权限申请结束
        interceptor?.finishPermissionRequest(activity, allPermissions, false, callback)
    }

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