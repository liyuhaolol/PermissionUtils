package spa.lyh.cn.permissionutils

import android.os.Build
import androidx.annotation.RequiresApi

object ManifestPro {
    object permission{
        const val READ_CALENDAR = "android.permission.READ_CALENDAR"
        const val WRITE_CALENDAR = "android.permission.WRITE_CALENDAR"
        const val CAMERA = "android.permission.CAMERA"
        const val READ_CONTACTS = "android.permission.READ_CONTACTS"
        const val WRITE_CONTACTS = "android.permission.WRITE_CONTACTS"
        const val GET_ACCOUNTS = "android.permission.GET_ACCOUNTS"
        const val ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION"
        const val ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION"
        const val RECORD_AUDIO = "android.permission.RECORD_AUDIO"
        const val READ_PHONE_STATE = "android.permission.READ_PHONE_STATE"
        const val READ_PHONE_STATE_BLOW_ANDROID_9 = "android.permission.READ_PHONE_STATE_BLOW_ANDROID_9"
        const val CALL_PHONE = "android.permission.CALL_PHONE"
        const val READ_CALL_LOG = "android.permission.READ_CALL_LOG"
        const val WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG"
        const val ADD_VOICEMAIL = "com.android.voicemail.permission.ADD_VOICEMAIL"
        const val USE_SIP = "android.permission.USE_SIP"
        const val PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS"
        const val BODY_SENSORS = "android.permission.BODY_SENSORS"
        const val SEND_SMS = "android.permission.SEND_SMS"
        const val RECEIVE_SMS = "android.permission.RECEIVE_SMS"
        const val READ_SMS = "android.permission.READ_SMS"
        const val RECEIVE_WAP_PUSH = "android.permission.RECEIVE_WAP_PUSH"
        const val RECEIVE_MMS = "android.permission.RECEIVE_MMS"
        const val READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"
        const val READ_EXTERNAL_STORAGE_BLOW_ANDROID_13 = "android.permission.READ_EXTERNAL_STORAGE_BLOW_ANDROID_13"
        const val WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE"
        const val WRITE_EXTERNAL_STORAGE_BLOW_ANDROID_9 = "android.permission.WRITE_EXTERNAL_STORAGE_BLOW_ANDROID_9"
        //Android13新引入权限
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        const val POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS"
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        const val READ_MEDIA_IMAGES = "android.permission.READ_MEDIA_IMAGES"
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        const val READ_MEDIA_VIDEO = "android.permission.READ_MEDIA_VIDEO"
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        const val READ_MEDIA_AUDIO = "android.permission.READ_MEDIA_AUDIO"
        //Android14新引入权限
        @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        const val READ_MEDIA_VISUAL_USER_SELECTED = "android.permission.READ_MEDIA_VISUAL_USER_SELECTED"
    }
}