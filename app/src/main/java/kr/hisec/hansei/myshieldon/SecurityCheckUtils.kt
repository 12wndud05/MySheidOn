package kr.hisec.hansei.myshieldon

import android.os.Build
import java.io.File
import java.io.OutputStream
import java.io.IOException

object SecurityCheckUtils {
    /**
     * 루팅 여부를 확인하는 함수.
     * 여러 방법을 조합하여 판단합니다.
     * @return 루팅 여부 (true = 루팅됨, false = 루팅 안됨)
     */
    fun isDeviceRooted(): Boolean {
        // 총 5가지 루팅 탐지 기법을 순차적으로 적용
        return (
                // 1. 일반적인 루팅 바이너리 및 파일 경로 확인
                checkForSuBinary() ||
                        // 2. test-keys 태그 존재 여부 확인
                        detectTestKeys() ||
                        // 3. BusyBox 바이너리 존재 여부 확인
                        checkForBusyBox() ||
                        // 4. su 명령어 실행 가능 여부 확인
                        checkSuExists() ||
                        // 5. 시스템 디렉토리 쓰기 권한 확인
                        checkForRWPaths()
                )
    }

    /**
     * 기기에서 흔히 사용되는 su 바이너리 파일 경로를 확인합니다.
     *  su 바이너리 존재 여부
     */
    private fun checkForSuBinary(): Boolean {
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
            "/su/bin/su" // Magisk 등 최신 루팅 도구 경로
        )
        for (path in paths) {
            if (File(path).exists()) {
                return true // 파일이 존재하면 루팅으로 간주
            }
        }
        return false
    }

    /**
     * 빌드 태그에 "test-keys"가 포함되어 있는지 확인합니다.
     * "test-keys" 태그 존재 여부
     */
    private fun detectTestKeys(): Boolean {
        // OEM이 아닌 비공식 빌드에서 주로 사용되는 태그
        return Build.TAGS != null && Build.TAGS.contains("test-keys")
    }

    /**
     * BusyBox 바이너리 파일의 존재 여부를 확인합니다.
     * BusyBox 존재 여부
     */
    private fun checkForBusyBox(): Boolean {
        return File("/system/xbin/busybox").exists()
    }

    /**
     * 새로운 기법 1: 'su' 명령어를 실행하여 셸 권한을 얻을 수 있는지 확인합니다.
     * 'su' 명령어 실행 가능 여부
     */
    private fun checkSuExists(): Boolean {
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            // su 명령어가 성공적으로 실행되면, 프로세스의 출력을 확인
            // which su의 결과가 있으면 su가 존재한다고 판단
            val os = process.outputStream
            os.write("exit\n".toByteArray())
            os.flush()
            val exitCode = process.waitFor()
            return exitCode == 0 // exit code가 0이면 성공적으로 실행된 것
        } catch (e: IOException) {
            // su 명령어를 찾을 수 없거나 실행할 수 없는 경우
            return false
        } catch (e: InterruptedException) {
            // 프로세스 대기 중 인터럽트 발생
            return false
        } finally {
            process?.destroy()
        }
    }

    /**
     * 새로운 기법 2: /system과 같은 읽기 전용 경로에 쓰기 권한이 있는지 확인합니다.
     * 쓰기 권한 존재 여부
     */
    private fun checkForRWPaths(): Boolean {
        val paths = arrayOf(
            "/system",
            "/system/bin",
            "/system/sbin",
            "/system/xbin",
            "/vendor/bin",
            "/data"
        )
        for (path in paths) {
            try {
                // 임시 파일을 생성해 쓰기 권한 테스트
                val tempFile = File(path, "test_file_" + System.currentTimeMillis())
                if (tempFile.createNewFile()) {
                    tempFile.delete() // 테스트 파일 삭제
                    return true // 파일 생성이 가능하면 쓰기 가능
                }
            } catch (e: Exception) {
                // 쓰기 권한이 없거나 예외 발생 시 무시
            }
        }
        return false
    }
}