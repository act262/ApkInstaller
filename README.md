

### Usage

#### Add dependency
app `build.gradle`
```groovy
repositories {
     mavenLocal()
     google()
     jcenter()
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])

    // Add here
    implementation 'io.zcx.apk:ApkInstaller:0.0.1'
}

```

#### install apk
```kotlin
        val apks = setOf(
            File("/sdcard", "plugin_biz_feature1-debug.apk"),
            File("/sdcard", "plugin_biz_feature2-debug.apk"),
            File("/sdcard", "plugin_biz_feature3-debug.apk")
        )

        ApkInstaller().install(
            this,
            InstallParams()
                .setDontKillApp(false)
                .setInstallApks(apks), object : InstallerCallback {
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
            }
        )

```