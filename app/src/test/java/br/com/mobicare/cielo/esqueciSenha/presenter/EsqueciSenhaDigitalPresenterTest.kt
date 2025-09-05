package br.com.mobicare.cielo.esqueciSenha.presenter

/*
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.esqueciSenha.data.clients.managers.EsqueciSenhaNewRepository
import br.com.mobicare.cielo.esqueciSenha.domains.entities.Login
import br.com.mobicare.cielo.esqueciSenha.domains.entities.Pid
import br.com.mobicare.cielo.esqueciSenha.domains.entities.RecoveryPassword
import br.com.mobicare.cielo.esqueciSenha.presentation.presenter.EsqueciSenhaDigitalPresenter
import br.com.mobicare.cielo.esqueciSenha.presentation.ui.EsqueciSenhaDigitalContract
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

const val USER_NAME_MOCK = "91136373047"
const val MERCHANT_ID_MOCK = "2014571923"
const val CARD_PROXY_MOCK = "000000000158419"
private const val AKAMAI_SENSOR_DATA = "8,a,tS5ta5h/0jnwDnC+qsrRETEZrTPwdL/dioHSXgVMMB/XLWydkVqNBUiwHJkOG1sLmiJ88aG0y"

class EsqueciSenhaDigitalPresenterTest {

    private val login = Login(username = USER_NAME_MOCK)
    private val pid = Pid(merchantId = MERCHANT_ID_MOCK, cardProxy = CARD_PROXY_MOCK)
    private val data = RecoveryPassword(login, pid)

    lateinit var presenter: EsqueciSenhaDigitalPresenter

    @Mock
    lateinit var view: EsqueciSenhaDigitalContract.View

    @Mock
    lateinit var repository: EsqueciSenhaNewRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        presenter = EsqueciSenhaDigitalPresenter(view, repository)
    }

    @Test
    fun `When calling API forgot password in account digital return is success`() {
        doAnswer {
            (it.arguments[1] as APICallbackDefault<String, String>).onSuccess(MSG_SUCCESS_MOCK)
        }.whenever(repository).recoveryPassword(
                data = eq(data),
                callback = any(),
                akamaiSensorData = eq(AKAMAI_SENSOR_DATA)
        )

        presenter.resetPassword(data)
        verify(view).hideProgress()
        verify(view).showSuccess(MSG_SUCCESS_MOCK)

    }

    @Test
    fun `When calling API forgot password in account digital return is error`() {
        val exception = RetrofitException(message = null,
                url = null,
                response = null,
                kind = RetrofitException.Kind.NETWORK,
                exception = null,
                retrofit = null,
                httpStatus = 500)

        val errorMessage = APIUtils.convertToErro(exception)

        doAnswer {
            (it.arguments[1] as APICallbackDefault<String, String>).onError(errorMessage)
        }.whenever(repository).recoveryPassword(
                data = eq(data),
                callback = any(),
                akamaiSensorData = eq(AKAMAI_SENSOR_DATA)
        )

        presenter.resetPassword(data)
        verify(view).hideProgress()
        verify(view).showError(errorMessage)
    }

}*/
