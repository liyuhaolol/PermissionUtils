package spa.lyh.cn.permissionutils.demo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import spa.lyh.cn.permissionutils.view.DefaultPermissionPopup

open class PermissionPopup(context: Context) : DefaultPermissionPopup(context){
    private var mContentView:ViewGroup? = null
    init {
        try {
            contentView = LayoutInflater.from(context).inflate(R.layout.permission_description_popup, context.getActivity()!!.window.decorView as ViewGroup, false)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    override fun setContentView(contentView: View?) {
        super.setContentView(contentView)
    }

    fun setTitle(title:String){
        if (contentView != null){
            val titleView:TextView = contentView.findViewById(R.id.title)
            titleView.text = title
        }
    }

    fun setContent(content:String){
        if (contentView != null){
            val contentView:TextView = contentView.findViewById(R.id.content)
            contentView.text = content
        }
    }
}