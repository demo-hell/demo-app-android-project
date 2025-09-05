package br.com.mobicare.cielo.commons.data.clients.api.newcieloservice

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.constants.MainConstants.BUILD_TYPE_RELEASE
import br.com.mobicare.cielo.commons.constants.MainConstants.FLAVOR_STORE
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import com.datadog.android.DatadogEventListener
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.datadog.android.DatadogInterceptor
import com.datadog.android.tracing.TracingInterceptor

fun <S> createCieloService(
    serviceClass: Class<S>,
    baseUrl: String,
    interceptor: CieloInterceptor
): S {

    val okHttpClientBuilder = createOkHttpClient(interceptor)

    if (BuildConfig.BUILD_TYPE.equals(BUILD_TYPE_RELEASE, true).not()) {
        okHttpClientBuilder.addInterceptor(HttpLoggingInterceptor().getSimpleLogging())
    }

    val isToggle =
        FeatureTogglePreference.instance.isActivate(FeatureTogglePreference.CERTIFICATE_PINNING)

    if (isToggle && BuildConfig.FLAVOR == FLAVOR_STORE &&
        BuildConfig.BUILD_TYPE == BUILD_TYPE_RELEASE
    ) {
        val certificate = CertificatePinner.Builder()
            .add(
                BuildConfig.URL_CERTIFICATE_PRD,
                BuildConfig.SHA_CERTIFICATE_PRD_RSA_AKAMAI
            ).build()
        okHttpClientBuilder.certificatePinner(certificate)
    }

    val retrofit = getClientBuilder(baseUrl)
        .client(okHttpClientBuilder.build())
        .build()
    return retrofit.create(serviceClass)
}

private fun createOkHttpClient(interceptor: CieloInterceptor): OkHttpClient.Builder = OkHttpClient.Builder().apply {
    readTimeout(NetworkConstants.READ_TIMEOUT_LONG, TimeUnit.SECONDS)
    connectTimeout(NetworkConstants.CONNECTION_TIMEOUT_LONG, TimeUnit.SECONDS)
    addInterceptor(interceptor)
    addInterceptor(DatadogInterceptor(listOf(BuildConfig.HOST_API)))
    addNetworkInterceptor(TracingInterceptor(listOf(BuildConfig.HOST_API)))
    eventListenerFactory(DatadogEventListener.Factory())
}

private fun getClientBuilder(baseUrl: String): Retrofit.Builder {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
}