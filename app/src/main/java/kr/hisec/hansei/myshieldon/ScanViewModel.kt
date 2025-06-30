package kr.hisec.hansei.myshieldon

import android.app.Application // AndroidViewModel을 위해 필요합니다.
import android.util.Log
import androidx.lifecycle.AndroidViewModel // ViewModel에서 AndroidViewModel로 변경!
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// UI 상태를 나타내는 Sealed Class
sealed class ScanUiState {
    object Idle : ScanUiState() // 초기 상태
    object Scanning : ScanUiState() // 스캔 중
    data class Success(val detectedApps: List<DetectedApp>) : ScanUiState() // 스캔 성공
    data class Error(val message: String) : ScanUiState() // 에러 발생
}

// [수정된 부분] ViewModel() 대신 AndroidViewModel(application)을 상속받습니다.
class ScanViewModel(private val application: Application) : AndroidViewModel(application) {

    // UI가 관찰할 수 있도록 StateFlow를 사용
    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState

    /**
     * 보안 스캔을 시작합니다.
     */
    fun startSecurityScan() {
        if (_uiState.value is ScanUiState.Scanning) return // 이미 스캔 중이면 무시

        viewModelScope.launch {
            _uiState.value = ScanUiState.Scanning
            Log.d("SecurityScan", "보안 스캔을 시작합니다...")

            try {
                // 1. 여기서 SecurityConfig를 설정합니다. (원격 또는 로컬 파일에서 로드 가능)
                val securityConfig = SecurityConfig(
                    permissionThreshold = 3,
                    dangerousPermissions = setOf(
                        "android.permission.READ_CONTACTS", "android.permission.READ_SMS", "android.permission.SEND_SMS",
                        "android.permission.CAMERA", "android.permission.RECORD_AUDIO", "android.permission.ACCESS_FINE_LOCATION",
                        "android.permission.READ_EXTERNAL_STORAGE", "android.permission.SYSTEM_ALERT_WINDOW",
                        "android.permission.REQUEST_INSTALL_PACKAGES"
                    ),
                    officialSignatures = mapOf(
                        "com.kbstar.kbbank" to "실제_KB스타뱅킹_서명_해시",
                        "com.shinhan.sbanking" to "실제_신한쏠_서명_해시",
                        "com.kakaobank.channel" to "실제_카카오뱅크_서명_해시"
                    )
                )

                // 2. 개선된 SecurityScanner를 사용합니다.
                val scanner = SecurityScanner(application, securityConfig)
                val detectedApps = scanner.scanInstalledApps()

                Log.d("SecurityScan", "총 ${detectedApps.size}개의 의심스러운 앱을 발견했습니다.")
                _uiState.value = ScanUiState.Success(detectedApps)

            } catch (e: Exception) {
                Log.e("SecurityScan", "스캔 중 오류 발생", e)
                _uiState.value = ScanUiState.Error("스캔 중 오류가 발생했습니다: ${e.message}")
            }
        }
    }
}