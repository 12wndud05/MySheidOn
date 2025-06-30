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
                    // [수정] ResultScreen에 viewModel의 초기화 함수를 넘겨줍니다.
                    ResultScreen(
                        result = result,
                        onGoBack = { viewModel.returnToIdle() }
                    )
                }
            }
        }
    }
}

// ... IdleScreen, ScanningScreen 함수는 이전과 동일 ...
@Composable
fun IdleScreen(onStartClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(
            onClick = onStartClick,
            modifier = Modifier.size(200.dp),
            shape = CircleShape,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
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
            CircularProgressIndicator(modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text("정밀 검사를 진행 중입니다...", style = MaterialTheme.typography.titleMedium)
        }
    }
}


// [수정] ResultScreen이 onGoBack 이라는 함수를 받도록 변경합니다.
@Composable
fun ResultScreen(result: ScanUiState, onGoBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Text("점검 완료", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // LazyColumn이 화면의 남은 공간을 모두 차지하도록 weight(1f)를 줍니다.
        Box(modifier = Modifier.weight(1f)) {
            when (result) {
                is ScanUiState.Success -> {
                    if (result.detectedApps.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("모든 앱이 안전합니다.", color = SuccessGreen, style = MaterialTheme.typography.titleMedium)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("총 ${result.detectedApps.size}개의 보안 위협이 탐지되었습니다.", color = WarningRed, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(16.dp))
                            LazyColumn {
                                items(result.detectedApps) { app ->
                                    DetectedAppCard(app)
                                }
                            }
                        }
                    }
                }
                is ScanUiState.Error -> {
                    Text("오류 발생: ${result.message}", color = WarningRed)
                }
                else -> {}
            }
        }

        // [추가] 화면 맨 아래에 '다시 점검하기' 버튼을 추가합니다.
        Button(
            onClick = onGoBack, // 버튼을 누르면 전달받은 onGoBack 함수를 실행
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("다시 점검하기", fontSize = 18.sp)
        }
    }
}

// ... DetectedAppCard 함수는 이전과 동일 ...
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
                        Text("  - 이슈: 과도한 위험 권한 보유 (${issue.permissions.size}개)", color = WarningRed)
                    }
                    is SecurityIssue.TamperedSignature -> {
                        Text("  - 이슈: ★★★ 서명 변조 의심 ★★★", color = WarningRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}