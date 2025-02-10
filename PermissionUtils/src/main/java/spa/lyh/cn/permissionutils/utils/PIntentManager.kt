package spa.lyh.cn.permissionutils.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings

object PIntentManager {

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

    /** 跳转到系统设置页面 */
    fun getAndroidSettingAppIntent(): Intent {
        return Intent(Settings.ACTION_SETTINGS);
    }
}