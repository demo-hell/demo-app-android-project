package br.com.mobicare.cielo.commons.analytics

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import com.datadog.android.Datadog
import com.datadog.android.log.Logger
import com.google.firebase.crashlytics.FirebaseCrashlytics

class DatadogEvent(private val context: Context, private val userPreferences: UserPreferences) {

    fun LoggerInfo(message: String, key: String, value: String) {
        try {
            val logger = logger()
            logger?.i("$message EC: ${userPreferences.numeroEC}", attributes = mapOf(key to value))
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun logger(): Logger? {
        if (Datadog.isInitialized().not()) {
            DatadogService(context)
        }
        if (Datadog.isInitialized()) {
            return Logger.Builder()
                .setNetworkInfoEnabled(true)
                .setLogcatLogsEnabled(true)
                .setBundleWithTraceEnabled(true)
                .setLoggerName(BuildConfig.APPLICATION_ID)
                .build()
        }
        return null
    }
}