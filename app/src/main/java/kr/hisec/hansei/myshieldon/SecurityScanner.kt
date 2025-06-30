package kr.hisec.hansei.myshieldon

import android.content.Context
import kotlinx.coroutines.delay

// 임시구현한 스캐너
class SecurityScanner(private val context: Context, private val config: SecurityConfig) {

    // 설치된 앱을 스캔하는 함수
    suspend fun scanInstalledApps(): List<DetectedApp> {
        // 임시 구현이기에 만든 대기
        delay(2000)

        // --- 임시 데이터 --
        return listOf(
            DetectedApp(
                appName = "의심스러운 앱 A",
                packageName = "com.fake.app.a",
                issues = listOf(
                    SecurityIssue.DangerousPermissions(setOf("android.permission.READ_CONTACTS", "android.permission.CAMERA")),
                    SecurityIssue.TamperedSignature
                )
            ),
            DetectedApp(
                appName = "수상한 앱 B",
                packageName = "com.malicious.app.b",
                issues = listOf(
                    SecurityIssue.DangerousPermissions(setOf("android.permission.SEND_SMS", "android.permission.RECORD_AUDIO"))
                )
            )
        )
        // --- 임시 데이터 끝 ---
    }
}