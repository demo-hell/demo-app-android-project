package br.com.mobicare.cielo.p2m.analytics

import android.os.Bundle
import br.com.mobicare.cielo.commons.analytics.Analytics
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Clock

class P2MGA4Test {

    private val eventNameSlot = slot<String>()
    private val eventsMapSlot = slot<Map<String, String>>()
    private val eventsListSlot = slot<ArrayList<Bundle>>()

    private val ga4 = P2MGA4()

    @Before
    fun setup() {
        mockkObject(Analytics.GoogleAnalytics4Tracking)
        mockkStatic(Clock::class)
        every { Clock.systemUTC().millis() } answers { 0L }
        every {
            Analytics.GoogleAnalytics4Tracking.trackEvent(
                any(), any(), any()
            )
        } answers { nothing }
        every {
            Analytics.GoogleAnalytics4Tracking.trackScreenView(
                any(), any()
            )
        } answers { nothing }
    }

    @Test
    fun logScreenView() {
        //ACT
        ga4.logScreenView(
            screenName = P2MGA4.SCREEN_VIEW_P2M_INTRODUCTION,
        )

        //ASSERT
        verify {
            Analytics.GoogleAnalytics4Tracking.trackScreenView(capture(eventNameSlot))
        }

        assertEquals("/home/p2m/introducao", eventNameSlot.captured)
    }

    @Test
    fun `logPurchase should call trackEvent with correct arguments`() {
        ga4.logPurchase(true)

        verify {
            Analytics.GoogleAnalytics4Tracking.trackEvent(
                capture(eventNameSlot), capture(eventsMapSlot), capture(eventsListSlot)
            )
        }
        assertEquals("purchase", eventNameSlot.captured)
        assertEquals(
            mapOf(
                "screen_name" to "/home/p2m/finalize_o_cadastro_no_whatsapp_business/sucesso",
                "transaction_id" to "t_venda_whatsapp.0",
                "transaction_type" to "contratacao"
            ), eventsMapSlot.captured
        )
    }
}