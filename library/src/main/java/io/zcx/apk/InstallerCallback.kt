package io.zcx.apk

import android.os.Bundle

/**
 * Install action callback.
 */
interface InstallerCallback {

    /**
     * Installation successful
     */
    fun onSuccess(bundle: Bundle)

    /**
     * Installation failing
     */
    fun onFailure(bundle: Bundle)

    /**
     * Installation aborted
     */
    fun onAborted(bundle: Bundle)

    /**
     * Installation waiting for user confirm
     */
    fun onPending(bundle: Bundle)

}
