package kr.hisec.hansei.myshieldon // MainActivity와 동일한 패키지 이름

import android.os.Build
import java.io.File

object SecurityCheckUtils {
    fun isDeviceRooted(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )
        for (path in paths) {
            if (File(path).exists()) {
                return true
            }
        }

        if (Build.TAGS != null && Build.TAGS.contains("test-keys")) {
            return true
        }

        if (File("/system/xbin/busybox").exists()) {
            return true
        }

        return false
    }
}