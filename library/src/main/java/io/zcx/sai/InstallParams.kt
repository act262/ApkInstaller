package io.zcx.sai

import java.io.File

class InstallParams {

    var fullInstall = false
    var installBase = false

    var dontKillApp = false

    var replaceExist = false

    var installDir: File? = null

    var installApks: Set<File>? = null

    fun setDontKillApp(dontKillApp: Boolean): InstallParams {
        this.dontKillApp = dontKillApp
        return this
    }

    fun setInstallDir(installDir: File): InstallParams {
        this.installDir = installDir
        return this
    }

    fun setInstallApks(installApks: Set<File>): InstallParams {
        this.installApks = installApks
        return this
    }

}
