package br.com.mobicare.cielo.commons.data.clients.api.newcieloservice

import com.google.common.truth.Truth.assertThat
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Test

class MockRequestFilterTest {
    companion object {
        private const val ORIGINAL_BASE_URL = "https://apihom.cielo.com.br"
        private const val MOCK_BASE_URL = "https://digitalhml.hdevelo.com.br/mock"
        private const val FLAVOR_DEV = "dev"
        private const val MOCKING_USER = "mock-"
    }

    private val requestBuilder = Request.Builder()
    private val originalUrl = "$ORIGINAL_BASE_URL/site-cielo/v1/pix/onboarding/fulfillment"
    private val transformedUrl = "$MOCK_BASE_URL/pix/onboarding/fulfillment"

    @Test
    fun `it should filter urls when their pattern matches`() {
        listOf(
            Pair("$ORIGINAL_BASE_URL/appcielo/v1/user-login/token", "$MOCK_BASE_URL/user-login/token"),
            Pair("$ORIGINAL_BASE_URL/site-cielo/v1/pix/onboarding", "$MOCK_BASE_URL/pix/onboarding"),
            Pair("$ORIGINAL_BASE_URL/site-cielo/v2/pix/onboarding", "$MOCK_BASE_URL/pix/onboarding"),
            Pair("$ORIGINAL_BASE_URL/site-cielo/v3/pix/onboarding", "$MOCK_BASE_URL/pix/onboarding"),
            Pair("$ORIGINAL_BASE_URL/site-cielo/v1/other/path", "$MOCK_BASE_URL/other/path"),
            Pair("$ORIGINAL_BASE_URL/appcielo/v2/another/path", "$MOCK_BASE_URL/another/path"),
            Pair("$ORIGINAL_BASE_URL/outro/exemplo", "$ORIGINAL_BASE_URL/outro/exemplo"),
            Pair("$ORIGINAL_BASE_URL/site-cielo/x1", "$ORIGINAL_BASE_URL/site-cielo/x1"),
            Pair("$ORIGINAL_BASE_URL/teste/v1", "$ORIGINAL_BASE_URL/teste/v1"),
        ).forEach { (inUrl, outUrl) ->
            MockRequestFilter(
                requestBuilder = requestBuilder.url(inUrl),
                usernameFromPreferences = MOCKING_USER,
                buildConfigFlavor = FLAVOR_DEV,
            ).filter()

            assertThat(requestBuilder.build().url.toString()).isEqualTo(outUrl)
        }
    }

    @Test
    fun `it should filter url based on username pattern`() {
        listOf(
            Pair("mock-", transformedUrl),
            Pair("mock-paramTest", transformedUrl),
            Pair("test", originalUrl),
            Pair("2211371790", originalUrl),
            Pair("26979691015", originalUrl),
        ).forEach { (username, outUrl) ->
            MockRequestFilter(
                requestBuilder = requestBuilder.url(originalUrl),
                usernameFromPreferences = username,
                buildConfigFlavor = FLAVOR_DEV,
            ).filter()

            assertThat(requestBuilder.build().url.toString()).isEqualTo(outUrl)
        }
    }

    @Test
    fun `it should filter url based on build flavor name`() {
        listOf(
            Pair("dev", transformedUrl),
            Pair("store", originalUrl),
        ).forEach { (flavor, outUrl) ->
            MockRequestFilter(
                requestBuilder = requestBuilder.url(originalUrl),
                usernameFromPreferences = MOCKING_USER,
                buildConfigFlavor = flavor,
            ).filter()

            assertThat(requestBuilder.build().url.toString()).isEqualTo(outUrl)
        }
    }

    @Test
    fun `it should send the correct header value`() {
        MockRequestFilter(
            requestBuilder = requestBuilder.url(originalUrl),
            usernameFromPreferences = "mock-paramTest",
            buildConfigFlavor = FLAVOR_DEV,
        ).filter()

        assertThat(requestBuilder.build().headers["Mock"]).isEqualTo("paramTest")
    }

    @Test
    fun `it should not transform blacklisted urls`() {
        listOf(
            "$ORIGINAL_BASE_URL/site-cielo/v1/configurations",
            "$ORIGINAL_BASE_URL/site-cielo/v1/configurations/featuretoggle",
        ).forEach { blacklistedUrl ->
            MockRequestFilter(
                requestBuilder = requestBuilder.url(blacklistedUrl),
                usernameFromPreferences = MOCKING_USER,
                buildConfigFlavor = FLAVOR_DEV,
            ).filter()

            assertThat(requestBuilder.build().url.toString()).isEqualTo(blacklistedUrl)
        }
    }

    @Test
    fun `it should parse request body to LoginRequest`() {
        // given
        val originalUserLoginTokenUrl = "$ORIGINAL_BASE_URL/appcielo/v1/user-login/token"
        val mockUserLoginTokenUrl = "$MOCK_BASE_URL/user-login/token"
        val builder =
            requestBuilder
                .url(originalUserLoginTokenUrl)
                .post(
                    "{\"username\":\"mock-\",\"password\":\"123456\"}"
                        .toRequestBody("application/json".toMediaTypeOrNull()),
                )

        // when
        MockRequestFilter(
            requestBuilder = builder,
            usernameFromPreferences = "",
            buildConfigFlavor = FLAVOR_DEV,
        ).filter()

        // then
        assertThat(requestBuilder.build().url.toString()).isEqualTo(mockUserLoginTokenUrl)
    }
}
