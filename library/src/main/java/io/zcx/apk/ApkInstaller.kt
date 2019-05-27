package io.zcx.apk

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageInstaller
import android.os.Bundle
import android.os.IBinder
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
    fun install(context: Context, params: InstallParams? = null, installerCallback: InstallerCallback? = null) {
        // check
        if (params == null) {
            Log.e(TAG, "InstallParams null.")
            return
        }

        if (params.installApks == null || params.installApks!!.isEmpty()) {
            Log.e(TAG, "Install apk list is empty, no need install.")
            return
        }
        context.bindService(
            Intent(context, InstallApkService::class.java), object : ServiceConnection {
                override fun onServiceDisconnected(name: ComponentName?) {

                }

                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    (service as InstallApkService.MyBinder).bindCallback(object : InstallerCallback {
                        override fun onSuccess(bundle: Bundle?) {
                            installerCallback?.onSuccess(bundle)
                        }

                        override fun onFailure(bundle: Bundle?) {
                            installerCallback?.onFailure(bundle)
                        }

                        override fun onPending(bundle: Bundle?) {
                            installerCallback?.onPending(bundle)
                        }

                        override fun onAborted(bundle: Bundle?) {
                            installerCallback?.onAborted(bundle)
                        }
                    })
                }
            },
            Context.BIND_AUTO_CREATE
        )

        thread(true) {
            Log.d(TAG, "Verified...")
            verifiedApk(context, params.installApks!!)
            Log.d(TAG, "Installing...")
            installApk(context, params)
        }

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

        // install allow testOnly
        val installFlagsField = sessionParams.javaClass.getField("installFlags")
        var installFlags = installFlagsField.getInt(sessionParams)
        installFlags = installFlags.or(INSTALL_ALLOW_TEST)
        installFlagsField.setInt(sessionParams, installFlags)

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


    companion object {
        /**
         * @see PackageInstaller
         * @see PackageManager#INSTALL_ALLOW_TEST
         */
        private const val INSTALL_ALLOW_TEST = 0x00000004
    }
}