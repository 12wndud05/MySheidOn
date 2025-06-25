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
                        Button(onClick = {
                            val isRooted = SecurityCheckUtils.isDeviceRooted()
                            val message = if (isRooted) "경고: 기기가 루팅되었습니다!" else "안전: 기기가 루팅되지 않았습니다."
                            android.widget.Toast.makeText(
                                this@MainActivity,
                                message,
                                android.widget.Toast.LENGTH_LONG
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
