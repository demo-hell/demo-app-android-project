package br.com.mobicare.cielo.meuCadastro.presentation.presenter

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.meuCadastroNovo.MeuCadastroNovoRepository
import br.com.mobicare.cielo.meuCadastroNovo.domain.Contact
import br.com.mobicare.cielo.meuCadastroNovo.domain.PhoneContato
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.contato.EditarDadosContatoContract
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.contato.EditarDadosContatoPresenter
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

class EditarDadosContatoPresenterTest {

    private val contact = Contact(
        null,
        "test@test.com",
        "Test",
        listOf(
            PhoneContato(null, "55", "923192705", "CELLPHONE"),
            PhoneContato(null, "55", "923192706", "CELLPHONE")
        ),
        listOf("CONTRACT", "SALE")
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
    lateinit var view: EditarDadosContatoContract.View

    @Mock
    lateinit var userPreferences: UserPreferences

    @Mock
    lateinit var repository: MeuCadastroNovoRepository

    private lateinit var presenter: EditarDadosContatoPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = EditarDadosContatoPresenter(repository, view, userPreferences)
    }

    @Test
    fun `success put contact data`() {
        val captor = argumentCaptor<Contact>()

        presenter.putContactData(contact)

        verify(view).showContactData(captor.capture())

        assertEquals(null, captor.firstValue.id)
        assertEquals("Test", captor.firstValue.name)
        assertEquals("test@test.com", captor.firstValue.email)
        assertEquals(
            listOf(
                PhoneContato(null, "55", "923192705", "CELLPHONE"),
                PhoneContato(null, "55", "923192706", "CELLPHONE")
            ),
            captor.firstValue.phones
        )
        assertEquals(listOf("CONTRACT", "SALE"), captor.firstValue.types)
    }

    @Test
    fun `success in saving changes to contact data`() {
        doReturn(TOKEN).whenever(userPreferences).token

        whenever(
            repository.putContact(
                eq(userPreferences.token),
                eq(OTP),
                eq(contact),
                any()
            )
        ).thenAnswer {
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onStart()
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onSuccess(
                Response.success(null)
            )
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onFinish()
        }

        presenter.putContactData(contact)

        presenter.save(
            contact.name,
            contact.email!!,
            tel1,
            tel2,
            EMPTY,
            OTP
        )

        val captorBool = argumentCaptor<Boolean>()
        val captorIndex = argumentCaptor<Int>()
        val captorString = argumentCaptor<Int>()

        verify(view).showContactData(any())
        verify(view).showNameFillError(captorBool.capture())
        verify(view).showEmailFillError(captorBool.capture())
        verify(view).showInvalidEmail(captorBool.capture())
        verify(view, times(2)).showPhoneFillError(captorIndex.capture(), captorString.capture())
        verify(view).showLoading()
        verify(view).showSaveSuccessful()
        verify(view, never()).hideLoading()
        verify(view, never()).showError(any())
        verify(view, never()).logout(any())

        assertEquals(false, captorBool.firstValue)
        assertEquals(false, captorBool.secondValue)
        assertEquals(false, captorBool.thirdValue)
        assertEquals(ZERO, captorIndex.firstValue)
        assertEquals(null, captorString.firstValue)
        assertEquals(ONE, captorIndex.secondValue)
        assertEquals(null, captorString.secondValue)
    }

    @Test
    fun `generic error in saving changes to contact data`() {
        doReturn(TOKEN).whenever(userPreferences).token

        whenever(
            repository.putContact(
                eq(userPreferences.token),
                eq(OTP),
                eq(contact),
                any()
            )
        ).thenAnswer {
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onStart()
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onError(
                errorMessage
            )
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onFinish()
        }

        presenter.putContactData(contact)

        presenter.save(
            contact.name,
            contact.email!!,
            tel1,
            tel2,
            EMPTY,
            OTP
        )

        val captorBool = argumentCaptor<Boolean>()
        val captor = argumentCaptor<ErrorMessage>()
        val captorIndex = argumentCaptor<Int>()
        val captorString = argumentCaptor<Int>()

        verify(view).showContactData(any())
        verify(view).showNameFillError(captorBool.capture())
        verify(view).showEmailFillError(captorBool.capture())
        verify(view).showInvalidEmail(captorBool.capture())
        verify(view, times(2)).showPhoneFillError(captorIndex.capture(), captorString.capture())
        verify(view).showLoading()
        verify(view, never()).showSaveSuccessful()
        verify(view).hideLoading()
        verify(view).showError(captor.capture())
        verify(view, never()).logout(any())

        assertEquals(false, captorBool.firstValue)
        assertEquals(false, captorBool.secondValue)
        assertEquals(false, captorBool.thirdValue)
        assertEquals(ZERO, captorIndex.firstValue)
        assertEquals(null, captorString.firstValue)
        assertEquals(ONE, captorIndex.secondValue)
        assertEquals(null, captorString.secondValue)
        assertEquals("Error", captor.firstValue.title)
        assertEquals("Error", captor.firstValue.errorMessage)
        assertEquals(500, captor.firstValue.httpStatus)
        assertEquals("HTTP_INTERNAL_ERROR", captor.firstValue.errorCode)
        assertEquals("500", captor.firstValue.code)
    }

    @Test
    fun `logout error in saving changes to contact data`() {
        doReturn(TOKEN).whenever(userPreferences).token

        whenever(
            repository.putContact(
                eq(userPreferences.token),
                eq(OTP),
                eq(contact),
                any()
            )
        ).thenAnswer {
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onStart()
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onError(
                errorLogout
            )
            (it.arguments[3] as APICallbackDefault<Response<Void>, String>).onFinish()
        }

        presenter.putContactData(contact)

        presenter.save(
            contact.name,
            contact.email!!,
            tel1,
            tel2,
            EMPTY,
            OTP
        )

        val captorBool = argumentCaptor<Boolean>()
        val captor = argumentCaptor<ErrorMessage>()
        val captorIndex = argumentCaptor<Int>()
        val captorString = argumentCaptor<Int>()

        verify(view).showContactData(any())
        verify(view).showNameFillError(captorBool.capture())
        verify(view).showEmailFillError(captorBool.capture())
        verify(view).showInvalidEmail(captorBool.capture())
        verify(view, times(2)).showPhoneFillError(captorIndex.capture(), captorString.capture())
        verify(view).showLoading()
        verify(view, never()).showSaveSuccessful()
        verify(view).hideLoading()
        verify(view, never()).showError(any())
        verify(view).logout(captor.capture())

        assertEquals(false, captorBool.firstValue)
        assertEquals(false, captorBool.secondValue)
        assertEquals(false, captorBool.thirdValue)
        assertEquals(ZERO, captorIndex.firstValue)
        assertEquals(null, captorString.firstValue)
        assertEquals(ONE, captorIndex.secondValue)
        assertEquals(null, captorString.secondValue)

        assertEquals("Error", captor.firstValue.message)
        assertEquals(true, captor.firstValue.logout)
    }

    @Test
    fun `error in saving changes to contact data because name is empty`() {
        doReturn(TOKEN).whenever(userPreferences).token

        presenter.putContactData(contact)

        presenter.save(
            EMPTY,
            contact.email!!,
            tel1,
            tel2,
            EMPTY,
            OTP
        )

        val captor = argumentCaptor<Boolean>()

        verify(view).showContactData(any())
        verify(view).showNameFillError(captor.capture())
        verify(view, never()).showEmailFillError(any())
        verify(view, never()).showInvalidEmail(any())
        verify(view, never()).showPhoneFillError(any(), anyOrNull())
        verify(view, never()).showLoading()
        verify(view, never()).showSaveSuccessful()
        verify(view, never()).hideLoading()
        verify(view, never()).showError(any())
        verify(view, never()).logout(any())

        assertEquals(true, captor.firstValue)
    }

    @Test
    fun `error in saving changes to contact data with email empty`() {
        doReturn(TOKEN).whenever(userPreferences).token

        presenter.putContactData(contact)

        presenter.save(
            contact.name,
            EMPTY,
            tel1,
            tel2,
            EMPTY,
            OTP
        )

        val captor = argumentCaptor<Boolean>()

        verify(view).showContactData(any())
        verify(view).showNameFillError(captor.capture())
        verify(view).showEmailFillError(captor.capture())
        verify(view, never()).showInvalidEmail(any())
        verify(view, never()).showPhoneFillError(any(), anyOrNull())
        verify(view, never()).showLoading()
        verify(view, never()).showSaveSuccessful()
        verify(view, never()).hideLoading()
        verify(view, never()).showError(any())
        verify(view, never()).logout(any())

        assertEquals(false, captor.firstValue)
        assertEquals(true, captor.secondValue)
    }

    @Test
    fun `error in saving changes to contact data with email incorrect`() {
        doReturn(TOKEN).whenever(userPreferences).token

        presenter.putContactData(contact)

        presenter.save(
            contact.name,
            emailIncorrect,
            tel1,
            tel2,
            EMPTY,
            OTP
        )

        val captor = argumentCaptor<Boolean>()

        verify(view).showContactData(any())
        verify(view).showNameFillError(captor.capture())
        verify(view).showEmailFillError(captor.capture())
        verify(view).showInvalidEmail(captor.capture())
        verify(view, never()).showPhoneFillError(any(), anyOrNull())
        verify(view, never()).showLoading()
        verify(view, never()).showSaveSuccessful()
        verify(view, never()).hideLoading()
        verify(view, never()).showError(any())
        verify(view, never()).logout(any())

        assertEquals(false, captor.firstValue)
        assertEquals(false, captor.secondValue)
        assertEquals(true, captor.thirdValue)
    }

    @Test
    fun `error in saving changes to contact data with tel empty`() {
        doReturn(TOKEN).whenever(userPreferences).token

        presenter.putContactData(contact)

        presenter.save(
            contact.name,
            contact.email!!,
            EMPTY,
            EMPTY,
            EMPTY,
            OTP
        )

        val captorBool = argumentCaptor<Boolean>()
        val captorIndex = argumentCaptor<Int>()
        val captorString = argumentCaptor<Int>()

        verify(view).showContactData(any())
        verify(view).showNameFillError(captorBool.capture())
        verify(view).showEmailFillError(captorBool.capture())
        verify(view).showInvalidEmail(captorBool.capture())
        verify(view).showPhoneFillError(captorIndex.capture(), captorString.capture())
        verify(view, never()).showLoading()
        verify(view, never()).showSaveSuccessful()
        verify(view, never()).hideLoading()
        verify(view, never()).showError(any())
        verify(view, never()).logout(any())

        assertEquals(false, captorBool.firstValue)
        assertEquals(false, captorBool.secondValue)
        assertEquals(false, captorBool.thirdValue)
        assertEquals(ZERO, captorIndex.firstValue)
        assertEquals(R.string.text_contact_phone_empty, captorString.firstValue)
    }

    @Test
    fun `error in saving changes to contact data with tel incorrect`() {
        doReturn(TOKEN).whenever(userPreferences).token

        presenter.putContactData(contact)

        presenter.save(
            contact.name,
            contact.email!!,
            telIncorrect,
            EMPTY,
            EMPTY,
            OTP
        )

        val captorBool = argumentCaptor<Boolean>()
        val captorIndex = argumentCaptor<Int>()
        val captorString = argumentCaptor<Int>()

        verify(view).showContactData(any())
        verify(view).showNameFillError(captorBool.capture())
        verify(view).showEmailFillError(captorBool.capture())
        verify(view).showInvalidEmail(captorBool.capture())
        verify(view).showPhoneFillError(captorIndex.capture(), captorString.capture())
        verify(view, never()).showLoading()
        verify(view, never()).showSaveSuccessful()
        verify(view, never()).hideLoading()
        verify(view, never()).showError(any())
        verify(view, never()).logout(any())

        assertEquals(false, captorBool.firstValue)
        assertEquals(false, captorBool.secondValue)
        assertEquals(false, captorBool.thirdValue)
        assertEquals(ZERO, captorIndex.firstValue)
        assertEquals(R.string.text_contact_phone_is_not_valid, captorString.firstValue)
    }

}