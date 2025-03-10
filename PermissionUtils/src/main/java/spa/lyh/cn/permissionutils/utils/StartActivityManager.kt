package spa.lyh.cn.permissionutils.utils

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent

object StartActivityManager {

    private const val SUB_INTENT_KEY = "sub_intent_key"

    fun getSubIntentInSuperIntent(superIntent: Intent): Intent? {
        return if (AVersion.isAndroid13()) {
            superIntent.getParcelableExtra(SUB_INTENT_KEY,Intent::class.java)
        } else {
            superIntent.getParcelableExtra(SUB_INTENT_KEY)
        }
    }

    fun getDeepSubIntent(intent: Intent): Intent {
        val subIntent = getSubIntentInSuperIntent(intent)
        return subIntent ?: intent
    }

    fun addSubIntentToMainIntent(mainIntent: Intent?, subIntent: Intent?): Intent {
        if (mainIntent == null && subIntent != null) {
            return subIntent
        }
        if (subIntent == null) {
            //这里是原作者java代码写的一个非空的坑，我只能从现实里猜测，不可能会出现mainIntent为空的情况
            return mainIntent!!
        }
        val deepSubIntent = getDeepSubIntent(mainIntent!!)
        deepSubIntent.putExtra(SUB_INTENT_KEY, subIntent)
        return mainIntent
    }

    fun startActivity(context: Context, intent: Intent): Boolean {
        return startActivity(StartActivityDelegateContextImpl(context), intent)
    }

    fun startActivity(activity: Activity, intent: Intent): Boolean {
        return startActivity(StartActivityDelegateActivityImpl(activity), intent)
    }

    fun startActivity(fragment: Fragment, intent: Intent): Boolean {
        return startActivity(StartActivityDelegateFragmentImpl(fragment), intent)
    }

    fun startActivity(delegate: StartActivityDelegate, intent: Intent): Boolean {
        return try {
            delegate.startActivity(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            val subIntent = getSubIntentInSuperIntent(intent)
            if (subIntent == null) {
                false
            } else {
                startActivity(delegate, subIntent)
            }
        }
    }

    fun startActivityForResult(activity: Activity, intent: Intent, requestCode: Int): Boolean {
        return startActivityForResult(StartActivityDelegateActivityImpl(activity), intent, requestCode)
    }

    fun startActivityForResult(fragment: Fragment, intent: Intent, requestCode: Int): Boolean {
        return startActivityForResult(StartActivityDelegateFragmentImpl(fragment), intent, requestCode)
    }

    fun startActivityForResult(delegate: StartActivityDelegate, intent: Intent, requestCode: Int): Boolean {
        return try {
            delegate.startActivityForResult(intent, requestCode)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            val subIntent = getSubIntentInSuperIntent(intent)
            if (subIntent == null) {
                false
            } else {
                startActivityForResult(delegate, subIntent, requestCode)
            }
        }
    }

    public interface StartActivityDelegate {
        fun startActivity(intent: Intent)
        fun startActivityForResult(intent: Intent, requestCode: Int)
    }

    private class StartActivityDelegateContextImpl(private val context: Context) : StartActivityDelegate {
        override fun startActivity(intent: Intent) {
            context.startActivity(intent)
        }

        override fun startActivityForResult(intent: Intent, requestCode: Int) {
            val activity = PUtils.findActivity(context)
            if (activity != null) {
                activity.startActivityForResult(intent, requestCode)
            } else {
                startActivity(intent)
            }
        }
    }

    private class StartActivityDelegateActivityImpl(private val activity: Activity) : StartActivityDelegate {
        override fun startActivity(intent: Intent) {
            activity.startActivity(intent)
        }

        override fun startActivityForResult(intent: Intent, requestCode: Int) {
            activity.startActivityForResult(intent, requestCode)
        }
    }

    private class StartActivityDelegateFragmentImpl(private val fragment: Fragment) : StartActivityDelegate {
        override fun startActivity(intent: Intent) {
            fragment.startActivity(intent)
        }

        override fun startActivityForResult(intent: Intent, requestCode: Int) {
            fragment.startActivityForResult(intent, requestCode)
        }
    }
}