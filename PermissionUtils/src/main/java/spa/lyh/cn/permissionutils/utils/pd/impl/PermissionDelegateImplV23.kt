package spa.lyh.cn.permissionutils.utils.pd.impl

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.RequiresApi
import spa.lyh.cn.permissionutils.ManifestPro
import spa.lyh.cn.permissionutils.utils.AVersion
import spa.lyh.cn.permissionutils.utils.GetInstalledAppsPermissionCompat
import spa.lyh.cn.permissionutils.utils.PHelper
import spa.lyh.cn.permissionutils.utils.PRUtils
import spa.lyh.cn.permissionutils.utils.PUtils
import spa.lyh.cn.permissionutils.utils.WindowPermissionCompat

open class PermissionDelegateImplV23: PermissionDelegateImplV21() {
    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        if (!PHelper.isSpecialPermission(permission)) {
            // 读取应用列表权限是比较特殊的危险权限，它和其他危险权限的判断方式不太一样，所以需要放在这里来判断
            if (PUtils.equalsPermission(permission, ManifestPro.permission.GET_INSTALLED_APPS)) {
                return GetInstalledAppsPermissionCompat.isGrantedPermission(context)
            }

            if (!AVersion.isAndroid6()) {
                return true
            }
            return PUtils.checkSelfPermission(context, permission);
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.SYSTEM_ALERT_WINDOW)) {
            return WindowPermissionCompat.isGrantedPermission(context)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.WRITE_SETTINGS)) {
            if (!AVersion.isAndroid6()) {
                return true
            }
            return isGrantedSettingPermission(context)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.ACCESS_NOTIFICATION_POLICY)) {
            if (!AVersion.isAndroid6()) {
                return true
            }
            return isGrantedNotDisturbPermission(context)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)) {
            if (!AVersion.isAndroid6()) {
                return true
            }
            return isGrantedIgnoreBatteryPermission(context)
        }
        return super.isGrantedPermission(context, permission)
    }

    override fun isDoNotAskAgainPermission(activity: Activity, permission: String): Boolean {
        if (!PHelper.isSpecialPermission(permission)) {
            // 读取应用列表权限是比较特殊的危险权限，它和其他危险权限的判断方式不太一样，所以需要放在这里来判断
            if (PUtils.equalsPermission(permission, ManifestPro.permission.GET_INSTALLED_APPS)) {
                return GetInstalledAppsPermissionCompat.isDoNotAskAgainPermission(activity)
            }

            if (!AVersion.isAndroid6()) {
                return false
            }
            return !PUtils.checkSelfPermission(activity, permission) &&
                    !PUtils.shouldShowRequestPermissionRationale(activity, permission)
        }

        if (PUtils.containsPermission(arrayListOf(
                    ManifestPro.permission.SYSTEM_ALERT_WINDOW,
                    ManifestPro.permission.WRITE_SETTINGS,
                    ManifestPro.permission.ACCESS_NOTIFICATION_POLICY,
                    ManifestPro.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS),
                permission)) {
            return false
        }
        return super.isDoNotAskAgainPermission(activity, permission)
    }

    override fun recheckPermissionResult(context: Context, permission: String, grantResult: Boolean): Boolean {
        // 如果是读取应用列表权限（国产权限），则需要重新检查权限的状态
        if (PUtils.equalsPermission(permission, ManifestPro.permission.GET_INSTALLED_APPS)) {
            return isGrantedPermission(context, permission)
        }
        return super.recheckPermissionResult(context, permission, grantResult)
    }

    override fun getPermissionSettingIntent(context: Context, permission: String): Intent {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.GET_INSTALLED_APPS)) {
            return GetInstalledAppsPermissionCompat.getPermissionIntent(context)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.SYSTEM_ALERT_WINDOW)) {
            return WindowPermissionCompat.getPermissionIntent(context)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.WRITE_SETTINGS)) {
            if (!AVersion.isAndroid6()) {
                return getApplicationDetailsIntent(context)
            }
            return getSettingPermissionIntent(context)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.ACCESS_NOTIFICATION_POLICY)) {
            if (!AVersion.isAndroid6()) {
                return getApplicationDetailsIntent(context)
            }
            return getNotDisturbPermissionIntent(context)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)) {
            if (!AVersion.isAndroid6()) {
                return getApplicationDetailsIntent(context)
            }
            return getIgnoreBatteryPermissionIntent(context)
        }
        return super.getPermissionSettingIntent(context, permission)
    }



    /**
     * 是否有系统设置权限
     */
    @RequiresApi(AVersion.ANDROID_6)
    private fun isGrantedSettingPermission(context:Context):Boolean {
        return Settings.System.canWrite(context)
    }

    /**
     * 是否有勿扰模式权限
     */
    @RequiresApi(AVersion.ANDROID_6)
    private fun isGrantedNotDisturbPermission(context:Context):Boolean {
        return context.getSystemService(NotificationManager::class.java).isNotificationPolicyAccessGranted()
    }

    /**
     * 是否忽略电池优化选项
     */
    @RequiresApi(AVersion.ANDROID_6)
    private fun isGrantedIgnoreBatteryPermission(context:Context):Boolean {
        return context.getSystemService(PowerManager::class.java).isIgnoringBatteryOptimizations(context.packageName)
    }

    /**
     * 获取系统设置权限界面意图
     */
    @RequiresApi(AVersion.ANDROID_6)
    private fun getSettingPermissionIntent(context:Context):Intent {
        var intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.setData(PUtils.getPackageNameUri(context))
        if (!PUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context)
        }
        return intent
    }

    /**
     * 获取勿扰模式设置界面意图
     */
    @RequiresApi(AVersion.ANDROID_6)
    private fun getNotDisturbPermissionIntent(context:Context):Intent {
        var intent:Intent
        if (AVersion.isAndroid10()) {
            // android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS
            intent = Intent("android.settings.NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS")
            intent.setData(PUtils.getPackageNameUri(context))

            // issue 地址：https://github.com/getActivity/XXPermissions/issues/190
            // 这里解释一下，为什么要排除鸿蒙系统，因为用代码能检测到有这个 Intent，也能跳转过去，但是会被马上拒绝
            // 测试过了其他厂商系统及 Android 原生系统都没有这个问题，就只有鸿蒙有这个问题
            // 只因为这个 Intent 是隐藏的意图，所以就不让用，鸿蒙 2.0 和 3.0 都有这个问题
            // 别问鸿蒙 1.0 有没有问题，问就是鸿蒙一发布就 2.0 了，1.0 版本都没有问世过
            // ------------------------ 我是一条华丽的分割线 ----------------------------
            // issue 地址：https://github.com/getActivity/XXPermissions/issues/233
            // 经过测试，荣耀下面这些机子都会出现加包名跳转不过去的问题
            // 荣耀 magic4 Android 13  MagicOs 7.0
            // 荣耀 80 Pro Android 12  MagicOs 7.0
            // 荣耀 X20 SE Android 11  MagicOs 4.1
            // 荣耀 Play5 Android 10  MagicOs 4.0
            if (PRUtils.isHarmonyOs() || PRUtils.isMagicOs()) {
                intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            }
        } else {
            intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        }

        if (!PUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context)
        }
        return intent
    }

    /**
     * 获取电池优化选项设置界面意图
     */
    @RequiresApi(AVersion.ANDROID_6)
    private fun getIgnoreBatteryPermissionIntent(context:Context):Intent {
        var intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(PUtils.getPackageNameUri(context))

        if (!PUtils.areActivityIntent(context, intent)) {
            intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        }

        if (!PUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context)
        }
        return intent
    }
}