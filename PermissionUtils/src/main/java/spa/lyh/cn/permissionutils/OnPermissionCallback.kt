package spa.lyh.cn.permissionutils

interface OnPermissionCallback {
    fun onGranted(permissions:List<String>,allGranted:Boolean)

    fun onDenied(permissions:List<String>,doNotAskAgain:Boolean)
}