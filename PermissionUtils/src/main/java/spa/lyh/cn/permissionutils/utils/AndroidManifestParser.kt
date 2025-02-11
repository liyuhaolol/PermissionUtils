package spa.lyh.cn.permissionutils.utils

import android.content.Context
import android.content.res.XmlResourceParser
import android.text.TextUtils
import androidx.annotation.NonNull
import org.xmlpull.v1.XmlPullParserException
import spa.lyh.cn.permissionutils.model.AndroidManifestInfo
import java.io.IOException

object AndroidManifestParser {

    private const val ANDROID_MANIFEST_FILE_NAME = "AndroidManifest.xml"
    private const val ANDROID_NAMESPACE_URI = "http://schemas.android.com/apk/res/android"

    private const val TAG_MANIFEST = "manifest"
    private const val TAG_USES_SDK = "uses-sdk"
    private const val TAG_USES_PERMISSION = "uses-permission"
    private const val TAG_USES_PERMISSION_SDK_23 = "uses-permission-sdk-23"
    private const val TAG_USES_PERMISSION_SDK_M = "uses-permission-sdk-m"
    private const val TAG_APPLICATION = "application"
    private const val TAG_ACTIVITY = "activity"
    private const val TAG_ACTIVITY_ALIAS = "activity-alias"
    private const val TAG_SERVICE = "service"

    private const val ATTR_PACKAGE = "package"
    private const val ATTR_NAME = "name"
    private const val ATTR_MAX_SDK_VERSION = "maxSdkVersion"
    private const val ATTR_MIN_SDK_VERSION = "minSdkVersion"
    private const val ATTR_USES_PERMISSION_FLAGS = "usesPermissionFlags"
    private const val ATTR_REQUEST_LEGACY_EXTERNAL_STORAGE = "requestLegacyExternalStorage"
    private const val ATTR_SUPPORTS_PICTURE_IN_PICTURE = "supportsPictureInPicture"
    private const val ATTR_PERMISSION = "permission"

    @Throws(IOException::class, XmlPullParserException::class)
    fun parseAndroidManifest(context: Context, apkCookie: Int): AndroidManifestInfo {
        val manifestInfo = AndroidManifestInfo()

        context.assets.openXmlResourceParser(apkCookie, ANDROID_MANIFEST_FILE_NAME).use { parser ->
            while (parser.next() != XmlResourceParser.END_DOCUMENT) {
                if (parser.eventType != XmlResourceParser.START_TAG) {
                    continue
                }

                val tagName = parser.name

                when {
                    TextUtils.equals(TAG_MANIFEST, tagName) -> {
                        manifestInfo.packageName = parser.getAttributeValue(null, ATTR_PACKAGE)
                    }
                    TextUtils.equals(TAG_USES_SDK, tagName) -> {
                        manifestInfo.usesSdkInfo = parseUsesSdkFromXml(parser)
                    }
                    TextUtils.equals(TAG_USES_PERMISSION, tagName) ||
                            TextUtils.equals(TAG_USES_PERMISSION_SDK_23, tagName) ||
                            TextUtils.equals(TAG_USES_PERMISSION_SDK_M, tagName) -> {
                        manifestInfo.permissionInfoList.add(parsePermissionFromXml(parser))
                    }
                    TextUtils.equals(TAG_APPLICATION, tagName) -> {
                        manifestInfo.applicationInfo = parseApplicationFromXml(parser)
                    }
                    TextUtils.equals(TAG_ACTIVITY, tagName) ||
                            TextUtils.equals(TAG_ACTIVITY_ALIAS, tagName) -> {
                        manifestInfo.activityInfoList.add(parseActivityFromXml(parser))
                    }
                    TextUtils.equals(TAG_SERVICE, tagName) -> {
                        manifestInfo.serviceInfoList.add(parseServerFromXml(parser))
                    }
                }
            }
        }

        return manifestInfo
    }

    @NonNull
    private fun parseUsesSdkFromXml(parser: XmlResourceParser): AndroidManifestInfo.UsesSdkInfo {
        val usesSdkInfo = AndroidManifestInfo.UsesSdkInfo()
        usesSdkInfo.minSdkVersion = parser.getAttributeIntValue(ANDROID_NAMESPACE_URI, ATTR_MIN_SDK_VERSION, 0)
        return usesSdkInfo
    }

    @NonNull
    private fun parsePermissionFromXml(parser: XmlResourceParser): AndroidManifestInfo.PermissionInfo {
        val permissionInfo = AndroidManifestInfo.PermissionInfo()
        permissionInfo.name = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME)
        permissionInfo.maxSdkVersion = parser.getAttributeIntValue(ANDROID_NAMESPACE_URI, ATTR_MAX_SDK_VERSION, Int.MAX_VALUE)
        permissionInfo.usesPermissionFlags = parser.getAttributeIntValue(ANDROID_NAMESPACE_URI, ATTR_USES_PERMISSION_FLAGS, 0)
        return permissionInfo
    }

    @NonNull
    private fun parseApplicationFromXml(parser: XmlResourceParser): AndroidManifestInfo.ApplicationInfo {
        val applicationInfo = AndroidManifestInfo.ApplicationInfo()
        applicationInfo.name = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME)
        applicationInfo.requestLegacyExternalStorage = parser.getAttributeBooleanValue(ANDROID_NAMESPACE_URI, ATTR_REQUEST_LEGACY_EXTERNAL_STORAGE, false)
        return applicationInfo
    }

    @NonNull
    private fun parseActivityFromXml(parser: XmlResourceParser): AndroidManifestInfo.ActivityInfo {
        val activityInfo = AndroidManifestInfo.ActivityInfo()
        activityInfo.name = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME)
        activityInfo.supportsPictureInPicture = parser.getAttributeBooleanValue(ANDROID_NAMESPACE_URI, ATTR_SUPPORTS_PICTURE_IN_PICTURE, false)
        return activityInfo
    }

    @NonNull
    private fun parseServerFromXml(parser: XmlResourceParser): AndroidManifestInfo.ServiceInfo {
        val serviceInfo = AndroidManifestInfo.ServiceInfo()
        serviceInfo.name = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_NAME)
        serviceInfo.permission = parser.getAttributeValue(ANDROID_NAMESPACE_URI, ATTR_PERMISSION)
        return serviceInfo
    }
}