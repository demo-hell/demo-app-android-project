package br.com.mobicare.cielo.commons.utils

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef.LATEST_ACTIVE_VERSION_ANDROID
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef.LATEST_VERSION_ANDROID
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference

fun Unit.executeUpdateDialog(runOnUpdateCondition: (needsUpdate: Boolean, forceUpdate: Boolean) -> Unit) {

    val configurationPref = ConfigurationPreference.instance

    val latestVersion = configurationPref
            .getConfigurationValue(LATEST_VERSION_ANDROID, "")

    val latestActiveVersion = configurationPref
            .getConfigurationValue(LATEST_ACTIVE_VERSION_ANDROID, "")

    val currentAppVersion = BuildConfig.VERSION_NAME
    val needsUpdate = (latestVersion.isNotEmpty() && compareVersions(currentAppVersion, latestVersion) == -1)
    val forceUpdate = (needsUpdate && latestActiveVersion.isNotEmpty() && compareVersions(currentAppVersion, latestActiveVersion) == -1)

    runOnUpdateCondition(needsUpdate, forceUpdate)
}

fun compareVersions(versionA: String, versionB: String): Int{

    val a = versionA.replace("[^0-9.]", "")
    val b = versionB.replace("[^0-9.]", "")

    if(a == b) return 0

    val numbsA = a.split('.').map { it.toInt() }
    val numbsB = b.split('.').map { it.toInt() }

    numbsA.forEachIndexed { index, numA ->
        if(numA > numbsB[index]){
            return 1
        }else if(numA < numbsB[index]){
            return -1
        }
    }
    return 0
}
