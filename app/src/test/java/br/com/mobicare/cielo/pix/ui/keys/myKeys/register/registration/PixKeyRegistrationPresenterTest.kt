package br.com.mobicare.cielo.pix.ui.keys.myKeys.register.registration

import br.com.mobicare.cielo.commons.constants.ERROR
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.login.domains.entities.EstabelecimentoObj
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.pix.api.claim.PixClaimRepositoryContract
import br.com.mobicare.cielo.pix.api.keys.PixKeysRepositoryContract
import br.com.mobicare.cielo.pix.constants.KEY_NOT_FOUND
import br.com.mobicare.cielo.pix.domain.*
import br.com.mobicare.cielo.pix.enums.PixClaimTypeEnum
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum
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

private const val KEY_MOCK = "63538573069"
private const val KEY_MASK_MOCK = "635.385.730-69"
private const val OTP_MOCK = "000000"
private const val CODE_MOCK = "123456"

class PixKeyRegistrationPresenterTest {

    private val response = "{\n" +
            "\"key\": \"63538573069\",\n" +
            "\"keyType\": \"CPF\",\n" +
            "\"participant\": \"1027058\",\n" +
            "\"participantName\": \"Vazio\",\n" +
            "\"branch\": \"0001\",\n" +
            "\"accountType\": \"PYMT\",\n" +
            "\"accountNumber\": \"2692100001\",\n" +
            "\"ownerType\": \"NATURAL_PERSON\",\n" +
            "\"ownerName\": \"Chapeuzinho Do Nascimento\",\n" +
            "\"ownerDocument\": \"***.828.261-**\",\n" +
            "\"creationDate\": \"2022-02-11T14:55:16.261Z\",\n" +
            "\"ownershipDate\": \"2022-02-11T14:55:16.258Z\",\n" +
            "\"endToEndId\": \"6b68b5e675a7487ca5b98613b7c1895a\"\n" +
            "}"

    private val responseClaim = "{\n" +
            "\"key\": \"63538573069\",\n" +
            "\"keyType\": \"CPF\",\n" +
            "\"participant\": \"1027058\",\n" +
            "\"participantName\": \"Vazio\",\n" +
            "\"branch\": \"0001\",\n" +
            "\"accountType\": \"PYMT\",\n" +
            "\"accountNumber\": \"2692100001\",\n" +
            "\"ownerType\": \"NATURAL_PERSON\",\n" +
            "\"ownerName\": \"Chapeuzinho Do Nascimento\",\n" +
            "\"ownerDocument\": \"***.828.261-**\",\n" +
            "\"creationDate\": \"2022-02-11T14:55:16.261Z\",\n" +
            "\"ownershipDate\": \"2022-02-11T14:55:16.258Z\",\n" +
            "\"claimType\": \"POSSESSION_CLAIM\",\n" +
            "\"endToEndId\": \"6b68b5e675a7487ca5b98613b7c1895a\"\n" +
            "}"

    private val responsePortability = "{\n" +
            "\"key\": \"63538573069\",\n" +
            "\"keyType\": \"CPF\",\n" +
            "\"participant\": \"1027058\",\n" +
            "\"participantName\": \"Vazio\",\n" +
            "\"branch\": \"0001\",\n" +
            "\"accountType\": \"PYMT\",\n" +
            "\"accountNumber\": \"2692100001\",\n" +
            "\"ownerType\": \"NATURAL_PERSON\",\n" +
            "\"ownerName\": \"Chapeuzinho Do Nascimento\",\n" +
            "\"ownerDocument\": \"***.828.261-**\",\n" +
            "\"creationDate\": \"2022-02-11T14:55:16.261Z\",\n" +
            "\"ownershipDate\": \"2022-02-11T14:55:16.258Z\",\n" +
            "\"claimType\": \"PORTABILITY\",\n" +
            "\"endToEndId\": \"6b68b5e675a7487ca5b98613b7c1895a\"\n" +
            "}"

    @Mock
    lateinit var view: PixKeyRegistrationContract.View

    @Mock
    lateinit var repository: PixKeysRepositoryContract

    @Mock
    lateinit var claimsRepository: PixClaimRepositoryContract

    @Mock
    lateinit var userPreferences: UserPreferences

    @Mock
    lateinit var menuPreference: MenuPreference

    private lateinit var presenter: PixKeyRegistrationPresenter

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = PixKeyRegistrationPresenter(
            view,
            menuPreference,
            userPreferences,
            repository,
            claimsRepository,
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
    fun `When getUsername is called and value is testeCielo, the return must be testeCielo`() {
        doReturn("loja1").whenever(userPreferences).userName
        assertEquals("loja1", presenter.getUsername())
    }

    @Test
    fun `When getDocument() is called and value is null, the return must be empty`() {
        doReturn(null).whenever(menuPreference).getEstablishment()
        assertEquals("", presenter.getDocument())
    }

    @Test
    fun `When getDocument() is called and value is 70897122000108, the return must be 70897122000108`() {
        doReturn(
            EstabelecimentoObj(
                ec = "101025",
                tradeName = "Estabelecimento de teste",
                cnpj = "70897122000108"
            )
        ).whenever(menuPreference).getEstablishment()

        assertEquals("70897122000108", presenter.getDocument())
    }

    @Test
    fun `when call onValidateKey, have a success return and claimType is POSSESSION_CLAIM show onShowClaim`() {
        val captorKey = argumentCaptor<String>()
        val captorType = argumentCaptor<String>()
        val captor = argumentCaptor<ValidateKeyResponse>()

        val response = Gson().fromJson(responseClaim, ValidateKeyResponse::class.java)

        val success = Observable.just(response)
        doReturn(success).whenever(repository)
            .validateKey(captorKey.capture(), captorType.capture())

        presenter.onValidateKey(OTP_MOCK, KEY_MASK_MOCK, PixKeyTypeEnum.CPF.name, CODE_MOCK)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onShowClaim(captor.capture())

        verify(view, never()).onShowPortability(any())
        verify(view, never()).onSuccessRegisterKey()
        verify(view, never()).onShowErrorValidateKey(any())

        assertEquals(KEY_MOCK, captorKey.firstValue)
        assertEquals(PixKeyTypeEnum.CPF.name, captorType.firstValue)

        assertEquals(KEY_MOCK, captor.firstValue.key)
        assertEquals(PixKeyTypeEnum.CPF.name, captor.firstValue.keyType)
        assertEquals("1027058", captor.firstValue.participant)
        assertEquals("Vazio", captor.firstValue.participantName)
        assertEquals("0001", captor.firstValue.branch)
        assertEquals("PYMT", captor.firstValue.accountType)
        assertEquals("2692100001", captor.firstValue.accountNumber)
        assertEquals("NATURAL_PERSON", captor.firstValue.ownerType)
        assertEquals("Chapeuzinho Do Nascimento", captor.firstValue.ownerName)
        assertEquals("***.828.261-**", captor.firstValue.ownerDocument)
        assertEquals("2022-02-11T14:55:16.261Z", captor.firstValue.creationDate)
        assertEquals("2022-02-11T14:55:16.258Z", captor.firstValue.ownershipDate)
        assertEquals("POSSESSION_CLAIM", captor.firstValue.claimType)
        assertEquals("6b68b5e675a7487ca5b98613b7c1895a", captor.firstValue.endToEndId)
    }

    @Test
    fun `when call onValidateKey, have a success return and claimType is PORTABILITY show onShowPortability`() {
        val captorKey = argumentCaptor<String>()
        val captorType = argumentCaptor<String>()
        val captor = argumentCaptor<ValidateKeyResponse>()

        val response = Gson().fromJson(responsePortability, ValidateKeyResponse::class.java)

        val success = Observable.just(response)
        doReturn(success).whenever(repository)
            .validateKey(captorKey.capture(), captorType.capture())

        presenter.onValidateKey(OTP_MOCK, KEY_MASK_MOCK, PixKeyTypeEnum.CPF.name, CODE_MOCK)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onShowPortability(captor.capture())

        verify(view, never()).onShowClaim(any())

        verify(view, never()).onSuccessRegisterKey()
        verify(view, never()).onShowErrorValidateKey(any())

        assertEquals(KEY_MOCK, captorKey.firstValue)
        assertEquals(PixKeyTypeEnum.CPF.name, captorType.firstValue)

        assertEquals(KEY_MOCK, captor.firstValue.key)
        assertEquals(PixKeyTypeEnum.CPF.name, captor.firstValue.keyType)
        assertEquals("1027058", captor.firstValue.participant)
        assertEquals("Vazio", captor.firstValue.participantName)
        assertEquals("0001", captor.firstValue.branch)
        assertEquals("PYMT", captor.firstValue.accountType)
        assertEquals("2692100001", captor.firstValue.accountNumber)
        assertEquals("NATURAL_PERSON", captor.firstValue.ownerType)
        assertEquals("Chapeuzinho Do Nascimento", captor.firstValue.ownerName)
        assertEquals("***.828.261-**", captor.firstValue.ownerDocument)
        assertEquals("2022-02-11T14:55:16.261Z", captor.firstValue.creationDate)
        assertEquals("2022-02-11T14:55:16.258Z", captor.firstValue.ownershipDate)
        assertEquals("PORTABILITY", captor.firstValue.claimType)
        assertEquals("6b68b5e675a7487ca5b98613b7c1895a", captor.firstValue.endToEndId)
    }

    @Test
    fun `when calling onValidateKey, it has a success return, the declaration type is other than PORTABILITY and POSSESSION_CLAIM and the return of the onRegisterKey call is success call the onSuccess of the view`() {
        val captorKey = argumentCaptor<String>()
        val captorType = argumentCaptor<String>()
        val captorOtp = argumentCaptor<String>()
        val captorRequest = argumentCaptor<CreateKeyRequest>()

        val validateKeyResponse = Gson().fromJson(response, ValidateKeyResponse::class.java)
        val createKeyResponse = CreateKeyResponse(
            claimOpeningDate = "2022-02-11T14:55:16.261Z",
            key = KEY_MOCK,
            ownershipDate = "2022-03-35T14:55:16.261Z",
            creationDate = "2022-03-16T13:56:16.261Z"
        )

        val successValidateKey = Observable.just(validateKeyResponse)
        val successCreateKey = Observable.just(createKeyResponse)

        doReturn(successValidateKey).whenever(repository)
            .validateKey(captorKey.capture(), captorType.capture())

        doReturn(successCreateKey).whenever(repository)
            .createKey(captorOtp.capture(), captorRequest.capture())

        `when`(view.onSuccess(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.onValidateKey(OTP_MOCK, KEY_MASK_MOCK, PixKeyTypeEnum.CPF.name, CODE_MOCK)
        verify(view).showLoading()
        verify(view).hideLoading()

        verify(view).onSuccess(any())
        verify(view).onSuccessRegisterKey()

        verify(view, never()).onShowPortability(any())
        verify(view, never()).onShowClaim(any())
        verify(view, never()).onShowErrorValidateKey(any())
        verify(view, never()).onShowError(any())
        verify(view, never()).onErrorCreateClaimPortability(any())
        verify(view, never()).onSuccessCreateClaimOwnership(any())
        verify(view, never()).onSuccessCreateClaimPortability(any())

        assertEquals(OTP_MOCK, captorOtp.firstValue)
        assertEquals(KEY_MOCK, captorKey.firstValue)
        assertEquals(PixKeyTypeEnum.CPF.name, captorType.firstValue)

        assertEquals(KEY_MOCK, captorRequest.firstValue.key)
        assertEquals(PixKeyTypeEnum.CPF.name, captorRequest.firstValue.keyType)
    }

    @Test
    fun `when calling onValidateKey and having an error return other than 420 call the onShowError of the view`() {
        val captorKey = argumentCaptor<String>()
        val captorType = argumentCaptor<String>()
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
            .validateKey(captorKey.capture(), captorType.capture())

        presenter.onValidateKey(OTP_MOCK, KEY_MASK_MOCK, PixKeyTypeEnum.CPF.name, CODE_MOCK)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onShowErrorValidateKey(captor.capture())

        verify(view, never()).onShowPortability(any())
        verify(view, never()).onShowClaim(any())

        verify(view, never()).onSuccessRegisterKey()

        assertEquals(KEY_MOCK, captorKey.firstValue)
        assertEquals(PixKeyTypeEnum.CPF.name, captorType.firstValue)

        assertEquals(500, captor.firstValue.httpStatus)
    }

    @Test
    fun `when calling onValidateKey and having an error return 420 but with the code error different from KEY_NOT_FOUND call the onShowError of the view`() {
        val captorKey = argumentCaptor<String>()
        val captorType = argumentCaptor<String>()
        val captor = argumentCaptor<ErrorMessage>()

        val errorMessage = ErrorMessage().apply {
            title = ""
            httpStatus = 420
            code = "420"
            errorCode = ERROR
        }

        val response = APIUtils.createResponse(errorMessage)

        val exception = RetrofitException(
            message = null,
            url = null,
            response = response,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 420
        )

        val errorObservable = Observable.error<RetrofitException>(exception)
        doReturn(errorObservable).whenever(repository)
            .validateKey(captorKey.capture(), captorType.capture())

        presenter.onValidateKey(OTP_MOCK, KEY_MASK_MOCK, PixKeyTypeEnum.CPF.name, CODE_MOCK)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onShowErrorValidateKey(captor.capture())

        verify(view, never()).onShowPortability(any())
        verify(view, never()).onShowClaim(any())

        verify(view, never()).onSuccessRegisterKey()

        assertEquals(KEY_MOCK, captorKey.firstValue)
        assertEquals(PixKeyTypeEnum.CPF.name, captorType.firstValue)

        assertEquals(420, captor.firstValue.httpStatus)
        assertEquals(ERROR, captor.firstValue.errorCode)
    }

    @Test
    fun `when calling onValidateKey, it has a success return, the declaration type is other than PORTABILITY and POSSESSION_CLAIM and the return of the onRegisterKey call is error call the onShowError of the view`() {
        val captorKey = argumentCaptor<String>()
        val captorType = argumentCaptor<String>()
        val captorOtp = argumentCaptor<String>()
        val captorRequest = argumentCaptor<CreateKeyRequest>()
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
        val validateKeyResponse = Gson().fromJson(response, ValidateKeyResponse::class.java)
        val errorObservable = Observable.error<RetrofitException>(exception)
        val successValidateKey = Observable.just(validateKeyResponse)

        doReturn(successValidateKey).whenever(repository)
            .validateKey(captorKey.capture(), captorType.capture())

        doReturn(errorObservable).whenever(repository)
            .createKey(captorOtp.capture(), captorRequest.capture())

        `when`(view.onShowError(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.onValidateKey(OTP_MOCK, KEY_MASK_MOCK, PixKeyTypeEnum.CPF.name, CODE_MOCK)

        verify(view).showLoading()
        verify(view).hideLoading()

        verify(view).onShowError(any())
        verify(view).onErrorRegisterKey(captorError.capture())

        verify(view, never()).onSuccess(any())
        verify(view, never()).onSuccessRegisterKey()
        verify(view, never()).onShowPortability(any())
        verify(view, never()).onShowClaim(any())
        verify(view, never()).onShowErrorValidateKey(any())
        verify(view, never()).onSuccessCreateClaimOwnership(any())
        verify(view, never()).onSuccessCreateClaimPortability(any())

        assertEquals(OTP_MOCK, captorOtp.firstValue)
        assertEquals(KEY_MOCK, captorKey.firstValue)
        assertEquals(PixKeyTypeEnum.CPF.name, captorType.firstValue)

        assertEquals(KEY_MOCK, captorRequest.firstValue.key)
        assertEquals(PixKeyTypeEnum.CPF.name, captorRequest.firstValue.keyType)

        assertEquals(500, captorError.firstValue.httpStatus)
    }

    @Test
    fun `when calling onValidateKey and receiving an error 420 with the error code KEY_NOT_FOUND, you must call onRegisterKey and if it returns a success call the view's onSuccess`() {
        val captorKey = argumentCaptor<String>()
        val captorType = argumentCaptor<String>()
        val captorOtp = argumentCaptor<String>()
        val captorRequest = argumentCaptor<CreateKeyRequest>()

        val errorMessage = ErrorMessage().apply {
            title = ""
            httpStatus = 420
            code = "420"
            errorCode = KEY_NOT_FOUND
        }

        val response = APIUtils.createResponse(errorMessage)

        val exception = RetrofitException(
            message = null,
            url = null,
            response = response,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 420
        )
        val createKeyResponse = CreateKeyResponse(
            claimOpeningDate = "2022-02-11T14:55:16.261Z",
            key = KEY_MOCK,
            ownershipDate = "2022-03-35T14:55:16.261Z",
            creationDate = "2022-03-16T13:56:16.261Z"
        )
        val successCreateKey = Observable.just(createKeyResponse)
        val errorObservable = Observable.error<RetrofitException>(exception)

        doReturn(errorObservable).whenever(repository)
            .validateKey(captorKey.capture(), captorType.capture())

        doReturn(successCreateKey).whenever(repository)
            .createKey(captorOtp.capture(), captorRequest.capture())

        `when`(view.onSuccess(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.onValidateKey(OTP_MOCK, KEY_MASK_MOCK, PixKeyTypeEnum.CPF.name, CODE_MOCK)

        verify(view).showLoading()
        verify(view).hideLoading()

        verify(view).onSuccess(any())
        verify(view).onSuccessRegisterKey()

        verify(view, never()).onShowPortability(any())
        verify(view, never()).onShowClaim(any())
        verify(view, never()).onShowErrorValidateKey(any())
        verify(view, never()).onShowError(any())
        verify(view, never()).onErrorCreateClaimPortability(any())
        verify(view, never()).onSuccessCreateClaimOwnership(any())
        verify(view, never()).onSuccessCreateClaimPortability(any())

        assertEquals(OTP_MOCK, captorOtp.firstValue)
        assertEquals(KEY_MOCK, captorKey.firstValue)
        assertEquals(PixKeyTypeEnum.CPF.name, captorType.firstValue)

        assertEquals(KEY_MOCK, captorRequest.firstValue.key)
        assertEquals(PixKeyTypeEnum.CPF.name, captorRequest.firstValue.keyType)
    }

    @Test
    fun `when calling onCreateClaim OWNERSHIP success`() {
        val captorOtp = argumentCaptor<String>()
        val captorRequest = argumentCaptor<ClaimsRequest>()
        val captorResponse = argumentCaptor<ClaimsResponse>()

        val response = ClaimsResponse(
            claimId = "1234",
            claimStatus = PixClaimTypeEnum.OWNERSHIP.name,
            lastModifiedDate = "2022-03-35T14:55:16.261Z",
            resolutionLimitDate = "2022-03-16T13:56:16.261Z"
        )

        val success = Observable.just(response)
        doReturn(success).whenever(claimsRepository)
            .createClaims(captorOtp.capture(), captorRequest.capture())

        `when`(view.onSuccess(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.onCreateClaim(
            OTP_MOCK,
            KEY_MASK_MOCK,
            PixKeyTypeEnum.CPF.name,
            PixClaimTypeEnum.OWNERSHIP.name,
            CODE_MOCK
        )


        verify(view).onSuccess(any())
        verify(view).onSuccessCreateClaimOwnership(captorResponse.capture())

        verify(view, never()).onShowPortability(any())
        verify(view, never()).onShowClaim(any())
        verify(view, never()).onShowErrorValidateKey(any())
        verify(view, never()).onShowError(any())
        verify(view, never()).onErrorCreateClaimPortability(any())
        verify(view, never()).onSuccessRegisterKey()
        verify(view, never()).onSuccessCreateClaimPortability(any())

        assertEquals(OTP_MOCK, captorOtp.firstValue)
        assertEquals(KEY_MOCK, captorRequest.firstValue.key)
        assertEquals(PixKeyTypeEnum.CPF.name, captorRequest.firstValue.keyType)
        assertEquals(PixClaimTypeEnum.OWNERSHIP.name, captorRequest.firstValue.claimType)

        assertTrue(captorResponse.allValues.contains(response))
    }

    @Test
    fun `when calling onCreateClaim PORTABILITY success`() {
        val captorOtp = argumentCaptor<String>()
        val captorRequest = argumentCaptor<ClaimsRequest>()
        val captorResponse = argumentCaptor<ClaimsResponse>()

        val response = ClaimsResponse(
            claimId = "1234",
            claimStatus = PixClaimTypeEnum.PORTABILITY.name,
            lastModifiedDate = "2022-03-35T14:55:16.261Z",
            resolutionLimitDate = "2022-03-16T13:56:16.261Z"
        )

        val success = Observable.just(response)
        doReturn(success).whenever(claimsRepository)
            .createClaims(captorOtp.capture(), captorRequest.capture())

        `when`(view.onSuccess(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.onCreateClaim(
            OTP_MOCK,
            KEY_MASK_MOCK,
            PixKeyTypeEnum.CPF.name,
            PixClaimTypeEnum.PORTABILITY.name,
            CODE_MOCK
        )


        verify(view).onSuccess(any())
        verify(view).onSuccessCreateClaimPortability(captorResponse.capture())

        verify(view, never()).onShowPortability(any())
        verify(view, never()).onShowClaim(any())
        verify(view, never()).onShowErrorValidateKey(any())
        verify(view, never()).onShowError(any())
        verify(view, never()).onErrorCreateClaimPortability(any())
        verify(view, never()).onSuccessRegisterKey()
        verify(view, never()).onSuccessCreateClaimOwnership(any())

        assertEquals(OTP_MOCK, captorOtp.firstValue)
        assertEquals(KEY_MOCK, captorRequest.firstValue.key)
        assertEquals(PixKeyTypeEnum.CPF.name, captorRequest.firstValue.keyType)
        assertEquals(PixClaimTypeEnum.PORTABILITY.name, captorRequest.firstValue.claimType)

        assertTrue(captorResponse.allValues.contains(response))
    }

    @Test
    fun `when calling onCreateClaim OWNERSHIP error`() {
        val captorOtp = argumentCaptor<String>()
        val captorRequest = argumentCaptor<ClaimsRequest>()
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
        doReturn(errorObservable).whenever(claimsRepository)
            .createClaims(captorOtp.capture(), captorRequest.capture())

        `when`(view.onShowError(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.onCreateClaim(
            OTP_MOCK,
            KEY_MASK_MOCK,
            PixKeyTypeEnum.CPF.name,
            PixClaimTypeEnum.OWNERSHIP.name,
            CODE_MOCK
        )


        verify(view).onShowError(any())
        verify(view).onErrorCreateClaimOwnership(captorError.capture())

        verify(view, never()).onSuccess(any())
        verify(view, never()).onShowPortability(any())
        verify(view, never()).onShowClaim(any())
        verify(view, never()).onShowErrorValidateKey(any())
        verify(view, never()).onSuccessRegisterKey()
        verify(view, never()).onSuccessCreateClaimOwnership(any())
        verify(view, never()).onSuccessCreateClaimPortability(any())
        verify(view, never()).onErrorCreateClaimPortability(any())


        assertEquals(OTP_MOCK, captorOtp.firstValue)
        assertEquals(KEY_MOCK, captorRequest.firstValue.key)
        assertEquals(PixKeyTypeEnum.CPF.name, captorRequest.firstValue.keyType)
        assertEquals(PixClaimTypeEnum.OWNERSHIP.name, captorRequest.firstValue.claimType)

        assertEquals(500, captorError.firstValue.httpStatus)
    }

    @Test
    fun `when calling onCreateClaim PORTABILITY error`() {
        val captorOtp = argumentCaptor<String>()
        val captorRequest = argumentCaptor<ClaimsRequest>()
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
        doReturn(errorObservable).whenever(claimsRepository)
            .createClaims(captorOtp.capture(), captorRequest.capture())

        `when`(view.onShowError(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.onCreateClaim(
            OTP_MOCK,
            KEY_MASK_MOCK,
            PixKeyTypeEnum.CPF.name,
            PixClaimTypeEnum.PORTABILITY.name,
            CODE_MOCK
        )


        verify(view).onShowError(any())
        verify(view).onErrorCreateClaimPortability(captorError.capture())

        verify(view, never()).onSuccess(any())
        verify(view, never()).onShowPortability(any())
        verify(view, never()).onShowClaim(any())
        verify(view, never()).onShowErrorValidateKey(any())
        verify(view, never()).onSuccessRegisterKey()
        verify(view, never()).onSuccessCreateClaimOwnership(any())
        verify(view, never()).onSuccessCreateClaimPortability(any())
        verify(view, never()).onErrorCreateClaimOwnership(any())

        assertEquals(OTP_MOCK, captorOtp.firstValue)
        assertEquals(KEY_MOCK, captorRequest.firstValue.key)
        assertEquals(PixKeyTypeEnum.CPF.name, captorRequest.firstValue.keyType)
        assertEquals(PixClaimTypeEnum.PORTABILITY.name, captorRequest.firstValue.claimType)

        assertEquals(500, captorError.firstValue.httpStatus)
    }
}