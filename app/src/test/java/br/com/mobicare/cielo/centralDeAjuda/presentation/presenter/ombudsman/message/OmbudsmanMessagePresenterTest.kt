package br.com.mobicare.cielo.centralDeAjuda.presentation.presenter.ombudsman.message

import br.com.mobicare.cielo.centralDeAjuda.data.clients.CentralAjudaLogadoRepository
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.OmbudsmanRequest
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.OmbudsmanResponse
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos.ombudsman.message.OmbudsmanMessageContract
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos.ombudsman.message.OmbudsmanMessagePresenter
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

private const val PROTOCOL_MOCK = "123"
private const val SUBJECT_MOCK = "Elogio"
private const val MESSAGE_MOCK = "Mensagem teste"

private const val TIME_MOCK = "2022-01-03T17:26:24.939"
private const val TEST_MOCK = "teste"

private const val CODE_ERROR_MOCK = "error_generic"

class OmbudsmanMessagePresenterTest {

    private val request = OmbudsmanRequest(
            contactPerson = TEST_MOCK,
            email = "teste@teste.com",
            merchant = "123",
            phone = "11999999999"
    )

    @Mock
    lateinit var view: OmbudsmanMessageContract.View

    @Mock
    lateinit var repository: CentralAjudaLogadoRepository

    private lateinit var presenter: OmbudsmanMessagePresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = OmbudsmanMessagePresenter(view, repository)
    }

    @Test
    fun `Check the success return when sendProtocol is called`() {
        doAnswer {
            (it.arguments[1] as APICallbackDefault<OmbudsmanResponse, String>).onSuccess(
                    OmbudsmanResponse(codigo = TEST_MOCK,
                            date = TIME_MOCK,
                            descricao = TEST_MOCK,
                            requestId = "1"
                    )
            )
        }.whenever(repository).sendProtocol(eq(request), any())

        presenter.onSendProtocol(request, SUBJECT_MOCK, PROTOCOL_MOCK, MESSAGE_MOCK)

        val captor = argumentCaptor<OmbudsmanResponse>()
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onSuccess(captor.capture())
        verify(view, never()).showError(any())

        assertEquals(TEST_MOCK, captor.firstValue.codigo)
        assertEquals(TIME_MOCK, captor.firstValue.date)
        assertEquals(TEST_MOCK, captor.firstValue.descricao)
        assertEquals("1", captor.firstValue.requestId)
    }

    @Test
    fun `Check error return with status 500 when sendProtocol is called`() {
        val error = ErrorMessage().apply {
            this.title = "Error"
            this.httpStatus = 500
            this.code = "500"
            this.errorCode = CODE_ERROR_MOCK
        }

        doAnswer {
            (it.arguments[1] as APICallbackDefault<OmbudsmanResponse, String>).onError(error)
        }.whenever(repository).sendProtocol(eq(request), any())

        presenter.onSendProtocol(request, SUBJECT_MOCK, PROTOCOL_MOCK, MESSAGE_MOCK)

        val captor = argumentCaptor<ErrorMessage>()
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showError(captor.capture())
        verify(view, never()).onSuccess(any())

        assertEquals(500, captor.firstValue.httpStatus)
        assertEquals(CODE_ERROR_MOCK, captor.firstValue.errorCode)
    }
}