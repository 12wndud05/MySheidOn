package kr.hisec.hansei.myshieldon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kr.hisec.hansei.myshieldon.ui.theme.MyShieldOnTheme
import android.widget.Toast

 class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyShieldOnTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    //Column을 사용하여 버튼을 중앙에 배치*/
                    Column(
                        modifier = Modifier
                            .fillMaxSize() // 화면 전체 채우기
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center, // 세로 중앙 정렬
                        horizontalAlignment = Alignment.CenterHorizontally // 가로 중앙 정렬
                    ) {
                        Text(
                            text = "ShieldOn 스마트폰 점검",
                            modifier = Modifier.padding(bottom = 16.dp) // 버튼과 텍스트 사이 간격
                        )
                        // MainActivity.kt 파일의 Button(onClick = { ... })
                        Button(onClick = {
                            // 1. 루팅 여부 체크
                            val isRooted = RootCheckUtils.isDeviceRooted() // RootCheckUtils 파일의 함수 호출
                            // 'rootMessage' 변수가 여기서 선언되어야 합니다.
                            val rootMessage = if (isRooted) "경고: 기기가 루팅되었습니다!" else "안전: 기기가 루팅되지 않았습니다."
                            Toast.makeText(
                                this@MainActivity,
                                rootMessage,
                                Toast.LENGTH_LONG
                            ).show()

                            // 2. 앱 스토어 외 설치 앱 체크
                            val nonStoreApps = AppInfoUtils.getNonStoreInstalledApps(this@MainActivity)
                            val nonStoreAppMessage = if (nonStoreApps.isNotEmpty()) {
                                "경고: 스토어 외 설치 앱 ${nonStoreApps.size}개 발견!\n${nonStoreApps.joinToString(", ")}"
                            } else {
                                "안전: 스토어 외 설치 앱이 없습니다."
                            }

                            // 3. 두 결과를 합쳐서 Toast 메시지로 표시
                            // 'rootMessage'와 'nonStoreAppMessage'가 이 시점에서 모두 선언되어 있어야 합니다.


                            Toast.makeText(
                                this@MainActivity,
                                nonStoreAppMessage,
                                Toast.LENGTH_LONG
                            ).show()
                        }) {
                            Text("점검 시작")
                        }
                    }
                }
            }
        }
    }
}
