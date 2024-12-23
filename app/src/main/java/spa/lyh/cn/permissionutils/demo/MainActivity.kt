package spa.lyh.cn.permissionutils.demo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import spa.lyh.cn.permissionutils.AskPermission
import spa.lyh.cn.permissionutils.ManifestPro
import spa.lyh.cn.permissionutils.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var b: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        b.btn1.setOnClickListener(View.OnClickListener{
            AskPermission
                .with(this@MainActivity)
                .permission(ManifestPro.permission.CAMERA)
        })
    }
}