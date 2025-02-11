package spa.lyh.cn.permissionutils.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.view.Display
import android.view.Surface
import androidx.annotation.RequiresApi
import org.xmlpull.v1.XmlPullParserException
import spa.lyh.cn.permissionutils.model.AndroidManifestInfo
import spa.lyh.cn.permissionutils.model.java.JC
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

object PUtils {

    @RequiresApi(AVersion.ANDROID_6)
    fun checkSelfPermission(context:Context, permission:String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

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

    fun getAndroidManifestInfo(context: Context): AndroidManifestInfo? {
        val apkPathCookie = PUtils.findApkPathCookie(context, context.applicationInfo.sourceDir)
        // 如果 cookie 为 0，证明获取失败
        if (apkPathCookie == 0) {
            return null
        }

        var androidManifestInfo: AndroidManifestInfo? = null
        try {
            androidManifestInfo = AndroidManifestParser.parseAndroidManifest(context, apkPathCookie)
            // 如果读取到的包名和当前应用的包名不是同一个的话，证明这个清单文件的内容不是当前应用的
            // 具体案例：https://github.com/getActivity/XXPermissions/issues/102
            if (!TextUtils.equals(context.packageName, androidManifestInfo?.packageName)) {
                return null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        }

        return androidManifestInfo
    }

    /**
     * 获取当前应用 Apk 在 AssetManager 中的 Cookie，如果获取失败，则为 0
     */
    @Suppress("JavaReflectionMemberAccess")
    @SuppressLint("PrivateApi")
    fun findApkPathCookie(context: Context, apkPath: String): Int {
        val assets: AssetManager = context.assets
        var cookie: Int = 0

        try {
            if (AVersion.getTargetSdkVersionCode(context) >= AVersion.ANDROID_9 &&
                AVersion.getAndroidVersionCode() >= AVersion.ANDROID_9 &&
                AVersion.getAndroidVersionCode() < AVersion.ANDROID_11) {
                val metaGetDeclaredMethod = Class::class.java.getDeclaredMethod("getDeclaredMethod", String::class.java,JC.getArrayClass())
                metaGetDeclaredMethod.isAccessible = true
                val findCookieForPathMethod = metaGetDeclaredMethod.invoke(AssetManager::class.java, "findCookieForPath", arrayOf(String::class.java)) as Method
                if (findCookieForPathMethod != null) {
                    findCookieForPathMethod.isAccessible = true
                    cookie = findCookieForPathMethod.invoke(context.assets, apkPath) as Int
                    if (cookie != null) {
                        return cookie
                    }
                }
            }

            val addAssetPathMethod = assets.javaClass.getDeclaredMethod("addAssetPath", String::class.java)
            cookie = addAssetPathMethod.invoke(assets, apkPath) as Int
            if (cookie != null) {
                return cookie
            }
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

        // 获取失败直接返回 0
        // 为什么不直接返回 Integer，而是返回 int 类型？
        // 去看看 AssetManager.findCookieForPath 获取失败会返回什么就知道了
        return 0
    }

    @RequiresApi(AVersion.ANDROID_4_4)
    fun checkOpNoThrow(context: Context, opFieldName: String, opDefaultValue: Int): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val appInfo = context.applicationInfo
        val pkg = context.applicationContext.packageName
        val uid = appInfo.uid

        try {
            val appOpsClass = Class.forName(AppOpsManager::class.java.name)
            val opValue: Int
            opValue = try {
                val opValueField = appOpsClass.getDeclaredField(opFieldName)
                opValueField.get(Int::class.java) as Int
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
                opDefaultValue
            }

            val checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Int::class.java, Int::class.java, String::class.java)
            return (checkOpNoThrowMethod.invoke(appOps, opValue, uid, pkg) as Int) == AppOpsManager.MODE_ALLOWED
        } catch (e: ClassNotFoundException) {
            return true
        } catch (e: NoSuchMethodException) {
            return true
        } catch (e: InvocationTargetException) {
            return true
        } catch (e: IllegalAccessException) {
            return true
        } catch (e: RuntimeException) {
            return true
        }
    }

    @RequiresApi(AVersion.ANDROID_4_4)
    fun checkOpNoThrow(context:Context, opName:String):Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        var mode = 0
        if (AVersion.isAndroid10()) {
            mode = appOps.unsafeCheckOpNoThrow(opName, context.applicationInfo.uid, context.packageName);
        } else {
            mode = appOps.checkOpNoThrow(opName, context.applicationInfo.uid, context.packageName);
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    /**
     * 解决 Android 12 调用 shouldShowRequestPermissionRationale 出现内存泄漏的问题
     * Android 12L 和 Android 13 版本经过测试不会出现这个问题，证明 Google 在新版本上已经修复了这个问题
     * 但是对于 Android 12 仍是一个历史遗留问题，这是我们所有 Android App 开发者不得不面对的一个事情
     *
     * issues 地址：https://github.com/getActivity/XXPermissions/issues/133
     */
    @RequiresApi(api = AVersion.ANDROID_6)
    fun shouldShowRequestPermissionRationale(activity: Activity, permission: String): Boolean {
        if (AVersion.getAndroidVersionCode() == AVersion.ANDROID_12) {
            try {
                val packageManager: PackageManager = activity.application.packageManager
                val method: Method = PackageManager::class.java.getMethod("shouldShowRequestPermissionRationale", String::class.java)
                return method.invoke(packageManager, permission) as Boolean
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        return activity.shouldShowRequestPermissionRationale(permission)
    }
}