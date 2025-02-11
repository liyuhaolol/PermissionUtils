package spa.lyh.cn.permissionutils.model

import android.content.pm.PackageInfo
import spa.lyh.cn.permissionutils.utils.AVersion

class AndroidManifestInfo {
    /** 应用包名 */
    var packageName: String = ""

    /** 使用 sdk 信息 */
    var usesSdkInfo: UsesSdkInfo? = null

    /** 权限节点信息 */
    val permissionInfoList: MutableList<PermissionInfo> = ArrayList()

    /** Application 节点信息 */
    var applicationInfo: ApplicationInfo? = null

    /** Activity 节点信息 */
    val activityInfoList: MutableList<ActivityInfo> = ArrayList()

    /** Service 节点信息 */
    val serviceInfoList: MutableList<ServiceInfo> = ArrayList()

    class UsesSdkInfo {
        /** 最小安装版本要求 **/
        var minSdkVersion: Int = 0
    }

    class PermissionInfo {
        /** 不需要请求地理位置标志 */
        private val REQUESTED_PERMISSION_NEVER_FOR_LOCATION = if (AVersion.isAndroid12()) {
            PackageInfo.REQUESTED_PERMISSION_NEVER_FOR_LOCATION
        } else {
            0x00010000
        }

        /** 权限名称 */
        var name: String = ""
        /** 最大生效 sdk 版本 */
        var maxSdkVersion: Int = 0
        /** 权限使用标志 */
        var usesPermissionFlags: Int = 0

        /**
         * 是否不会用当前权限需要推导地理位置
         */
        fun neverForLocation(): Boolean {
            return (usesPermissionFlags and REQUESTED_PERMISSION_NEVER_FOR_LOCATION) != 0
        }
    }

    class ApplicationInfo {
        /** 应用的类名 */
        var name: String = ""
        /** 是否忽略分区存储特性 */
        var requestLegacyExternalStorage: Boolean = false
    }

    class ActivityInfo {
        /** 活动的类名 */
        var name: String = ""
        /** 窗口是否支持画中画 */
        var supportsPictureInPicture: Boolean = false
    }

    class ServiceInfo {
        /** 服务的类名 */
        var name: String = ""

        /** 服务所使用到的权限 */
        var permission: String = ""
    }
}