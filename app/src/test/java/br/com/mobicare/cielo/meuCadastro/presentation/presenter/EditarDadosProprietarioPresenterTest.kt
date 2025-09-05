package br.com.mobicare.cielo.meuCadastro.presentation.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.meuCadastroNovo.MeuCadastroNovoRepository
import br.com.mobicare.cielo.meuCadastroNovo.domain.Owner
import br.com.mobicare.cielo.meuCadastroNovo.domain.Phone
import br.com.mobicare.cielo.meuCadastroNovo.domain.PhoneContato
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.owner.EditarDadosProprietarioContract
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.owner.EditarDadosProprietarioPresenter
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import retrofit2.Response

private const val TOKEN = "zTZRBe5g98MkDcx6cRK"
private const val OTP = "000000"
private const val EMPTY = ""

class EditarDadosProprietarioPresenterTest {

    private val owner = Owner(
        email = "test@test.com",
        phones = listOf(
            Phone("55", "923192705", "CELLPHONE"),
            Phone("55", "923192706", "CELLPHONE")
        ),
        cpf = "97812218015",
        birthDate = "10/10/1990",
        name = "tester"
    )

    private val tel1 = "(55) 923192705"
    private val tel2 = "(55) 923192706"
    private val telIncorrect = "123456"
    private val emailIncorrect = "test@test"

    private val errorMessage = ErrorMessage().apply {
        title = "Error"
        errorMessage = "Error"
        httpStatus = 500
        errorCode = "HTTP_INTERNAL_ERROR"
        code = "500"
    }

    private val errorLogout = ErrorMessage().apply {
        logout = true
        errorMessage = "Error"
    }

    @Mock
    lateinit var view: EditarDadosProprietarioContract.View

    @Mock
    lateinit var userPreferences: UserPreferences

    @Mock
    lateinit var repository: MeuCadastroNovoRepository

    private lateinit var presenter: EditarDadosProprietarioPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = EditarDadosProprietarioPresenter(repository, view, userPreferences)
    }

    @Test
    fun `success put owner data`() {
        val captor = argumentCaptor<Owner>()

        presenter.putOwnerData(owner)

        verify(view).showOwnerData(captor.capture())

        assertEquals("tester", captor.firstValue.name)
        assertEquals("test@test.com", captor.firstValue.email)
        assertEquals(
            listOf(
                Phone("55", "923192705", "CELLPHONE"),
                Phone("55", "923192706", "CELLPHONE")
            ),
            captor.firstValue.phones
        )
        assertEquals("97812218015", captor.firstValue.cpf)
        assertEquals("10/10/1990", captor.firstValue.birthDate)
    }

    @Test
    fun `success in saving changes to owner data`() {
        doReturn(TOKEN).whenever(userPreferences).token

        whenever(
            repository.putOwner(
                eq(userPreferences.token),
                eq(OTP),
                eq(owner),
                any()
            )
        ).thenAnswer {
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onStart()
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onSuccess(
                Response.success(null)
            )
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onFinish()
        }

        presenter.putOwnerData(owner)

        presenter.save(
            OTP,
            owner.email!!,
            tel1,
            tel2,
            EMPTY,
        )

        val captor = argumentCaptor<Boolean>()

        verify(view).showOwnerData(any())
        verify(view).showPhoneFillError(captor.capture())
        verify(view).showEmailFillError(captor.capture())
        verify(view).showLoading()
        verify(view).showSaveSuccessful()
        verify(view, never()).showError(any())
        verify(view, never()).logout(any())

        assertEquals(false, captor.firstValue)
        assertEquals(false, captor.secondValue)
    }

    @Test
    fun `generic error in saving changes to owner data`() {
        doReturn(TOKEN).whenever(userPreferences).token

        whenever(
            repository.putOwner(
                eq(userPreferences.token),
                eq(OTP),
                eq(owner),
                any()
            )
        ).thenAnswer {
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onStart()
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onError(
                errorMessage
            )
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onFinish()
        }

        presenter.putOwnerData(owner)

        presenter.save(
            OTP,
            owner.email!!,
            tel1,
            tel2,
            EMPTY,
        )

        val captor = argumentCaptor<ErrorMessage>()
        val captorBoolean = argumentCaptor<Boolean>()

        verify(view).showOwnerData(any())
        verify(view).showPhoneFillError(captorBoolean.capture())
        verify(view).showEmailFillError(captorBoolean.capture())
        verify(view).showLoading()
        verify(view, never()).showSaveSuccessful()
        verify(view).showError(captor.capture())
        verify(view, never()).logout(any())

        assertEquals(false, captorBoolean.firstValue)
        assertEquals(false, captorBoolean.secondValue)
        assertEquals("Error", captor.firstValue.title)
        assertEquals("Error", captor.firstValue.errorMessage)
        assertEquals(500, captor.firstValue.httpStatus)
        assertEquals("HTTP_INTERNAL_ERROR", captor.firstValue.errorCode)
        assertEquals("500", captor.firstValue.code)
    }

    @Test
    fun `logout error in saving changes to owner data`() {
        doReturn(TOKEN).whenever(userPreferences).token

        whenever(
            repository.putOwner(
                eq(userPreferences.token),
                eq(OTP),
                eq(owner),
                any()
            )
        ).thenAnswer {
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onStart()
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onError(
                errorLogout
            )
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onFinish()
        }

        presenter.putOwnerData(owner)

        presenter.save(
            OTP,
            owner.email!!,
            tel1,
            tel2,
            EMPTY,
        )

        val captor = argumentCaptor<ErrorMessage>()
        val captorBoolean = argumentCaptor<Boolean>()

        verify(view).showOwnerData(any())
        verify(view).showPhoneFillError(captorBoolean.capture())
        verify(view).showEmailFillError(captorBoolean.capture())
        verify(view).showLoading()
        verify(view, never()).showSaveSuccessful()
        verify(view, never()).showError(any())
        verify(view).logout(captor.capture())

        assertEquals(false, captorBoolean.firstValue)
        assertEquals(false, captorBoolean.secondValue)
        assertEquals(true, captor.firstValue.logout)
        assertEquals("Error", captor.firstValue.errorMessage)
    }

    @Test
    fun `error in saving changes to owner data because tel is empty`() {
        doReturn(TOKEN).whenever(userPreferences).token

        presenter.putOwnerData(owner)

        presenter.save(
            OTP,
            EMPTY,
            EMPTY,
            EMPTY,
            EMPTY,
        )

        val captor = argumentCaptor<Boolean>()

        verify(view).showOwnerData(any())
        verify(view).showPhoneFillError(captor.capture())
        verify(view, never()).showEmailFillError(any())
        verify(view, never()).showLoading()
        verify(view, never()).showSaveSuccessful()
        verify(view, never()).showError(any())
        verify(view, never()).logout(any())

        assertEquals(true, captor.firstValue)
    }

    @Test
    fun `error in saving changes to owner data because tel is incorrect`() {
        doReturn(TOKEN).whenever(userPreferences).token

        presenter.putOwnerData(owner)

        presenter.save(
            OTP,
            EMPTY,
            telIncorrect,
            telIncorrect,
            EMPTY,
        )

        verify(view).showOwnerData(any())
        verify(view, never()).showPhoneFillError(any())
        verify(view, never()).showEmailFillError(any())
        verify(view, never()).showLoading()
        verify(view, never()).showSaveSuccessful()
        verify(view, never()).showError(any())
        verify(view, never()).logout(any())
    }

    @Test
    fun `error in saving changes to owner data because email is empty`() {
        doReturn(TOKEN).whenever(userPreferences).token

        presenter.putOwnerData(owner)

        presenter.save(
            OTP,
            EMPTY,
            tel1,
            tel2,
            EMPTY,
        )

        val captor = argumentCaptor<Boolean>()

        verify(view).showOwnerData(any())
        verify(view).showPhoneFillError(captor.capture())
        verify(view).showEmailFillError(captor.capture())
        verify(view, never()).showLoading()
        verify(view, never()).showSaveSuccessful()
        verify(view, never()).showError(any())
        verify(view, never()).logout(any())

        assertEquals(false, captor.firstValue)
        assertEquals(true, captor.secondValue)
    }

    @Test
    fun `error in saving changes to owner data because email is incorrect`() {
        doReturn(TOKEN).whenever(userPreferences).token

        presenter.putOwnerData(owner)

        presenter.save(
            OTP,
            emailIncorrect,
            tel1,
            tel2,
            EMPTY,
        )

        val captor = argumentCaptor<Boolean>()

        verify(view).showOwnerData(any())
        verify(view).showPhoneFillError(captor.capture())
        verify(view).showEmailFillError(captor.capture())
        verify(view, never()).showLoading()
        verify(view, never()).showSaveSuccessful()
        verify(view, never()).showError(any())
        verify(view, never()).logout(any())

        assertEquals(false, captor.firstValue)
    }

}