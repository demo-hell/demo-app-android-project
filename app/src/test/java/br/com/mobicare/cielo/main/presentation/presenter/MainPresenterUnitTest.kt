//package br.com.mobicare.cielo.main.presentation.presenter
//
//import android.content.Context
//import android.view.View
//import br.com.mobicare.cielo.R
//import br.com.mobicare.cielo.antecipeSuasVendas.domains.entities.Antecipacao
//import br.com.mobicare.cielo.antecipeSuasVendas.presentation.ui.fragments.AntecipeFragment
//import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.fragments.CentralAjudaFragment
//import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
//import br.com.mobicare.cielo.extrato.presentation.ui.fragments.ExtratoFragment
//import br.com.mobicare.cielo.login.domains.entities.EstabelecimentoObj
//import br.com.mobicare.cielo.login.domains.entities.LoginObj
//import br.com.mobicare.cielo.main.data.managers.MenuRepository
//import br.com.mobicare.cielo.main.presentation.ui.MainContract
//import br.com.mobicare.cielo.meuCadastro.presetantion.ui.fragments.MeuCadastroFragment
//import br.com.mobicare.cielo.meusRecebimentos.presentation.ui.fragments.MeusRecebimentosFragment
//import org.junit.After
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Test
//import org.mockito.ArgumentMatchers
//import org.mockito.Mockito
//import org.mockito.Mockito.*
//
///**
// * Created by benhur.souza on 09/05/2017.
// */
//
//class MainPresenterUnitTest {
//
//    private lateinit var mView: MainContract.View
//    private lateinit var mMenuDeslogado: MainContract.View.MenuDeslogado
//    private lateinit var mMenuLogado: MainContract.View.MenuLogado
//    private lateinit var mMockContext: Context
//    private lateinit var mMockView: View
//    private lateinit var repository: MenuRepository
//    private lateinit var presenter: MainPresenter
//
//    @After
//    fun validate() {
//        validateMockitoUsage()
//    }
//
//    @Before
//    @Throws(Exception::class)
//    fun setUp() {
//        mView = Mockito.mock(MainContract.View::class.java)
//        mMenuDeslogado = Mockito.mock(MainContract.View.MenuDeslogado::class.java)
//        mMenuLogado = Mockito.mock(MainContract.View.MenuLogado::class.java)
//        mMockContext = Mockito.mock(Context::class.java)
//        mMockView = Mockito.mock(View::class.java)
//        repository = Mockito.mock(MenuRepository::class.java)
//
//        presenter = MainPresenter(mView, mMenuDeslogado, mMenuLogado, repository)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun checkMenuDeslogado() {
//        presenter.checkUseState(false, false, false, false, false, null)
//        verify(mView).showMenuDeslogado()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun checkMenuLogado() {
//        presenter.checkUseState(true, false, false, false, false, null, true)
//        verify(mView).showMenuLogado()
//    }
//
//    @Test
//    fun checkMenuPromoHide() {
//        presenter.checkUseState(true, false, false, false, false, null,true)
//        verify(mMenuLogado).hidePromocoes()
//    }
//
//    @Test
//    fun checkMenuPromoShow() {
//        presenter.checkUseState(true, false, true, false, false, null,true)
//        verify(mMenuLogado).showPromocoes()
//    }
//
//    @Test
//    fun checkMenuFidelidadeHide() {
//        presenter.checkUseState(true, false, false, false, false, null,true)
//        verify(mMenuLogado).hideFidelidade()
//    }
//
//    @Test
//    fun checkMenuFidelidadeShow() {
//        presenter.checkUseState(true, false, false, true, false, null,true)
//        verify(mMenuLogado).showFidelidade()
//    }
//
//    @Test
//    fun checkMenuAntecipacaoHide() {
//        presenter.checkUseState(true, false, false, false, false, null,true)
//        verify(mMenuLogado).hideAntecipacao()
//    }
//
//    @Test
//    fun checkMenuAntecipacaoShow() {
//        presenter.checkUseState(true, false, false, false, true, null,true)
//        verify(mMenuLogado).showAntecipacao()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun checkFluxoDeslogadoMassivaTest() {
//        presenter.checkUseState(false, true, false, false, false, null,true)
////        verify(mView).changeFragment(ArgumentMatchers.isA(LoginMassivaFragment::class.java), eq(-1))
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun checkFluxoDeslogadoLoginTest() {
//        presenter.checkUseState(false, false, false, false, false, null,true)
////        verify(mView).changeFragment(ArgumentMatchers.isA(LoginFragment::class.java), eq(-1))
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickInicioMassivaTest() {
//        presenter.checkUseState(false, true, false, false, false, null,true)
//        `when`(mMockView.id).thenReturn(R.id.textview_menu_inicio_deslogado)
//        presenter.onClickItemMenu(mMockView)
//
//        verify(mMenuDeslogado, times(2)).disableAllItem()
//        verify(mMenuDeslogado, times(2)).selectInicio()
////        verify(mView, times(2)).changeFragment(ArgumentMatchers.isA(LoginMassivaFragment::class.java), eq(-1))
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickInicioLoginTest() {
//        presenter.checkUseState(false, false, false, false, false, null)
//        `when`(mMockView.id).thenReturn(R.id.textview_menu_inicio_deslogado)
//        presenter.onClickItemMenu(mMockView)
//
//        verify(mMenuDeslogado, times(2)).disableAllItem()
//        verify(mMenuDeslogado, times(2)).selectInicio()
//
////        verify(mView, times(2)).changeFragment(ArgumentMatchers.isA(LoginFragment::class.java), eq(-1))
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickInicioLogadoTest() {
//        presenter.checkUseState(true, false, false, false, false, null,true)
//        `when`(mMockView.id).thenReturn(R.id.textview_menu_inicio)
//        presenter.onClickItemMenu(mMockView)
//
//        verify(mMenuLogado, Mockito.times(2)).disableAllItem()
//        verify(mMenuLogado, Mockito.times(2)).selectInicio()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickAjudaDeslogadoTest() {
//        presenter.checkUseState(false, false, false, false, false, null,true)
//        `when`(mMockView.id).thenReturn(R.id.textview_menu_ajuda_deslogado)
//        presenter.onClickItemMenu(mMockView)
//
//        verify(mMenuDeslogado, times(2)).disableAllItem()
//        verify(mMenuDeslogado).selectAjuda()
//
//        verify(mView).changeFragment(ArgumentMatchers.isA(CentralAjudaFragment::class.java), eq(R.string.menu_central_ajuda))
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickAjudaLogadoTest() {
//        presenter.checkUseState(true, false, false, false, false, null,true)
//        `when`(mMockView.id).thenReturn(R.id.textview_menu_ajuda)
//        presenter.onClickItemMenu(mMockView)
//
//        verify(mMenuLogado, times(2)).disableAllItem()
//        verify(mMenuLogado).selectedAjuda()
//
//        verify(mView).changeFragment(ArgumentMatchers.isA(CentralAjudaFragment::class.java), eq(R.string.menu_central_ajuda))
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickMeuCadastroLogadoTest() {
//        presenter.checkUseState(true, false, false, false, false, null, true)
//        `when`(mMockView.id).thenReturn(R.id.textview_menu_cadastro)
//        presenter.onClickItemMenu(mMockView)
//
//        verify(mMenuLogado, times(2)).disableAllItem()
//        verify(mMenuLogado).selectMeuCadastro()
//
//        verify(mView).changeFragment(ArgumentMatchers.isA(MeuCadastroFragment::class.java), eq(R.string.menu_meu_cadastro))
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickNotificacoesDeslogadoTest() {
//        presenter.checkUseState(false, false, false, false, false, null,true)
//        `when`(mMockView.id).thenReturn(R.id.textview_menu_notificacoes_deslogado)
//        presenter.onClickItemMenu(mMockView)
//
//
//
//        //TODO testar quando a Notificacoes estiver pronta
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickNotificacoesLogadoTest() {
//        presenter.checkUseState(true, false, false, false, false, null,true)
//        `when`(mMockView.id).thenReturn(R.id.textview_menu_notificacoes)
//        presenter.onClickItemMenu(mMockView)
//
//        //TODO testar quando a Notificacoes estiver pronta
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickOpniaoDeslogadoTest() {
//        presenter.checkUseState(false, false, false, false, false, null,true)
//        `when`(mMockView.id).thenReturn(R.id.layout_menu_cielo_opniao_deslogado)
//        presenter.onClickItemMenu(mMockView)
//        verify(mView).openEmail()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickOpniaoLogadoTest() {
//        presenter.checkUseState(true, false, false, false, false, null,true)
//        `when`(mMockView.id).thenReturn(R.id.layout_menu_opniao)
//        presenter.onClickItemMenu(mMockView)
//        verify(mView).openEmail()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickSobreDeslogadoTest() {
//        presenter.checkUseState(false, false, false, false, false, null,true)
//        `when`(mMockView.id).thenReturn(R.id.textview_menu_sobre_deslogado)
//        presenter.onClickItemMenu(mMockView)
//
//
//        //TODO testar quando a Notificacoes estiver pronta
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickSobreLogadoTest() {
//        presenter.checkUseState(true, false, false, false, false, null,true)
//        `when`(mMockView.id).thenReturn(R.id.textview_menu_sobre)
//        presenter.onClickItemMenu(mMockView)
//
//
//        //TODO testar quando a Notificacoes estiver pronta
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickCieloMobileDeslogadoTest() {
//        presenter.checkUseState(false, false, false, false, false, null,true)
//        `when`(mMockView.id).thenReturn(R.id.layout_menu_cielo_mobile_deslogado)
//        presenter.onClickItemMenu(mMockView)
//
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickCieloMobileLogadoTest() {
//        presenter.checkUseState(true, false, false, false, false, null,true)
//        `when`(mMockView.id).thenReturn(R.id.layout_menu_cielo_mobile)
//        presenter.onClickItemMenu(mMockView)
//
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickMinhasVendasTest() {
//        presenter.checkUseState(true, false, false, false, false, null,true)
//        `when`(mMockView.id).thenReturn(R.id.textview_minhas_vendas)
//        presenter.onClickItemMenu(mMockView)
//
//        verify(mMenuLogado, times(2)).disableAllItem()
//        verify(mMenuLogado).selectMinhasVendas()
//        verify(mView).changeFragment(ArgumentMatchers.isA(ExtratoFragment::class.java), eq(R.string.menu_minhas_vendas))
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickMeusRecebimentosTest() {
//        presenter.checkUseState(true, false, false, false, false, null,true)
//        `when`(mMockView.id).thenReturn(R.id.textview_menu_recebimento)
//        presenter.onClickItemMenu(mMockView)
//
//        verify(mMenuLogado, times(2)).disableAllItem()
//        verify(mMenuLogado).selectMeusRecebimentos()
//        verify(mView).changeFragment(ArgumentMatchers.isA(MeusRecebimentosFragment::class.java), eq(R.string.menu_meus_recebimentos))
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickProdutosTest() {
//        presenter.checkUseState(true, false, false, false, false, null,true)
//        `when`(mMockView.id).thenReturn(R.id.textview_menu_produtos)
//        presenter.onClickItemMenu(mMockView)
//
//        //TODO testar quando PRODUTOS estiver pronta
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickAntecipeTest() {
//        presenter.checkUseState(true, false, false, false, false, null,true)
//        `when`(mMockView.id).thenReturn(R.id.textview_menu_antecipe)
//        presenter.onClickItemMenu(mMockView)
//
//        verify(mView).changeFragment(ArgumentMatchers.isA(AntecipeFragment::class.java), eq(R.string.menu_meus_antecipe))
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickFidelidadeTest() {
//        presenter.checkUseState(true, false, false, false, false, null,true)
//        `when`(mMockView.id).thenReturn(R.id.textview_menu_fidelidade)
//        presenter.onClickItemMenu(mMockView)
//
//
//        //TODO testar quando FIDELIDADE estiver pronta
//    }
//
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickSairTest() {
//        `when`(mMockView.id).thenReturn(R.id.textview_menu_sair)
//        presenter.onClickItemMenu(mMockView)
//
//        verify(mView).logout()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun showListECTest(){
//        presenter.listECIsVisible = false
//        presenter.onClickTrocaEstabelecimento()
//
//        verify(mMenuLogado).showListaEC()
//
//        Assert.assertTrue(presenter.listECIsVisible)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun hideListaECTest(){
//        presenter.listECIsVisible = true
//        presenter.onClickTrocaEstabelecimento()
//
//        verify(mMenuLogado).hideListaEC()
//
//        Assert.assertFalse(presenter.listECIsVisible)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun managerTrocaECListNullTest(){
//        presenter.estableshimentList = null
//        presenter.managerTrocaEC()
//        verify(mMenuLogado).hideTrocarEstabelecimento()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun managerTrocaECListEmptyTest(){
//        presenter.estableshimentList = ArrayList()
//        presenter.managerTrocaEC()
//        verify(mMenuLogado).hideTrocarEstabelecimento()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun managerTrocaECTest(){
//        presenter.estableshimentList = ArrayList()
//        presenter.estableshimentList?.add(EstabelecimentoObj("123345","abc","abc"))
//        presenter.managerTrocaEC()
//        verify(mMenuLogado).showTrocarEstabelecimento()
//        verify(mMenuLogado).hideListaEC()
//        verify(mMenuLogado).initListEC()
//
//        Assert.assertFalse(presenter.listECIsVisible)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun setListTest(){
//        var list : ArrayList<EstabelecimentoObj> = ArrayList()
//        list.add(EstabelecimentoObj("123345","abc","abc"))
//
//        Assert.assertNull(presenter.estableshimentList)
//
//        presenter.estableshimentList = list
//        Assert.assertEquals(presenter.estableshimentList, list)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onSuccessTest(){
//        var obj = LoginObj()
//        obj.scheduledAnticipationStatus = Antecipacao.ELIGIBLE
//        obj.looseAntecipationStatus = Antecipacao.ELIGIBLE
//        obj.hasLoyalty = true
//        obj.hasOffer = true
//
//        presenter.onSuccess(obj)
//
//        verify(mView).saveNewEC(obj)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onStartTest(){
//        presenter.onStart()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onFinishTest(){
//        presenter.onFinish()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onErrorTest(){
//        presenter.onError(ErrorMessage().apply { message = "Error" })
//    }
//
//}