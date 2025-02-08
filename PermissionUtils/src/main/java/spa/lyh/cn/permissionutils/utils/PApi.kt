package spa.lyh.cn.permissionutils.utils

import android.content.Context
import android.content.pm.PackageManager
import spa.lyh.cn.permissionutils.utils.pd.PermissionDelegate
import spa.lyh.cn.permissionutils.utils.pd.impl.PermissionDelegateImplV34

object PApi {
    //private val DELEGATE:PermissionDelegate = PermissionDelegateImplV34()

/*    fun recheckPermissionResult(context: Context,permission:String,grantResult: Boolean): Boolean{
        return DELEGATE.recheckPermissionResult(context, permission, grantResult)
    }*/

    /**
     * 获取已授予的权限
     */
    fun getGrantedPermissions(permissions: ArrayList<String>,grantResults: IntArray): ArrayList<String>{
        val grantedPermissions = arrayListOf<String>()
        grantResults.forEachIndexed { index,item ->
            // 把授予过的权限加入到集合中
            if (item == PackageManager.PERMISSION_GRANTED){
                grantedPermissions.add(permissions[index])
            }
        }
        return grantedPermissions
    }
}
