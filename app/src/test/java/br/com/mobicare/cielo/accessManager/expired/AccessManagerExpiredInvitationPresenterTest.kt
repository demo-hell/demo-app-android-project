package br.com.mobicare.cielo.accessManager.expired

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.AccessManagerRepository
import br.com.mobicare.cielo.accessManager.model.AccessManagerExpiredInviteResponse
import br.com.mobicare.cielo.accessManager.model.Item
import br.com.mobicare.cielo.accessManager.model.Profile
import br.com.mobicare.cielo.commons.constants.AccessManager.DEFAULT_OTP
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
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

class AccessManagerExpiredInvitationPresenterTest {

    private val firstExpiredInviteResponse = "{\n" +
            "   \"summary\":{\n" +
            "      \"totalQuantity\":3,\n" +
            "      \"totalAmount\":3\n" +
            "   },\n" +
            "   \"pagination\":{\n" +
            "      \"pageNumber\":1,\n" +
            "      \"pageSize\":2,\n" +
            "      \"totalElements\":2,\n" +
            "      \"firstPage\":true,\n" +
            "      \"lastPage\":false,\n" +
            "      \"numPages\":2\n" +
            "   },\n" +
            "   \"items\":[\n" +
            "      {\n" +
            "         \"id\":\"123456\",\n" +
            "         \"cpf\":\"83286254070\",\n" +
            "         \"email\":\"idonboarding@teste.com.br\",\n" +
            "         \"role\":\"ADMIN\",\n" +
            "         \"expiresOn\":\"2022-08-10T15:54:26.262Z\",\n" +
            "         \"expired\":true,\n" +
            "         \"profile\":{\n" +
            "              \"id\":\"9E67E3A5E05B4CDBAFB0\",\n" +
            "              \"name\":\"Pix\",\n" +
            "              \"custom\":true,\n" +
            "              \"p2Eligible\":true\n" +
            "           },\n" +
            "         \"expiresOnFormatted\":\"10/08/2022\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"id\":\"654321\",\n" +
            "         \"cpf\":\"74509082045\",\n" +
            "         \"email\":\"teste@teste.com.br\",\n" +
            "         \"role\":\"READER\",\n" +
            "         \"expiresOn\":\"2022-08-10T15:54:26.262Z\",\n" +
            "         \"expired\":true,\n" +
            "         \"profile\":{\n" +
            "              \"id\":\"9B32095BCDA342019498\",\n" +
            "              \"name\":\"Vendas\",\n" +
            "              \"custom\":true,\n" +
            "              \"p2Eligible\":true\n" +
            "           },\n" +
            "         \"expiresOnFormatted\":\"10/08/2022\"\n" +
            "      }\n" +
            "   ]\n" +
            "}"

    private val lastExpiredInviteResponse = "{\n" +
            "   \"summary\":{\n" +
            "      \"totalQuantity\":3,\n" +
            "      \"totalAmount\":3\n" +
            "   },\n" +
            "   \"pagination\":{\n" +
            "      \"pageNumber\":2,\n" +
            "      \"pageSize\":1,\n" +
            "      \"totalElements\":1,\n" +
            "      \"firstPage\":false,\n" +
            "      \"lastPage\":true,\n" +
            "      \"numPages\":2\n" +
            "   },\n" +
            "   \"items\":[\n" +
            "      {\n" +
            "         \"id\":\"986428\",\n" +
            "         \"cpf\":\"46513451078\",\n" +
            "         \"email\":\"testeid@teste.com.br\",\n" +
            "         \"role\":\"READER\",\n" +
            "         \"expiresOn\":\"2022-08-10T15:54:26.262Z\",\n" +
            "         \"expired\":true,\n" +
            "         \"profile\":{\n" +
            "              \"id\":\"F1B8300234074E88906A\",\n" +
            "              \"name\":\"Pix ou Vendas\",\n" +
            "              \"custom\":true,\n" +
            "              \"p2Eligible\":true\n" +
            "           },\n" +
            "         \"expiresOnFormatted\":\"10/08/2022\"\n" +
            "      }\n" +
            "   ]\n" +
            "}"

    private val listExpiredInvite: List<Item> = listOf(
        Item(id = "986428", profile = Profile(id = "F1B8300234074E88906A", name = "Pix ou Vendas", roles = arrayListOf(), custom = true, p2Eligible = true, legacy = true, admin = true)),
        Item(id = "654321", profile = Profile(id = "9E67E3A5E05B4CDBAFB0", name = "Pix", roles = arrayListOf(), custom = true, p2Eligible = true, legacy = true, admin = true)),
        Item(id = "123456", profile = Profile(id = "9B32095BCDA342019498", name = "Vendas", roles = arrayListOf(), custom = true, p2Eligible = true, legacy = true, admin = true))
    )

    private val listIds: List<String> = listOf(
        "986428",
        "654321",
        "123456"
    )

    @Mock
    lateinit var view: AccessManagerExpiredInvitationContract.View

    @Mock
    lateinit var repository: AccessManagerRepository

    private lateinit var presenter: AccessManagerExpiredInvitationPresenter
    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = AccessManagerExpiredInvitationPresenter(
            view,
            repository,
            uiScheduler,
            ioScheduler
        )
    }

    @Test
    fun `when onResendInvite is called and it returns a success it is necessary to call the methods showLoading, hideLoading and onSuccessResendInvite`() {
        val captor = argumentCaptor<Int>()
        val captorOtpCode = argumentCaptor<String>()
        val captorList = argumentCaptor<List<String>>()
        val captorLoading = argumentCaptor<Int>()

        doReturn(Observable.just(retrofit2.Response.success(200))).whenever(repository)
            .resendInvite(
                captorList.capture(),
                captorOtpCode.capture()
            )

        presenter.onResendInvite(listExpiredInvite, DEFAULT_OTP)

        verify(view).showLoading(captorLoading.capture())
        verify(view).hideLoading()
        verify(view).onSuccessResendInvite(captor.capture())

        verify(view, never()).showError(any())

        assertEquals(
            R.string.access_manager_resend_invite_loading_message,
            captorLoading.firstValue
        )
        assertEquals(THREE, captor.firstValue)
        assertEquals(DEFAULT_OTP, captorOtpCode.firstValue)
        assertTrue(captorList.allValues.contains(listIds))
    }

    @Test
    fun `when onResendInvite is called and it returns an error it is necessary to call the methods showLoading, hideLoading and showError`() {
        val captor = argumentCaptor<ErrorMessage>()
        val captorOtpCode = argumentCaptor<String>()
        val captorList = argumentCaptor<List<String>>()
        val captorLoading = argumentCaptor<Int>()

        val exception = RetrofitException(
            message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500
        )

        doReturn(Observable.error<RetrofitException>(exception)).whenever(repository)
            .resendInvite(
                captorList.capture(),
                captorOtpCode.capture()
            )

        presenter.onResendInvite(listExpiredInvite, DEFAULT_OTP)

        verify(view).showLoading(captorLoading.capture())
        verify(view).hideLoading()
        verify(view).showError(captor.capture())

        verify(view, never()).onSuccessResendInvite(any())

        assertEquals(
            R.string.access_manager_resend_invite_loading_message,
            captorLoading.firstValue
        )
        assertEquals(500, captor.firstValue.httpStatus)
        assertEquals(DEFAULT_OTP, captorOtpCode.firstValue)

        assertTrue(captorList.allValues.contains(listIds))
    }

    @Test
    fun `when getExpiredInvites is called for the first time and it returns a success it is necessary to call the methods showLoading, onShowExpiredInvites and hideLoading`() {
        val captor = argumentCaptor<AccessManagerExpiredInviteResponse>()
        val captorIsUpdate = argumentCaptor<Boolean>()
        val captorPageNumber = argumentCaptor<Int>()
        val captorPageSize = argumentCaptor<Int>()

        val response = createResponse()
        successRequest(
            isFirstPage = false,
            response = response,
            captorPageSize = captorPageSize,
            captorPageNumber = captorPageNumber
        )

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onShowExpiredInvites(captor.capture(), captorIsUpdate.capture())

        verify(view, never()).onErrorGetExpiredInvites(any())
        verify(view, never()).showLoadingMore()
        verify(view, never()).hideLoadingMore()

        assertEquals(ONE, captorPageNumber.firstValue)
        assertEquals(100, captorPageSize.firstValue)

        assertEquals(true, captorIsUpdate.firstValue)
        assertTrue(captor.allValues.contains(response))
    }

    @Test
    fun `when getExpiredInvites is called for the second time and it returns a success it is necessary to call the methods showLoadingMore, onShowExpiredInvites and hideLoadingMore`() {
        val captor = argumentCaptor<AccessManagerExpiredInviteResponse>()
        val captorIsUpdate = argumentCaptor<Boolean>()
        val captorPageNumber = argumentCaptor<Int>()
        val captorPageSize = argumentCaptor<Int>()

        val response = createResponse(lastExpiredInviteResponse)
        successRequest(
            isFirstPage = false,
            response = response,
            captorPageSize = captorPageSize,
            captorPageNumber = captorPageNumber,
            isLoading = false,
            pageNumber = TWO
        )

        verify(view).showLoadingMore()
        verify(view).hideLoadingMore()
        verify(view).onShowExpiredInvites(captor.capture(), captorIsUpdate.capture())

        verify(view, never()).onErrorGetExpiredInvites(any())
        verify(view, never()).showLoading()
        verify(view, never()).hideLoading()

        assertEquals(TWO, captorPageNumber.firstValue)
        assertEquals(100, captorPageSize.firstValue)

        assertEquals(true, captorIsUpdate.firstValue)
        assertTrue(captor.allValues.contains(response))
    }

    @Test
    fun `when getExpiredInvites is called the first time and returns an error it is necessary to call the methods showLoading, onErrorGetExpiredInvites and hideLoading`() {
        val captor = argumentCaptor<ErrorMessage>()
        val captorPageNumber = argumentCaptor<Int>()
        val captorPageSize = argumentCaptor<Int>()

        errorRequest(
            captorPageSize = captorPageSize,
            captorPageNumber = captorPageNumber
        )

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onErrorGetExpiredInvites(captor.capture())

        verify(view, never()).onShowExpiredInvites(any(), any())
        verify(view, never()).showLoadingMore()
        verify(view, never()).hideLoadingMore()

        assertEquals(ONE, captorPageNumber.firstValue)
        assertEquals(100, captorPageSize.firstValue)

        assertEquals(500, captor.firstValue.httpStatus)
    }

    @Test
    fun `when getExpiredInvites is called the second time and returns an error we can't show error messages or update the list of expired invites`() {
        val captorPageNumber = argumentCaptor<Int>()
        val captorPageSize = argumentCaptor<Int>()

        errorRequest(
            isFirstPage = false,
            captorPageSize = captorPageSize,
            captorPageNumber = captorPageNumber,
            isLoading = false,
            pageNumber = TWO
        )

        verify(view).showLoadingMore()
        verify(view).hideLoadingMore()

        verify(view, never()).onShowExpiredInvites(any(), any())
        verify(view, never()).onErrorGetExpiredInvites(any())
        verify(view, never()).showLoading()
        verify(view, never()).hideLoading()

        assertEquals(TWO, captorPageNumber.firstValue)
        assertEquals(100, captorPageSize.firstValue)
    }

    private fun createResponse(expiredInviteResponse: String = firstExpiredInviteResponse) =
        Gson().fromJson(
            expiredInviteResponse,
            AccessManagerExpiredInviteResponse::class.java
        )

    private fun successRequest(
        isFirstPage: Boolean = true,
        response: AccessManagerExpiredInviteResponse,
        captorPageSize: KArgumentCaptor<Int>,
        captorPageNumber: KArgumentCaptor<Int>,
        isLoading: Boolean = true,
        pageNumber: Int = ONE
    ) {
        presenter.isFirstPage = isFirstPage
        doReturn(Observable.just(response)).whenever(repository)
            .getExpiredInvites(
                captorPageSize.capture(),
                captorPageNumber.capture()
            )

        presenter.getExpiredInvites(isLoading, pageNumber)
    }

    private fun errorRequest(
        isFirstPage: Boolean = true,
        captorPageSize: KArgumentCaptor<Int>,
        captorPageNumber: KArgumentCaptor<Int>,
        isLoading: Boolean = true,
        pageNumber: Int = ONE
    ) {
        presenter.isFirstPage = isFirstPage

        val exception = RetrofitException(
            message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500
        )

        doReturn(Observable.error<RetrofitException>(exception)).whenever(repository)
            .getExpiredInvites(
                captorPageSize.capture(),
                captorPageNumber.capture()
            )

        presenter.getExpiredInvites(isLoading, pageNumber)
    }

    @Test
    fun `when onDeleteInvite is called and it returns a success it is necessary to call the methods showLoading, hideLoading and onSuccessDeleteInvite`() {
        val captor = argumentCaptor<Int>()
        val captorOtpCode = argumentCaptor<String>()
        val captorList = argumentCaptor<List<String>>()
        val captorLoading = argumentCaptor<Int>()

        doReturn(Observable.just(retrofit2.Response.success(200))).whenever(repository)
            .deleteInvite(
                captorList.capture(),
                captorOtpCode.capture()
            )

        presenter.onDeleteInvite(listExpiredInvite, DEFAULT_OTP)

        verify(view).showLoading(captorLoading.capture())
        verify(view).hideLoading()
        verify(view).onSuccessDeleteInvite(captor.capture())

        verify(view, never()).showError(any())

        assertEquals(
            R.string.access_manager_delete_invite_loading_message,
            captorLoading.firstValue
        )
        assertEquals(THREE, captor.firstValue)
        assertEquals(DEFAULT_OTP, captorOtpCode.firstValue)
        assertTrue(captorList.allValues.contains(listIds))
    }

    @Test
    fun `when onDeleteInvite is called and it returns an error it is necessary to call the methods showLoading, hideLoading and showError`() {
        val captor = argumentCaptor<ErrorMessage>()
        val captorOtpCode = argumentCaptor<String>()
        val captorList = argumentCaptor<List<String>>()
        val captorLoading = argumentCaptor<Int>()

        val exception = RetrofitException(
            message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500
        )

        doReturn(Observable.error<RetrofitException>(exception)).whenever(repository)
            .deleteInvite(
                captorList.capture(),
                captorOtpCode.capture()
            )

        presenter.onDeleteInvite(listExpiredInvite, DEFAULT_OTP)

        verify(view).showLoading(captorLoading.capture())
        verify(view).hideLoading()
        verify(view).showError(captor.capture())

        verify(view, never()).onSuccessDeleteInvite(any())

        assertEquals(
            R.string.access_manager_delete_invite_loading_message,
            captorLoading.firstValue
        )
        assertEquals(500, captor.firstValue.httpStatus)
        assertEquals(DEFAULT_OTP, captorOtpCode.firstValue)

        assertTrue(captorList.allValues.contains(listIds))
    }
}