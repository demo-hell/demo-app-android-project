package br.com.mobicare.cielo.home.presentation.feesPerBrand

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.home.presentation.main.presenter.FeeAndPlansHomePresenter
import br.com.mobicare.cielo.home.presentation.main.ui.fragment.FeeAndPlansHomeContract
import br.com.mobicare.cielo.meuCadastroNovo.domain.Brand
import br.com.mobicare.cielo.meuCadastroNovo.domain.Solution
import br.com.mobicare.cielo.home.utils.BrandsRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

private const val TOKEN = "z899zOSJ3hhGF"

class FeeAndPlansHomePresenterTest {
    private val allBrands =
        "[{\"banks\":[{\"name\":\"BANCO DA AMAZONIA S.A.\",\"code\":\"3\",\"agency\":\"6550\",\"accountNumber\":\"1013783\",\"accountDigit\":\"7\",\"imgSource\":\"https://digitalhml.hdevelo.com.br/merchant/solutions/static/assets/img/banks/bank_0003.png\",\"brands\":[{\"code\":7,\"name\":\"ELO\",\"imgSource\":\"https://digitalhml.hdevelo.com.br/merchant/solutions/static/assets/img/brands/brand_7.png\",\"products\":[{\"name\":\"Parcelado Loja\",\"prazoFlexivel\":false,\"conditions\":[{\"settlementTerm\":1,\"mdr\":0.75,\"minimumInstallments\":0,\"maximumInstallments\":0,\"anticipationAllowed\":false,\"minimumMDRAmmount\":0,\"minimumMDR\":false,\"flexibleTerm\":false,\"flexibleTermPayment\":{\"frequency\":[],\"mdr\":0.75},\"flexibleTermPaymentMDR\":0.75}],\"pixType\":\"ADQ\",\"productCode\":15},{\"name\":\"Parcelado Cliente\",\"prazoFlexivel\":false,\"conditions\":[{\"settlementTerm\":10,\"mdr\":2,\"minimumInstallments\":0,\"maximumInstallments\":0,\"anticipationAllowed\":true,\"minimumMDRAmmount\":0,\"minimumMDR\":false,\"flexibleTerm\":false,\"flexibleTermPayment\":{\"frequency\":[],\"mdr\":2},\"flexibleTermPaymentMDR\":2}],\"pixType\":\"ADQ\",\"productCode\":70}]}],\"savingsAccount\":false,\"digitalAccount\":false}]}]"

    @Mock
    lateinit var view: FeeAndPlansHomeContract.View

    @Mock
    lateinit var repository: BrandsRepository

    @Mock
    lateinit var userPreferences: UserPreferences

    @Mock
    lateinit var featureTogglePreference: FeatureTogglePreference

    private lateinit var presenter: FeeAndPlansHomePresenter
    private val ioScheduler = Schedulers.trampoline()
    private val uiScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = FeeAndPlansHomePresenter(
            view,
            uiScheduler,
            ioScheduler,
            repository,
            userPreferences,
            featureTogglePreference
        )
    }

    @Test
    fun `generic error getting all brands`() {
        val exception = RetrofitException(
            message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500
        )

        val errorObservable = Observable.error<RetrofitException>(exception)
        val isAnErrorCaptor = argumentCaptor<Boolean>()
        val brandsCaptor = argumentCaptor<ArrayList<Brand>>()

        doReturn(TOKEN).whenever(userPreferences).token
        doReturn(errorObservable).whenever(repository).loadAllBrands(any())
        doReturn(true).whenever(featureTogglePreference).
            getFeatureTogle(FeatureTogglePreference.FEES_PER_FLAG_HOME)

        presenter.getBrands()

        verify(view).showFeePerBrand(brandsCaptor.capture(), isAnErrorCaptor.capture())
        verify(view, never()).hideFeesAndPlans()

        assertTrue(isAnErrorCaptor.firstValue)
        assertTrue(brandsCaptor.firstValue.isEmpty())
    }


    @Test
    fun `success getting all brands`() {
        val listSolutionsType = object : TypeToken<List<Solution>>() {}.type
        val response: List<Solution> =
            Gson().fromJson(allBrands, listSolutionsType)
        val returnSuccess = Observable.just(response)

        val isAnErrorCaptor = argumentCaptor<Boolean>()
        val brandsCaptor = argumentCaptor<ArrayList<Brand>>()

        doReturn(TOKEN).whenever(userPreferences).token
        doReturn(returnSuccess).whenever(repository).loadAllBrands(any())
        doReturn(true).whenever(featureTogglePreference).
            getFeatureTogle(FeatureTogglePreference.FEES_PER_FLAG_HOME)

        presenter.getBrands()

        verify(view).showFeePerBrand(brandsCaptor.capture(), isAnErrorCaptor.capture())
        verify(view, never()).hideFeesAndPlans()

        assertFalse(isAnErrorCaptor.firstValue)
        assertFalse(brandsCaptor.firstValue.isEmpty())
        assertTrue(brandsCaptor.allValues.size in 0..3)
    }

    @Test
    fun `hide fees and plans container when FT is disabled`() {
        doReturn(false).whenever(featureTogglePreference).
            getFeatureTogle(FeatureTogglePreference.FEES_PER_FLAG_HOME)

        presenter.getBrands()

        verify(view, never()).showFeePerBrand(any(), any())
        verify(view).hideFeesAndPlans()
    }

    @Test
    fun `hide fees and plans container when brand list is empty`() {
        val solutionResponse = listOf<Solution>()
        val returnSuccess = Observable.just(solutionResponse)

        doReturn(TOKEN).whenever(userPreferences).token
        doReturn(returnSuccess).whenever(repository).loadAllBrands(any())
        doReturn(true).whenever(featureTogglePreference).
        getFeatureTogle(FeatureTogglePreference.FEES_PER_FLAG_HOME)

        presenter.getBrands()

        verify(view, never()).showFeePerBrand(any(), any())
        verify(view).hideFeesAndPlans()
    }
}