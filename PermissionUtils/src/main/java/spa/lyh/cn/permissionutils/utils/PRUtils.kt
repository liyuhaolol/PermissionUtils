package spa.lyh.cn.permissionutils.utils

import android.os.Build
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Properties

object PRUtils {
    private val ROM_HUAWEI = arrayOf("huawei")
    private val ROM_VIVO = arrayOf("vivo")
    private val ROM_XIAOMI = arrayOf("xiaomi")
    private val ROM_OPPO = arrayOf("oppo")
    private val ROM_LEECO = arrayOf("leeco", "letv")
    private val ROM_360 = arrayOf("360", "qiku")
    private val ROM_ZTE = arrayOf("zte")
    private val ROM_ONEPLUS = arrayOf("oneplus")
    private val ROM_NUBIA = arrayOf("nubia")
    private val ROM_SAMSUNG = arrayOf("samsung")
    private val ROM_HONOR = arrayOf("honor")

    private const val ROM_NAME_MIUI = "ro.miui.ui.version.name"

    private const val VERSION_PROPERTY_HUAWEI = "ro.build.version.emui"
    private const val VERSION_PROPERTY_VIVO = "ro.vivo.os.build.display.id"
    private const val VERSION_PROPERTY_XIAOMI = "ro.build.version.incremental"
    private val VERSION_PROPERTY_OPPO = arrayOf("ro.build.version.opporom", "ro.build.version.oplusrom.display")
    private const val VERSION_PROPERTY_LEECO = "ro.letv.release.version"
    private const val VERSION_PROPERTY_360 = "ro.build.uiversion"
    private const val VERSION_PROPERTY_ZTE = "ro.build.MiFavor_version"
    private const val VERSION_PROPERTY_ONEPLUS = "ro.rom.version"
    private const val VERSION_PROPERTY_NUBIA = "ro.build.rom.id"
    private val VERSION_PROPERTY_MAGIC = arrayOf("msc.config.magic.version", "ro.build.version.magic")

    /**
     * 判断当前厂商系统是否为 emui
     */
    fun isEmui(): Boolean {
        return !getPropertyName(VERSION_PROPERTY_HUAWEI).isNullOrEmpty()
    }

    /**
     * 判断当前厂商系统是否为 miui
     */
    fun isMiui(): Boolean {
        return !getPropertyName(ROM_NAME_MIUI).isNullOrEmpty()
    }

    /**
     * 判断当前厂商系统是否为 ColorOs
     */
    fun isColorOs(): Boolean {
        for (property in VERSION_PROPERTY_OPPO) {
            val versionName = getPropertyName(property)
            if (!versionName.isNullOrEmpty()) {
                return true
            }
        }
        return false
    }

    /**
     * 判断当前厂商系统是否为 OriginOS
     */
    fun isOriginOs(): Boolean {
        return !getPropertyName(VERSION_PROPERTY_VIVO).isNullOrEmpty()
    }

    /**
     * 判断当前厂商系统是否为 OneUI
     */
    fun isOneUi(): Boolean {
        return isRightRom(getBrand(), getManufacturer(), *ROM_SAMSUNG)
    }

    /**
     * 判断当前是否为鸿蒙系统
     */
    fun isHarmonyOs(): Boolean {
        if (!AVersion.isAndroid10()) {
            return false
        }
        return try {
            val buildExClass = Class.forName("com.huawei.system.BuildEx")
            val osBrand = buildExClass.getMethod("getOsBrand").invoke(buildExClass)
            "Harmony".equals(osBrand.toString(), ignoreCase = true)
        } catch (e: Throwable) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 判断当前是否为 MagicOs 系统（荣耀）
     */
    fun isMagicOs(): Boolean {
        return isRightRom(getBrand(), getManufacturer(), *ROM_HONOR)
    }

    /**
     * 判断 miui 优化开关
     */
    fun isMiuiOptimization(): Boolean {
        return try {
            val clazz = Class.forName("android.os.SystemProperties")
            val getMethod = clazz.getMethod("get", String::class.java, String::class.java)
            val ctsValue = getMethod.invoke(clazz, "ro.miui.cts", "") as String
            val getBooleanMethod = clazz.getMethod("getBoolean", String::class.java, Boolean::class.javaPrimitiveType)
            getBooleanMethod.invoke(clazz, "persist.sys.miui_optimization", ctsValue != "1") as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }
    }

    /**
     * 返回厂商系统版本号
     */
    fun getRomVersionName(): String? {
        val brand = getBrand()
        val manufacturer = getManufacturer()
        return when {
            isRightRom(brand, manufacturer, *ROM_HUAWEI) -> {
                val version = getPropertyName(VERSION_PROPERTY_HUAWEI)
                val temp = version.split("_")
                when {
                    temp.size > 1 -> temp[1]
                    version.contains("EmotionUI") -> version.replaceFirst("EmotionUI\\s*".toRegex(), "")
                    else -> version
                }
            }
            isRightRom(brand, manufacturer, *ROM_VIVO) -> getPropertyName(VERSION_PROPERTY_VIVO)
            isRightRom(brand, manufacturer, *ROM_XIAOMI) -> getPropertyName(VERSION_PROPERTY_XIAOMI)
            isRightRom(brand, manufacturer, *ROM_OPPO) -> {
                for (property in VERSION_PROPERTY_OPPO) {
                    val versionName = getPropertyName(property)
                    if (!versionName.isNullOrEmpty()) {
                        return versionName
                    }
                }
                ""
            }
            isRightRom(brand, manufacturer, *ROM_LEECO) -> getPropertyName(VERSION_PROPERTY_LEECO)
            isRightRom(brand, manufacturer, *ROM_360) -> getPropertyName(VERSION_PROPERTY_360)
            isRightRom(brand, manufacturer, *ROM_ZTE) -> getPropertyName(VERSION_PROPERTY_ZTE)
            isRightRom(brand, manufacturer, *ROM_ONEPLUS) -> getPropertyName(VERSION_PROPERTY_ONEPLUS)
            isRightRom(brand, manufacturer, *ROM_NUBIA) -> getPropertyName(VERSION_PROPERTY_NUBIA)
            isRightRom(brand, manufacturer, *ROM_HONOR) -> {
                for (property in VERSION_PROPERTY_MAGIC) {
                    val versionName = getPropertyName(property)
                    if (!versionName.isNullOrEmpty()) {
                        return versionName
                    }
                }
                ""
            }
            else -> getPropertyName("")
        }
    }

    private fun isRightRom(brand: String, manufacturer: String, vararg names: String): Boolean {
        return names.any { brand.contains(it, ignoreCase = true) || manufacturer.contains(it, ignoreCase = true) }
    }

    private fun getBrand(): String {
        return Build.BRAND.lowercase()
    }

    private fun getManufacturer(): String {
        return Build.MANUFACTURER.lowercase()
    }

    private fun getPropertyName(propertyName: String): String {
        return if (propertyName.isNotEmpty()) {
            getSystemProperty(propertyName)
        } else {
            ""
        }
    }

    private fun getSystemProperty(name: String): String {
        return getSystemPropertyByShell(name).ifEmpty {
            getSystemPropertyByStream(name).ifEmpty {
                if (Build.VERSION.SDK_INT < 28) getSystemPropertyByReflect(name) else ""
            }
        }
    }

    private fun getSystemPropertyByShell(propName: String): String {
        return try {
            val process = Runtime.getRuntime().exec("getprop $propName")
            process.inputStream.bufferedReader().use { it.readLine() ?: "" }
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

    private fun getSystemPropertyByStream(key: String): String {
        return try {
            val prop = Properties()
            FileInputStream(File(Environment.getRootDirectory(), "build.prop")).use { prop.load(it) }
            prop.getProperty(key, "")
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun getSystemPropertyByReflect(key: String): String {
        return try {
            val clazz = Class.forName("android.os.SystemProperties")
            val getMethod = clazz.getMethod("get", String::class.java, String::class.java)
            getMethod.invoke(clazz, key, "") as String
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}