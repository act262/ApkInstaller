package io.zcx.sai

import android.app.Service
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.IBinder
import android.util.Log

class InstallApkService : Service() {

    private val TAG: String = "InstallApkService"

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent != null) {
            val status = intent.getIntExtra(
                PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE
            )
            Log.d(TAG, " onReceive status=$status")

            when (status) {
                // Install success
                PackageInstaller.STATUS_SUCCESS -> {
                    Log.d(TAG, "Install success")
                    success()
                }

                // Install need user confirm
                PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                    Log.d(TAG, "Install pending user confirm")
                    requireInstall(intent)
                }

                // Install abandon by user
                PackageInstaller.STATUS_FAILURE_ABORTED -> {
                    Log.d(TAG, "Install aborted by user")
                    aborted()
                }

                // Other install failure
                else -> {
                    val statusMsg = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
                    Log.d(TAG, "Install failure : ${statusMsg}")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun success() {

    }

    private fun aborted() {

    }

    private fun requireInstall(intent: Intent) {
        val newIntent = intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(newIntent)
    }
}
