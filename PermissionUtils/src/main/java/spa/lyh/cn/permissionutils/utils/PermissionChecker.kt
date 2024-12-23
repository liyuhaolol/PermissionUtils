package spa.lyh.cn.permissionutils.utils

import android.app.Activity
import android.util.Log

object PermissionChecker {
    val TAG = "PermissionChecker"

    fun checkActivityStatus(activity:Activity?): Boolean{
        // 检查当前 Activity 状态是否是正常的，如果不是则不请求权限
        if (activity == null){
            Log.e(TAG,"The instance of the context must be an activity object")
            return false
        }
        if (activity.isFinishing){
            Log.e(TAG,"The activity has been finishing, please manually determine the status of the activity")
            return false
        }
        return true
    }
}