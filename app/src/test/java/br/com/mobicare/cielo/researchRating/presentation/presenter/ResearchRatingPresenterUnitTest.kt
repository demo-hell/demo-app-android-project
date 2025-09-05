//package br.com.mobicare.cielo.researchRating.presentation.presenter
//
//import android.content.Context
//import android.test.mock.MockContext
//import br.com.mobicare.cielo.BuildConfig
//import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
//import br.com.mobicare.cielo.research.ResearchContract
//import br.com.mobicare.cielo.research.ResearchPresenter
//import br.com.mobicare.cielo.research.domains.entities.ResearchRating
//import br.com.mobicare.cielo.research.domains.entities.ResearchResponse
//import com.nhaarman.mockito_kotlin.mock
//import org.junit.Before
//import org.junit.Test
//
//class ResearchRatingPresenterUnitTest {
//
//    lateinit var presenter: ResearchPresenter
//    lateinit var view: ResearchContract.ResearchView
//    lateinit var context: Context
//
//    @Before
//    fun setup(){
//        this.view = mock()
//        this.context = MockContext()
//
//        presenter = ResearchPresenter(CieloAPIServices.getInstance(context, BuildConfig.SERVER_URL), view, "2002009583", "d2002009583")
//
//        presenter.ioScheduler = Schedulers.trampoline()
//        presenter.uiScheduler = Schedulers.trampoline()
//    }
//
//    @Test
//    fun saveResearchSuccess(){
//        presenter.saveResearch(ResearchRating(0, ""))
//    }
//
//    @Test
//    fun saveResearchWhenNull(){
//        presenter.saveResearch(null)
//    }
//
//
//    @Test
//    fun getResearch(){
//        val testSubscriber = TestSubscriber<ResearchResponse>()
//        presenter.createResearchInstance("D2002009583", "2002009583").subscribe(testSubscriber)
//
//        presenter.getResearch()
//    }
//
//}