package io.zcx.sai

import android.app.Service
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log

class InstallApkService : Service() {

    private val TAG: String = "InstallApkService"

    class MyBinder : Binder() {
        var callback: InstallerCallback? = null
        fun bindCallback(callback: InstallerCallback) {
            this.callback = callback
        }

    }

    private val binder = MyBinder()

    override fun onBind(intent: Intent): IBinder {
        return binder
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
                    success(intent)
                }

                // Install need user confirm
                PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                    Log.d(TAG, "Install pending user confirm")
                    requireInstall(intent)
                }

                // Install abandon by user
                PackageInstaller.STATUS_FAILURE_ABORTED -> {
                    Log.d(TAG, "Install aborted by user")
                    aborted(intent)
                }

                // Other install failure
                else -> {
                    val statusMsg = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
                    Log.d(TAG, "Install failure : ${statusMsg}")
                    failure(intent)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun success(intent: Intent) {
        val bundle = Bundle()
        binder.callback?.onSuccess(bundle)
    }

    private fun aborted(intent: Intent) {
        binder.callback?.onAborted(Bundle())
    }

    private fun failure(intent: Intent) {
        val msg = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
        val bundle = Bundle()
        bundle.putString("msg", msg)

        binder.callback?.onFailure(bundle)
    }

    private fun requireInstall(intent: Intent) {
        binder.callback?.onPending(Bundle())

        val newIntent = intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(newIntent)
    }
}
