package spa.lyh.cn.permissionutils.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.view.Display
import android.view.Surface

object PUtils {

    fun containsPermission(permissions:Collection<String>,permission: String): Boolean{
        if (permissions.isEmpty()){
            return false
        }
        for (p in permissions){
            if (equalsPermission(p,permission)){
                return true
            }
        }
        return false
    }

    fun equalsPermission(permission1: String,permission2: String): Boolean{
        val length = permission1.length
        if (length != permission2.length){
            return false
        }
        for (i in length - 1 downTo 0) {
            if (permission1[i] != permission2[i]) {
                return false
            }
        }
        return true
    }

    fun findActivity(context:Context?):Activity?{
        var ctx = context
        while (ctx != null) {
            if (ctx is Activity) {
                return ctx
            } else if (ctx is ContextWrapper) {
                ctx = ctx.baseContext
            } else {
                return null
            }
        }
        return null
    }
    /** * 锁定当前 Activity 的方向 */
    @SuppressLint("SwitchIntDef")
    fun lockActivityOrientation(activity: Activity) {
        try {
            // 兼容问题：在 Android 8.0 的手机上可以固定 Activity 的方向，但是这个 Activity 不能是透明的，否则就会抛出异常
            // 复现场景：只需要给 Activity 主题设置 <item name="android:windowIsTranslucent">true</item> 属性即可
            when (activity.resources.configuration.orientation){
                Configuration.ORIENTATION_LANDSCAPE -> {
                    activity.requestedOrientation = if (isActivityReverse(activity)) {
                        ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                    }else {
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }
                }
                Configuration.ORIENTATION_PORTRAIT -> {
                    activity.requestedOrientation = if (isActivityReverse(activity)) {
                        ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                    }else{
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                }
            }
        }catch (e: IllegalStateException) {
            // java.lang.IllegalStateException: Only fullscreen activities can request orientation
            e.printStackTrace()
        }
    }

    /**
     * 判断 Activity 是否反方向旋转了
     */
    fun isActivityReverse(activity: Activity): Boolean {
        val display: Display? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.display
        } else {
            val windowManager = activity.windowManager
            windowManager?.defaultDisplay
        }

        if (display == null) {
            return false
        }

        // 获取 Activity 旋转的角度
        val activityRotation = display.rotation
        return when (activityRotation) {
            Surface.ROTATION_180, Surface.ROTATION_270 -> true
            Surface.ROTATION_0, Surface.ROTATION_90 -> false
            else -> false
        }
    }

    fun <T> asArrayList(vararg array: T): ArrayList<T> {
        val initialCapacity = array.size
        val list = ArrayList<T>(initialCapacity)
        array.forEach { t ->
            t?.let { list.add(it) }
        }
        return list
    }

    /**
     * 判断这个意图的 Activity 是否存在
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    fun areActivityIntent(context:Context, intent:Intent?): Boolean {
        if (intent == null) {
            return false
        }
        // 这里为什么不用 Intent.resolveActivity(intent) != null 来判断呢？
        // 这是因为在 OPPO R7 Plus （Android 5.0）会出现误判，明明没有这个 Activity，却返回了 ComponentName 对象
        val packageManager = context.packageManager;
        if (AVersion.isAndroid13()) {
            return !packageManager.queryIntentActivities(intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())).isEmpty();
        }
        return !packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty();
    }

    /**
     * 获取包名 uri
     */
    fun getPackageNameUri(context:Context): Uri {
        return Uri.parse("package:" + context.packageName);
    }
}