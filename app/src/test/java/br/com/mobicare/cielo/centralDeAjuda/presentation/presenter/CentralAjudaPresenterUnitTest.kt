package br.com.mobicare.cielo.centralDeAjuda.presentation.presenter


import br.com.mobicare.cielo.centralDeAjuda.data.clients.managers.CentralDeAjudaRepository
import br.com.mobicare.cielo.centralDeAjuda.domains.entities.CentralAjudaObj
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.CentralAjudaContract
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class CentralAjudaPresenterUnitTest {

    @Mock
    lateinit var view: CentralAjudaContract.View

    @Mock
    lateinit var repository: CentralDeAjudaRepository

    private lateinit var presenter: CentralAjudaContract.Presenter

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    private val helpResponse = "{\"id\":\"63bc5a95212ded48d28c1fff\",\"pwd\":{\"title\":\"Esqueciasenha\",\"description\":\"Recuperesuasenha\",\"value\":\"appcielo://pwd\",\"showLoggedArea\":false},\"technicalSupport\":{\"title\":\"SuporteTécnico\",\"description\":\"Acionequandoprecisardesuportetécnicoparaasuamáquina,TEFouCieloMobile.Todososdias,24horaspordia.\",\"value\":\"appcielo://suportecnico\",\"showLoggedArea\":true},\"faq\":{\"title\":\"DúvidasGerais\",\"description\":\"Confiraasrespostasparaassuasprincipaisdúvidas\",\"value\":\"https://cielo.com.br/atendimento\",\"showLoggedArea\":true},\"technicalQuestions\":{\"title\":\"DúvidasTécnicas\",\"description\":\"Conheçaassoluçõesparaassuasprincipaisdúvidas.\",\"value\":\"https://www.cielo.com.br/suportetecnico\",\"showLoggedArea\":true},\"onlineConsultant\":{\"title\":\"Chat\",\"description\":\"Faleagoracomumconsultorviachatdiretonoseucelular.\",\"value\":\"https://apa3.xgen.com.br/cielo1/ivr_app.html\",\"showLoggedArea\":true},\"phonesSupport\":[{\"title\":\"Centralderelacionamento\",\"description\":\"FalecomagenteparasolicitaralgumprodutoouserviçodaCielo:\",\"storeHours\":\"Desegundaasábado,das8hàs22h.\",\"items\":[{\"description\":\"Capitaiseregiõesmetropolitanas\",\"value\":\"40025472\"},{\"description\":\"Demaislocalidades\",\"value\":\"08005708472\"}]},{\"title\":\"Suportetécnico\",\"description\":\"ACieloofereceomelhorsuportetécnicoaseusclientes:\",\"storeHours\":\"Desegundaasábado,das8hàs22h.\",\"items\":[{\"description\":\"Capitaiseregiõesmetropolitanas\",\"value\":\"40029111\"},{\"description\":\"Demaislocalidades\",\"value\":\"08005700111\"}]},{\"title\":\"ProgramaCieloFidelidade\",\"description\":\"InformaçõessobreoprogramaCieloFidelidade:\",\"storeHours\":\"Desegundaasábado,das8hàs22h.\",\"items\":[{\"description\":\"Capitaiseregiõesmetropolitanas\",\"value\":\"40025472\"},{\"description\":\"Demaislocalidades\",\"value\":\"08005708472\"}]},{\"title\":\"Ouvidoria\",\"description\":\"Antesdeacionaraouvidoria,tenhasempreoprotocolodoseuatendimentoemmãos.\",\"storeHours\":\"Desegundaasexta,das8h30às17h30.\",\"items\":[{\"description\":\"Todasaslocalidades\\nDesegundaasexta,das8h30às17h30.\",\"value\":\"08005702288\"}]}]}"

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = spy(CentralAjudaPresenter(
                        view,
                        repository,
                        uiScheduler,
                        ioScheduler
                )
        )
    }

    @Test
    fun `success help info request`() {
        val success = Gson().fromJson(
                helpResponse,
                CentralAjudaObj::class.java
        )
        val successObservable = Observable.just(success)
        doReturn(successObservable).whenever(repository).registrationData()

        presenter.callAPI()

        verify(view).showProgress()
        verify(view).hideProgress()
        verify(view).showContent(success)
        verify(view, never()).showError(any())
    }

    @Test
    fun `error help info request`() {
        val exception = RetrofitException(
                message = null,
                response = null,
                kind = RetrofitException.Kind.NETWORK,
                exception = null,
                retrofit = null,
                httpStatus = 500
        )
        val errorObservable = Observable.error<RetrofitException>(exception)
                doReturn(errorObservable).whenever(repository).registrationData()

        presenter.callAPI()

        verify(view).showProgress()
        verify(view).hideProgress()
        verify(view).showError(any())
        verify(view, never()).showContent(any())
    }

}