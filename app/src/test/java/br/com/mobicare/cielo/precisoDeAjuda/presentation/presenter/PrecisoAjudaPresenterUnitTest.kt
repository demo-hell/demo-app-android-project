//package br.com.mobicare.cielo.precisoDeAjuda.presentation.presenter
//
//import br.com.mobicare.cielo.R
//import br.com.mobicare.cielo.precisoDeAjuda.presentation.PrecisoAjudaContract
//import org.junit.Before
//import org.junit.Test
//import org.mockito.ArgumentMatchers
//import org.mockito.Mockito
//import org.mockito.Mockito.`when`
//import org.mockito.Mockito.verify
//
//class PrecisoAjudaPresenterUnitTest {
//
//    private lateinit var mView: PrecisoAjudaContract.View
//    private lateinit var presenter: PrecisoAjudaPresenter
//
//    private val forgetStablishmentTitle = "Esqueci o estabelecimento"
//
//    @Before
//    @Throws(Exception::class)
//    fun setUp() {
//        mView = Mockito.mock(PrecisoAjudaContract.View::class.java)
//        presenter = PrecisoAjudaPresenter(mView, false)
//        `when`(mView.isAttached()).thenReturn(true)
//    }
//
//
//    @Test
//    @Throws(Exception::class)
//    fun showAlertTest(){
//        presenter = PrecisoAjudaPresenter(mView, true)
//        verify(mView).showAlert(forgetStablishmentTitle)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickEsqueciEstabelecimentoTest() {
//        presenter.onClickEsqueciEstabelecimento("Esqueci o estabelecimento")
//        verify(mView).isAttached()
//        verify(mView).showAlert("Esqueci o estabelecimento")
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickEsqueciUsuarioTest() {
//        presenter.onClickEsqueciUsuario()
//        verify(mView).isAttached()
//        verify(mView)
//                .showForgetUserAndStablishment(ArgumentMatchers.eq(false), forgetStablishmentTitle)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickEsqueciSenhaTest() {
//        presenter.onClickEsqueciSenha()
//        verify(mView).isAttached()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickProsseguirCPFTest() {
//        presenter.onClickProsseguir(R.id.radio_button_ajuda_alert_pessoa_fisica, forgetStablishmentTitle)
//        verify(mView).isAttached()
//        verify(mView).showForgetUserAndStablishment(ArgumentMatchers.eq(true), forgetStablishmentTitle)
//    }
//
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickProsseguirCNPJTest() {
//        presenter.onClickProsseguir(R.id.radio_button, forgetStablishmentTitle)
//        verify(mView).isAttached()
//        verify(mView).showForgetUserAndStablishment(ArgumentMatchers.eq(true), forgetStablishmentTitle)
//    }
//}