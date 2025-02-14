package spa.lyh.cn.permissionutils.utils.pd.impl

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.annotation.RequiresApi
import spa.lyh.cn.permissionutils.ManifestPro
import spa.lyh.cn.permissionutils.utils.AVersion
import spa.lyh.cn.permissionutils.utils.PUtils

open class PermissionDelegateImplV31 : PermissionDelegateImplV30(){

    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.SCHEDULE_EXACT_ALARM)) {
            if (!AVersion.isAndroid12()) {
                return true
            }
            return isGrantedAlarmPermission(context)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.BLUETOOTH_SCAN)) {
            if (!AVersion.isAndroid6()) {
                return true
            }
            if (!AVersion.isAndroid12()) {
                return PUtils.checkSelfPermission(context, ManifestPro.permission.ACCESS_FINE_LOCATION);
            }
            return PUtils.checkSelfPermission(context, permission)
        }

        if (PUtils.containsPermission(arrayListOf(
                ManifestPro.permission.BLUETOOTH_CONNECT,
                ManifestPro.permission.BLUETOOTH_ADVERTISE), permission)) {
            if (!AVersion.isAndroid12()) {
                return true
            }
            return PUtils.checkSelfPermission(context, permission)
        }
        return super.isGrantedPermission(context, permission)
    }

    override fun isDoNotAskAgainPermission(activity: Activity, permission: String): Boolean {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.SCHEDULE_EXACT_ALARM)) {
            return false;
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.BLUETOOTH_SCAN)) {
            if (!AVersion.isAndroid6()) {
                return false
            }
            if (!AVersion.isAndroid12()) {
                return !PUtils.checkSelfPermission(activity, ManifestPro.permission.ACCESS_FINE_LOCATION) &&
                        !PUtils.shouldShowRequestPermissionRationale(activity, ManifestPro.permission.ACCESS_FINE_LOCATION)
            }
            return !PUtils.checkSelfPermission(activity, permission) &&
                    !PUtils.shouldShowRequestPermissionRationale(activity, permission)
        }

        if (PUtils.containsPermission(arrayListOf(
                ManifestPro.permission.BLUETOOTH_CONNECT,
                ManifestPro.permission.BLUETOOTH_ADVERTISE), permission)) {
            if (!AVersion.isAndroid12()) {
                return false
            }
            return !PUtils.checkSelfPermission(activity, permission) &&
                    !PUtils.shouldShowRequestPermissionRationale(activity, permission)
        }

        if (PUtils.equalsPermission(permission, ManifestPro.permission.ACCESS_BACKGROUND_LOCATION) &&
            AVersion.isAndroid6() && AVersion.getTargetSdkVersionCode(activity) >= AVersion.ANDROID_12) {

            if (!PUtils.checkSelfPermission(activity, ManifestPro.permission.ACCESS_FINE_LOCATION) &&
                !PUtils.checkSelfPermission(activity, ManifestPro.permission.ACCESS_COARSE_LOCATION)) {
                return !PUtils.shouldShowRequestPermissionRationale(activity, ManifestPro.permission.ACCESS_FINE_LOCATION) &&
                        !PUtils.shouldShowRequestPermissionRationale(activity, ManifestPro.permission.ACCESS_COARSE_LOCATION)
            }

            return !PUtils.checkSelfPermission(activity, permission) &&
                    !PUtils.shouldShowRequestPermissionRationale(activity, permission)
        }
        return super.isDoNotAskAgainPermission(activity, permission)
    }

    override fun getPermissionSettingIntent(context: Context, permission: String): Intent {
        if (PUtils.equalsPermission(permission, ManifestPro.permission.SCHEDULE_EXACT_ALARM)) {
            if (!AVersion.isAndroid12()) {
                return getApplicationDetailsIntent(context)
            }
            return getAlarmPermissionIntent(context)
        }
        return super.getPermissionSettingIntent(context, permission)
    }


    /**
     * 是否有闹钟权限
     */
    @RequiresApi(AVersion.ANDROID_12)
    private fun isGrantedAlarmPermission(context:Context): Boolean {
        return context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()
    }

    /**
     * 获取闹钟权限设置界面意图
     */
    @RequiresApi(AVersion.ANDROID_12)
    private fun getAlarmPermissionIntent(context:Context): Intent {
        var intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        intent.setData(PUtils.getPackageNameUri(context))
        if (!PUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context)
        }
        return intent
    }
}