## 🔍 점검 항목별 개발 현황

| 항목 번호 | 점검 항목 | 상태 | 설명 / 구현 방식 |
|-----------|------------------------------|--------|-------------------------|
| 1 | 루팅 탐지 (Rooting Status Detection) | ✅ 완료 | `RootCheckUtils.kt` 내 아래 5가지 방식으로 종합 진단 수행<br><br>• `checkTestKeys()` - test-keys 여부 확인<br>• `checkSuperuserApk()` - `/system/app/Superuser.apk` 존재 여부<br>• `checkSuExists()` - `which su` 명령 수행으로 `su` 바이너리 존재 확인<br>• `checkDangerousProps()` - `ro.debuggable`, `ro.secure` 등 위험 속성 확인<br>• `checkRootManagementApps()` - 루팅 관리 앱 설치 여부 검사 |
| 2 | 비공식 스토어 설치 앱 탐지 (Unofficial App Sources Check) | 🔴 미구현 | `PackageManager.getInstallerPackageName()` 활용 예정. Google Play 외 설치 앱 탐지 계획 |
| 3 | 다운로드 경로 APK 파일 존재 여부 (APK File Presence Check) | 🔴 미구현 | `Environment.getExternalStorageDirectory()` 경로 내 `.apk` 확장자 탐색 예정 |
| 4 | 위험 권한 다중 보유 앱 목록 (Risky Permissions Application Check) | 🔴 미구현 | `Manifest.permission.*` 중 위험 권한 보유 수 ≥ 3인 앱 탐지 로직 구상 중 |
| 5 | 운영체제 최신 업데이트/보안 패치 정보 (OS Update/Security Patch Status) | 🟡 진행 중 | `Build.VERSION.RELEASE`, `Build.VERSION.SECURITY_PATCH` 등 활용, UI 표시만 구현됨 |
| 6 | 백그라운드 과다 사용 앱 (Monitoring Background Usage) | 🔴 미구현 | `UsageStatsManager` 기반 24시간 이상 백그라운드 사용량 높은 앱 탐지 예정 |
| 7 | 주요 금융/국민앱 APK 서명 무결성 검사 (App Signature Integrity Check) | 🟡 준비 중 | 금융/공공 앱 100개 대상 서명 SHA-256 정리 중. `PackageInfo.signatures` 활용 예정 |
| 8 | ADB 모드/알 수 없는 출처 허용 상태 점검 (Developer Mode Status Check) | 🔴 미구현 | `Settings.Global.ADB_ENABLED`, `Settings.Secure.INSTALL_NON_MARKET_APPS` 로 점검 예정 |

---

###🔧 구현 파일 참고

- `RootCheckUtils.kt` - 루팅 탐지 로직 포함
- `AppInfoUtils.kt` - 앱 서명/패키지 정보 관련 유틸 함수 구성
- `MainActivity.kt` - 원터치 진단 UI 및 함수 연결