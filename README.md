[![Download](https://api.bintray.com/packages/act262/maven/ApkInstaller/images/download.svg)](https://bintray.com/act262/maven/ApkInstaller/_latestVersion)

### Demo
```sh
./run.sh
```

### Usage

#### Add dependency

Add jcenter repositories

app `build.gradle`
```groovy
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])

    // Add here
    implementation 'io.zcx.apk:ApkInstaller:$latestVersion'
}

```

#### install apk
```kotlin
        val apks = setOf(
            File("/sdcard", "plugin_biz_feature1-debug.apk"),
            File("/sdcard", "plugin_biz_feature2-debug.apk"),
            File("/sdcard", "plugin_biz_feature3-debug.apk")
        )

        val apkInstaller = ApkInstaller(this)
        apkInstaller.install(
            InstallParams()
                .setDontKillApp(true)
                .allowTestOnly(true)
                .setInstallApks(apks),
            object : InstallerCallback {
                override fun onSuccess(p0: Bundle?) {
                    toast("Install succeed")
                }

                override fun onFailure(p0: Bundle?) {
                    toast("Install failure")
                }

                override fun onPending(p0: Bundle?) {
                }

                override fun onAborted(p0: Bundle?) {
                    toast("Install aborted")
                }
            })
```