package br.com.mobicare.cielo.balcaoRecebiveisExtrato.analytics

import br.com.mobicare.cielo.balcaoRecebiveisExtrato.analytics.ReceivablesAnalyticsGA4Constants.CIELO_NEGOTIATION
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.analytics.ReceivablesAnalyticsGA4Constants.DETAILS_OF_OPERATION
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.analytics.ReceivablesAnalyticsGA4Constants.SCREEN_VIEW_RECEIVABLES_NEGOTIATION_CIELO
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values
import br.com.mobicare.cielo.commons.constants.Text.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ReceivablesAnalyticsGA4Test {

    private val eventNameSlot = slot<String>()
    private val eventsMapSlot = slot<Map<String, String>>()
    private val analyticsGA4 = ReceivablesAnalyticsGA4()

    @Before
    fun setup() {
        mockkObject(Analytics.GoogleAnalytics4Tracking)
        every {
            Analytics.GoogleAnalytics4Tracking.trackEvent(
                any(),
                any(),
                any(),
                any()
            )
        } answers { nothing }
        every {
            Analytics.GoogleAnalytics4Tracking.trackScreenView(
                any(), any()
            )
        } answers { nothing }
    }

    @After
    fun tearDown() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `logScreenView should call trackEvent`() {
        analyticsGA4.logScreenView(
            screenName = SCREEN_VIEW_RECEIVABLES_NEGOTIATION_CIELO,
        )

        verify {
            Analytics.GoogleAnalytics4Tracking.trackScreenView(
                SCREEN_VIEW_RECEIVABLES_NEGOTIATION_CIELO
            )
        }
    }

    @Test
    fun `logException should call trackEvent with correct parameters`() {
        val screenName = "Home"
        val error: NewErrorMessage? = null

        analyticsGA4.logException(screenName, error)

        verify {
            Analytics.GoogleAnalytics4Tracking.trackEvent(
                capture(eventNameSlot),
                capture(eventsMapSlot),
            )
        }

        assertEquals("exception", eventNameSlot.captured)
        assertEquals(
            mapOf(
                "screen_name" to screenName,
                "description" to EMPTY,
                "status_code" to EMPTY,
            ), eventsMapSlot.captured
        )
    }

    @Test
    fun logDisplayContentTest() {
        val screenName = "Screen Name"
        analyticsGA4.logDisplayContent(screenName)

        verify {
            Analytics.GoogleAnalytics4Tracking.trackEvent(
                capture(eventNameSlot),
                capture(eventsMapSlot)
            )
        }

        assertEquals("display_content", eventNameSlot.captured)
        assertEquals(
            mapOf(
                "screen_name" to screenName,
                "content_type" to GoogleAnalytics4Values.MODAL,
                "content_component" to CIELO_NEGOTIATION,
                "description" to DETAILS_OF_OPERATION
            ), eventsMapSlot.captured
        )
    }
}