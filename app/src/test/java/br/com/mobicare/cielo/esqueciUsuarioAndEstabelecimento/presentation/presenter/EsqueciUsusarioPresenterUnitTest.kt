//package br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.presentation.presenter
//
//import android.content.Context
//import br.com.mobicare.cielo.R
//import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
//import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.data.clients.managers.EsqueciUsuarioAndEstabelecimentoRepository
//import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.EsqueciEstabelecimentoObj
//import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.EsqueciUsuarioObj
//import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.UserNameObj
//import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.presentation.ui.EsqueciUsuarioAndEstabelecimentoContract
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Test
//import org.mockito.ArgumentMatchers
//import org.mockito.Mockito
//
///**
// * Created by Benhur on 12/05/17.
// */
//class EsqueciUsusarioPresenterUnitTest {
//
//    private lateinit var repository: EsqueciUsuarioAndEstabelecimentoRepository
//    private lateinit var mView: EsqueciUsuarioAndEstabelecimentoContract.View
//    private val CNPJ = "95789581000107"
//    private val CPF = "78887332207"
//    private val EC = "123456"
//    private lateinit var mockContext: Context
//
//    private lateinit var presenter: EsqueciUsuarioAndEstabelecimentoPresenter
//
//    @Before
//    fun setUp() {
//        mockContext = Mockito.mock(Context::class.java)
//        mView = Mockito.mock(EsqueciUsuarioAndEstabelecimentoContract.View::class.java)
//        repository = Mockito.mock(EsqueciUsuarioAndEstabelecimentoRepository::class.java)
//        presenter = EsqueciUsuarioAndEstabelecimentoPresenter(mockContext, mView, repository, false, false)
//        Mockito.`when`(mView.isVisible()).thenReturn(true)
//    }
//
//    @Test
//    fun getShowCPFTest(){
//        presenter.showCPF = false
//        Assert.assertFalse(presenter.showCPF)
//
//        presenter.showCPF = true
//        Assert.assertTrue(presenter.showCPF)
//    }
//
//    @Test
//    fun getIsEstableshimentTest(){
//        presenter.isEstablishment = false
//        Assert.assertFalse(presenter.isEstablishment)
//
//        presenter.isEstablishment = true
//        Assert.assertTrue(presenter.isEstablishment)
//    }
//
//
//    @Test
//    fun getECParamsTest(){
//        presenter.ecParam = null
//        Assert.assertNull(presenter.ecParam)
//
//        presenter.ecParam = EC
//        Assert.assertEquals(presenter.ecParam, EC)
//    }
//
//    @Test
//    fun getListTest(){
//        Assert.assertNull(presenter.list)
//
//        presenter.list = getList()
//        Assert.assertNotNull(presenter.list)
//        Assert.assertArrayEquals(presenter.list, getList())
//    }
//
//    @Test
//    fun checkUsuarioScreenTest() {
//        presenter.checkLabels()
//        Mockito.verify(mView).changeLabels(R.string.ajuda_esqueci_usuario)
//        Mockito.verify(mView).managerField(R.string.esqueci_usuario_ec_hint)
//    }
//
//    @Test
//    fun callRecoveryUserTest() {
//        presenter.callAPI(EC)
//        Mockito.verify(mView, Mockito.times(4)).isVisible()
//    }
//
//    @Test
//    fun callRecoveryUserErrorTest() {
//        presenter.callAPI("")
//        Mockito.verify(mView, Mockito.times(2)).isVisible()
//        Mockito.verify(mView).showLocalError(R.string.dialog_error_ec)
//    }
//
//    @Test
//    fun callRecoveryECCNPJTest() {
//        presenter = EsqueciUsuarioAndEstabelecimentoPresenter(mockContext, mView, repository, true, false)
//        presenter.callAPI(CNPJ)
//        Mockito.verify(mView, Mockito.times(4)).isVisible()
//    }
//
//    @Test
//    fun callRecoveryECCNPJErrorTest() {
//        presenter = EsqueciUsuarioAndEstabelecimentoPresenter(mockContext, mView, repository, true, false)
//        presenter.callAPI("123465")
//        Mockito.verify(mView, Mockito.times(2)).isVisible()
//        Mockito.verify(mView).showLocalError(R.string.dialog_error_cnpj)
//    }
//
//    @Test
//    fun callRecoveryECCPFTest() {
//        presenter = EsqueciUsuarioAndEstabelecimentoPresenter(mockContext, mView, repository, true, true)
//        presenter.callAPI(CPF)
//        Mockito.verify(mView, Mockito.times(4)).isVisible()
//    }
//
//    @Test
//    fun callRecoveryECCPFErrorTest() {
//        presenter = EsqueciUsuarioAndEstabelecimentoPresenter(mockContext, mView, repository, true, true)
//        presenter.callAPI("123465")
//        Mockito.verify(mView, Mockito.times(2)).isVisible()
//        Mockito.verify(mView).showLocalError(R.string.dialog_error_cpf)
//    }
//
//
//    @Test
//    fun checkECScreenPFTest() {
//        presenter = EsqueciUsuarioAndEstabelecimentoPresenter(mockContext, mView, repository, true, true)
//        presenter.checkLabels()
//        Mockito.verify(mView).changeLabels(R.string.ajuda_esqueci_ec)
//        Mockito.verify(mView).managerField(R.string.esqueci_ec_cpf_hint)
//        Mockito.verify(mView).addMask(R.string.esqueci_cpf_mask)
//    }
//
//    @Test
//    fun checkECScreenPJTest() {
//        presenter = EsqueciUsuarioAndEstabelecimentoPresenter(mockContext, mView, repository, true, false)
//        presenter.checkLabels()
//        Mockito.verify(mView).changeLabels(R.string.ajuda_esqueci_ec)
//        Mockito.verify(mView).managerField(R.string.esqueci_ec_cnpj_hint)
//        Mockito.verify(mView).addMask(R.string.esqueci_cnpj_mask)
//    }
//
//
//    @Test
//    fun checkCNPJTest() {
//        Assert.assertFalse(presenter.validCNPJ(""))
//        Assert.assertFalse(presenter.validCNPJ("123456789"))
//        Assert.assertTrue(presenter.validCNPJ(CNPJ))
//
//        presenter.isEstablishment = true
//        presenter.showCPF = false
//        Assert.assertTrue(presenter.isValid(CNPJ))
//
//        presenter.isEstablishment = true
//        presenter.showCPF = true
//        Assert.assertFalse(presenter.isValid(CNPJ))
//        Mockito.verify(mView).showLocalError(R.string.dialog_error_cpf)
//
//        presenter.isEstablishment = false
//        Assert.assertTrue(presenter.isValid(CNPJ))
//
//        Mockito.verify(mView, Mockito.times(2)).showLocalError(R.string.dialog_error_cnpj)
//    }
//
//    @Test
//    fun checkCPFTest() {
//        Assert.assertFalse(presenter.validCPF(""))
//        Assert.assertFalse(presenter.validCPF("123456789"))
//        Assert.assertTrue(presenter.validCPF(CPF))
//
//        presenter.isEstablishment = true
//        presenter.showCPF = true
//        Assert.assertTrue(presenter.isValid(CPF))
//
//        presenter.isEstablishment = true
//        presenter.showCPF = false
//        Assert.assertFalse(presenter.isValid(CPF))
//        Mockito.verify(mView).showLocalError(R.string.dialog_error_cnpj)
//
//        presenter.isEstablishment = false
//        Assert.assertTrue(presenter.isValid(CPF))
//
//
//        Mockito.verify(mView, Mockito.times(2)).showLocalError(R.string.dialog_error_cpf)
//    }
//
//
//    @Test
//    fun checkECTest() {
//        Assert.assertFalse(presenter.validEC(""))
//        Assert.assertTrue(presenter.validEC("123456789"))
//
//        presenter.isEstablishment = true
//        presenter.showCPF = true
//        Assert.assertFalse(presenter.isValid("123456789"))
//        Mockito.verify(mView).showLocalError(R.string.dialog_error_cpf)
//
//        presenter.isEstablishment = true
//        presenter.showCPF = false
//        Assert.assertFalse(presenter.isValid("123456789"))
//        Mockito.verify(mView).showLocalError(R.string.dialog_error_cnpj)
//
//        presenter.isEstablishment = false
//        Assert.assertTrue(presenter.isValid("123456789"))
//
//        Mockito.verify(mView).showLocalError(R.string.dialog_error_ec)
//    }
//
//    @Test
//    fun chooseItemEstablishmentTest(){
//        presenter.list = getList()
//        presenter.isEstablishment = true
//        presenter.chooseItem(0)
//
//        Mockito.verify(mView).isVisible()
//        Mockito.verify(mView).saveEC(presenter.list?.get(0))
//        Mockito.verify(mView).changeActivity(false)
//    }
//
//    @Test
//    fun chooseItemUserTest(){
//        presenter.ecParam = "123456"
//        presenter.list = getList()
//        presenter.isEstablishment = false
//        presenter.chooseItem(0, "123456")
//
//        Mockito.verify(mView, Mockito.times(3)).isVisible()
//        Mockito.verify(mView).showProgress()
//
////        Mockito.verify(repository).sendEmail(ArgumentMatchers.eq(presenter.pageElements?.get(0)), presenter.ecParam, Mockito.any())
//    }
//
//    @Test
//    fun onErrorTest(){
//        var error = "Error"
//        presenter.onError(ErrorMessage().apply { message = error } )
//        Mockito.verify(mView).isVisible()
//        Mockito.verify(mView).showMessageError(error)
//    }
//
//    @Test
//    fun onSuccessECEmptyTest(){
//        var establishment = EsqueciEstabelecimentoObj()
//        presenter.onSuccess(establishment)
//        Mockito.verify(mView).isVisible()
//        Mockito.verify(mView).showMessageError(msgId = R.string.dialog_esqueci_ec_error)
//    }
//
//    @Test
//    fun onSuccessECTest(){
//        var establishment = EsqueciEstabelecimentoObj()
//        establishment.ecs = getList()
//
//        presenter.onSuccess(establishment)
//        Mockito.verify(mView).isVisible()
////        Mockito.verify(mView).onEndSucessClick(ArgumentMatchers.eq(Mockito.anyString()),R.string.dialog_esqueci_ec_title, getPageElements(), R.string.dialog_esqueci_usar_numero)
//    }
//
//    @Test
//    fun onSuccessUserTest(){
//        var user = EsqueciUsuarioObj()
//        user.emailResponse = "email"
//
//        presenter.onSuccess(user)
//        Mockito.verify(mView).isVisible()
////        Mockito.verify(mView).showMessage(user.emailResponse, Mockito.any())
//    }
//
//    @Test
//    fun setListTest(){
//        val obj = UserNameObj()
//        presenter.listUsers  = arrayOf(obj)
//        Assert.assertNotNull(presenter.listUsers)
//    }
//
//    fun getList() : Array<String>{
//        return arrayOf("000000000","111111111","333333333")
//    }
//}