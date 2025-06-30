package kr.hisec.hansei.myshieldon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kr.hisec.hansei.myshieldon.ui.theme.MyShieldOnTheme
import kr.hisec.hansei.myshieldon.ui.theme.SuccessGreen
import kr.hisec.hansei.myshieldon.ui.theme.WarningRed

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyShieldOnTheme {
                val viewModel: ScanViewModel = viewModel(factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application))
                ShieldOnRootScreen(viewModel)
            }
        }
    }
}

@Composable
fun ShieldOnRootScreen(viewModel: ScanViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = uiState::class,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "Screen Animation"
        ) { targetState ->
            when (targetState) {
                ScanUiState.Idle::class -> {
                    IdleScreen(onStartClick = { viewModel.startSecurityScan() })
                }
                ScanUiState.Scanning::class -> {
                    ScanningScreen()
                }
                ScanUiState.Success::class, ScanUiState.Error::class -> {
                    val result = uiState
                    ResultScreen(result = result)
                }
            }
        }
    }
}

@Composable
fun IdleScreen(onStartClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(
            onClick = onStartClick,
            modifier = Modifier.size(200.dp),
            shape = CircleShape,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            // 버튼 색은 Theme.kt에서 지정한 primary 색상(ModernBlue)을 자동으로 따라갑니다.
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Shield, "보안 방패", modifier = Modifier.size(60.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("점검 시작", fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun ScanningScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 진행률 표시 색상도 테마의 primary 색상을 자동으로 따라갑니다.
            CircularProgressIndicator(modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text("정밀 검사를 진행 중입니다...", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun ResultScreen(result: ScanUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Text("점검 완료", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        when (result) {
            is ScanUiState.Success -> {
                if (result.detectedApps.isEmpty()) {
                    // [수정] 직접 지정한 색 대신, Color.kt에 정의한 SuccessGreen을 사용합니다.
                    Text("모든 앱이 안전합니다.", color = SuccessGreen, style = MaterialTheme.typography.titleMedium)
                } else {
                    // [수정] 직접 지정한 색 대신, Color.kt에 정의한 WarningRed를 사용합니다.
                    Text("총 ${result.detectedApps.size}개의 보안 위협이 탐지되었습니다.", color = WarningRed, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn {
                        items(result.detectedApps) { app ->
                            DetectedAppCard(app)
                        }
                    }
                }
            }
            is ScanUiState.Error -> {
                // [수정] 직접 지정한 색 대신, Color.kt에 정의한 WarningRed를 사용합니다.
                Text("오류 발생: ${result.message}", color = WarningRed)
            }
            else -> {}
        }
    }
}

@Composable
fun DetectedAppCard(app: DetectedApp) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${app.appName} (${app.packageName})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            app.issues.forEach { issue ->
                when (issue) {
                    is SecurityIssue.DangerousPermissions -> {
                        // [수정] 직접 지정한 색 대신, Color.kt에 정의한 WarningRed를 사용합니다.
                        Text("  - 이슈: 과도한 위험 권한 보유 (${issue.permissions.size}개)", color = WarningRed)
                    }
                    is SecurityIssue.TamperedSignature -> {
                        // [수정] 직접 지정한 색 대신, Color.kt에 정의한 WarningRed를 사용합니다.
                        Text("  - 이슈: ★★★ 서명 변조 의심 ★★★", color = WarningRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}