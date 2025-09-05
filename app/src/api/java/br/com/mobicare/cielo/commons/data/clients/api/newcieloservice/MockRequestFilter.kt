package br.com.mobicare.cielo.commons.data.clients.api.newcieloservice

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.constants.DASH
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import br.com.mobicare.cielo.newLogin.domain.LoginRequest
import com.google.gson.Gson
import okhttp3.Request
import okio.Buffer

/**
 * Class for filtering requests in development builds for specific usernames starting with "mock-".
 *
 * @property requestBuilder The builder for the request to be filtered.
 * @property usernameFromPreferences The username of the user making the request.
 * @property buildConfigFlavor The flavor of the build, used to determine if it's a development build.
 */
class MockRequestFilter(
    private val requestBuilder: Request.Builder,
    private val usernameFromPreferences: String,
    buildConfigFlavor: String = BuildConfig.FLAVOR,
) {
    private val isDevBuild = buildConfigFlavor == FLAVOR_DEV

    private val originalRequest = requestBuilder.build()

    private val originalUrl = originalRequest.url().toString()

    private val username by lazy { usernameFromLoginRequest ?: usernameFromPreferences }

    private val isMockingUser get() = username.startsWith(MOCKING_USER_PATTERN)

    private val parameter get() =
        username.split(DASH).run {
            if (size > ONE) get(ONE) else null
        }

    private val transformedUrl get() =
        ORIGINAL_URL_PATTERN.toRegex().replace(originalUrl) {
            "$MOCK_BASE_URL${it.groupValues[TWO]}"
        }

    private val usernameFromLoginRequest get() =
        if (originalUrl.contains(USER_LOGIN_TOKEN_PATH)) {
            originalRequest.body()?.let { body ->
                try {
                    val buffer = Buffer().also { body.writeTo(it) }
                    val bodyString = buffer.readUtf8()
                    val loginRequest = Gson().fromJson(bodyString, LoginRequest::class.java)
                    loginRequest.username
                } catch (e: Exception) {
                    e.message.logFirebaseCrashlytics()
                    null
                }
            }
        } else {
            null
        }

    private val isBlacklistedUrl get() = BLACKLIST_URLS.any { originalUrl.startsWith(it) }

    /**
     * Filters the request if the current build is a development build and the user is a mocking user.
     */
    fun filter() {
        if (isDevBuild && isMockingUser && isBlacklistedUrl.not()) {
            requestBuilder.apply {
                url(transformedUrl)
                parameter?.let { if (it.isNotBlank()) addHeader(MOCK_HEADER_KEY, it) }
            }
        }
    }

    companion object {
        private const val MOCK_BASE_URL = "https://digitalhml.hdevelo.com.br/mock"
        private const val ORIGINAL_URL_PATTERN = "https://apihom\\.cielo\\.com\\.br/(site-cielo|appcielo)/v\\d+(/.*)"
        private const val MOCKING_USER_PATTERN = "mock-"
        private const val USER_LOGIN_TOKEN_PATH = "/user-login/token"
        private const val FLAVOR_DEV = "dev"
        private const val MOCK_HEADER_KEY = "Mock"
        private val BLACKLIST_URLS =
            listOf(
                "https://apihom.cielo.com.br/site-cielo/v1/configurations",
                "https://apihom.cielo.com.br/site-cielo/v1/configurations/featuretoggle",
            )
    }
}
