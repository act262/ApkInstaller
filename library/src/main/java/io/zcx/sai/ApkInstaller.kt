package io.zcx.sai

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.util.Log
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import kotlin.concurrent.thread

/**
 * APK installer
 *
 * Work on API 21+ (L)
 */
class ApkInstaller {

    private val TAG: String = ApkInstaller::class.java.simpleName

    /**
     * Install split apk with InstallParams
     */
    fun install(context: Context, params: InstallParams?) {
        // check
        if (params == null) {
            Log.e(TAG, "InstallParams null.")
            return
        }

        if (params.installApks == null || params.installApks!!.isEmpty()) {
            Log.e(TAG, "Install apk list is empty, no need install.")
            return
        }

        thread {
            Log.d(TAG, "Installing...")
            verifiedApk(context, params.installApks!!)
            installApk(context, params)
            Log.d(TAG, "Install done.")
        }.start()
    }

    private fun verifiedApk(context: Context, apks: Set<File>) {
        for (apk in apks) {
//            TODO(verified apk)
        }
    }

    private fun installApk(context: Context, params: InstallParams) {
        val sessionId = create(context, params)

        for (file in params.installApks!!) {
            write(context, sessionId, file)
        }

        commit(context, sessionId)
    }

    private fun create(context: Context, params: InstallParams): Int {
        val installer = context.packageManager.packageInstaller
        val sessionParams = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_INHERIT_EXISTING)
        sessionParams.setAppPackageName(context.packageName)

        // --dont-kill
        if (params.dontKillApp) {
            val method = sessionParams.javaClass.getMethod("setDontKillApp", Boolean::class.javaPrimitiveType)
            method.invoke(sessionParams, true)
        }

        val sessionId = installer.createSession(sessionParams)
        return sessionId
    }

    private fun write(context: Context, sessionId: Int, file: File) {
        val installer = context.packageManager.packageInstaller
        val session = installer.openSession(sessionId)
        val out = session.openWrite(file.name, 0, file.length())
        BufferedInputStream(FileInputStream(file)).copyTo(out)
        session.fsync(out)
        out.close()
        session.close()
    }

    private fun commit(context: Context, sessionId: Int) {
        val installer = context.packageManager.packageInstaller
        val session = installer.openSession(sessionId)
        val intent = Intent(context, InstallApkService::class.java)
        val intentSender = PendingIntent.getService(
            context, 0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        ).intentSender
        session.commit(intentSender)
        session.close()
    }

}