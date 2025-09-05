package br.com.mobicare.cielo.pix.ui.transfer.key

import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pix.api.keys.PixKeysRepositoryContract
import br.com.mobicare.cielo.pix.domain.ValidateKeyResponse
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

private const val KEY = "63538573069"

class PixInsertKeyPresenterTest {

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
            "\"claimType\": \"POSSESSION_CLAIM\",\n" +
            "\"endToEndId\": \"6b68b5e675a7487ca5b98613b7c1895a\"\n" +
            "}"

    @Mock
    lateinit var view: PixInsertKeyContract.View

    @Mock
    lateinit var repository: PixKeysRepositoryContract

    private lateinit var presenter: PixInsertKeyPresenter

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = PixInsertKeyPresenter(
            view,
            repository,
            uiScheduler, ioScheduler
        )
    }

    @Test
    fun `when call onValidateKey and have a success return show onValidKey`() {
        val captor = argumentCaptor<ValidateKeyResponse>()
        val key = argumentCaptor<String>()
        val type = argumentCaptor<String>()

        val response = Gson().fromJson(response, ValidateKeyResponse::class.java)

        val success = Observable.just(response)
        doReturn(success).whenever(repository)
            .validateKey(key.capture(), type.capture())

        presenter.onValidateKey(KEY, PixKeyTypeEnum.CPF)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onValidKey(captor.capture())

        verify(view, never()).onErrorInput(any())
        verify(view, never()).showError(any())

        assertEquals(KEY, key.firstValue)
        assertEquals(PixKeyTypeEnum.CPF.name, type.firstValue)

        assertEquals(KEY, captor.firstValue.key)
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
    fun `when call onValidateKey and get error 500 return show showError`() {
        val captor = argumentCaptor<ErrorMessage>()
        val key = argumentCaptor<String>()
        val type = argumentCaptor<String>()

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
            .validateKey(key.capture(), type.capture())

        presenter.onValidateKey(KEY, PixKeyTypeEnum.CPF)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showError(captor.capture())

        verify(view, never()).onValidKey(any())
        verify(view, never()).onErrorInput(any())

        assertEquals(KEY, key.firstValue)
        assertEquals(PixKeyTypeEnum.CPF.name, type.firstValue)

        assertEquals(500, captor.firstValue.httpStatus)
    }

    @Test
    fun `when call onValidateKey and get error 420 return show onErrorInput`() {
        val captor = argumentCaptor<ErrorMessage>()
        val key = argumentCaptor<String>()
        val type = argumentCaptor<String>()

        val exception = RetrofitException(
            message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 420)

        val errorObservable = Observable.error<RetrofitException>(exception)
        doReturn(errorObservable).whenever(repository)
            .validateKey(key.capture(), type.capture())

        presenter.onValidateKey(KEY, PixKeyTypeEnum.CPF)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onErrorInput(captor.capture())

        verify(view, never()).showError(any())
        verify(view, never()).onValidKey(any())

        assertEquals(KEY, key.firstValue)
        assertEquals(PixKeyTypeEnum.CPF.name, type.firstValue)

        assertEquals(420, captor.firstValue.httpStatus)
    }
}