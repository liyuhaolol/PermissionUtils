package spa.lyh.cn.permissionutils

interface OnPermissionPageCallback {
    /**
     * 权限已经授予
     */
    fun onGranted()

    /**
     * 权限已经拒绝
     */
    fun onDenied() {}
}