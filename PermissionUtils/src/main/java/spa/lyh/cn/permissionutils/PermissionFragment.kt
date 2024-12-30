package spa.lyh.cn.permissionutils

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import android.util.Log
import java.util.Random

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
                requestCode = random.nextInt(Math.pow(2.0, 8.0) as Int)
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
    override fun run() {
        // 如果用户离开太久，会导致 Activity 被回收掉
        // 所以这里要判断当前 Fragment 是否有被添加到 Activity
        // 可在开发者模式中开启不保留活动来复现这个 Bug
        if (!isAdded) {
            return;
        }
        Log.e("qwer","run")
    }
}