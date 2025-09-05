//package br.com.mobicare.cielo.meuCadastro.presentation.presenter
//
//import android.content.Context
//import android.view.View
//import br.com.mobicare.cielo.R
//import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
//import br.com.mobicare.cielo.commons.utils.ReaderJson
//import br.com.mobicare.cielo.meuCadastro.data.clients.managers.MeuCadastroRepository
//import br.com.mobicare.cielo.meuCadastro.domains.entities.*
//import br.com.mobicare.cielo.meuCadastro.presetantion.presenter.MeuCadastroPresenter
//import br.com.mobicare.cielo.meuCadastro.presetantion.ui.MeuCadastroContract
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.Mockito
//import org.mockito.Mockito.*
//import org.mockito.runners.MockitoJUnitRunner
//
///**
// * Created by benhur.souza on 02/05/2017.
// */
//
//@RunWith(MockitoJUnitRunner::class)
//class MeuCadastroPresenterUnitTest {
//
//    private lateinit var mView: MeuCadastroContract.View
//    private lateinit var repository: MeuCadastroRepository
//    private lateinit var mockContext: Context
//    private lateinit var mMockView: View
//    private lateinit var presenter: MeuCadastroPresenter
//
//    @Before
//    fun setUp() {
//        mockContext = Mockito.mock(Context::class.java)
//        mView = Mockito.mock(MeuCadastroContract.View::class.java)
//        mMockView = Mockito.mock(View::class.java)
//        repository = Mockito.mock(MeuCadastroRepository::class.java)
//        presenter = MeuCadastroPresenter(mView, repository, false)
//        Mockito.`when`(mView.isAttached()).thenReturn(true)
//    }
//
//    @Test
//    fun callAPITest() {
//        Mockito.verify(mView).hideContent()
//        presenter.callAPI()
//        verify(repository).getMeuCadastro(presenter)
//    }
//
//    @Test
//    fun onStartTest() {
//        presenter.onStart()
//        //Verifica se o progress é exibido
//        Mockito.verify(mView).showProgress()
//    }
//
//    @Test
//    fun onSuccessTest() {
//        val obj = meuCadastroMock
//        presenter.onSuccess(obj)
//
//        Assert.assertNotNull(obj.getEndereco(MeuCadastroEndereco.Tipo.CONTATO.toString()))
//        Assert.assertNotNull(obj.getEndereco(MeuCadastroEndereco.Tipo.FISICO.toString()))
//
//        Mockito.verify(mView).showContent()
//
//        Mockito.verify(mView).loadDadosEstabelecimento(obj)
////        Mockito.verify(mView).loadBandeirasHabilitadas(obj.enabledBrands!!)
//        Mockito.verify(mView).loadSolucoesContratadas(obj.hiredSolutions!!)
//        Mockito.verify(mView).loadUniqueDomicilioBancario(obj.bankDatas!![0])
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickEnderecoFisicoOKTest() {
//        val obj = meuCadastroMock
//        presenter.onSuccess(obj)
//
//        val end = obj.getEndereco(MeuCadastroEndereco.Tipo.FISICO)
//        Assert.assertNotNull(end)
//        end?.status = "OK"
//
//        presenter.onClickEnderecoFisico()
//        Mockito.verify(mView, times(3)).isAttached()
//        Mockito.verify(mView, times(1)).selectedEnderecoFisico()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun onClickEnderecoFisicoErrorTest() {
//        val obj = meuCadastroMock
//        presenter.onSuccess(obj)
//
//        val end = obj.getEndereco(MeuCadastroEndereco.Tipo.FISICO)
//        Assert.assertNotNull(end)
//        end?.status = "Error"
//
//        Assert.assertEquals(end?.getUrl(), "error")
//
//        presenter.onClickEnderecoFisico()
//        Mockito.verify(mView, times(3)).isAttached()
//        Mockito.verify(mView, times(1)).selectedEnderecoFisico()
//    }
//
//    @Test
//    fun onClickEnderecoFisicoNullTest() {
//        val obj = meuCadastroMock
//        obj.userAddresses = null
//        presenter.onSuccess(obj)
//
//        presenter.onClickEnderecoFisico()
//        Mockito.verify(mView, Mockito.times(3)).isAttached()
//        Mockito.verify(mView).hideEnderecos()
//    }
//
//    @Test
//    fun onClickEnderecoFisicoNotAttachedTest() {
//        Mockito.`when`(mView.isAttached()).thenReturn(false)
//
//        presenter.onClickEnderecoFisico()
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView, Mockito.never()).selectedEnderecoFisico()
//    }
//
//    @Test
//    fun onClickEnderecoContatoTest() {
//        val obj = meuCadastroMock
//        presenter.onSuccess(obj)
//
//        val end = obj.getEndereco(MeuCadastroEndereco.Tipo.CONTATO)
//        Assert.assertNotNull(end)
//        end?.status = "OK"
//
//        presenter.onClickEnderecoContato()
//        Mockito.verify(mView, Mockito.times(3)).isAttached()
//        Mockito.verify(mView).selectedEnderecoContato()
//    }
//
//    @Test
//    fun onClickEnderecoContatoNotAttachedTest() {
//        Mockito.`when`(mView.isAttached()).thenReturn(false)
//
//        presenter.onClickEnderecoContato()
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView, Mockito.never()).selectedEnderecoContato()
//    }
//
//    @Test
//    fun onClickEnderecoContatoErrorTest() {
//        val obj = meuCadastroMock
//        presenter.onSuccess(obj)
//
//        val end = obj.getEndereco(MeuCadastroEndereco.Tipo.CONTATO)
//        Assert.assertNotNull(end)
//        end?.status = "Error"
//
//        Assert.assertEquals(end?.getUrl(), "error")
//
//        presenter.onClickEnderecoContato()
//        Mockito.verify(mView, Mockito.times(3)).isAttached()
//        Mockito.verify(mView).selectedEnderecoContato()
//    }
//
//    @Test
//    fun onClickEnderecoContatoNullTest() {
//        val obj = meuCadastroMock
//        obj.userAddresses = null
//        presenter.onSuccess(obj)
//
//        presenter.onClickEnderecoContato()
//        Mockito.verify(mView, Mockito.times(3)).isAttached()
//        Mockito.verify(mView).hideEnderecos()
//    }
//
////    @Test
////    fun returnUrlMapTest() {
////        Assert.assertNotNull(presenter.returnUrlMaps("Address"))
////        Assert.assertNull(presenter.returnUrlMaps(""))
////        Assert.assertNull(presenter.returnUrlMaps(null))
////    }
//
//    @Test
//    fun managerEnderecosTest() {
//        val endereco = addressMock
//        endereco.status = "OK"
//
//        presenter.managerEnderecos(endereco)
//        Mockito.verify(mView).loadMaps(endereco.getUrl())
//        Mockito.verify(mView).loadEndederecos(endereco)
//    }
//
//    @Test
//    fun managerEnderecosNullTest() {
//        val endereco = null
//
//        presenter.managerEnderecos(endereco)
//        Mockito.verify(mView).hideEnderecos()
//    }
//
//    @Test
//    fun managerVisibilidadeEnderecoTest() {
//        val address = addressMock
//        address.status = "OK"
//        presenter.managerEnderecos(address)
//        Mockito.verify(mView).isAttached()
//        Mockito.verify(mView).selectedEnderecoFisico()
//
//        address.type = MeuCadastroEndereco.Tipo.CONTATO
//        presenter.managerEnderecos(address)
//        Mockito.verify(mView).selectedEnderecoFisico()
//    }
//
//    @Test
//    fun managerSolucoesContratadasTest() {
//        presenter.managerSolucoesContratadas(null as Array<MeuCadastroSolucoesContratadas>?)
//        Mockito.verify(mView)!!.hideSolucoesContratadas()
//
//        val solucoes = solucoesContratadasMock
//        presenter.managerSolucoesContratadas(solucoes)
//        Mockito.verify(mView)!!.loadSolucoesContratadas(solucoes)
//    }
//
//    @Test
//    fun managerBandeirasHabilitadasConvivenciaTest() {
//        presenter.isConvivenciaUser = true
//        presenter.managerBandeirasHabilitadas(CardBrandFees())
//        Mockito.verify(mView)!!.hideBandeirasHabilitadas()
//
//        val bandeiras = bandeirasHabilitadasMock
//
//        presenter.managerBandeirasHabilitadas(bandeiras)
//        Mockito.verify(mView).loadBandeirasHabilitadas(bandeiras)
//    }
//
//    @Test
//    fun managerBandeirasHabilitadasEmptySECTest() {
//        presenter.isConvivenciaUser = false
//        presenter.managerBandeirasHabilitadas(CardBrandFees())
//        Mockito.verify(mView).hideBandeirasHabilitadas()
//    }
//
//    @Test
//    fun managerBandeirasHabilitadasEmptyConvivenciaTest() {
//        presenter.isConvivenciaUser = true
//        presenter.managerBandeirasHabilitadas(CardBrandFees())
//        Mockito.verify(mView).hideBandeirasHabilitadas()
//    }
//
//
//    @Test
//    fun managerDomicilioBancarioTest() {
//        presenter.managerDomicilioBancario(null as Array<MeuCadastroDomicilioBancario>?)
//        Mockito.verify(mView).hideDomicilioBancario()
//
//        val uniqueBank = arrayOf(domiciliosBancarioMock)
//
//        presenter.managerDomicilioBancario(uniqueBank)
//        Mockito.verify(mView).loadUniqueDomicilioBancario(uniqueBank[0])
//
//
//        val bancoList = arrayOf(domiciliosBancarioMock, domiciliosBancarioMock)
//
//        presenter.managerDomicilioBancario(bancoList)
//        Mockito.verify(mView).loadDomicilioBancario(bancoList)
//    }
//
//    @Test
//    fun onFinishTest() {
//        presenter.onFinish()
//
//        //Verifica se o progress é exibido
//        Mockito.verify(mView).hideProgress()
//    }
//
//    @Test
//    fun onErrorTest() {
//        var error = ErrorMessage()
//        error.logout = false
//        presenter.onError(error)
//        Mockito.verify(mView).hideProgress()
//        Mockito.verify(mView).showError(error)
//        Mockito.verify(mView, never()).logout(error.message)
//    }
//
//    @Test
//    fun onErrorNotAttachedTest() {
//        Mockito.`when`(mView.isAttached()).thenReturn(false)
//        var error = ErrorMessage()
//        presenter.onError(error)
//
//        Mockito.verify(mView, never()).hideProgress()
//        Mockito.verify(mView, never()).showError(error)
//    }
//
//
//    @Test
//    fun onLogoutTest() {
//        var error = ErrorMessage()
//        error.logout = true
//        presenter.onError(error)
//        Mockito.verify(mView).hideProgress()
//        Mockito.verify(mView).logout(error.message)
//        Mockito.verify(mView, never()).showError(error)
//    }
//
//    @Test
//    fun onLoadPhysicalAddressOKTest() {
//        val obj = meuCadastroMock
//        presenter.onSuccess(obj)
//
//        presenter.onLoadPhysicalAddress("OK")
//        val end = obj.getEndereco(MeuCadastroEndereco.Tipo.FISICO)
//        Assert.assertTrue(end?.showAddress()!!)
//    }
//
//    @Test
//    fun onLoadPhysicalAddressNullTest() {
//        val obj = meuCadastroMock
//        presenter.onSuccess(obj)
//
//        presenter.onLoadPhysicalAddress(null)
//        Mockito.verify(mView, Mockito.times(2)).isAttached()
//        Mockito.verify(mView).hideEnderecoFisico()
//    }
//
//    @Test
//    fun onLoadContactAddressOKTest() {
//        val obj = meuCadastroMock
//        presenter.onSuccess(obj)
//
//        presenter.onLoadContactAddress("OK")
//        val end = obj.getEndereco(MeuCadastroEndereco.Tipo.CONTATO)
//        Assert.assertTrue(end?.showAddress()!!)
//    }
//
////    @Test
////    fun onLoadContactAddressNullTest() {
////        val obj = meuCadastroMock
////        val contato = null
////        contato.type =  MeuCadastroEndereco.Tipo.CONTATO
////
////        obj.userAddresses = arrayOf(physical)
////        presenter.onEndSucessClick(obj)
////
////        presenter.onLoadContactAddress("OK")
////
//////        val end = obj.getEndereco(MeuCadastroEndereco.Tipo.CONTATO)
//////        Assert.assertTrue(end?.showAddress()!!)
////
////        val endP = obj.getEndereco(MeuCadastroEndereco.Tipo.FISICO)
////        Assert.assertTrue(endP?.showAddress()!!)
////
//////        Mockito.verify(mView).loadEndederecos(endP)
////        Mockito.verify(mView).selectedEnderecoFisico()
////    }
//
//
//    @Test
//    fun onLoadContactAddressPhysicalNullTest() {
//        val obj = meuCadastroMock
//        val contato = addressMock
//        contato.type =  MeuCadastroEndereco.Tipo.CONTATO
//        obj.userAddresses = arrayOf(contato)
//        presenter.onSuccess(obj)
//
//        presenter.onLoadContactAddress("OK")
//        val end = obj.getEndereco(MeuCadastroEndereco.Tipo.CONTATO)
//        Assert.assertTrue(end?.showAddress()!!)
//        Assert.assertEquals(end?.status, "OK")
//
//        Mockito.verify(mView).loadMaps(end.getUrl())
//        Mockito.verify(mView).loadEndederecos(end)
//        Mockito.verify(mView).selectedEnderecoContato()
//    }
//
//    @Test
//    fun onLoadContactAddressStatusNullTest() {
//        val obj = meuCadastroMock
//        presenter.onSuccess(obj)
//
//        presenter.onLoadPhysicalAddress(null)
//        Mockito.verify(mView, Mockito.times(2)).isAttached()
//        Mockito.verify(mView).hideEnderecoFisico()
//
//    }
//
//    @Test
//    fun isConvivenciaUserTest() {
//        presenter = MeuCadastroPresenter(mView, repository, true)
//        Assert.assertTrue(presenter.isConvivenciaUser)
//
//        presenter = MeuCadastroPresenter(mView, repository, false)
//        Assert.assertFalse(presenter.isConvivenciaUser)
//    }
//
//
//    /********************  MOCKS   *******************/
//
//    private val solucoesContratadasMock: Array<MeuCadastroSolucoesContratadas>
//        get() {
//            val solucoesContratadas = MeuCadastroSolucoesContratadas()
//            solucoesContratadas.quantity = 1
//            solucoesContratadas.description = "description"
//            solucoesContratadas.name = "name"
//
//            return arrayOf(solucoesContratadas)
//        }
//
//    private val bandeirasHabilitadasMock: CardBrandFees
//        get() {
//            var bandeira = CardBrandFees()
//            bandeira.cardBrands = mutableListOf()
//            bandeira.cardBrands[0].products = mutableListOf()
//            bandeira.cardBrands[0].products[0].fee = "3,90 "
//            bandeira.cardBrands[0].products[0].name = "Master"
//            bandeira.cardBrands[0].products[0].installmentsText = "0"
//            bandeira.cardBrands[0].products[0].paymentInstallments = "none"
//
//            return bandeira
//        }
//
//
//    private val domiciliosBancarioMock: MeuCadastroDomicilioBancario
//        get() {
//            val banco = MeuCadastroDomicilioBancario()
//            banco.name = "name"
//            banco.code = "code"
//
//            return banco
//        }
//
//    private val addressMock: MeuCadastroEndereco
//        get() {
//            val fisico = MeuCadastroEndereco()
//            fisico.type = MeuCadastroEndereco.Tipo.FISICO
//            fisico.street = "street"
//            fisico.city = "city"
//            fisico.complement = "complement"
//            fisico.postalCode = "123"
//            fisico.state = "state"
//            fisico.status = "OK"
//
//            return fisico
//        }
//
//    private //Endereço
//    val meuCadastroMock: MeuCadastroObj
//        get() {
//            val obj = MeuCadastroObj()
//            obj.name = "name"
//            val fisico = addressMock
//            val contato = addressMock
//            contato.type = MeuCadastroEndereco.Tipo.CONTATO
//
//            obj.userAddresses = arrayOf(fisico, contato)
//
//            obj.bankDatas = arrayOf(domiciliosBancarioMock)
//            obj.hiredSolutions = solucoesContratadasMock
//
//            return obj
//        }
//
//
//}
