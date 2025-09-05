package br.com.mobicare.cielo.commons.analytics

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.constants.DatadogEnvironments
import br.com.mobicare.cielo.commons.constants.MainConstants.BUILD_TYPE_RELEASE
import br.com.mobicare.cielo.commons.constants.MainConstants.FLAVOR_STORE
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import com.datadog.android.Datadog
import com.datadog.android.DatadogSite
import com.datadog.android.core.configuration.Configuration
import com.datadog.android.core.configuration.Credentials
import com.datadog.android.privacy.TrackingConsent
import com.datadog.android.rum.GlobalRum
import com.datadog.android.rum.RumMonitor
import com.datadog.android.rum.tracking.MixedViewTrackingStrategy
import com.datadog.android.rum.tracking.ViewTrackingStrategy
import com.datadog.android.tracing.AndroidTracer
import io.opentracing.util.GlobalTracer

class DatadogService(context: Context) {

    private val trackingStrategy: ViewTrackingStrategy = MixedViewTrackingStrategy(true)
    private val trackingConsent: TrackingConsent = TrackingConsent.GRANTED
    private val clientToken = BuildConfig.DATADOG_TOKEN
    private val applicationId = BuildConfig.DATADOG_APPLICATION_ID
    private val appVariantName = BuildConfig.FLAVOR
    private val tracedHosts = listOf(BuildConfig.HOST_API)

    init {
        Datadog.initialize(context, credentials(), configuration(), trackingConsent)
        setUserInfo()
        registerAndroidTracer()
        registerRumMonitor()
    }

    private fun setUserInfo() {
        val user = UserPreferences.getInstance().currentUserLogged
        if (user != null && Datadog.isInitialized()) {
            Datadog.setUserInfo(
                user.ecNumber,
                user.username,
                user.email
            )
        }
    }

    private fun registerAndroidTracer() {
        GlobalTracer.registerIfAbsent(
            AndroidTracer.Builder()
                .setServiceName(BuildConfig.APPLICATION_ID)
                .build()
        )
    }

    private fun configuration(): Configuration {
        return Configuration.Builder(
            logsEnabled = true,
            tracesEnabled = true,
            crashReportsEnabled = true,
            rumEnabled = true
        ).useSite(DatadogSite.US1)
            .trackInteractions()
            .trackLongTasks()
            .setFirstPartyHosts(tracedHosts)
            .useViewTrackingStrategy(trackingStrategy)
            .trackBackgroundRumEvents(true)
            .build()
    }

    private fun registerRumMonitor() {
        GlobalRum.registerIfAbsent(RumMonitor.Builder().build())
    }

    private fun credentials(): Credentials {
        return Credentials(
            clientToken,
            environment(),
            appVariantName,
            applicationId
        )
    }

    private fun environment(): String {
        return if (BuildConfig.FLAVOR == FLAVOR_STORE && BuildConfig.BUILD_TYPE == BUILD_TYPE_RELEASE)
            DatadogEnvironments.DATADOG_PROD
        else
            DatadogEnvironments.DATAGOG_HOMOLOG
    }
}
