package br.com.mobicare.cielo.newRecebaRapido.analytics

import android.os.Bundle
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.utils.getTimestampNow
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RAGA4Test {

    private val eventNameSlot = slot<String>()
    private val eventsMapSlot = slot<Map<String, String>>()
    private val eventsListSlot = slot<ArrayList<Bundle>>()
    private val rAGA4 = RAGA4()

    @Before
    fun setup() {
        mockkStatic(::getTimestampNow)
        every { getTimestampNow() } answers {1L}
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
    fun `logScreenView should call trackScreenView with correct Arguments`() {
        rAGA4.logScreenView(
            screenName = "someScreenName",
        )

        verify {
            Analytics.GoogleAnalytics4Tracking.trackScreenView(
                "someScreenName"
            )
        }
    }

    @Test
    fun `logScreenView should call trackScreenView with offer path when is offer`() {
        rAGA4.logScreenView(
            screenName = rAGA4.getHomeScreenName(true),
        )

        verify {
            Analytics.GoogleAnalytics4Tracking.trackScreenView(
                "/servicos/recebimento_automatico/oferta"
            )
        }
    }

    @Test
    fun `logScreenView should call trackScreenView`() {
        rAGA4.logScreenView(
            screenName = rAGA4.getHomeScreenName(false),
        )

        verify {
            Analytics.GoogleAnalytics4Tracking.trackScreenView(
                "/servicos/recebimento_automatico"
            )
        }
    }

    @Test
    fun `logRABeginCheckout should call trackEvent with correct arguments`() {
        val transactionType = "BOTH"
        val periodicitySelected = "DAILY"

        rAGA4.logRABeginCheckout(transactionType, periodicitySelected)

        verify {
            Analytics.GoogleAnalytics4Tracking.trackEvent(
                capture(eventNameSlot),
                capture(eventsMapSlot),
                capture(eventsListSlot)
            )
        }

        assertEquals("begin_checkout", eventNameSlot.captured)
        assertEquals(
            mapOf(
                "screen_name" to "/servicos/recebimento_automatico/customizado/vendas_parceladas_e_a_vista",
                "transaction_type" to "contratacao"
            ), eventsMapSlot.captured
        )
    }

    @Test
    fun `logPurchase should call trackEvent with correct arguments`() {
        val transactionType = "BOTH"
        val periodicitySelected = "DAILY"

        rAGA4.logPurchase(transactionType, periodicitySelected)

        verify {
            Analytics.GoogleAnalytics4Tracking.trackEvent(
                capture(eventNameSlot),
                capture(eventsMapSlot),
                capture(eventsListSlot)
            )
        }

        assertEquals("purchase", eventNameSlot.captured)
        assertEquals(
            mapOf(
                "screen_name" to "/servicos/recebimento_automatico/customizado/vendas_parceladas_e_a_vista/sucesso" ,
                "transaction_id" to "t_recebimento_automatico_customizado.1",
                "transaction_type" to "contratacao"
            ), eventsMapSlot.captured
        )
    }

    @Test
    fun `logPurchase should call trackEvent when credit type selected`() {
        val transactionType = "CREDIT"
        val periodicitySelected = "DAILY"

        rAGA4.logPurchase(transactionType, periodicitySelected)

        verify {
            Analytics.GoogleAnalytics4Tracking.trackEvent(
                capture(eventNameSlot),
                capture(eventsMapSlot),
                capture(eventsListSlot)
            )
        }

        assertEquals("purchase", eventNameSlot.captured)
        assertEquals(
            mapOf(
                "screen_name" to "/servicos/recebimento_automatico/customizado/a_vista/sucesso" ,
                "transaction_id" to "t_recebimento_automatico_customizado.1",
                "transaction_type" to "contratacao"
            ), eventsMapSlot.captured
        )
    }

    @Test
    fun `logPurchase should call trackEvent when installment type selected`() {
        val transactionType = "INSTALLMENT"
        val periodicitySelected = "DAILY"

        rAGA4.logPurchase(transactionType, periodicitySelected)

        verify {
            Analytics.GoogleAnalytics4Tracking.trackEvent(
                capture(eventNameSlot),
                capture(eventsMapSlot),
                capture(eventsListSlot)
            )
        }

        assertEquals("purchase", eventNameSlot.captured)
        assertEquals(
            mapOf(
                "screen_name" to "/servicos/recebimento_automatico/customizado/parceladas/sucesso" ,
                "transaction_id" to "t_recebimento_automatico_customizado.1",
                "transaction_type" to "contratacao"
            ), eventsMapSlot.captured
        )
    }

    @Test
    fun `logRAConfirmAddPaymentInfo should call trackEvent with correct arguments`() {
        val transactionType = "BOTH"
        val periodicitySelected = "DAILY"

        rAGA4.logRAConfirmAddPaymentInfo(transactionType, periodicitySelected)

        verify {
            Analytics.GoogleAnalytics4Tracking.trackEvent(
                capture(eventNameSlot),
                capture(eventsMapSlot),
                capture(eventsListSlot)
            )
        }

        assertEquals("add_payment_info", eventNameSlot.captured)
        assertEquals(
            mapOf(
                "screen_name" to "/servicos/recebimento_automatico/customizado/vendas_parceladas_e_a_vista/confirmar_solicitacao",
                "transaction_type" to "contratacao"
            ), eventsMapSlot.captured
        )
    }
}