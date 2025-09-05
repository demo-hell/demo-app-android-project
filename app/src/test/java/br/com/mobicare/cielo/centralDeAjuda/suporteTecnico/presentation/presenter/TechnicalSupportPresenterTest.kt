//package br.com.mobicare.cielo.centralDeAjuda.suporteTecnico.presentation.presenter
//
//import br.com.mobicare.cielo.suporteTecnico.TechnicalSupportContract
//import br.com.mobicare.cielo.suporteTecnico.domain.entities.Problem
//import br.com.mobicare.cielo.suporteTecnico.domain.entities.ProblemSolution
//import br.com.mobicare.cielo.suporteTecnico.domain.entities.Support
//import br.com.mobicare.cielo.suporteTecnico.domain.entities.SupportItem
//import br.com.mobicare.cielo.suporteTecnico.domain.repo.TechnicalSupportRepository
//import br.com.mobicare.cielo.suporteTecnico.ui.presenter.TechnicalSupportPresenter
//import com.nhaarman.mockito_kotlin.doReturn
//import com.nhaarman.mockito_kotlin.mock
//import org.junit.Before
//import org.junit.Test
//import org.mockito.Mockito.verify
//import io.reactivex.Observable
//import io.reactivex.schedulers.Schedulers
//import java.util.*
//
//
//class TechnicalSupportPresenterTest {
//
//    lateinit var presenter: TechnicalSupportPresenter
//    lateinit var repository: TechnicalSupportRepository
//    lateinit var view: TechnicalSupportContract.View
//
//    lateinit var support: Support
//
//    @Before
//    fun setup() {
//
//        val supportItems = listOf(SupportItem("Visor não liga",
//                true, Date(), arrayListOf(Problem("Tela quebrada.",
//                "telaquebrada", arrayListOf(ProblemSolution("Troca de tela",
//                "trocadetela"))))))
//
//        this.support = Support(supportItems)
//
//        this.repository = mock {
//            on { fetchTechnicalSupportRepository() } doReturn Observable.just(support)
//        }
//
//        this.view = mock()
//
//        presenter = TechnicalSupportPresenter(repository, view)
//        presenter.ioScheduler = Schedulers.trampoline()
//        presenter.uiScheduler = Schedulers.trampoline()
//    }
//
//    @Test
//    fun loadItemsSuccessfulyTest() {
//
//        val testSubscriber = TestSubscriber<Support>()
//        repository.fetchTechnicalSupportRepository().subscribe(testSubscriber)
//
//        testSubscriber.awaitTerminalEvent()
//        testSubscriber.assertNoErrors()
//
//        presenter.loadItems()
//
//        verify(view).showLoading()
//        verify(view).hideLoading()
//        verify(view).loadTechnicalSupportItems(support)
//    }
//
//}