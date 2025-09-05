package br.com.mobicare.cielo.pix.ui.transfer.agency

import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.pix.api.transfer.PixTransferRepositoryContract
import br.com.mobicare.cielo.pix.model.PixBank
import br.com.mobicare.cielo.pix.ui.transfer.agency.selectBank.PixSelectBankContract
import br.com.mobicare.cielo.pix.ui.transfer.agency.selectBank.PixSelectBankPresenter
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class PixSelectBankPresenterTest {

    private lateinit var responseBanks: MutableList<PixBank>

    @Mock
    lateinit var view: PixSelectBankContract.View

    @Mock
    lateinit var repository: PixTransferRepositoryContract

    private lateinit var presenter: PixSelectBankPresenter

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        presenter = PixSelectBankPresenter(
                view,
                repository,
                uiScheduler,
                ioScheduler
        )

        responseBanks = mutableListOf(
                PixBank(988, "33147315", "BCO BRADESCO BERJ S.A.", "Banco Bradesco BERJ S.A."),
                PixBank(479, "60394079", "BCO ITAUBANK S.A.", "Banco ItauBank S.A."),
                PixBank(362, "1027058", "CIELO S.A.", "CIELO S.A.")
        )
    }

    @Test
    fun `success getting the bank list`() {
        val bankListCaptor = argumentCaptor<List<PixBank>>()

        val success = Observable.just(responseBanks)
        doReturn(success).whenever(repository).getAllBanks()

        presenter.getAllBanks()

        verify(view).showLoading()
        verify(view).setupBankListView(bankListCaptor.capture())
        verify(view, never()).showError()
        verify(view).hideLoading()

        val bank = bankListCaptor.firstValue[0]

        assertEquals(988, bank.code)
        assertEquals("33147315", bank.ispb)
        assertEquals("BCO BRADESCO BERJ S.A.", bank.shortName)
        assertEquals("Banco Bradesco BERJ S.A.", bank.name)
        assertTrue(presenter.allBanks.isNotEmpty())
    }

    @Test
    fun `error getting the bank list`() {
        val exception = RetrofitException(
                message = null,
                url = null,
                response = null,
                kind = RetrofitException.Kind.NETWORK,
                exception = null,
                retrofit = null,
                httpStatus = 500)

        val errorObservable = Observable.error<RetrofitException>(exception)
        doReturn(errorObservable).whenever(repository).getAllBanks()

        presenter.getAllBanks()

        verify(view).showLoading()
        verify(view, never()).setupBankListView(any())
        verify(view).showError()
        verify(view).hideLoading()

        assertTrue(presenter.allBanks.isEmpty())
    }

    @Test
    fun `when looking for a bank by its name, it returns three values`() {
        val filteredBanksCaptor = argumentCaptor<MutableList<PixBank>>()

        presenter.allBanks.addAll(responseBanks)

        presenter.searchBank("s.A")

        verify(view).showFilteredBanks(filteredBanksCaptor.capture())

        val bankAt0 = filteredBanksCaptor.firstValue.elementAt(0)
        val bankAt1 = filteredBanksCaptor.firstValue.elementAt(1)
        val bankAt2 = filteredBanksCaptor.firstValue.elementAt(2)

        assertEquals(988, bankAt0.code)
        assertEquals("33147315", bankAt0.ispb)
        assertEquals("BCO BRADESCO BERJ S.A.", bankAt0.shortName)
        assertEquals("Banco Bradesco BERJ S.A.", bankAt0.name)

        assertEquals(479, bankAt1.code)
        assertEquals("60394079", bankAt1.ispb)
        assertEquals("BCO ITAUBANK S.A.", bankAt1.shortName)
        assertEquals("Banco ItauBank S.A.", bankAt1.name)

        assertEquals(362, bankAt2.code)
        assertEquals("1027058", bankAt2.ispb)
        assertEquals("CIELO S.A.", bankAt2.shortName)
        assertEquals("CIELO S.A.", bankAt2.name)

        assertTrue(presenter.filteredBanks.isNotEmpty())
    }

    @Test
    fun `when looking for a bank by its code, it returns at least one value`() {
        val filteredBanksCaptor = argumentCaptor<MutableList<PixBank>>()

        presenter.allBanks.addAll(responseBanks)

        presenter.searchBank("362")

        verify(view).showFilteredBanks(filteredBanksCaptor.capture())

        val bank = filteredBanksCaptor.firstValue.elementAt(0)

        assertEquals(362, bank.code)
        assertEquals("1027058", bank.ispb)
        assertEquals("CIELO S.A.", bank.shortName)
        assertEquals("CIELO S.A.", bank.name)

        assertTrue(presenter.filteredBanks.isNotEmpty())
    }

    @Test
    fun `when looking for a bank by its wrong name, it returns an empty list`() {
        presenter.allBanks.addAll(responseBanks)

        presenter.searchBank("teste")

        verify(view).showFilteredBanks(any())

        assertTrue(presenter.filteredBanks.isEmpty())
    }
}