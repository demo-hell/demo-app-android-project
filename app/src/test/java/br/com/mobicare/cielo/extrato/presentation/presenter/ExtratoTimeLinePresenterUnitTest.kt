//package br.com.mobicare.cielo.extrato.presentation.presenter
//
//import android.view.View
//import br.com.mobicare.cielo.R
//import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
//import br.com.mobicare.cielo.extrato.data.managers.ExtratoRepository
//import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoTimeLineObj
//import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoTransicaoObj
//import br.com.mobicare.cielo.extrato.presentation.ui.ExtratoTimeLineContract
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Test
//import org.mockito.Mockito
//import org.mockito.Mockito.`when`
//
///**
// * Created by benhur.souza on 05/06/2017.
// */
//class ExtratoTimeLinePresenterUnitTest {
//
//    private lateinit var presenter: ExtratoTimeLinePresenter
//    private lateinit var mMockView: View
//    private lateinit var mView: ExtratoTimeLineContract.View
//    private lateinit var repository: ExtratoRepository
//
//    @Before
//    fun setUp(){
//        mView = Mockito.mock(ExtratoTimeLineContract.View::class.java)
//        mMockView = Mockito.mock(View::class.java)
//        repository = Mockito.mock(ExtratoRepository::class.java)
//        presenter = ExtratoTimeLinePresenter(mView, repository)
//        `when`(mView.isAttached()).thenReturn(true)
//    }
//
//    @Test
//    fun setViewTest(){
//        presenter.mView = mView
//        Assert.assertNotNull(mView)
//        Assert.assertEquals(presenter.mView, mView)
//    }
//
//
//    @Test
//    fun setReposirotyTest(){
//        presenter.repository = repository
//        Assert.assertNotNull(repository)
//        Assert.assertEquals(presenter.repository, repository)
//    }
//
//
//    @Test
//    @Throws(Exception::class)
//    fun startAPITest() {
//        presenter.onStart()
//        Mockito.verify(mView).showProgress()
//    }
//
//    @Test
//    fun finishAPITest(){
//        presenter.onFinish()
//        Mockito.verify(mView).hideProgress()
//    }
//
//    @Test
//    fun errorAPITest(){
//        var errorMessage = ErrorMessage()
//        errorMessage.message = "Error"
//
//        presenter.onError(errorMessage)
//        Mockito.verify(mView).showError(errorMessage)
//    }
//
//    @Test
//    fun successAPTEmptyTest(){
//        presenter.onSuccess(getMock())
//        Mockito.verify(mView).showEmptyMsg(R.string.extrato_empty)
//    }
//
//    @Test
//    fun successAPTTest(){
//        var response = getMock()
//        var item = ExtratoTransicaoObj()
//        response.transactions?.add(item)
//
//        presenter.onSuccess(response)
//        Mockito.verify(mView).loadFooter(response.quantity, response.totalAmount)
//        Mockito.verify(mView).loadTimeLine(response.transactions)
//    }
//
////    @Test
////    fun callAPITest(){
////        var date : String = "21/05/2017"
////        presenter.callAPI(date)
////        Mockito.verify(repository).timeLine(date, presenter)
////    }
//
//
//
//    fun getMock(): ExtratoTimeLineObj {
//        var obj = ExtratoTimeLineObj()
//        obj.quantity = 1
//        obj.totalAmount = "R$ 10.000,00"
//        obj.transactions = ArrayList<ExtratoTransicaoObj>()
//
//        return obj
//    }
//
//
//
//
//
//}