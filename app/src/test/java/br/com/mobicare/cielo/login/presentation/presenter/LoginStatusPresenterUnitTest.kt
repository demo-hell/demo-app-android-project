//package br.com.mobicare.cielo.login.presentation.presenter
//
//import br.com.mobicare.cielo.R
//import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
//import br.com.mobicare.cielo.login.data.managers.LoginRepository
//import br.com.mobicare.cielo.login.domains.entities.LoginObj
//import br.com.mobicare.cielo.login.presentation.ui.LoginContract
//import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
//import org.junit.After
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Test
//import org.mockito.Mockito
//
//
//class LoginPresenterUnitTest {
//
//    private lateinit var presenter: LoginPresenter
//    private lateinit var mView: LoginContract.View
//    private lateinit var repository: LoginRepository
//
//    private val ecNumberTitle: String = "N sei o n do ec"
//
//    @After
//    fun validate() {
//        Mockito.validateMockitoUsage()
//    }
//
//    @Before
//    fun initMock() {
//        mView = Mockito.mock(LoginContract.View::class.java)
//        repository = Mockito.mock(LoginRepository::class.java)
//        presenter = LoginPresenter(mView, repository)
//        Mockito.`when`(mView.isAttached()).thenReturn(true)
//    }
//
//
//    @Test
//    fun checkECFieldErrorTest() {
//        presenter.validateLogin("", "", "")
//        Mockito.verify(mView).showAlert(messageId = R.string.login_ec_error)
//    }
//
//    @Test
//    fun checkUserNameFieldErrorTest() {
//        presenter.validateLogin("1234567890", "", "")
//        Mockito.verify(mView).showAlert(messageId = R.string.error_login_user)
//    }
//
//    @Test
//    fun checkPasswordFieldErrorTest() {
//        presenter.validateLogin("1234567890", "username", "")
//        Mockito.verify(mView).showAlert(messageId = R.string.error_login_password)
//    }
//
//    @Test
//    fun checkPasswordFieldSuccessTest() {
//        presenter.validateLogin("1234567890", "username", "123456ABC")
//        Mockito.verify(mView, Mockito.times(4)).isAttached()
//        Mockito.verify(mView).showProgress()
//    }
//
//    @Test
//    fun onClickECHelp() {
//        presenter.showDuvidaEc(ecNumberTitle)
////        Mockito.verify(mView).showRecuperarEC(R.string.login_title_duvida_ec, R.string.login_message_duvida_ec)
//    }
//
////    @Test
////    fun onClickUserHelp() {
////        presenter.showDuvidaUser()
////        Mockito.verify(mView).sendEventGA(R.string.ga_login, R.string.ga_login_duvida_user)
////        Mockito.verify(mView).showAlert(R.string.login_title_duvida_user, R.string.login_message_duvida_user)
////    }
//
//    /*@Test
//    fun onClickKeepLogin() {
//        presenter.storeLoginData(true)
////        Mockito.verify(mView).enableKeepLogin()
//
//        presenter.storeLoginData(false)
////        Mockito.verify(mView).disableKeepLogin()
//    }*/
//
//
//    @Test
//    fun onClickSejaClienteTest() {
//        presenter.onClickSejaCliente()
//        Mockito.verify(mView).openBrowser(R.string.login_seja_cliente, ConfigurationDef.URL_LOGIN_QUERO_SER_CLIENTE)
//    }
//
//
//    @Test
//    fun onClickCriarUsuarioTest() {
//        presenter.onClickCriarUsuario()
//        Mockito.verify(mView).openBrowser(R.string.login_criar_usuario, ConfigurationDef.URL_LOGIN_CRIAR_USUARIO)
//    }
//
//    @Test
//    fun onStartTest() {
//        presenter.onStart()
//        Mockito.verify(mView).showProgress()
//    }
//
//    @Test
//    fun onFinish() {
//        presenter.onFinish()
//        Mockito.verify(mView).hideProgress()
//        Mockito.verify(mView).updateMenu()
//    }
//
//    @Test
//    fun onErrorTest() {
//        val error = ErrorMessage().apply { message = "Error" }
//        presenter.onError(error)
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).hideProgress()
//        Mockito.verify(mView).showAlert(errorMessage = error)
//    }
//
////    @Test
////    fun callAPITest(){
////        presenter.callAPI("123456","user","123456")
////        Mockito.verify(mView).showProgress()
//////        Mockito.verify(repository).login("123456","user","123456", presenter)
////    }
//
//    @Test
//    fun onSuccess(){
//        presenter.onSuccess(LoginObj())
//        presenter.storeLoginData("2000463023",  "leosorg1", true)
//        Mockito.verify(mView).saveData(true)
//        Mockito.verify(mView).updateMenu()
//        Mockito.verify(mView, Mockito.times(2)).isAttached()
//    }
//
//
//    @Test
//    fun ecIsValidTest() {
//        Assert.assertFalse(presenter.ecIsValid(""))
//        Assert.assertTrue(presenter.ecIsValid("123"))
//    }
//
//    /*@Test
//    fun keepLoginCheckedTest() {
//        presenter.loadSavedFields()
//
//        Mockito.verify(mView).changeVisibilityCheckPreferences(true)
//    }*/
//
//    /*@Test
//    fun keepLoginUncheckedTest() {
//        presenter.loadSavedFields()
//
//        Mockito.verify(mView).changeVisibilityCheckPreferences(false)
//    }
//*/
//    @Test
//    fun showDuvidaUserTest(){
//        presenter.showDuvidaUser(ecNumberTitle)
//        Mockito.verify(mView)
//                .showRecuperarEC(R.string.login_title_duvida_user,
//                        R.string.login_message_duvida_user, true, ecNumberTitle)
//    }
//}