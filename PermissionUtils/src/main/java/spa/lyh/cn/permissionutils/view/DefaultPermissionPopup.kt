package spa.lyh.cn.permissionutils.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.core.graphics.drawable.toDrawable
import spa.lyh.cn.permissionutils.R

open class DefaultPermissionPopup(context: Context):PopupWindow(context) {
    init {
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        animationStyle = R.style.popUpAnimation
        setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        isTouchable = true
        isOutsideTouchable = true
    }

    fun Context.getActivity(): Activity? {
        var ctx = this
        while (ctx is ContextWrapper) {
            if (ctx is Activity) return ctx
            ctx = ctx.baseContext
        }
        return null
    }
}