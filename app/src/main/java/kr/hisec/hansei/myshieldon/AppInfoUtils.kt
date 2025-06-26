package kr.hisec.hansei.myshieldon

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

/**
 * 앱 설치 정보 분석 유틸리티 함수들을 제공하는 싱글톤 객체.
 */
object AppInfoUtils {

    /**
     * 앱 스토어 외부에서 설치된 앱 목록을 확인합니다.
     * @param context 애플리케이션 Context
     * @return 앱 스토어 외부에서 설치된 앱의 패키지명 목록
     */
    fun getNonStoreInstalledApps(context: Context): List<String> {
        val nonStoreApps = mutableListOf<String>()
        val packageManager = context.packageManager

        val installedPackages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)

        for (packageInfo in installedPackages) {
            val appInfo = packageInfo.applicationInfo

            // 널(null) 체크: appInfo가 null이 아닌 경우에만 아래 로직을 실행합니다.
            if (appInfo != null) { // <--- 이 줄을 추가합니다.
                // 1. 시스템 앱은 제외
                if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0) {
                    continue
                }

                // 2. 패키지 설치 소스를 확인합니다.
                val installerPackageName = packageManager.getInstallerPackageName(packageInfo.packageName)

                // installerPackageName이 null이거나 공식 스토어가 아니면 비공식 경로로 설치된 것으로 간주
                if (installerPackageName == null || !isOfficialStore(installerPackageName)) {
                    nonStoreApps.add(packageInfo.packageName)
                }
            }
        }
        return nonStoreApps
    }

    /**
     * 설치 소스 패키지명이 공식 앱 스토어인지 확인합니다.
     * @param installerPackageName 설치 소스 패키지명
     * @return 공식 스토어 여부
     */
    private fun isOfficialStore(installerPackageName: String): Boolean {
        // 공식 Google Play Store, Samsung Galaxy Store, One Store 등의 패키지명
        val officialStores = arrayOf(
            "com.android.vending", // Google Play Store
            "com.google.android.gms", // Google Play Services (Play Store와 관련됨)
            "com.sec.android.app.samsungapps", // Samsung Galaxy Store
            "com.skt.skaf.A000Z00040" // One Store (원스토어)
        )
        return installerPackageName in officialStores
    }
}