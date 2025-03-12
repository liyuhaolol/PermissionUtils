package spa.lyh.cn.permissionutils.demo

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import spa.lyh.cn.permissionutils.AskPermission
import spa.lyh.cn.permissionutils.ManifestPro
import spa.lyh.cn.permissionutils.OnPermissionCallback
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
                .permission(arrayListOf<String>(ManifestPro.permission.CAMERA, ManifestPro.permission.ACCESS_FINE_LOCATION))
                .interceptor(PermissionInterceptor(this)
                    .setTitle("权限说明标题")
                    .setDescription("权限说明内容，你为什么要申请这个权限，要做什么。")
                    .enforce(true))
                .request(object : OnPermissionCallback{
                    override fun onGranted(
                        permissions: List<String>,
                        allGranted: Boolean
                    ) {
                        if (allGranted){
                            Toast.makeText(this@MainActivity,"全部通过了", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@MainActivity,"部分通过了", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onDenied(
                        permissions: List<String>,
                        doNotAskAgain: Boolean
                    ) {
                        if (doNotAskAgain){
                            Toast.makeText(this@MainActivity,"拒绝且不再询问", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@MainActivity,"拒绝了", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        })
        ////////////////////////////////////////////////////
        /////                  分割线                   /////
        ////////////////////////////////////////////////////
        b.btn2.setOnClickListener(View.OnClickListener{
            AskPermission
                .with(this@MainActivity)
                .permission(ManifestPro.permission.MANAGE_EXTERNAL_STORAGE)
                .interceptor(PermissionInterceptor(this)
                    .setTitle("权限说明标题")
                    .setDescription("权限说明内容，你为什么要申请这个权限，要做什么。"))
                .request(object : OnPermissionCallback{
                    override fun onGranted(
                        permissions: List<String>,
                        allGranted: Boolean
                    ) {
                        if (allGranted){
                            Toast.makeText(this@MainActivity,"全部通过了", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@MainActivity,"部分通过了", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onDenied(
                        permissions: List<String>,
                        doNotAskAgain: Boolean
                    ) {
                        if (doNotAskAgain){
                            Toast.makeText(this@MainActivity,"拒绝且不再询问", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@MainActivity,"拒绝了", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        })
    }
}