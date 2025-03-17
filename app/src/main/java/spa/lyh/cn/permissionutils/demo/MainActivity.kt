package spa.lyh.cn.permissionutils.demo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import spa.lyh.cn.permissionutils.AskPermission
import spa.lyh.cn.permissionutils.ManifestPro
import spa.lyh.cn.permissionutils.OnPermissionCallback
import spa.lyh.cn.permissionutils.demo.databinding.ActivityMainBinding
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class MainActivity : AppCompatActivity() {
    lateinit var b: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
/*        val time: Duration = 1.seconds
        Log.e("qwer","${time.inWholeMilliseconds}")*/
/*        val time: java.time.Duration = java.time.Duration.ofMinutes(1)
        Log.e("qwer","${time.toMillis()}")*/

        b.btn1.setOnClickListener(View.OnClickListener{
            AskPermission
                .with(this@MainActivity)
                .permission(arrayListOf<String>(ManifestPro.permission.CAMERA, ManifestPro.permission.ACCESS_FINE_LOCATION))
                .interceptor(PermissionInterceptor(this)
                    .setTitle("权限说明标题")//设置说明的标题
                    .setDescription("权限说明内容，你为什么要申请这个权限，要做什么。")//设置说明内容
                    .enforce(true)//权限拒绝后是否强制显示弹窗，对特殊权限无效
                    .interval(30.seconds)//相同权限重复请求的间隔时间
                )
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
        ////////////////////////////////////////////////////
        /////                  分割线                   /////
        ////////////////////////////////////////////////////
        b.btn3.setOnClickListener(View.OnClickListener{
            AskPermission
                .with(this@MainActivity)
                .permission(ManifestPro.permission.NOTIFICATION_SERVICE)
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