//package br.com.mobicare.cielo.home.presentation.antecipacao.presenter
//
//import br.com.mobicare.cielo.antecipeSuasVendas.data.clients.managers.AntecipeRepository
//import br.com.mobicare.cielo.antecipeSuasVendas.domains.entities.Antecipacao
//import br.com.mobicare.cielo.antecipeSuasVendas.domains.entities.AntecipacaoAvulsaObj
//import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
//import br.com.mobicare.cielo.commons.utils.Utils
//import br.com.mobicare.cielo.home.presentation.antecipacao.AntecipacaoHomeContract
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.Mockito
//import org.mockito.runners.MockitoJUnitRunner
//
///**
// * Created by Benhur on 04/08/17.
// */
//
//@RunWith(MockitoJUnitRunner::class)
//class AntecipacaoHomePresenterUnitTest{
//    lateinit var mView : AntecipacaoHomeContract.View
//    lateinit var mRepository: AntecipeRepository
//    lateinit var mPresenter: AntecipacaoHomePresenter
//
//    @Before
//    fun setUp() {
//        mView = Mockito.mock(AntecipacaoHomeContract.View::class.java)
//        mRepository = Mockito.mock(AntecipeRepository::class.java)
//        mPresenter = AntecipacaoHomePresenter(mView, mRepository)
//        Mockito.`when`(mView.isAttached()).thenReturn(true)
//    }
//
//    @Test
//    fun onStartTest(){
//        mPresenter.onStart()
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).showLoading()
//    }
//
//    @Test
//    fun onFinishTest(){
//        mPresenter.onFinish()
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).hideLoading()
//    }
//
//    @Test
//    fun initProgramadaElegivelTet(){
//        mPresenter.init(Antecipacao.NOT_ELIGIBLE, Antecipacao.ELIGIBLE)
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).showContent()
//        Mockito.verify(mView).loadCardProgramada()
//    }
//
//    @Test
//    fun initProgramadaContratadaTet(){
//        mPresenter.init(Antecipacao.NOT_ELIGIBLE, Antecipacao.HIRED)
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).hideContent()
//    }
//
//    @Test
//    fun initProgramadaNaoElegivelTet(){
//        mPresenter.init(Antecipacao.NOT_ELIGIBLE, Antecipacao.NOT_ELIGIBLE)
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).hideContent()
//    }
//
//    @Test
//    fun initAvulsaHorarioTet(){
//        mPresenter.init(Antecipacao.HOUR_BLOCK, Antecipacao.ELIGIBLE)
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).showContent()
//        Mockito.verify(mView).loadCardProgramada()
//    }
//
//    @Test
//    fun initHorarioProgramadaContratadaTet(){
//        mPresenter.init(Antecipacao.HOUR_BLOCK, Antecipacao.HIRED)
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).hideContent()
//    }
//
//    @Test
//    fun initHorarioProgramadaNaoElegivelTet(){
//        mPresenter.init(Antecipacao.HOUR_BLOCK, Antecipacao.NOT_ELIGIBLE)
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).hideContent()
//    }
//
////    @Test
////    fun initAvulsaElegivelTet(){
////        mPresenter.init(Antecipacao.ELIGIBLE, Antecipacao.ELIGIBLE)
//////        Mockito.verify(mRepository).getTaxasAvulsas(mPresenter)
////        Mockito.verify(mView).isAttached()
////        Mockito.verify(mView).showLoading()
////    }
//
//    @Test
//    fun onSuccessTest(){
//        var response = AntecipacaoAvulsaObj()
//        response.anticipationAmount = 12.0
//
//        mPresenter.onSuccess(response)
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).showContent()
//        Mockito.verify(mView).loadCardAvulsa(Utils.formatValue(response.anticipationAmount))
//    }
//
//    @Test
//    fun onSuccessNullTest(){
//        mPresenter.onSuccess(null)
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).hideContent()
//    }
//
//    @Test
//    fun onErrorTest(){
//        var erroMessage = ErrorMessage()
//        mPresenter.onError(erroMessage)
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).showError(error = erroMessage)
//    }
//
//    @Test
//    fun logoutTest(){
//        var erroMessage = ErrorMessage()
//        erroMessage.logout = true
//        mPresenter.onError(erroMessage)
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).logout(erroMessage)
//    }
//
//    @Test
//    fun hourBlockProgramadaTest(){
//        mPresenter.init(Antecipacao.ELIGIBLE, Antecipacao.ELIGIBLE)
//        mPresenter.hourBlock("")
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).loadCardProgramada()
//    }
//
//
//    @Test
//    fun hourBlockTest(){
//        mPresenter.init(Antecipacao.ELIGIBLE, Antecipacao.HIRED)
//        mPresenter.hourBlock("")
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).hideContent()
//    }
//
//    @Test
//    fun hasHiredTest(){
//        mPresenter.init(Antecipacao.ELIGIBLE, Antecipacao.HIRED)
//        mPresenter.hasHired("")
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).hideContent()
//    }
//
//    @Test
//    fun hasHiredProgramadaTest(){
//        mPresenter.init(Antecipacao.ELIGIBLE, Antecipacao.ELIGIBLE)
//        mPresenter.hasHired("")
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).loadCardProgramada()
//    }
//
//    @Test
//    fun noEligibleProgramadaTest(){
//        mPresenter.init(Antecipacao.ELIGIBLE, Antecipacao.ELIGIBLE)
//        mPresenter.noEligible()
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).loadCardProgramada()
//    }
//
//    @Test
//    fun noEligibleTest(){
//        mPresenter.init(Antecipacao.ELIGIBLE, Antecipacao.HIRED)
//        mPresenter.noEligible()
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).hideContent()
//    }
//
//
//
//}