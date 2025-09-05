package br.com.mobicare.cielo.pix.ui.home.account

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pix.api.account.PixAccountRepositoryContract
import br.com.mobicare.cielo.pix.domain.PixMerchantResponse
import br.com.mobicare.cielo.pix.domain.PixProfileRequest
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

const val TOKEN_MOCK = "101010"
const val OTP_MOCK = "00000"

class PixFreeMovementAccountManagementPresenterTest {

    private val merchant = "{\n" +
            "    \"name\":\"teste teste\",\n" +
            "    \"merchantNumber\":\"1234\",\n" +
            "    \"documentType\":\"4222393\",\n" +
            "    \"documentNumber\":\"4222393\",\n" +
            "    \"nonPixAccount\":{\n" +
            "        \"beneficiaryName\":\"teste 123\",\n" +
            "        \"bank\":\"Cielo\",\n" +
            "        \"bankName\":\"Cielo\",\n" +
            "        \"ispb\":\"13728\",\n" +
            "        \"agency\":\"1234\",\n" +
            "        \"account\":\"123456\",\n" +
            "        \"accountDigit\":\"7\",\n" +
            "        \"accountType\":\"CC\"\n" +
            "    },\n" +
            "    \"pixFullActive\":false\n" +
            "}"

    @Mock
    lateinit var view: PixFreeMovementAccountManagementContract.View

    @Mock
    lateinit var userPreferences: UserPreferences

    @Mock
    lateinit var repository: PixAccountRepositoryContract

    private lateinit var presenter: PixFreeMovementAccountManagementPresenter

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        doReturn(TOKEN_MOCK).whenever(userPreferences).token

        presenter = PixFreeMovementAccountManagementPresenter(
            view,
            userPreferences,
            repository,
            uiScheduler,
            ioScheduler
        )
    }

    @Test
    fun `When getUsername is called and value is empty, the return must be empty`() {
        doReturn("").whenever(userPreferences).userName
        assertEquals("", presenter.getUsername())
    }

    @Test
    fun `When getUsername is called and value is tester, the return must be tester`() {
        doReturn("tester").whenever(userPreferences).userName
        assertEquals("tester", presenter.getUsername())
    }

    @Test
    fun `When getMerchant is called and returns success it should show onSuccessMerchant`() {
        val captorResponse = argumentCaptor<PixMerchantResponse>()

        val response = Gson().fromJson(merchant, PixMerchantResponse::class.java)
        val success = Observable.just(response)
        doReturn(success).whenever(repository).getMerchant()

        presenter.getMerchant()

        verify(view).onShowMerchantLoading()
        verify(view).onHideMerchantLoading()
        verify(view).onSuccessMerchant(captorResponse.capture())

        verify(view, never()).onErrorMerchant(any())
        verify(view, never()).onErrorChangePixAccount(any())
        verify(view, never()).onSuccessChangePixAccount()

        assertTrue(captorResponse.allValues.contains(response))
    }

    @Test
    fun `When getMerchant is called and returns error, it should show onErrorMerchant`() {
        val captorError = argumentCaptor<ErrorMessage>()

        val exception = RetrofitException(
            message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500
        )

        val errorObservable = Observable.error<RetrofitException>(exception)
        doReturn(errorObservable).whenever(repository)
            .getMerchant()

        presenter.getMerchant()

        verify(view).onShowMerchantLoading()
        verify(view).onHideMerchantLoading()
        verify(view).onErrorMerchant(captorError.capture())

        verify(view, never()).onSuccessMerchant(any())
        verify(view, never()).onErrorChangePixAccount(any())
        verify(view, never()).onSuccessChangePixAccount()

        assertEquals(500, captorError.firstValue.httpStatus)
    }

    @Test
    fun `When changePixAccount is called and it returns error it should show onErrorChangePixAccount`() {
        val captorOTP = argumentCaptor<String>()
        val captorRequest = argumentCaptor<PixProfileRequest>()
        val captorError = argumentCaptor<ErrorMessage>()

        val exception = RetrofitException(
            message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500
        )

        val errorObservable = Observable.error<RetrofitException>(exception)

        doReturn(errorObservable).whenever(repository)
            .updateProfile(captorOTP.capture(), captorRequest.capture())

        `when`(view.onErrorChangePixAccount(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }
        presenter.changePixAccount(OTP_MOCK)

        verify(view).onErrorChangePixAccount(any())
        verify(view).showError(captorError.capture())

        verify(view, never()).showLoading()
        verify(view, never()).hideLoading()
        verify(view, never()).onShowMerchantLoading()
        verify(view, never()).onHideMerchantLoading()
        verify(view, never()).onSuccessMerchant(any())
        verify(view, never()).onErrorMerchant(any())
        verify(view, never()).onSuccessChangePixAccount()

        assertEquals(OTP_MOCK, captorOTP.firstValue)

        assertEquals(true, captorRequest.firstValue.settlementActive)
        assertEquals(500, captorError.firstValue.httpStatus)
    }

    @Test
    fun `When changePixAccount is called and returns success it should show onSuccessChangePixAccount`() {
        val captorOTP = argumentCaptor<String>()
        val captorRequest = argumentCaptor<PixProfileRequest>()

        val response = retrofit2.Response.success(200)
        val success = Observable.just(response)

        doReturn(success).whenever(repository)
            .updateProfile(captorOTP.capture(), captorRequest.capture())

        presenter.changePixAccount(OTP_MOCK)

        verify(view).onSuccessChangePixAccount()

        verify(view, never()).onErrorChangePixAccount(any())
        verify(view, never()).showError(any())
        verify(view, never()).showLoading()
        verify(view, never()).hideLoading()
        verify(view, never()).onShowMerchantLoading()
        verify(view, never()).onHideMerchantLoading()
        verify(view, never()).onSuccessMerchant(any())
        verify(view, never()).onErrorMerchant(any())

        assertEquals(OTP_MOCK, captorOTP.firstValue)

        assertEquals(true, captorRequest.firstValue.settlementActive)
    }
}