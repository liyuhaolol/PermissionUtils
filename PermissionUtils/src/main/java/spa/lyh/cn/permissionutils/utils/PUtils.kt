package spa.lyh.cn.permissionutils.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

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

    private fun equalsPermission(permission1: String,permission2: String): Boolean{
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
}