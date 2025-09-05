package br.com.mobicare.cielo.meusRecebimentos.presentation.presenter.home

import android.os.Handler
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.DateTimeHelper
import br.com.mobicare.cielo.commons.utils.DataCustom
import br.com.mobicare.cielo.commons.utils.format
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.domain.Feature
import br.com.mobicare.cielo.home.presentation.meusrecebimentonew.MeusRecebimentosHomeContract
import br.com.mobicare.cielo.home.presentation.meusrecebimentonew.MyReceiptsHomePresenter
import br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph.repository.MeusRecebimentosGraficoRepository
import br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph.repository.PostingsResponse
import br.com.mobicare.cielo.meusrecebimentosnew.models.Summary
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.*

class MyReceiptsHomePresenterTest {

    private val featureToggleObj = Feature(
        featureName = FeatureTogglePreference.MEUS_RECEBIMENTOS,
        show = true,
        status = "activated",
        statusMessage = "ft activated"
    )

    private val yesterdayDate = DataCustom(DateTimeHelper.decreaseDateByNumberDays(Calendar.getInstance().time, ONE)).toDate().format()
    private val todayDate = Date().format()

    private val response = "{\n" +
            "  \"summary\": [\n" +
            "    {\n" +
            "      \"totalAmount\": 7986.21,\n" +
            "      \"date\": \"${yesterdayDate}\",\n" +
            "      \"status\": \"Previsto\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"totalAmount\": 555.21,\n" +
            "      \"date\": \"${todayDate}\",\n" +
            "      \"status\": \"Previsto\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"items\": []\n" +
            "}"

    private val incorrectDateResponse = "{\n" +
            "  \"summary\": [\n" +
            "    {\n" +
            "      \"totalAmount\": 7986.21,\n" +
            "      \"date\": \"2023-01-02\",\n" +
            "      \"status\": \"Previsto\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"totalAmount\": 555.21,\n" +
            "      \"date\": \"2023-01-03\",\n" +
            "      \"status\": \"Previsto\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"items\": []\n" +
            "}"

    @Mock
    lateinit var view: MeusRecebimentosHomeContract.View

    @Mock
    lateinit var repository: MeusRecebimentosGraficoRepository

    @Mock
    private lateinit var featureTogglePreference: FeatureTogglePreference

    @Mock
    lateinit var handler: Handler

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()
    private lateinit var presenter: MyReceiptsHomePresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        doReturn(featureToggleObj).whenever(featureTogglePreference).getFeatureToggleObject(FeatureTogglePreference.MEUS_RECEBIMENTOS)
        presenter = MyReceiptsHomePresenter(view, repository, featureTogglePreference, uiScheduler, ioScheduler, handler)
    }

    @Test
    fun `success on get receivables`() {
        val jsonResponse = Gson().fromJson(response, PostingsResponse::class.java)

        val successObservable = Observable.just(jsonResponse)
        doReturn(successObservable).whenever(repository).getPostingsGraph(any(), any())

        val captor = argumentCaptor<Summary>()
        val isRefreshCaptor = argumentCaptor<Boolean>()

        presenter.getAllReceivables()

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showReceivablesInfo(captor.capture(), captor.capture(), isRefreshCaptor.capture())
        verify(view, never()).showError()

        assertEquals(7986.21, captor.firstValue.totalAmount, 0.001)
        assertEquals(yesterdayDate, captor.firstValue.date)
        assertEquals("Previsto", captor.firstValue.status)

        assertEquals(555.21, captor.secondValue.totalAmount, 0.001)
        assertEquals(todayDate, captor.secondValue.date)
        assertEquals("Previsto", captor.secondValue.status)
        assertFalse(isRefreshCaptor.firstValue)
    }

    @Test
    fun `success on get receivables by refreshing`() {
        val jsonResponse = Gson().fromJson(response, PostingsResponse::class.java)

        val successObservable = Observable.just(jsonResponse)
        doReturn(successObservable).whenever(repository).getPostingsGraph(any(), any())

        val captor = argumentCaptor<Summary>()
        val isRefreshCaptor = argumentCaptor<Boolean>()

        presenter.getAllReceivables(isByRefreshing = true)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showReceivablesInfo(captor.capture(), captor.capture(), isRefreshCaptor.capture())
        verify(view, never()).showError()

        assertEquals(7986.21, captor.firstValue.totalAmount, 0.001)
        assertEquals(yesterdayDate, captor.firstValue.date)
        assertEquals("Previsto", captor.firstValue.status)

        assertEquals(555.21, captor.secondValue.totalAmount, 0.001)
        assertEquals(todayDate, captor.secondValue.date)
        assertEquals("Previsto", captor.secondValue.status)
        assertTrue(isRefreshCaptor.firstValue)
    }

    @Test
    fun `get null object when the date is incorrect`() {
        val jsonResponse = Gson().fromJson(incorrectDateResponse, PostingsResponse::class.java)

        val successObservable = Observable.just(jsonResponse)
        doReturn(successObservable).whenever(repository).getPostingsGraph(any(), any())

        val captor = argumentCaptor<Summary>()

        presenter.getAllReceivables()

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showReceivablesInfo(captor.capture(), captor.capture(), any())
        verify(view, never()).showError()

        assertNull(captor.firstValue)
        assertNull(captor.secondValue)
    }

    @Test
    fun `error on get receivables when feature toggle is false`() {
        featureToggleObj.show = false

        doReturn(featureToggleObj).whenever(featureTogglePreference).getFeatureToggleObject(FeatureTogglePreference.MEUS_RECEBIMENTOS)

        `when`(
            handler.postDelayed(
                ArgumentMatchers.any(Runnable::class.java),
                anyLong()
            )
        ).thenAnswer {
            (it.arguments[0] as? Runnable)?.run()
            true
        }

        presenter.getAllReceivables()

        verify(view).unavailableReceivables()
        verify(view).hideLoading()
        verify(view, never()).showError(any(), anyBoolean())
        verify(view, never()).showReceivablesInfo(any(), any(), anyBoolean())
    }

    @Test
    fun `error on get receivables`() {
        val exception = RetrofitException(message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500)

        val errorObservable = Observable.error<RetrofitException>(exception)
        doReturn(errorObservable).whenever(repository).getPostingsGraph(any(), any())

        presenter.getAllReceivables()

        val captorError = argumentCaptor<ErrorMessage>()

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showError(captorError.capture(), anyBoolean())
        verify(view, never()).showReceivablesInfo(any(), any(), any())

        assertEquals(captorError.firstValue.httpStatus, 500)
    }
}