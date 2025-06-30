package kr.hisec.hansei.myshieldon

// 보안 설정 값들을 담는 데이터 클래스
data class SecurityConfig(
    val permissionThreshold: Int,
    val dangerousPermissions: Set<String>,
    val officialSignatures: Map<String, String>
)

// 탐지된 앱의 정보를 담는 데이터 클래스
data class DetectedApp(
    val appName: String,
    val packageName: String,
    val issues: List<SecurityIssue>
)


sealed class SecurityIssue {
    data class DangerousPermissions(val permissions: Set<String>) : SecurityIssue()
    object TamperedSignature : SecurityIssue()
}