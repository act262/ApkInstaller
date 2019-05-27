package io.zcx.apk.demo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.zcx.apk.ApkInstaller
import io.zcx.apk.InstallParams
import io.zcx.apk.InstallerCallback
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println("  $classLoader")
    }

    fun installApk(view: View) {
        var dontKillApp = cb_dont_kill_app.isChecked
        when (view.id) {
            R.id.btn_install_all -> {
                val apks = setOf(
                        File("/sdcard", "dynamic_feature1-debug.apk"),
                        File("/sdcard", "dynamic_feature2-debug.apk")
                )

                installApk(apks, dontKillApp)
            }

            R.id.btn_install_feature1 -> {
                installApk(setOf(File("/sdcard/dynamic_feature1-debug.apk")), dontKillApp)
            }

            R.id.btn_install_feature2 -> {
                installApk(setOf(File("/sdcard/dynamic_feature2-debug.apk")), dontKillApp)
            }
        }
    }

    private fun installApk(apks: Set<File>, dontKillApp: Boolean) {
        // Check storage permission
        val re = checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (re == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 10
            )

            return
        }

        val installer = packageManager.packageInstaller
        installer.registerSessionCallback(object : PackageInstaller.SessionCallback() {
            override fun onProgressChanged(sessionId: Int, progress: Float) {
                println("MainActivity.onProgressChanged")
                println("sessionId = [${sessionId}], progress = [${progress}]")
            }

            override fun onActiveChanged(sessionId: Int, active: Boolean) {
            }

            override fun onFinished(sessionId: Int, success: Boolean) {
                println("MainActivity.onFinished")
                println("sessionId = [${sessionId}], success = [${success}]")
            }

            override fun onBadgingChanged(sessionId: Int) {
            }

            override fun onCreated(sessionId: Int) {
                println("MainActivity.onCreated")
                println("sessionId = [${sessionId}]")
            }

        })

        val apkInstaller = ApkInstaller(this)
        apkInstaller.install(
                InstallParams()
                        .setDontKillApp(dontKillApp)
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
    }

    fun goFeature1(view: View) {
        try {
            val clazz = Class.forName("io.zcx.apk.dynamic_feature1.Feature1Activity")

            alert(Appcompat, "Loaded feature1 apk", "Hi there") {
                yesButton {
                    toast("Oh…")
                    startActivity(Intent(this@MainActivity, clazz))
                }
                noButton {}
            }.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun goFeature2(view: View) {
        try {
            val clazz = Class.forName("io.zcx.apk.dynamic_feature2.Feature2Activity")

            startActivity(Intent(this@MainActivity, clazz))
        } catch (e: Exception) {
        }
    }

}
