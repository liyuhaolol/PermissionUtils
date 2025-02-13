package spa.lyh.cn.permissionutils.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings

object PIntentManager {
    /** 华为手机管家 App 包名 */
    private val EMUI_MOBILE_MANAGER_APP_PACKAGE_NAME = "com.huawei.systemmanager"

    /** OPPO 安全中心 App 包名 */
    private val COLOR_OS_SAFE_CENTER_APP_PACKAGE_NAME_1 = "com.oppo.safe"
    private val COLOR_OS_SAFE_CENTER_APP_PACKAGE_NAME_2 = "com.color.safecenter"
    private val COLOR_OS_SAFE_CENTER_APP_PACKAGE_NAME_3 = "com.oplus.safecenter"

    /** vivo 安全中心 App 包名 */
    private val ORIGIN_OS_MOBILE_MANAGER_APP_PACKAGE_NAME = "com.iqoo.secure"

    /** 小米手机管家 App 包名 */
    private val MIUI_MOBILE_MANAGER_APP_PACKAGE_NAME = "com.miui.securitycenter"

    fun getApplicationDetailsIntent(context:Context): Intent {
        return getApplicationDetailsIntent(context, null)
    }

    fun getApplicationDetailsIntent(context: Context, permissions: List<String>?): Intent {
        var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = PUtils.getPackageNameUri(context)
        }
        if (!permissions.isNullOrEmpty() && PRUtils.isColorOs()) {
            // OPPO 应用权限受阻跳转优化适配：https://open.oppomobile.com/new/developmentDoc/info?id=12983
            val bundle = Bundle().apply {
                // 元素为受阻权限的原生权限名字符串常量
                putStringArrayList(
                    "permissionList",
                    if (permissions is ArrayList) permissions else ArrayList(permissions)
                )
            }
            intent.putExtras(bundle)
            // 传入跳转优化标识
            intent.putExtra("isGetPermission", true)
        }
        if (PUtils.areActivityIntent(context, intent)) {
            return intent
        }

        intent = Intent(Settings.ACTION_APPLICATION_SETTINGS)
        if (PUtils.areActivityIntent(context, intent)) {
            return intent
        }

        intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
        if (PUtils.areActivityIntent(context, intent)) {
            return intent
        }

        return getAndroidSettingAppIntent()
    }

    fun getMiuiPermissionPageIntent(context:Context):Intent? {
        val appPermEditorActionIntent = Intent()
            .setAction("miui.intent.action.APP_PERM_EDITOR")
            .putExtra("extra_pkgname", context.getPackageName());

        val xiaoMiMobileManagerAppIntent = getXiaoMiMobileManagerAppIntent(context);

        var intent:Intent? = null
        if (PUtils.areActivityIntent(context, appPermEditorActionIntent)) {
            intent = appPermEditorActionIntent;
        }

        if (PUtils.areActivityIntent(context, xiaoMiMobileManagerAppIntent)) {
            intent = StartActivityManager.addSubIntentToMainIntent(intent, xiaoMiMobileManagerAppIntent);
        }

        return intent;
    }

    fun getXiaoMiMobileManagerAppIntent(context:Context):Intent? {
        val intent = context.packageManager.getLaunchIntentForPackage(MIUI_MOBILE_MANAGER_APP_PACKAGE_NAME);
        if (PUtils.areActivityIntent(context, intent)) {
            return intent
        }
        return null
    }

    /** 跳转到系统设置页面 */
    fun getAndroidSettingAppIntent(): Intent {
        return Intent(Settings.ACTION_SETTINGS);
    }

    fun getEmuiWindowPermissionPageIntent(context:Context): Intent? {
        // EMUI 发展史：http://www.360doc.com/content/19/1017/10/9113704_867381705.shtml
        // android 华为版本历史,一文看完华为EMUI发展史：https://blog.csdn.net/weixin_39959369/article/details/117351161

        val addViewMonitorActivityIntent = Intent();
        // emui 3.1 的适配（华为荣耀 7 Android 5.0、华为揽阅 M2 青春版 Android 5.1、华为畅享 5S Android 5.1）
        addViewMonitorActivityIntent.setClassName(EMUI_MOBILE_MANAGER_APP_PACKAGE_NAME, EMUI_MOBILE_MANAGER_APP_PACKAGE_NAME + ".addviewmonitor.AddViewMonitorActivity")

        val notificationManagementActivityIntent = Intent()
        // emui 3.0 的适配（华为麦芒 3S Android 4.4）
        notificationManagementActivityIntent.setClassName(EMUI_MOBILE_MANAGER_APP_PACKAGE_NAME, "com.huawei.notificationmanager.ui.NotificationManagmentActivity")

        // 华为手机管家主页
        val huaWeiMobileManagerAppIntent = getHuaWeiMobileManagerAppIntent(context)

        // 获取厂商版本号
        var romVersionName = PRUtils.getRomVersionName()
        if (romVersionName == null) {
            romVersionName = ""
        }

        var intent:Intent? = null;
        if (romVersionName.startsWith("3.0")) {
            // 3.0、3.0.1
            if (PUtils.areActivityIntent(context, notificationManagementActivityIntent)) {
                intent = notificationManagementActivityIntent;
            }

            if (PUtils.areActivityIntent(context, addViewMonitorActivityIntent)) {
                intent = StartActivityManager.addSubIntentToMainIntent(intent, addViewMonitorActivityIntent);
            }
        } else {
            // 3.1、其他的
            if (PUtils.areActivityIntent(context, addViewMonitorActivityIntent)) {
                intent = addViewMonitorActivityIntent;
            }

            if (PUtils.areActivityIntent(context, notificationManagementActivityIntent)) {
                intent = StartActivityManager.addSubIntentToMainIntent(intent, notificationManagementActivityIntent);
            }
        }

        if (PUtils.areActivityIntent(context, huaWeiMobileManagerAppIntent)) {
            intent = StartActivityManager.addSubIntentToMainIntent(intent, huaWeiMobileManagerAppIntent);
        }

        return intent
    }

    /**
     * 返回华为手机管家 App 意图
     */
    fun getHuaWeiMobileManagerAppIntent(context:Context): Intent? {
        val intent = context.packageManager.getLaunchIntentForPackage(EMUI_MOBILE_MANAGER_APP_PACKAGE_NAME);
        if (PUtils.areActivityIntent(context, intent)) {
            return intent
        }
        return null
    }

    fun getMiuiWindowPermissionPageIntent(context:Context):Intent? {
        return getMiuiPermissionPageIntent(context)
    }

    /**
     * 获取 oppo 悬浮窗权限设置意图
     */
    fun getColorOsWindowPermissionPageIntent(context:Context):Intent? {
        // com.color.safecenter 是之前 oppo 安全中心的包名，而 com.oppo.safe 是 oppo 后面改的安全中心的包名
        // 经过测试发现是在 ColorOs 2.1 的时候改的，Android 4.4 还是 com.color.safecenter，到了 Android 5.0 变成了 com.oppo.safe

        // java.lang.SecurityException: Permission Denial: starting Intent
        // { cmp=com.oppo.safe/.permission.floatwindow.FloatWindowListActivity (has extras) } from
        // ProcessRecord{839a7c5 10595:com.hjq.permissions.demo/u0a3781} (pid=10595, uid=13781) not exported from uid 1000
        // intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.floatwindow.FloatWindowListActivity");

        // java.lang.SecurityException: Permission Denial: starting Intent
        // { cmp=com.color.safecenter/.permission.floatwindow.FloatWindowListActivity (has extras) } from
        // ProcessRecord{42b660b0 31279:com.hjq.permissions.demo/u0a204} (pid=31279, uid=10204) not exported from uid 1000
        // intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.floatwindow.FloatWindowListActivity");

        // java.lang.SecurityException: Permission Denial: starting Intent
        // { cmp=com.color.safecenter/.permission.PermissionAppAllPermissionActivity (has extras) } from
        // ProcessRecord{42c49dd8 1791:com.hjq.permissions.demo/u0a204} (pid=1791, uid=10204) not exported from uid 1000
        // intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.PermissionAppAllPermissionActivity");

        // 虽然不能直接到达悬浮窗界面，但是到达它的上一级页面（权限隐私页面）还是可以的，所以做了简单的取舍
        // 测试机是 OPPO R7 Plus（Android 5.0，ColorOs 2.1）、OPPO R7s（Android 4.4，ColorOs 2.1）
        // com.oppo.safe.permission.PermissionTopActivity
        // com.oppo.safe..permission.PermissionAppListActivity
        // com.color.safecenter.permission.PermissionTopActivity
        val permissionTopActivityActionIntent = Intent("com.oppo.safe.permission.PermissionTopActivity")

        val oppoSafeCenterAppIntent = getOppoSafeCenterAppIntent(context)

        var intent:Intent? = null

        if (PUtils.areActivityIntent(context, permissionTopActivityActionIntent)) {
            intent = permissionTopActivityActionIntent;
        }

        if (PUtils.areActivityIntent(context, oppoSafeCenterAppIntent)) {
            intent = StartActivityManager.addSubIntentToMainIntent(intent, oppoSafeCenterAppIntent)
        }

        return intent;
    }

    fun getOppoSafeCenterAppIntent(context:Context):Intent? {
        var intent:Intent? = context.packageManager.getLaunchIntentForPackage(COLOR_OS_SAFE_CENTER_APP_PACKAGE_NAME_1);
        if (PUtils.areActivityIntent(context, intent)) {
            return intent;
        }
        intent = context.packageManager.getLaunchIntentForPackage(COLOR_OS_SAFE_CENTER_APP_PACKAGE_NAME_2);
        if (PUtils.areActivityIntent(context, intent)) {
            return intent;
        }
        intent = context.packageManager.getLaunchIntentForPackage(COLOR_OS_SAFE_CENTER_APP_PACKAGE_NAME_3);
        if (PUtils.areActivityIntent(context, intent)) {
            return intent
        }
        return null
    }

    /**
     * 获取 vivo 悬浮窗权限设置意图
     */
    fun getOriginOsWindowPermissionPageIntent(context:Context):Intent? {
        // java.lang.SecurityException: Permission Denial: starting Intent
        // { cmp=com.iqoo.secure/.ui.phoneoptimize.FloatWindowManager (has extras) } from
        // ProcessRecord{2c3023cf 21847:com.hjq.permissions.demo/u0a4633} (pid=21847, uid=14633) not exported from uid 10055
        // intent.setClassName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.FloatWindowManager");

        // java.lang.SecurityException: Permission Denial: starting Intent
        // { cmp=com.iqoo.secure/.safeguard.PurviewTabActivity (has extras) } from
        // ProcessRecord{2c3023cf 21847:com.hjq.permissions.demo/u0a4633} (pid=21847, uid=14633) not exported from uid 10055
        // intent.setClassName("com.iqoo.secure", "com.iqoo.secure.safeguard.PurviewTabActivity");

        // 经过测试在 vivo x7 Plus（Android 5.1）上面能跳转过去，但是显示却是一个空白页面
        // intent.setClassName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity");

        val intent = getVivoMobileManagerAppIntent(context);
        if (PUtils.areActivityIntent(context, intent)) {
            return intent
        }
        return null
    }

    /**
     * 获取 vivo 管家手机意图
     */
    fun getVivoMobileManagerAppIntent(context:Context):Intent? {
        val intent = context.packageManager.getLaunchIntentForPackage(ORIGIN_OS_MOBILE_MANAGER_APP_PACKAGE_NAME);
        if (PUtils.areActivityIntent(context, intent)) {
            return intent
        }
        return null
    }

    fun getOneUiWindowPermissionPageIntent(context:Context):Intent? {
        return getOneUiPermissionPageIntent(context);
    }

    /**
     * 获取三星权限设置意图
     */
   fun getOneUiPermissionPageIntent(context:Context):Intent? {
        val intent = Intent()
        intent.setClassName("com.android.settings", "com.android.settings.Settings\$AppOpsDetailsActivity")
        val extraShowFragmentArguments = Bundle()
        extraShowFragmentArguments.putString("package", context.packageName)
        intent.putExtra(":settings:show_fragment_args", extraShowFragmentArguments)
        intent.setData(PUtils.getPackageNameUri(context))
        if (PUtils.areActivityIntent(context, intent)) {
            return intent
        }
        return null
    }
}