package br.com.mobicare.cielo.meuCadastro.presentation.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.meuCadastroNovo.MeuCadastroNovoRepository
import br.com.mobicare.cielo.meuCadastroNovo.domain.Owner
import br.com.mobicare.cielo.meuCadastroNovo.domain.Phone
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment.AlertaCadastralContract
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment.DadosProprietarioPresenter
import com.nhaarman.mockito_kotlin.*
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import retrofit2.Response

private const val TOKEN = "zTZRBe5g98MkDcx6cRK"
private const val OTP = "000000"

class DadosProprietarioAlertaCadastralPresenterTest {

    private val owner = Owner(
        email = "automacao@osxos.net",
        phones = listOf(Phone("55", "923192705", "CELLPHONE")),
        cpf = "97812218015",
        birthDate = "10/10/1990",
        name = "tester"
    )

    private val errorMessage = ErrorMessage().apply {
        title = "Error"
        errorMessage = "Error"
        httpStatus = 500
        errorCode = "HTTP_INTERNAL_ERROR"
        code = "500"
    }

    @Mock
    lateinit var userPreferences: UserPreferences

    @Mock
    lateinit var view: AlertaCadastralContract.View

    @Mock
    lateinit var repository: MeuCadastroNovoRepository

    private lateinit var presenter: DadosProprietarioPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = DadosProprietarioPresenter(userPreferences, view, repository)
    }

    @Test
    fun `success on confirm owner data`() {
        doReturn(TOKEN).whenever(userPreferences).token

        whenever(repository.putOwner(eq(userPreferences.token), eq(OTP), eq(owner), any())).thenAnswer {
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onStart()
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onSuccess(
                Response.success(
                    null
                )
            )
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onFinish()
        }

        presenter.submitOwnerData(OTP, owner)

        verify(view).showLoading()
        verify(view, times(2)).removeAlertMessage()
        verify(view).hideLoading()
        verify(view, never()).showError(any())
        verify(view, never()).addAlertMessage()
    }

    @Test
    fun `error on confirm owner data when token is null`() {
        doReturn(null).whenever(userPreferences).token

        presenter.submitOwnerData(OTP, owner)

        verify(repository, never()).putOwner(any(), any(), any(), any())
    }

    @Test
    fun `error on confirm owner data`() {
        doReturn(TOKEN).whenever(userPreferences).token

        whenever(repository.putOwner(eq(userPreferences.token), eq(OTP), eq(owner), any())).thenAnswer {
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onStart()
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onError(errorMessage)
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onFinish()
        }

        val captor = argumentCaptor<ErrorMessage>()

        presenter.submitOwnerData(OTP, owner)

        verify(view).showLoading()
        verify(view).removeAlertMessage()
        verify(view).showError(captor.capture())
        verify(view).addAlertMessage()
        verify(view).hideLoading()

        assertEquals("Error", captor.firstValue.title)
        assertEquals("Error", captor.firstValue.errorMessage)
        assertEquals(500, captor.firstValue.httpStatus)
        assertEquals("HTTP_INTERNAL_ERROR", captor.firstValue.errorCode)
        assertEquals("500", captor.firstValue.code)
    }
}

