package spa.lyh.cn.permissionutils.demo

import android.content.Context
import spa.lyh.cn.permissionutils.DefaultPermissionInterceptor
import spa.lyh.cn.permissionutils.demo.pop.PermissionPopup
import spa.lyh.cn.permissionutils.demo.pop.SettingDialog

class PermissionInterceptor(mContext: Context): DefaultPermissionInterceptor() {
    var permissionPopup: PermissionPopup = PermissionPopup(mContext)
    var settingPopup:SettingDialog = SettingDialog(mContext)

    init {
        setPopUpWindow(permissionPopup)
        setPopSettingDialog(settingPopup)
    }


    fun setTitle(title:String):PermissionInterceptor{
        permissionPopup.setTitle(title)
        settingPopup.setTitle(title)
        return this
    }

    fun setContent(content:String):PermissionInterceptor{
        permissionPopup.setContent(content)
        settingPopup.setContent(content)
        return this
    }
}