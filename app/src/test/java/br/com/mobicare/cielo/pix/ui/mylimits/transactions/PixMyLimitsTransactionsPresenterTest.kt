package br.com.mobicare.cielo.pix.ui.mylimits.transactions

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pix.api.myLimits.PixMyLimitsRepositoryContract
import br.com.mobicare.cielo.pix.api.myLimits.timeManagement.PixTimeManagementRepositoryContract
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.domain.PixMyLimitsRequest
import br.com.mobicare.cielo.pix.domain.PixMyLimitsResponse
import br.com.mobicare.cielo.pix.domain.PixTimeManagementResponse
import br.com.mobicare.cielo.pix.enums.BeneficiaryTypeEnum
import br.com.mobicare.cielo.pix.enums.PixServicesGroupEnum
import br.com.mobicare.cielo.pix.enums.PixTimeManagementEnum
import br.com.mobicare.cielo.pix.ui.mylimits.utils.MyLimitsTestFactory
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import retrofit2.Response
import java.net.HttpURLConnection

class PixMyLimitsTransactionsPresenterTest {

    @Mock
    lateinit var view: PixMyLimitsTransactionsContract.View

    @Mock
    lateinit var userPreferences: UserPreferences

    @Mock
    lateinit var repository: PixMyLimitsRepositoryContract

    @Mock
    lateinit var timeManagementRepository: PixTimeManagementRepositoryContract

    private lateinit var presenter: PixMyLimitsTransactionsPresenter

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    private val responseCaptor = argumentCaptor<PixMyLimitsResponse>()
    private val timeManagement = argumentCaptor<PixTimeManagementEnum>()

    private val servicesGroupNameCaptor = argumentCaptor<String>()
    private val beneficiaryTypeCaptor = argumentCaptor<String>()
    private val errorMessageCaptor = argumentCaptor<ErrorMessage>()
    private val otpCaptor = argumentCaptor<String>()
    private val pixMyLimitsRequestCaptor = argumentCaptor<PixMyLimitsRequest>()

    private val servicesGroupPixName = PixServicesGroupEnum.PIX.name
    private val beneficiaryTypeCpf = BeneficiaryTypeEnum.CPF

    private val networkException = RetrofitException(
        message = null,
        url = null,
        response = null,
        kind = RetrofitException.Kind.NETWORK,
        exception = null,
        retrofit = null,
        httpStatus = 500
    )

    private val myLimitsForNaturalPersonResponse = Gson().fromJson(
        MyLimitsTestFactory.myLimitsForNaturalPersonJson, PixMyLimitsResponse::class.java)

    private val myLimitsEmptyResponse = Gson().fromJson(
        MyLimitsTestFactory.myLimitsEmptyJson, PixMyLimitsResponse::class.java)

    private val timeManagementResponse = Gson().fromJson(
        MyLimitsTestFactory.timeManagementResponse, PixTimeManagementResponse::class.java)

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = PixMyLimitsTransactionsPresenter(
            view,
            userPreferences,
            repository,
            timeManagementRepository,
            uiScheduler,
            ioScheduler
        )
    }

    @Test
    fun `when getUsername is called and value is empty, the return must be empty`() {
        doReturn("").whenever(userPreferences).userName
        assertEquals("", presenter.getUsername())
    }

    @Test
    fun `when getUsername is called and value is testeCielo, the return must be testeCielo`() {
        doReturn("testeCielo").whenever(userPreferences).userName
        assertEquals("testeCielo", presenter.getUsername())
    }

    @Test
    fun `it should call onShowMyLimits on success result of getMyLimits`() {
        // given
        doReturn(Observable.just(myLimitsForNaturalPersonResponse))
            .whenever(repository)
            .getLimits(servicesGroupNameCaptor.capture(), beneficiaryTypeCaptor.capture())

        // when
        presenter.getMyLimits(beneficiaryTypeCpf)

        // then
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onShowMyLimits(responseCaptor.capture())

        verify(view, never()).onErrorGetLimits(any())

        assertEquals(servicesGroupPixName, servicesGroupNameCaptor.firstValue)
        assertEquals(beneficiaryTypeCpf.personType, beneficiaryTypeCaptor.firstValue)
        assertTrue(responseCaptor.allValues.contains(myLimitsForNaturalPersonResponse))
    }

    @Test
    fun `it should call onErrorGetLimits on success result of getMyLimits with empty or null limits in response`() {
        // given
        doReturn(Observable.just(myLimitsEmptyResponse))
            .whenever(repository)
            .getLimits(servicesGroupNameCaptor.capture(), beneficiaryTypeCaptor.capture())

        // when
        presenter.getMyLimits(beneficiaryTypeCpf)

        // then
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onErrorGetLimits()

        verify(view, never()).onShowMyLimits(responseCaptor.capture())

        assertEquals(servicesGroupPixName, servicesGroupNameCaptor.firstValue)
        assertEquals(beneficiaryTypeCpf.personType, beneficiaryTypeCaptor.firstValue)
    }

    @Test
    fun `it should call onErrorGetLimits on error result of getMyLimits`() {
        // given
        doReturn(Observable.error<RetrofitException>(networkException))
            .whenever(repository)
            .getLimits(servicesGroupNameCaptor.capture(), beneficiaryTypeCaptor.capture())

        // when
        presenter.getMyLimits(beneficiaryTypeCpf)

        // then
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onErrorGetLimits(errorMessageCaptor.capture())

        verify(view, never()).onShowMyLimits(responseCaptor.capture())

        assertEquals(servicesGroupPixName, servicesGroupNameCaptor.firstValue)
        assertEquals(beneficiaryTypeCpf.personType, beneficiaryTypeCaptor.firstValue)
        assertEquals(500, errorMessageCaptor.firstValue.httpStatus)
    }

    @Test
    fun `it should call onErrorUpdateLimit on onUpdateLimit call when listLimits param is null or empty`() {
        // when
        presenter.onUpdateLimit(
            otp = MyLimitsTestFactory.OTP,
            listLimits = mutableListOf(),
            fingerprint = EMPTY,
            beneficiaryType = beneficiaryTypeCpf
        )

        // then
        verify(view).onErrorUpdateLimit()


        verify(view, never()).onErrorUpdateLimits(any())
        verify(view, never()).onSuccessUpdateLimit()
    }

    @Test
    fun `it should call onSuccessUpdateLimit on success result of updateLimits with http code between 200 to 204`() {
        val httpNoContentResponse =
            Response.success<Void?>(HttpURLConnection.HTTP_NO_CONTENT,null)

        // given
        doReturn(Observable.just(httpNoContentResponse))
            .whenever(repository)
            .updateLimits(otpCaptor.capture(), pixMyLimitsRequestCaptor.capture())

        // when
        presenter.onUpdateLimit(
            otp = MyLimitsTestFactory.OTP,
            listLimits = MyLimitsTestFactory.listWithOneLimitRequest,
            fingerprint = EMPTY,
            beneficiaryType = beneficiaryTypeCpf
        )

        // then

        verify(view).onSuccessUpdateLimit()

        verify(view, never()).onErrorUpdateLimits(any())
        verify(view, never()).onErrorUpdateLimit()

        pixMyLimitsRequestCaptor.firstValue.run {
            assertEquals(MyLimitsTestFactory.OTP, otpCaptor.firstValue)
            assertEquals(servicesGroupPixName, serviceGroup)
            assertEquals(EMPTY, fingerprint)
            assertEquals(MyLimitsTestFactory.listWithOneLimitRequest, limits)
            assertEquals(beneficiaryTypeCpf.personType, beneficiaryType)
        }
    }

    @Test
    fun `it should call onErrorUpdateLimits on success result of updateLimits with http code out of 200 to 204 range`() {
        val httpResetResponse =
            Response.success<Void?>(HttpURLConnection.HTTP_RESET,null)

        // given
        doReturn(Observable.just(httpResetResponse))
            .whenever(repository)
            .updateLimits(otpCaptor.capture(), pixMyLimitsRequestCaptor.capture())

        // when
        presenter.onUpdateLimit(
            otp = MyLimitsTestFactory.OTP,
            listLimits = MyLimitsTestFactory.listWithOneLimitRequest,
            fingerprint = EMPTY,
            beneficiaryType = beneficiaryTypeCpf
        )

        // then

        verify(view).onErrorUpdateLimits(any())

        verify(view, never()).onSuccessUpdateLimit()
        verify(view, never()).onErrorUpdateLimit()
    }

    @Test
    fun `it should call onErrorUpdateLimits on error result of updateLimits`() {
        // given
        doReturn(Observable.error<RetrofitException>(networkException))
            .whenever(repository)
            .updateLimits(otpCaptor.capture(), pixMyLimitsRequestCaptor.capture())

        // when
        presenter.onUpdateLimit(
            otp = MyLimitsTestFactory.OTP,
            listLimits = MyLimitsTestFactory.listWithOneLimitRequest,
            fingerprint = EMPTY,
            beneficiaryType = beneficiaryTypeCpf
        )

        // then

        verify(view).onErrorUpdateLimits(any())

        verify(view, never()).onSuccessUpdateLimit()
    }

    @Test
    fun `it should call onSuccessGetNightTime on success result of getNightTime`() {
        // given
        doReturn(Observable.just(timeManagementResponse))
            .whenever(timeManagementRepository)
            .getNightTime()

        // when
        presenter.getNightTime()

        // then
        verify(view).onSuccessGetNightTime(timeManagement.capture())


        verify(view, never()).showLoading()
        verify(view, never()).hideLoading()

        assertEquals(PixTimeManagementEnum.EIGHT, timeManagement.firstValue)
    }

}