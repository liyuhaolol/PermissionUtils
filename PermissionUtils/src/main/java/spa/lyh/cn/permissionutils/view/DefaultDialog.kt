package spa.lyh.cn.permissionutils.view

import android.app.Dialog
import android.content.Context
import android.view.View.OnClickListener
import spa.lyh.cn.permissionutils.R

abstract class DefaultDialog(context: Context,themeResId:Int):Dialog(context,themeResId) {
    constructor(context: Context):this(context, R.style.DefaultDialog)

    init {
        setCanceledOnTouchOutside(false)
    }


    abstract fun setPositiveButton(onClick:OnClickListener)
    abstract fun setNegativeButton(onClick:OnClickListener)
}