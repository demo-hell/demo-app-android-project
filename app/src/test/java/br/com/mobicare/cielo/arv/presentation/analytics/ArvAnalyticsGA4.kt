package br.com.mobicare.cielo.arv.presentation.analytics

import android.os.Bundle
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_AUTOMATIC_CONFIGURATION
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_SINGLE_CONFIRMATION
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_SINGLE_SUCCESS
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values
import br.com.mobicare.cielo.commons.constants.Text.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
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

class ArvAnalyticsGA4Test {

    private val eventNameSlot = slot<String>()
    private val eventsMapSlot = slot<Map<String, String>>()
    private val eventsListSlot = slot<ArrayList<Bundle>>()
    private val arvAnalyticsGA4 = ArvAnalyticsGA4()

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
    fun `logScreenView should call trackEvent with correct arguments without page number`() {
        arvAnalyticsGA4.logScreenView(
            screenName = SCREEN_VIEW_ARV_AUTOMATIC_CONFIGURATION,
        )

        verify {
            Analytics.GoogleAnalytics4Tracking.trackScreenView(
                SCREEN_VIEW_ARV_AUTOMATIC_CONFIGURATION
            )
        }
    }


    @Test
    fun `logScreenView should call trackEvent with correct arguments with page number`() {
        val pageNumber = "1"
        val screenName = SCREEN_VIEW_ARV_AUTOMATIC_CONFIGURATION
        val fullPageNumber = "${screenName}/${pageNumber}"

        arvAnalyticsGA4.logScreenView(
            screenName = screenName,
            pageNumber = pageNumber
        )

        verify {
            Analytics.GoogleAnalytics4Tracking.trackScreenView(
                fullPageNumber
            )
        }
    }

    @Test
    fun `logPurchase should call trackEvent with correct arguments`() {
        val screenName = "Test screen"
        val transactionId = "12345"
        val bankName = "testbank"
        val itemCategory3 = "Test Category"

        arvAnalyticsGA4.logPurchase(screenName, transactionId, bankName, itemCategory3)

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
                "screen_name" to screenName,
                "transaction_id" to transactionId,
                "transaction_type" to "contratacao",
                "bank" to bankName
            ), eventsMapSlot.captured
        )
    }

    @Test
    fun `logException should call trackEvent with correct parameters`() {
        val screenName = "Home"
        val error: NewErrorMessage? = null

        arvAnalyticsGA4.logException(screenName, error)

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
    fun `logAddPaymentInfo should call trackEvent with correct parameters`() {
        val screenName = "Home"
        val bankName = "Banco do Brasil"
        val itemCategory3 = "Some category"

        arvAnalyticsGA4.logAddPaymentInfo(screenName, bankName, itemCategory3)

        verify {
            Analytics.GoogleAnalytics4Tracking.trackEvent(
                capture(eventNameSlot),
                capture(eventsMapSlot),
                capture(eventsListSlot),
            )
        }

        assertEquals("add_payment_info", eventNameSlot.captured)
        assertEquals(
            mapOf(
                "screen_name" to screenName,
                "transaction_type" to "contratacao",
                "bank" to bankName.normalizeToLowerSnakeCase(),
            ), eventsMapSlot.captured
        )
    }


    @Test
    fun `logAnticipationSingleAddPaymentInfo should call trackEvent with correct parameters`() {
        val value = 100.0
        val periodStart = "2022-01-01"
        val periodEnd = "2022-01-31"
        val bankName = "Banco do Brasil"
        val itemCategory3 = "Some category"

        arvAnalyticsGA4.logAnticipationSingleAddPaymentInfo(
            value,
            periodStart,
            periodEnd,
            bankName,
            itemCategory3
        )

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
                "screen_name" to SCREEN_VIEW_ARV_SINGLE_CONFIRMATION,
                "currency" to GoogleAnalytics4Values.BRL,
                "value" to value,
                "transaction_type" to "contratacao",
                "period_start" to periodStart,
                "period_end" to periodEnd,
                "bank" to bankName.normalizeToLowerSnakeCase()
            ), eventsMapSlot.captured
        )
    }

    @Test
    fun `logSingleAnticipationSuccessPurchase should call trackEvent with correct parameters`() {
        val value = 100.0
        val transactionId = "123456"
        val periodStart = "2022-01-01"
        val periodEnd = "2022-01-31"
        val bankName = "Banco do Brasil"
        val itemCategory3 = "Some category"

        arvAnalyticsGA4.logSingleAnticipationSuccessPurchase(
            value,
            transactionId,
            periodStart,
            periodEnd,
            bankName,
            itemCategory3
        )

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
                "screen_name" to SCREEN_VIEW_ARV_SINGLE_SUCCESS,
                "transaction_id" to transactionId,
                "currency" to GoogleAnalytics4Values.BRL,
                "value" to value,
                "transaction_type" to "contratacao",
                "period_start" to periodStart,
                "period_end" to periodEnd,
                "bank" to bankName.normalizeToLowerSnakeCase()
            ), eventsMapSlot.captured
        )
    }


    @Test
    fun logDisplayContentTest() {
        val screenName = "Screen Name"
        val description = "Description"
        arvAnalyticsGA4.logDisplayContent(screenName, description)

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
                "description" to description,
                "content_component" to "message"
            ), eventsMapSlot.captured
        )
    }

    @Test
    fun logClickTest() {
        val screenName = "Screen Name"
        val contentComponent = "Content Component"
        val contentName = "Content name"
        val cardName = "Card name"

        arvAnalyticsGA4.logClick(screenName, contentName, cardName, contentComponent)

        verify {
            Analytics.GoogleAnalytics4Tracking.trackEvent(
                capture(eventNameSlot),
                capture(eventsMapSlot),
            )
        }

        assertEquals("click", eventNameSlot.captured)
        assertEquals(
            mapOf(
                "screen_name" to screenName,
                "content_type" to "button",
                "card_name" to cardName,
                "content_name" to contentName,
                "content_component" to contentComponent
            ), eventsMapSlot.captured
        )
    }

    @Test
    fun logBeginCheckoutTest() {
        val screenName = "Screen Name"
        val itemCategory2 = "Item Category 2"
        arvAnalyticsGA4.logBeginCheckout(screenName, itemCategory2)


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
                "screen_name" to screenName,
                "transaction_type" to "contratacao"
            ), eventsMapSlot.captured
        )
    }

    @Test
    fun logCancelTest() {
        arvAnalyticsGA4.logCancel("reason_test")


        verify {
            Analytics.GoogleAnalytics4Tracking.trackEvent(
                capture(eventNameSlot),
                capture(eventsMapSlot)
            )
        }

        assertEquals("cancel", eventNameSlot.captured)
        assertEquals(
            mapOf(
                "screen_name" to "/arv/cancelamento/sucesso",
                "cancellation_reason" to "reason_test"
            ), eventsMapSlot.captured
        )
    }
}