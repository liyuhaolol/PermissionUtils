package spa.lyh.cn.permissionutils

import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import spa.lyh.cn.permissionutils.utils.AVersion
import spa.lyh.cn.permissionutils.utils.PApi
import spa.lyh.cn.permissionutils.utils.PUtils
import spa.lyh.cn.permissionutils.utils.StartActivityManager
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
    private var mSpecialRequest: Boolean = false
    private var mDangerousRequest: Boolean = false
    private var mRequestFlag: Boolean = false
    private var mCallBack:OnPermissionCallback? = null
    private var mInterceptor:OnPermissionInterceptor? = null
    private var mScreenOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

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

    fun setOnPermissionCallback(callback:OnPermissionCallback?){
        mCallBack = callback
    }

    fun setRequestFlag(flag: Boolean){
        mRequestFlag = flag
    }

    fun setOnPermissionInterceptor(interceptor:OnPermissionInterceptor){
        mInterceptor = interceptor
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
        // 取消引用监听器，避免内存泄漏
        mCallBack = null
    }

    override fun onResume() {
        super.onResume()
        // 如果当前 Fragment 是通过系统重启应用触发的，则不进行权限申请
        if (!mRequestFlag) {
            detachByActivity(activity)
            return
        }

        // 如果在 Activity 不可见的状态下添加 Fragment 并且去申请权限会导致授权对话框显示不出来
        // 所以必须要在 Fragment 的 onResume 来申请权限，这样就可以保证应用回到前台的时候才去申请权限
        if (mSpecialRequest) {
            return
        }

        mSpecialRequest = true
        requestSpecialPermission()
    }

    /**
     * 申请特殊权限
     */
    fun requestSpecialPermission() {
        if (arguments == null || activity == null) {
            return;
        }

        val allPermissions = arguments.getStringArrayList(REQUEST_PERMISSIONS)
        if (allPermissions == null || allPermissions.isEmpty) {
            return
        }

        // 是否需要申请特殊权限
        var requestSpecialPermission = false

        // 使用 for 循环从最后一个元素开始遍历数组
        // Github issue：https://github.com/getActivity/XXPermissions/issues/292
        for (permission in allPermissions.reversed()){
            if (!PApi.isSpecialPermission(permission)) {
                continue
            }
            if (PApi.isGrantedPermission(activity, permission)) {
                // 已经授予过了，可以跳过
                continue
            }
            if (!AVersion.isAndroid11() && PUtils.equalsPermission(permission, ManifestPro.permission.MANAGE_EXTERNAL_STORAGE)) {
                // 当前必须是 Android 11 及以上版本，因为在旧版本上是拿旧权限做的判断
                continue
            }
            // 跳转到特殊权限授权页面
            StartActivityManager.startActivityForResult(this, PApi.getSmartPermissionIntent(activity,
                PUtils.asArrayList(permission)), getArguments().getInt(REQUEST_CODE))
            requestSpecialPermission = true
        }

        if (requestSpecialPermission) {
            return
        }
        // 如果没有跳转到特殊权限授权页面，就直接申请危险权限
        requestDangerousPermission()
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
        if (!AVersion.isAndroid6()) {
            // 如果是 Android 6.0 以下，没有危险权限的概念，则直接回调监听
            val grantResults = IntArray(allPermissions.size) { i ->
                if (PApi.isGrantedPermission(activity, allPermissions[i])) {
                    PackageManager.PERMISSION_GRANTED
                } else {
                    PackageManager.PERMISSION_DENIED
                }
            }
            onRequestPermissionsResult(requestCode, allPermissions.toTypedArray(), grantResults)
            return
        }
        // Android 13 传感器策略发生改变，申请后台传感器权限的前提是要有前台传感器权限
        if (AVersion.isAndroid13()
            && allPermissions.size >= 2
            && PUtils.containsPermission(allPermissions, ManifestPro.permission.BODY_SENSORS_BACKGROUND)) {
            val bodySensorsPermission = ArrayList(allPermissions)
            bodySensorsPermission.remove(ManifestPro.permission.BODY_SENSORS_BACKGROUND);

            // 在 Android 13 的机型上，需要先申请前台传感器权限，再申请后台传感器权限
            splitTwiceRequestPermission(activity, allPermissions, bodySensorsPermission, requestCode)
            return
        }
        // Android 10 定位策略发生改变，申请后台定位权限的前提是要有前台定位权限（授予了精确或者模糊任一权限）
        if (AVersion.isAndroid10()
            && allPermissions.size >= 2
            && PUtils.containsPermission(allPermissions, ManifestPro.permission.ACCESS_BACKGROUND_LOCATION)) {
            val locationPermission = ArrayList(allPermissions)
            locationPermission.remove(ManifestPro.permission.ACCESS_BACKGROUND_LOCATION)

            // 在 Android 10 的机型上，需要先申请前台定位权限，再申请后台定位权限
            splitTwiceRequestPermission(activity, allPermissions, locationPermission, requestCode)
            return
        }
        // 必须要有文件读取权限才能申请获取媒体位置权限
        if (AVersion.isAndroid10()
            && PUtils.containsPermission(allPermissions, ManifestPro.permission.ACCESS_MEDIA_LOCATION)
            && PUtils.containsPermission(allPermissions, ManifestPro.permission.READ_EXTERNAL_STORAGE)) {

            val storagePermission = ArrayList(allPermissions);
            storagePermission.remove(ManifestPro.permission.ACCESS_MEDIA_LOCATION)

            // 在 Android 10 的机型上，需要先申请存储权限，再申请获取媒体位置权限
            splitTwiceRequestPermission(activity, allPermissions, storagePermission, requestCode)
            return
        }
        // 发起权限请求
        requestPermissions(allPermissions.toTypedArray(), requestCode)
    }

    fun splitTwiceRequestPermission(activity: Activity, allPermissions: List<String>, firstPermissions: List<String>, requestCode: Int) {
        val secondPermissions = ArrayList(allPermissions)
        firstPermissions.forEach { permission ->
            secondPermissions.remove(permission)
        }

        PermissionFragment.launch(activity, firstPermissions, object : OnPermissionInterceptor {}, object : OnPermissionCallback {
            override fun onGranted(permissions: List<String>, allGranted: Boolean) {
                if (!allGranted || !isAdded) {
                    return
                }

                val delayMillis = if (AVersion.isAndroid13()) 150L else 0L
                PUtils.postDelayed({
                    PermissionFragment.launch(activity, secondPermissions, object : OnPermissionInterceptor {}, object : OnPermissionCallback {
                        override fun onGranted(permissions: List<String>, allGranted: Boolean) {
                            if (!allGranted || !isAdded) {
                                return
                            }
                            // 所有的权限都授予了
                            val grantResults = IntArray(allPermissions.size) { PackageManager.PERMISSION_GRANTED }
                            onRequestPermissionsResult(requestCode, allPermissions.toTypedArray(), grantResults)
                        }

                        override fun onDenied(permissions: List<String>, doNotAskAgain: Boolean) {
                            if (!isAdded) {
                                return
                            }
                            // 第二次申请的权限失败了，但是第一次申请的权限已经授予了
                            val grantResults = IntArray(allPermissions.size) { i ->
                                if (PUtils.containsPermission(secondPermissions, allPermissions[i])) {
                                    PackageManager.PERMISSION_DENIED
                                } else {
                                    PackageManager.PERMISSION_GRANTED
                                }
                            }
                            onRequestPermissionsResult(requestCode, allPermissions.toTypedArray(), grantResults)
                        }
                    })
                }, delayMillis)
            }

            override fun onDenied(permissions: List<String>, doNotAskAgain: Boolean) {
                if (!isAdded) {
                    return
                }
                // 第一次申请的权限失败了，没有必要进行第二次申请
                val grantResults = IntArray(allPermissions.size) { PackageManager.PERMISSION_DENIED }
                onRequestPermissionsResult(requestCode, allPermissions.toTypedArray(), grantResults)
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String?>, grantResults: IntArray) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        //这里是重新检查权限
        permissions.forEachIndexed { index,item ->
            grantResults[index] = if (PApi.recheckPermissionResult(activity, item.toString(), grantResults[index] == PackageManager.PERMISSION_GRANTED)){
                PackageManager.PERMISSION_GRANTED
            }else{
                PackageManager.PERMISSION_DENIED
            }
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)
        if (activity == null || arguments == null || mDangerousRequest ||
            requestCode != arguments.getInt(REQUEST_CODE)) {
            return
        }

        val allPermissions = arguments.getStringArrayList(REQUEST_PERMISSIONS)
        if (allPermissions == null || allPermissions.isEmpty) {
            return
        }

        mDangerousRequest = true
        PUtils.postActivityResult(allPermissions, this)
    }

    override fun run() {
        // 如果用户离开太久，会导致 Activity 被回收掉
        // 所以这里要判断当前 Fragment 是否有被添加到 Activity
        // 可在开发者模式中开启不保留活动来复现这个 Bug
        if (!isAdded) {
            return
        }
        requestDangerousPermission()
    }
}