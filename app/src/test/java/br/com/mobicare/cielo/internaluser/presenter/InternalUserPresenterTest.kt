package br.com.mobicare.cielo.internaluser.presenter

import br.com.mobicare.cielo.changeEc.ChangeEcRepository
import br.com.mobicare.cielo.changeEc.domain.CnpjHierarchy
import br.com.mobicare.cielo.changeEc.domain.HierachyResponse
import br.com.mobicare.cielo.changeEc.domain.Hierarchy
import br.com.mobicare.cielo.changeEc.domain.Pagination
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.internaluser.InternalUserView
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

const val ACCESS_TOKEN_MOCK = "123456"
const val PAGE = 1
const val PAGE_SIZE = 25

class InternalUserPresenterTest {

    private val searchCriteria = "eh noise"

    private lateinit var presenter: InternalUserPresenterImpl

    @Mock
    private lateinit var view: InternalUserView

    @Mock
    private lateinit var repository: ChangeEcRepository

    @Mock
    private lateinit var userPreferences: UserPreferences

    private lateinit var response: HierachyResponse

    private val hierarchy = Hierarchy(clienteIndividual = false,
            formaRecebimento = "Individual",
            id = "1033466970",
            matrizPagamentoEnabled = false, nivelHierarquia = "PONTO_VENDA",
            noHierarquia = "121627", nome = "RAZAO SOCIAL TESTE TESTE",
            nomeFantasia = "Cielo eh noises",
            nomeHierarquia = "Individual",
            tipoPessoa = "JURIDICA",
            cnpj = CnpjHierarchy(raiz = "01027058", completo = "01.027.058/0320-42"))

    private val pagination = Pagination(pageNumber = 1, pageSize = 25, numPages = 2, firstPage = true, lastPage = false, totalElements = 38)

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        response = HierachyResponse(arrayOf(hierarchy), pagination)
        doReturn(ACCESS_TOKEN_MOCK).whenever(userPreferences).token
        presenter = InternalUserPresenterImpl(view, userPreferences, repository)
    }

    @Test
    fun `validate_showLoading_on_first_search`() {
        presenter.searchChild(1, this.searchCriteria)
        verify(view).showLoading()
    }

    @Test
    fun `success when get searchChild`() {

        doAnswer {
            (it.arguments[4] as APICallbackDefault<HierachyResponse, String>).onSuccess(response)
        }.whenever(repository)
                .children(eq(ACCESS_TOKEN_MOCK),
                        eq(PAGE_SIZE),
                        eq(PAGE),
                        eq(searchCriteria),
                        any())

        presenter.searchChild(PAGE, searchCriteria)

        verify(view).showLoading()
        verify(view).showChildren(any())
        verify(repository).children(eq(ACCESS_TOKEN_MOCK),
                eq(PAGE_SIZE),
                eq(PAGE),
                eq(searchCriteria),
                any())
    }

    @Test
    fun `error when get searchChild`() {

        val exception = RetrofitException(message = null,
                url = null,
                response = null,
                kind = RetrofitException.Kind.NETWORK,
                exception = null,
                retrofit = null,
                httpStatus = 500)

        val errorMessage = APIUtils.convertToErro(exception)

        doAnswer {
            (it.arguments[4] as APICallbackDefault<HierachyResponse, String>).onError(errorMessage)
        }.whenever(repository)
                .children(eq(ACCESS_TOKEN_MOCK),
                        eq(PAGE_SIZE),
                        eq(PAGE),
                        eq(searchCriteria),
                        any())

        presenter.searchChild(PAGE, searchCriteria)

        verify(view).showLoading()
        verify(view).showError(any())
    }
}