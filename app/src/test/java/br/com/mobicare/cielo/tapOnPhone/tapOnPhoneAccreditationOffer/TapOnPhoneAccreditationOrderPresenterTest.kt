package br.com.mobicare.cielo.tapOnPhone.tapOnPhoneAccreditationOffer

import android.app.Activity
import android.content.Context
import android.nfc.NfcManager
import android.os.Build
import br.com.mobicare.cielo.antifraud.KYCAntiFraudContract
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.utils.getVersionAndroid
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OfferResponse
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.tapOnPhone.domain.repository.TapOnPhoneAccreditationRepository
import br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.offer.TapOnPhoneAccreditationOfferContract
import br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.offer.TapOnPhoneAccreditationOfferPresenter
import br.com.mobicare.cielo.tapOnPhone.utils.TapOnPhoneFactory
import com.nhaarman.mockito_kotlin.*
import io.mockk.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class TapOnPhoneAccreditationOrderPresenterTest {

    @Mock
    private lateinit var fragmentActivity: Activity

    @Mock
    private lateinit var repository: TapOnPhoneAccreditationRepository

    @Mock
    private lateinit var view: TapOnPhoneAccreditationOfferContract.View

    @Mock
    private lateinit var antiFraud: KYCAntiFraudContract

    @Mock
    lateinit var featureTogglePreference: FeatureTogglePreference

    private lateinit var nfcManager: NfcManager
    private lateinit var presenter: TapOnPhoneAccreditationOfferPresenter

    private val offerResponse = TapOnPhoneFactory.offerResponseMock
    private val offerCaptor = argumentCaptor<OfferResponse>()
    private val successResult = Observable.just(offerResponse)

    private val exception = RetrofitException(
        message = null,
        url = null,
        response = null,
        kind = RetrofitException.Kind.NETWORK,
        exception = null,
        retrofit = null,
        httpStatus = 500
    )
    private val errorResult = Observable.error<RetrofitException>(exception)

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        nfcManager = mockk(relaxed = true)
        presenter = TapOnPhoneAccreditationOfferPresenter(
            view = view,
            repository = repository,
            KYCAntiFraudIntegration = antiFraud,
            uiScheduler = uiScheduler,
            ioScheduler = ioScheduler,
            featureTogglePreference = featureTogglePreference
        )
    }

    @Test
    fun `it should call onShowOffer on success result of reloadOffer call`() {
        doReturn(successResult).whenever(repository).loadOffers(anyOrNull())

        presenter.reloadOffer(true)

        verify(view).showLoadingOffers()
        verify(view).hideLoadingOffers()
        verify(view).onShowOffer(offerCaptor.capture())

        verify(view, never()).showOfferError()
    }

    @Test
    fun `it should call showOfferError on error result of reloadOffer call`() {
        doReturn(errorResult).whenever(repository).loadOffers(anyOrNull())

        presenter.reloadOffer(true)

        verify(view).showLoadingOffers()
        verify(view).hideLoadingOffers()
        verify(view).showOfferError()

        verify(view, never()).onShowOffer(offerCaptor.capture())
    }

    @Test
    fun `isShowBSAlertDeviceIncompatibility when device has NFC and Android greater than or equal 9`(){
        `when`(fragmentActivity.getSystemService(Context.NFC_SERVICE)).thenReturn(nfcManager)

        mockkStatic(::getVersionAndroid)
        every{
            getVersionAndroid()
        } returns Build.VERSION_CODES.P

        val value = presenter.isShowBSAlertDeviceIncompatibility(fragmentActivity)
        assertEquals(false, value)
    }

    @Test
    fun `isShowBSAlertDeviceIncompatibility when device has NFC and Android lower 9`(){
        `when`(fragmentActivity.getSystemService(Context.NFC_SERVICE)).thenReturn(nfcManager)

        mockkStatic(::getVersionAndroid)
        every{
            getVersionAndroid()
        } returns Build.VERSION_CODES.O_MR1

        val value = presenter.isShowBSAlertDeviceIncompatibility(fragmentActivity)
        assertEquals(true, value)
    }

    @Test
    fun `isShowBSAlertDeviceIncompatibility when device doesn't have NFC and Android greater than or equal 9`(){
        `when`(fragmentActivity.getSystemService(Context.NFC_SERVICE)).thenReturn(null)

        mockkStatic(::getVersionAndroid)
        every{
            getVersionAndroid()
        } returns Build.VERSION_CODES.P

        val value = presenter.isShowBSAlertDeviceIncompatibility(fragmentActivity)
        assertEquals(true, value)
    }

    @Test
    fun `isShowBSAlertDeviceIncompatibility when device doesn't have NFC and lower 9`(){
        `when`(fragmentActivity.getSystemService(Context.NFC_SERVICE)).thenReturn(null)

        mockkStatic(::getVersionAndroid)
        every{
            getVersionAndroid()
        } returns Build.VERSION_CODES.O_MR1

        val value = presenter.isShowBSAlertDeviceIncompatibility(fragmentActivity)
        assertEquals(true, value)
    }

}