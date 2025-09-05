package br.com.mobicare.cielo.pix.ui.keys.myKeys.home

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pix.api.claim.PixClaimRepositoryContract
import br.com.mobicare.cielo.pix.api.keys.PixKeysRepositoryContract
import br.com.mobicare.cielo.pix.domain.*
import br.com.mobicare.cielo.pix.enums.PixRevokeClaimsEnum
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

private const val OTP_MOCK = "000000"
private const val CLAIM_ID_MOCK = "62de8a02-3563-4b49-b734-45dea5b0d7c9"

private const val KEY = "6b68b5e675a7487ca5b98613b7c"
private const val OTP = "000000"

class PixMyKeysPresenterTest {

    private val response = "{\n" +
            "    \"keys\":{\n" +
            "        \"date\":\"2022-03-17 16:29:26\",\n" +
            "        \"count\":11,\n" +
            "        \"keys\":[\n" +
            "            {\n" +
            "                \"key\":\"e0aeb7cc-823b-4cd5-8921-b9d1f95cf57e\",\n" +
            "                \"keyType\":\"EVP\",\n" +
            "                \"main\":true\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\":\"12911111111\",\n" +
            "                \"keyType\":\"PHONE\",\n" +
            "                \"claimType\":\"OWNERSHIP\",\n" +
            "                \"main\":false,\n" +
            "                \"claimDetail\":{\n" +
            "                    \"claimType\":\"OWNERSHIP\",\n" +
            "                    \"participationType\":\"DONOR\",\n" +
            "                    \"keyType\":\"PHONE\",\n" +
            "                    \"key\":\"12911111111\",\n" +
            "                    \"claimId\":\"62de8a02-3563-4b49-b734-45dea5b0d7c9\",\n" +
            "                    \"claimStatus\":\"WAITING\",\n" +
            "                    \"resolutionLimitDate\":\"2022-03-24T13:20:00.000Z\",\n" +
            "                    \"completionLimitDate\":\"2022-03-31T13:20:00.000Z\",\n" +
            "                    \"lastModifiedDate\":\"2022-03-17T13:19:52.553Z\",\n" +
            "                    \"keyOwningRevalidationRequired\":false\n" +
            "                }\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\":\"03868033000146\",\n" +
            "                \"keyType\":\"CNPJ\",\n" +
            "                \"claimType\":\"PORTABILITY\",\n" +
            "                \"main\":false\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\":\"61cb32b4-0e81-4b75-9be1-0576e4afa6d7\",\n" +
            "                \"keyType\":\"EVP\",\n" +
            "                \"main\":false\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\":\"ec5ff66a-acf8-468b-b69f-e85448152004\",\n" +
            "                \"keyType\":\"EVP\",\n" +
            "                \"main\":false\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"claims\":{\n" +
            "        \"date\":\"2022-03-17 16:29:26\",\n" +
            "        \"count\":0,\n" +
            "        \"keys\":[\n" +
            "            \n" +
            "        ]\n" +
            "    }\n" +
            "}"

    private val claimsResponse = "{\n" +
            "    \"keys\":{\n" +
            "        \"date\":\"2022-03-17 16:29:26\",\n" +
            "        \"count\":11,\n" +
            "        \"keys\":[\n" +
            "        ]\n" +
            "    },\n" +
            "    \"claims\":{\n" +
            "        \"date\":\"2022-03-17 16:29:26\",\n" +
            "        \"count\":0,\n" +
            "        \"keys\":[\n" +
            "            {\n" +
            "                \"key\":\"+5512911111111\",\n" +
            "                \"keyType\":\"PHONE\",\n" +
            "                \"claimType\":\"OWNERSHIP\",\n" +
            "                \"main\":false,\n" +
            "                \"claimDetail\":{\n" +
            "                    \"claimType\":\"OWNERSHIP\",\n" +
            "                    \"participationType\":\"DONOR\",\n" +
            "                    \"keyType\":\"PHONE\",\n" +
            "                    \"key\":\"12911111111\",\n" +
            "                    \"claimId\":\"62de8a02-3563-4b49-b734-45dea5b0d7c9\",\n" +
            "                    \"claimStatus\":\"WAITING\",\n" +
            "                    \"resolutionLimitDate\":\"2022-03-24T13:20:00.000Z\",\n" +
            "                    \"completionLimitDate\":\"2022-03-31T13:20:00.000Z\",\n" +
            "                    \"lastModifiedDate\":\"2022-03-17T13:19:52.553Z\",\n" +
            "                    \"keyOwningRevalidationRequired\":false\n" +
            "                }\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}"

    private val claimsPortabilityResponse = "{\n" +
            "    \"keys\":{\n" +
            "        \"date\":\"2022-03-17 16:29:26\",\n" +
            "        \"count\":11,\n" +
            "        \"keys\":[\n" +
            "        ]\n" +
            "    },\n" +
            "    \"claims\":{\n" +
            "        \"date\":\"2022-03-17 16:29:26\",\n" +
            "        \"count\":0,\n" +
            "        \"keys\":[\n" +
            "            {\n" +
            "                \"key\":\"+5512911111111\",\n" +
            "                \"keyType\":\"PHONE\",\n" +
            "                \"claimType\":\"PORTABILITY\",\n" +
            "                \"main\":false,\n" +
            "                \"claimDetail\":{\n" +
            "                    \"claimType\":\"PORTABILITY\",\n" +
            "                    \"participationType\":\"DONOR\",\n" +
            "                    \"keyType\":\"PHONE\",\n" +
            "                    \"key\":\"12911111111\",\n" +
            "                    \"claimId\":\"62de8a02-3563-4b49-b734-45dea5b0d7c9\",\n" +
            "                    \"claimStatus\":\"WAITING\",\n" +
            "                    \"resolutionLimitDate\":\"2022-03-24T13:20:00.000Z\",\n" +
            "                    \"completionLimitDate\":\"2022-03-31T13:20:00.000Z\",\n" +
            "                    \"lastModifiedDate\":\"2022-03-17T13:19:52.553Z\",\n" +
            "                    \"keyOwningRevalidationRequired\":false\n" +
            "                }\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}"

    @Mock
    lateinit var view: PixMyKeysContract.View

    @Mock
    lateinit var userPreferences: UserPreferences

    @Mock
    lateinit var repository: PixKeysRepositoryContract

    @Mock
    lateinit var claimsRepository: PixClaimRepositoryContract

    private lateinit var presenter: PixMyKeysPresenter

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = spy(PixMyKeysPresenter(
                view,
                repository,
                claimsRepository,
                userPreferences,
                uiScheduler,
                ioScheduler
        ))
    }

    @Test
    fun `when call getMyKeys and have a success and keys is not empty but claims is empty return show onShowMyKeys and onHideVerificationKeys`() {
        val captor = argumentCaptor<Key>()

        val response = Gson().fromJson(response, PixKeysResponse::class.java)
        val success = Observable.just(response)
        doReturn(success).whenever(repository)
            .getKeys()
        presenter.getMyKeys()

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onShowMyKeys(captor.capture(), any())
        verify(view).onHideVerificationKeys()
        verify(view, never()).onHideMyKeys()
        verify(view, never()).onShowVerificationKeys(any())
        verify(view, never()).onNoKeyRegistered()
        verify(view, never()).showError(any())

        assertTrue(captor.allValues.contains(response.key))
    }

    @Test
    fun `when call getMyKeys and have a success and keys is empty but claims is not empty return show onShowVerificationKeys and onHideMyKeys`() {
        val captor = argumentCaptor<List<MyKey>>()
        val response = Gson().fromJson(claimsResponse, PixKeysResponse::class.java)

        val success = Observable.just(response)
        doReturn(success).whenever(repository)
            .getKeys()

        presenter.getMyKeys()

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onHideMyKeys()
        verify(view).onShowVerificationKeys(captor.capture())

        verify(view, never()).onHideVerificationKeys()
        verify(view, never()).onShowMyKeys(any(), any())
        verify(view, never()).onNoKeyRegistered()
        verify(view, never()).showError(any())
    }

    @Test
    fun `when call getMyKeys and have a success and keys and claims is empty return show onNoKeyRegistered`() {
        val response = PixKeysResponse(
            Key(count = 0, date = null, keys = listOf()),
            Key(count = 0, date = null, keys = listOf())
        )

        val successBalance = Observable.just(response)
        doReturn(successBalance).whenever(repository)
            .getKeys()

        presenter.getMyKeys()

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onNoKeyRegistered()

        verify(view, never()).onShowMyKeys(any(), any())
        verify(view, never()).showError(any())
    }

    @Test
    fun `when call getMyKeys and get an error return show showError`() {
        val captor = argumentCaptor<ErrorMessage>()
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
            .getKeys()

        presenter.getMyKeys()

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showError(captor.capture())

        verify(view, never()).onNoKeyRegistered()
        verify(view, never()).onShowMyKeys(any(), any())
        verify(view, never()).onHideMyKeys()
        verify(view, never()).onShowVerificationKeys(any())
        verify(view, never()).onHideVerificationKeys()

        assertEquals(500, captor.firstValue.httpStatus)
    }

    @Test
    fun `when try to delete a key without OTP animation it has a success return`() {
        val successResponse = PixKeyDeleteRequest(KEY)
        val successObservable = Observable.just(successResponse)
        val argumentCaptorOtp = argumentCaptor<String>()
        val argumentCaptorKey = argumentCaptor<PixKeyDeleteRequest>()

        doReturn(successObservable).whenever(repository)
                .deleteKey(argumentCaptorOtp.capture(), argumentCaptorKey.capture())

        `when`(view.onSuccess(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.deleteKey(OTP, KEY, false)

        verify(view).onSuccess(any())
        verify(view).onGetMyKeys()

        verify(view, never()).onShowAllErrors(any())
        verify(view, never()).onErrorDelete(any())

        assertEquals(OTP, argumentCaptorOtp.firstValue)
        assertEquals(KEY, argumentCaptorKey.firstValue.key)
    }

    @Test
    fun `when try to delete a key with OTP animation it has a success return`() {
        val successReturn = PixKeyDeleteRequest(KEY)
        val successObservable = Observable.just(successReturn)
        val argumentCaptorOtp = argumentCaptor<String>()
        val argumentCaptorKey = argumentCaptor<PixKeyDeleteRequest>()

        doReturn(successObservable).whenever(repository)
                .deleteKey(argumentCaptorOtp.capture(), argumentCaptorKey.capture())

        `when`(view.onSuccess(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.deleteKey(OTP, KEY, true)

        verify(view).onSuccess(any())
        verify(view).onGetMyKeys()

        verify(view, never()).onShowAllErrors(any())
        verify(view, never()).onErrorDelete(any())

        assertEquals(OTP, argumentCaptorOtp.firstValue)
        assertEquals(KEY, argumentCaptorKey.firstValue.key)
    }

    @Test
    fun `when try to delete a key and get an error should call showError`() {
        val argumentCaptorErrorMessage = argumentCaptor<ErrorMessage>()
        val argumentCaptorOtp = argumentCaptor<String>()
        val argumentCaptorKey = argumentCaptor<PixKeyDeleteRequest>()
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
            .deleteKey(argumentCaptorOtp.capture(), argumentCaptorKey.capture())

        `when`(view.onShowAllErrors(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.deleteKey(OTP, KEY, false)

        verify(view).onShowAllErrors(any())
        verify(view).onErrorDelete(argumentCaptorErrorMessage.capture())

        verify(view, never()).onSuccess(any())

        assertEquals(500, argumentCaptorErrorMessage.firstValue.httpStatus)
        assertEquals(OTP, argumentCaptorOtp.firstValue)
        assertEquals(KEY, argumentCaptorKey.firstValue.key)
    }

    @Test
    fun `when call cancelClaim and is not portabilityOrClaimKey have a success return show onSuccess`() {
        val otpCaptor = argumentCaptor<String>()
        val requestCaptor = argumentCaptor<RevokeClaimsRequest>()
        val key = Gson().fromJson(claimsResponse, PixKeysResponse::class.java)

        val response = RevokeClaimsResponse(
                cancellationReason = PixRevokeClaimsEnum.CLIENT_SOLICITATION.name,
                claimId = CLAIM_ID_MOCK,
                claimStatus = "OPENED",
                lastModificationDate = "2022-03-20T13:12:45.000Z"
        )

        val successBalance = Observable.just(response)
        doReturn(successBalance).whenever(claimsRepository)
                .revokeClaims(otpCaptor.capture(), requestCaptor.capture())

        `when`(view.onSuccess(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.cancelClaim(OTP_MOCK, key?.claims?.keys?.get(0), false)

        verify(view).onSuccess(any())
        verify(view).onGetMyKeys()

        verify(view, never()).onShowSuccessToKeepKey()
        verify(view, never()).onHideVerificationKeys()
        verify(view, never()).hideLoading()
        verify(view, never()).showLoading()
        verify(view, never()).onErrorDelete(any())
        verify(view, never()).onShowAllErrors(any())
        verify(view, never()).onShowAllErrors(any())
        verify(view, never()).onNoKeyRegistered()
        verify(view, never()).showError(any())

        assertEquals(OTP_MOCK, otpCaptor.firstValue)
        assertEquals(CLAIM_ID_MOCK, requestCaptor.firstValue.claimId)
        assertEquals(true, requestCaptor.firstValue.isClaimer)
        assertEquals(PixRevokeClaimsEnum.CLIENT_SOLICITATION.name, requestCaptor.firstValue.reason)
    }

    @Test
    fun `when call cancelClaim and is portabilityOrClaimKey have a success return show onSuccess`() {
        val otpCaptor = argumentCaptor<String>()
        val requestCaptor = argumentCaptor<RevokeClaimsRequest>()
        val key = Gson().fromJson(claimsResponse, PixKeysResponse::class.java)

        val response = RevokeClaimsResponse(
                cancellationReason = PixRevokeClaimsEnum.CLIENT_SOLICITATION.name,
                claimId = CLAIM_ID_MOCK,
                claimStatus = "OPENED",
                lastModificationDate = "2022-03-20T13:12:45.000Z"
        )

        val successBalance = Observable.just(response)
        doReturn(successBalance).whenever(claimsRepository)
                .revokeClaims(otpCaptor.capture(), requestCaptor.capture())

        `when`(view.onSuccess(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.cancelClaim(OTP_MOCK, key?.claims?.keys?.get(0), isPortabilityOrClaimKey = true, isClaimer = false)

        verify(view).onSuccess(any())
        verify(view).onShowSuccessToKeepKey()

        verify(view, never()).onGetMyKeys()
        verify(view, never()).onHideVerificationKeys()
        verify(view, never()).hideLoading()
        verify(view, never()).showLoading()
        verify(view, never()).onErrorDelete(any())
        verify(view, never()).onShowAllErrors(any())
        verify(view, never()).onShowAllErrors(any())
        verify(view, never()).onNoKeyRegistered()
        verify(view, never()).showError(any())

        assertEquals(OTP_MOCK, otpCaptor.firstValue)
        assertEquals(CLAIM_ID_MOCK, requestCaptor.firstValue.claimId)
        assertEquals(false, requestCaptor.firstValue.isClaimer)
        assertEquals(PixRevokeClaimsEnum.CLIENT_SOLICITATION.name, requestCaptor.firstValue.reason)
    }

    @Test
    fun `when call cancelClaim and portabilityOrClaimKey is false and myKey is null show onShowAllErrors`() {
        val errorCaptor = argumentCaptor<ErrorMessage>()

        `when`(view.onShowAllErrors(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.cancelClaim(OTP_MOCK, null, isPortabilityOrClaimKey = false)

        verify(view).onShowAllErrors(any())
        verify(view).onErrorDefault(errorCaptor.capture())

        verify(view, never()).onSuccess(any())
        verify(view, never()).onHideVerificationKeys()
        verify(view, never()).hideLoading()
        verify(view, never()).showLoading()
        verify(view, never()).onErrorDelete(any())

        verify(view, never()).showError(any())


        assertEquals(null, errorCaptor.firstValue)
    }

    @Test
    fun `when call cancelClaim and have an error return show showAllErrors`() {
        val otpCaptor = argumentCaptor<String>()
        val requestCaptor = argumentCaptor<RevokeClaimsRequest>()
        val errorCaptor = argumentCaptor<ErrorMessage>()

        val key = Gson().fromJson(claimsResponse, PixKeysResponse::class.java)

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
        val successBalance = Observable.just(errorObservable)
        doReturn(successBalance).whenever(claimsRepository)
            .revokeClaims(otpCaptor.capture(), requestCaptor.capture())

        `when`(view.onShowAllErrors(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.cancelClaim(OTP_MOCK, key?.claims?.keys?.get(0))

        verify(view).onShowAllErrors(any())
        verify(view).onErrorDefault(errorCaptor.capture())

        verify(view, never()).onSuccess(any())
        verify(view, never()).onHideVerificationKeys()
        verify(view, never()).hideLoading()
        verify(view, never()).showLoading()
        verify(view, never()).onErrorDelete(any())

        verify(view, never()).onNoKeyRegistered()
        verify(view, never()).showError(any())

        assertEquals(500, errorCaptor.firstValue.httpStatus)

        assertEquals(OTP_MOCK, otpCaptor.firstValue)
        assertEquals(CLAIM_ID_MOCK, requestCaptor.firstValue.claimId)
        assertEquals(true, requestCaptor.firstValue.isClaimer)
        assertEquals(PixRevokeClaimsEnum.CLIENT_SOLICITATION.name, requestCaptor.firstValue.reason)
    }

    @Test
    fun `when call confirmClaim and have a success return show onSuccessToReleaseKey`() {
        val key = Gson().fromJson(claimsResponse, PixKeysResponse::class.java)
        val otpCaptor = argumentCaptor<String>()
        val requestRevokeClaimsCaptor = argumentCaptor<ConfirmClaimsRequest>()

        val response = ConfirmClaimsResponse(
                confirmationReason = PixRevokeClaimsEnum.CLIENT_SOLICITATION.name,
                claimId = CLAIM_ID_MOCK,
                claimStatus = "OPENED",
                lastModificationDate = "2022-03-20T13:12:45.000Z"
        )

        val success = Observable.just(response)
        doReturn(success).whenever(claimsRepository)
                .confirmClaims(otpCaptor.capture(), requestRevokeClaimsCaptor.capture())

        `when`(view.onSuccess(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        `when`(view.onShowAllErrors(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.confirmClaim(OTP_MOCK, key?.claims?.keys?.get(0))

        verify(view).onSuccess(any())
        verify(view).onShowSuccessToReleaseKey()
        verify(view, never()).onShowAllErrors(any())
        verify(view, never()).onErrorDefault(any())

        verify(presenter, never()).setupConfirmClaimError(any(), any())

        assertEquals(CLAIM_ID_MOCK, requestRevokeClaimsCaptor.firstValue.claimId)
        assertEquals(OTP_MOCK, otpCaptor.firstValue)
        assertEquals(PixRevokeClaimsEnum.CLIENT_SOLICITATION.name, requestRevokeClaimsCaptor.firstValue.reason)
    }

    @Test
    fun `when call confirmClaim and claimDetail is null should show onShowAllErrors`() {
        val response = ConfirmClaimsResponse(
                confirmationReason = PixRevokeClaimsEnum.CLIENT_SOLICITATION.name,
                claimId = CLAIM_ID_MOCK,
                claimStatus = "OPENED",
                lastModificationDate = "2022-03-20T13:12:45.000Z"
        )

        val success = Observable.just(response)
        doReturn(success).whenever(claimsRepository)
                .confirmClaims(any(), any())

        `when`(view.onShowAllErrors(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.confirmClaim(OTP_MOCK, null)

        verify(view, never()).onShowSuccessToReleaseKey()
        verify(presenter, never()).setupConfirmClaimError(any(), any())
        verify(view).onShowAllErrors(any())
        verify(view).onErrorDefault(anyOrNull())
    }

    @Test
    fun `when call confirmClaim to portability and have an error at first time should show a error bottomSheet`() {
        val key = Gson().fromJson(claimsPortabilityResponse, PixKeysResponse::class.java)

        val otpCaptor = argumentCaptor<String>()
        val requestCaptor = argumentCaptor<ConfirmClaimsRequest>()
        val errorCaptor = argumentCaptor<ErrorMessage>()

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
        val responseError = Observable.just(errorObservable)
        doReturn(responseError).whenever(claimsRepository)
                .confirmClaims(otpCaptor.capture(), requestCaptor.capture())

        `when`(view.onShowAllErrors(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.tryAgainTimes = 1
        presenter.confirmClaim(OTP_MOCK, key?.claims?.keys?.get(0))

        verify(view).onShowAllErrors(any())
        verify(view).onErrorCreateClaimPortability(errorCaptor.capture())
        verify(presenter).setupConfirmClaimError(any(), any())

        verify(view, never()).onShowSuccessToReleaseKey()

        assertEquals(PixRevokeClaimsEnum.CLIENT_SOLICITATION.name, requestCaptor.firstValue.reason)
        assertEquals(OTP_MOCK, otpCaptor.firstValue)
        assertEquals(CLAIM_ID_MOCK, requestCaptor.firstValue.claimId)
    }

    @Test
    fun `when call confirmClaim to portability and have an error at fourth time should show a error bottomSheet`() {
        val key = Gson().fromJson(claimsPortabilityResponse, PixKeysResponse::class.java)

        val otpCaptor = argumentCaptor<String>()
        val requestCaptor = argumentCaptor<ConfirmClaimsRequest>()
        val errorCaptor = argumentCaptor<ErrorMessage>()

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
        val responseError = Observable.just(errorObservable)
        doReturn(responseError).whenever(claimsRepository)
                .confirmClaims(otpCaptor.capture(), requestCaptor.capture())

        `when`(view.onShowAllErrors(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.tryAgainTimes = 4
        presenter.confirmClaim(OTP_MOCK, key?.claims?.keys?.get(0))

        verify(view).onShowAllErrors(any())
        verify(view).onErrorCreateClaimPortability(errorCaptor.capture())
        verify(presenter).setupConfirmClaimError(any(), any())

        verify(view, never()).onShowSuccessToReleaseKey()

        assertEquals(PixRevokeClaimsEnum.CLIENT_SOLICITATION.name, requestCaptor.firstValue.reason)
        assertEquals(OTP_MOCK, otpCaptor.firstValue)
        assertEquals(CLAIM_ID_MOCK, requestCaptor.firstValue.claimId)
    }
}