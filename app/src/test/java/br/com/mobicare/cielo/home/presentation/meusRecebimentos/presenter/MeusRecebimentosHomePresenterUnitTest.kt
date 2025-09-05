//package br.com.mobicare.cielo.home.presentation.meusRecebimentos.presenter
//
//import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
//import br.com.mobicare.cielo.home.presentation.meusrecebimentonew.MeusRecebimentosHomeContract
//import br.com.mobicare.cielo.meusRecebimentos.domains.entities.MeusRecebimentosObj
//import br.com.mobicare.cielo.meusRecebimentos.managers.MeusRecebimentosRepository
//import org.junit.Before
//import org.junit.Test
//import org.mockito.Mockito
//
///**
// * Created by benhur.souza on 02/08/2017.
// */
//class MeusRecebimentosHomePresenterUnitTest {
//
//    private lateinit var mView: MeusRecebimentosHomeContract.View
//    private lateinit var mRepository: MeusRecebimentosRepository
//    private lateinit var presenter: MeusRecebimentosHomePresenter
//
//    @Before
//    fun setUp() {
//        mView = Mockito.mock(MeusRecebimentosHomeContract.View::class.java)
//        mRepository = Mockito.mock(MeusRecebimentosRepository::class.java)
//        presenter = MeusRecebimentosHomePresenter(mView, mRepository)
//        Mockito.`when`(mView.isAttached()).thenReturn(true)
//    }
//
////    @Test
////    fun startTest(){
////        presenter.onStart()
////        Mockito.verify(mView).isAttached()
////        Mockito.verify(mView).hideContent()
////        Mockito.verify(mView).hideError()
////        Mockito.verify(mView).showLoading()
////    }
//
//    @Test
//    fun errorTest(){
//        var error = ErrorMessage()
//        presenter.onError(error)
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).showError(error)
//        Mockito.verify(mView).hideLoading()
//    }
//
//    @Test
//    fun logoutTest(){
//        var error = ErrorMessage()
//        error.logout = true
//        presenter.onError(error)
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).logout(error)
//        Mockito.verify(mView).hideLoading()
//    }
//
//    @Test
//    fun successTest(){
//        var response = MeusRecebimentosObj()
//        response.incomings = ArrayList()
//
//        presenter.onSuccess(response)
//        Mockito.verify(mView).isAttached()
//        mView.loadCard(response.incomings)
//        mView.showContent()
//    }
//
//    @Test
//    fun finishTest(){
//        presenter.onFinish()
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).hideLoading()
//    }
//
//    @Test
//    fun callAPITest(){
//        presenter.callAPI()
//        Mockito.verify(mView).isAttached()
////        Mockito.verify(mRepository).meusRecebimentos(presenter)
//    }
//
//}