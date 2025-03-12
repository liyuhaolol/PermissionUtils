package spa.lyh.cn.permissionutils.demo.pop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import spa.lyh.cn.permissionutils.demo.R
import spa.lyh.cn.permissionutils.view.DefaultDialog

class SettingDialog(context: Context):DefaultDialog(context) {
    private lateinit var contentView: ViewGroup
    private var positiveButtonClickListener:OnClickListener? = null
    private var negativeButtonClickListener:OnClickListener? = null
    init {
        initDialogStyle()
    }

    private fun initDialogStyle(){
        setContentView(createDialogView(R.layout.dialog_setting))
        contentView.findViewById<TextView>(R.id.tv_confirm).setOnClickListener { v->
            positiveButtonClickListener?.onClick(v)
        }
        contentView.findViewById<TextView>(R.id.tv_cancel).setOnClickListener { v->
            negativeButtonClickListener?.onClick(v)
        }
    }

    private fun createDialogView(layoutId: Int): ViewGroup {
        contentView = LayoutInflater.from(context).inflate(layoutId, null) as ViewGroup
        return contentView
    }

    override fun setPositiveButton(onClick: View.OnClickListener) {
        positiveButtonClickListener = onClick
    }

    override fun setNegativeButton(onClick: View.OnClickListener) {
        negativeButtonClickListener = onClick
    }

    fun setTitle(title:String){
        val titleView:TextView = contentView.findViewById(R.id.title)
        titleView.text = title
    }

    fun setContent(content:String){
        val contentView:TextView = contentView.findViewById(R.id.content)
        contentView.text = content
    }
}