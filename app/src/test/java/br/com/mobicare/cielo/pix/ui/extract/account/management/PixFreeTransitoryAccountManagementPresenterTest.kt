package br.com.mobicare.cielo.pix.ui.extract.account.management

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.idOnboarding.IDOnboardingRepository
import br.com.mobicare.cielo.idOnboarding.model.IDOnboardingWhitelistResponse
import br.com.mobicare.cielo.main.UserInformationRepository
import br.com.mobicare.cielo.me.MeResponse
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

const val TOKEN_MOCK = "123456"
const val OTP_MOCK = "00000"

class PixFreeTransitoryAccountManagementPresenterTest {

    private val merchant = "{\n" +
            "    \"name\":\"teste 123\",\n" +
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

    private val meMaster = "{\n" +
            "    \"advertisingId\":\"4271ad\",\n" +
            "    \"username\":\"teste\",\n" +
            "    \"login\":\"1234\",\n" +
            "    \"email\":\"teste@teste.com\",\n" +
            "    \"birthDate\":\"1990-06-11\",\n" +
            "    \"identity\":{\n" +
            "        \"cpf\":\"11111111111\",\n" +
            "        \"foreigner\":false\n" +
            "    },\n" +
            "    \"phoneNumber\":\"(11) 99999-9999\",\n" +
            "    \"roles\":[\n" +
            "        \"MASTER\"\n" +
            "    ],\n" +
            "    \"merchant\":{\n" +
            "        \"id\":\"123\",\n" +
            "        \"receivableType\":\"Individual\",\n" +
            "        \"hierarchyLevel\":\"POINT_OF_SALE\",\n" +
            "        \"individual\":true\n" +
            "    },\n" +
            "    \"activeMerchant\":{\n" +
            "        \"id\":\"123\",\n" +
            "        \"name\":\"teste\",\n" +
            "        \"receivableType\":\"Individual\",\n" +
            "        \"hierarchyLevel\":\"POINT_OF_SALE\",\n" +
            "        \"individual\":true,\n" +
            "        \"migrated\":true\n" +
            "    },\n" +
            "    \"impersonating\":false,\n" +
            "    \"impersonationEnabled\":true,\n" +
            "    \"lastLoginDate\":\"2022-05-12T00:00:00\",\n" +
            "    \"isMigrationRequired\":false,\n" +
            "    \"onboardingRequired\":false,\n" +
            "    \"mainRole\":\"ADMIN\"\n" +
            "}"

    private val meReaderP2 = "{\n" +
            "    \"advertisingId\":\"4271ad\",\n" +
            "    \"username\":\"teste\",\n" +
            "    \"login\":\"1234\",\n" +
            "    \"email\":\"teste@teste.com\",\n" +
            "    \"birthDate\":\"1990-06-11\",\n" +
            "    \"identity\":{\n" +
            "        \"cpf\":\"11111111111\",\n" +
            "        \"foreigner\":false\n" +
            "    },\n" +
            "    \"phoneNumber\":\"(11) 99999-9999\",\n" +
            "    \"roles\":[\n" +
            "        \"MASTER\"\n" +
            "    ],\n" +
            "    \"merchant\":{\n" +
            "        \"id\":\"123\",\n" +
            "        \"receivableType\":\"Individual\",\n" +
            "        \"hierarchyLevel\":\"POINT_OF_SALE\",\n" +
            "        \"individual\":true\n" +
            "    },\n" +
            "    \"activeMerchant\":{\n" +
            "        \"id\":\"123\",\n" +
            "        \"name\":\"teste\",\n" +
            "        \"receivableType\":\"Individual\",\n" +
            "        \"hierarchyLevel\":\"POINT_OF_SALE\",\n" +
            "        \"individual\":true,\n" +
            "        \"migrated\":true\n" +
            "    },\n" +
            "    \"impersonating\":false,\n" +
            "    \"impersonationEnabled\":true,\n" +
            "    \"lastLoginDate\":\"2022-05-12T00:00:00\",\n" +
            "    \"isMigrationRequired\":false,\n" +
            "    \"onboardingRequired\":true,\n" +
            "    \"mainRole\":\"READER\"\n" +
            "}"

    private val meReader = "{\n" +
            "    \"advertisingId\":\"4271ad\",\n" +
            "    \"username\":\"teste\",\n" +
            "    \"login\":\"1234\",\n" +
            "    \"email\":\"teste@teste.com\",\n" +
            "    \"birthDate\":\"1990-06-11\",\n" +
            "    \"identity\":{\n" +
            "        \"cpf\":\"11111111111\",\n" +
            "        \"foreigner\":false\n" +
            "    },\n" +
            "    \"phoneNumber\":\"(11) 99999-9999\",\n" +
            "    \"roles\":[\n" +
            "        \"MASTER\"\n" +
            "    ],\n" +
            "    \"merchant\":{\n" +
            "        \"id\":\"123\",\n" +
            "        \"receivableType\":\"Individual\",\n" +
            "        \"hierarchyLevel\":\"POINT_OF_SALE\",\n" +
            "        \"individual\":true\n" +
            "    },\n" +
            "    \"activeMerchant\":{\n" +
            "        \"id\":\"123\",\n" +
            "        \"name\":\"teste\",\n" +
            "        \"receivableType\":\"Individual\",\n" +
            "        \"hierarchyLevel\":\"POINT_OF_SALE\",\n" +
            "        \"individual\":true,\n" +
            "        \"migrated\":true\n" +
            "    },\n" +
            "    \"impersonating\":false,\n" +
            "    \"impersonationEnabled\":true,\n" +
            "    \"lastLoginDate\":\"2022-05-12T00:00:00\",\n" +
            "    \"isMigrationRequired\":false,\n" +
            "    \"onboardingRequired\":false,\n" +
            "    \"mainRole\":\"READER\"\n" +
            "}"

    private val meMainRoleNull = "{\n" +
            "    \"advertisingId\":\"4271ad\",\n" +
            "    \"username\":\"teste\",\n" +
            "    \"login\":\"1234\",\n" +
            "    \"email\":\"teste@teste.com\",\n" +
            "    \"birthDate\":\"1990-06-11\",\n" +
            "    \"identity\":{\n" +
            "        \"cpf\":\"11111111111\",\n" +
            "        \"foreigner\":false\n" +
            "    },\n" +
            "    \"phoneNumber\":\"(11) 99999-9999\",\n" +
            "    \"roles\":[\n" +
            "        \"MASTER\"\n" +
            "    ],\n" +
            "    \"merchant\":{\n" +
            "        \"id\":\"123\",\n" +
            "        \"receivableType\":\"Individual\",\n" +
            "        \"hierarchyLevel\":\"POINT_OF_SALE\",\n" +
            "        \"individual\":true\n" +
            "    },\n" +
            "    \"activeMerchant\":{\n" +
            "        \"id\":\"123\",\n" +
            "        \"name\":\"teste\",\n" +
            "        \"receivableType\":\"Individual\",\n" +
            "        \"hierarchyLevel\":\"POINT_OF_SALE\",\n" +
            "        \"individual\":true,\n" +
            "        \"migrated\":true\n" +
            "    },\n" +
            "    \"impersonating\":false,\n" +
            "    \"impersonationEnabled\":true,\n" +
            "    \"lastLoginDate\":\"2022-05-12T00:00:00\",\n" +
            "    \"isMigrationRequired\":false,\n" +
            "    \"onboardingRequired\":true\n" +
            "}"

    private val meMainRoleNullRequiredFalse = "{\n" +
            "    \"advertisingId\":\"4271ad\",\n" +
            "    \"username\":\"teste\",\n" +
            "    \"login\":\"1234\",\n" +
            "    \"email\":\"teste@teste.com\",\n" +
            "    \"birthDate\":\"1990-06-11\",\n" +
            "    \"identity\":{\n" +
            "        \"cpf\":\"11111111111\",\n" +
            "        \"foreigner\":false\n" +
            "    },\n" +
            "    \"phoneNumber\":\"(11) 99999-9999\",\n" +
            "    \"roles\":[\n" +
            "        \"MASTER\"\n" +
            "    ],\n" +
            "    \"merchant\":{\n" +
            "        \"id\":\"123\",\n" +
            "        \"receivableType\":\"Individual\",\n" +
            "        \"hierarchyLevel\":\"POINT_OF_SALE\",\n" +
            "        \"individual\":true\n" +
            "    },\n" +
            "    \"activeMerchant\":{\n" +
            "        \"id\":\"123\",\n" +
            "        \"name\":\"teste\",\n" +
            "        \"receivableType\":\"Individual\",\n" +
            "        \"hierarchyLevel\":\"POINT_OF_SALE\",\n" +
            "        \"individual\":true,\n" +
            "        \"migrated\":true\n" +
            "    },\n" +
            "    \"impersonating\":false,\n" +
            "    \"impersonationEnabled\":true,\n" +
            "    \"lastLoginDate\":\"2022-05-12T00:00:00\",\n" +
            "    \"isMigrationRequired\":false,\n" +
            "    \"onboardingRequired\":false\n" +
            "}"

    @Mock
    lateinit var view: PixTransitoryAccountManagementContract.View

    @Mock
    lateinit var userPreferences: UserPreferences

    @Mock
    lateinit var userInformationRepository: UserInformationRepository

    @Mock
    lateinit var onboardingRepository: IDOnboardingRepository

    @Mock
    lateinit var repository: PixAccountRepositoryContract

    private lateinit var presenter: PixTransitoryAccountManagementPresenter

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        doReturn(TOKEN_MOCK).whenever(userPreferences).token
        presenter = PixTransitoryAccountManagementPresenter(
            view,
            userPreferences,
            userInformationRepository,
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
    fun `When getUsername is called and value is testeCielo, the return must be testeCielo`() {
        doReturn("testeCielo").whenever(userPreferences).userName
        assertEquals("testeCielo", presenter.getUsername())
    }

    @Test
    fun `When getMerchant is called and returns success it should show onSuccessMerchant`() {
        val captorResponse = argumentCaptor<PixMerchantResponse>()

        val response = Gson().fromJson(merchant, PixMerchantResponse::class.java)
        val success = Observable.just(response)
        doReturn(success).whenever(repository)
            .getMerchant()

        presenter.getMerchant()

        verify(view).onShowLoadingMerchant()
        verify(view).onHideMerchant()
        verify(view).onSuccessMerchant(captorResponse.capture())

        verify(view, never()).onErrorMerchant(any())
        verify(view, never()).onShowIDOnboarding()
        verify(view, never()).onNotAdmin()
        verify(view, never()).onValidateMFA()
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

        verify(view).onShowLoadingMerchant()
        verify(view).onHideMerchant()
        verify(view).onErrorMerchant(captorError.capture())

        verify(view, never()).onSuccessMerchant(any())
        verify(view, never()).onShowIDOnboarding()
        verify(view, never()).onNotAdmin()
        verify(view, never()).onValidateMFA()
        verify(view, never()).onErrorChangePixAccount(any())
        verify(view, never()).onSuccessChangePixAccount()

        assertEquals(500, captorError.firstValue.httpStatus)
    }

    @Test
    fun `When getUserInformation is called and returns success with mainRole = ADMIN parameter, it should show onValidateMFA`() {
        val captorToken = argumentCaptor<String>()

        val response = Gson().fromJson(meMaster, MeResponse::class.java)
        val success = Observable.just(response)
        doReturn(success).whenever(userInformationRepository)
            .getUserInformation(captorToken.capture(), any())

        presenter.getUserInformation()

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onValidateMFA()

        verify(view, never()).onShowLoadingMerchant()
        verify(view, never()).onHideMerchant()
        verify(view, never()).onSuccessMerchant(any())
        verify(view, never()).onErrorMerchant(any())
        verify(view, never()).onNotAdmin()
        verify(view, never()).onShowIDOnboarding()
        verify(view, never()).onErrorChangePixAccount(any())
        verify(view, never()).onSuccessChangePixAccount()

        assertEquals(TOKEN_MOCK, captorToken.firstValue)
    }

    @Test
    fun `When getUserInformation is called and returns success with the parameters mainRole = READER and onboardingRequired = true , it should show onShowIDOnboarding`() {
        val captorToken = argumentCaptor<String>()

        val response = Gson().fromJson(meReaderP2, MeResponse::class.java)
        val success = Observable.just(response)
        doReturn(success).whenever(userInformationRepository)
            .getUserInformation(captorToken.capture(), any())

        presenter.getUserInformation()

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onShowIDOnboarding()

        verify(view, never()).onShowLoadingMerchant()
        verify(view, never()).onHideMerchant()
        verify(view, never()).onSuccessMerchant(any())

        verify(view, never()).onErrorMerchant(any())
        verify(view, never()).onNotAdmin()
        verify(view, never()).onValidateMFA()
        verify(view, never()).onErrorChangePixAccount(any())
        verify(view, never()).onSuccessChangePixAccount()

        assertEquals(TOKEN_MOCK, captorToken.firstValue)
    }

    @Test
    fun `When getUserInformation is called and returns success with the parameters mainRole = READER and onboardingRequired = false , it should show onNotAdmin`() {
        val captorToken = argumentCaptor<String>()

        val response = Gson().fromJson(meReader, MeResponse::class.java)
        val success = Observable.just(response)
        doReturn(success).whenever(userInformationRepository)
            .getUserInformation(captorToken.capture(), any())

        presenter.getUserInformation()

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onNotAdmin()

        verify(view, never()).onShowLoadingMerchant()
        verify(view, never()).onHideMerchant()
        verify(view, never()).onSuccessMerchant(any())

        verify(view, never()).onErrorMerchant(any())
        verify(view, never()).onShowIDOnboarding()
        verify(view, never()).onValidateMFA()
        verify(view, never()).onErrorChangePixAccount(any())
        verify(view, never()).onSuccessChangePixAccount()

        assertEquals(TOKEN_MOCK, captorToken.firstValue)
    }

    @Test
    fun `When getUserInformation is called and returns success with the parameters mainRole = null and onboardingRequired = true , it should show onShowIDOnboarding`() {
        val captorToken = argumentCaptor<String>()

        val response = Gson().fromJson(meMainRoleNull, MeResponse::class.java)
        val success = Observable.just(response)
        doReturn(success).whenever(userInformationRepository)
            .getUserInformation(captorToken.capture(), any())

        presenter.getUserInformation()

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onValidateMFA()

        verify(view, never()).onShowLoadingMerchant()
        verify(view, never()).onHideMerchant()
        verify(view, never()).onSuccessMerchant(any())
        verify(view, never()).onErrorMerchant(any())
        verify(view, never()).onNotAdmin()
        verify(view, never()).onErrorChangePixAccount(any())
        verify(view, never()).onSuccessChangePixAccount()

        assertEquals(TOKEN_MOCK, captorToken.firstValue)
    }

    @Test
    fun `When getUserInformation is called and it returns error it should show showError`() {
        val captorToken = argumentCaptor<String>()
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

        doReturn(errorObservable).whenever(userInformationRepository)
            .getUserInformation(captorToken.capture(), any())

        presenter.getUserInformation()

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showError(captorError.capture())

        verify(view, never()).onShowLoadingMerchant()
        verify(view, never()).onHideMerchant()
        verify(view, never()).onSuccessMerchant(any())
        verify(view, never()).onErrorMerchant(any())
        verify(view, never()).onNotAdmin()
        verify(view, never()).onShowIDOnboarding()
        verify(view, never()).onValidateMFA()
        verify(view, never()).onErrorChangePixAccount(any())
        verify(view, never()).onSuccessChangePixAccount()

        assertEquals(TOKEN_MOCK, captorToken.firstValue)
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
        verify(view, never()).onShowLoadingMerchant()
        verify(view, never()).onHideMerchant()
        verify(view, never()).onSuccessMerchant(any())
        verify(view, never()).onErrorMerchant(any())
        verify(view, never()).onNotAdmin()
        verify(view, never()).onShowIDOnboarding()
        verify(view, never()).onValidateMFA()
        verify(view, never()).onSuccessChangePixAccount()

        assertEquals(OTP_MOCK, captorOTP.firstValue)

        assertEquals(false, captorRequest.firstValue.settlementActive)
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
        verify(view, never()).onShowLoadingMerchant()
        verify(view, never()).onHideMerchant()
        verify(view, never()).onSuccessMerchant(any())
        verify(view, never()).onErrorMerchant(any())
        verify(view, never()).onNotAdmin()
        verify(view, never()).onShowIDOnboarding()
        verify(view, never()).onValidateMFA()

        assertEquals(OTP_MOCK, captorOTP.firstValue)

        assertEquals(false, captorRequest.firstValue.settlementActive)
    }
}