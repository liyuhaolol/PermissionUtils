package spa.lyh.cn.permissionutils.demo

import android.content.Context
import spa.lyh.cn.permissionutils.DefaultPermissionInterceptor
import spa.lyh.cn.permissionutils.demo.pop.PermissionPopup
import spa.lyh.cn.permissionutils.demo.pop.SettingDialog

//自行决定这个方法要如何来写
class PermissionInterceptor(mContext: Context): DefaultPermissionInterceptor() {
    var permissionPopup: PermissionPopup = PermissionPopup(mContext)
    var settingPopup:SettingDialog = SettingDialog(mContext)

    init {
        setPopUpWindow(permissionPopup)
        setSepcialSettingDialog(settingPopup)
    }


    fun setTitle(title:String):PermissionInterceptor{
        permissionPopup.setTitle(title)
        settingPopup.setTitle(title)
        return this
    }

    fun setDescription(content:String):PermissionInterceptor{
        permissionPopup.setContent(content)
        settingPopup.setContent(content)
        return this
    }

    //如果权限被永久拒绝后，是否强制显示弹窗请求授权
    fun enforce(enable:Boolean):PermissionInterceptor{
        this.forceShowSetting = enable
        return this
    }
}