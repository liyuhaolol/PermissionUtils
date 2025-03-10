package spa.lyh.cn.permissionutils.demo

import android.app.Activity
import android.content.Context
import android.widget.TextView
import spa.lyh.cn.permissionutils.DefaultPermissionInterceptor
import spa.lyh.cn.permissionutils.OnPermissionCallback
import spa.lyh.cn.permissionutils.OnPermissionInterceptor

class PermissionInterceptor(mContext: Context): DefaultPermissionInterceptor() {
    var permissionPopup: PermissionPopup = PermissionPopup(mContext)

    init {
        setPopUpWindow(permissionPopup)
    }


    fun setTitle(title:String):PermissionInterceptor{
        permissionPopup.setTitle(title)
        return this
    }

    fun setContent(content:String):PermissionInterceptor{
        permissionPopup.setContent(content)
        return this
    }
}