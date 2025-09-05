//package br.com.mobicare.cielo.featureToggle.presentation.presenter
//
//import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
//import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleObj
//import br.com.mobicare.cielo.featureToggle.presenter.FeatureTogglePresenter
//import br.com.mobicare.cielo.featureToggle.data.managers.FeatureToggleRepository
//import br.com.mobicare.cielo.login.presentation.ui.LoginContract
//import org.junit.Before
//import org.junit.Test
//import org.mockito.Mockito
//
//class FeatureTogglePresenterUnitTest {
//
//    private lateinit var presenter: FeatureTogglePresenter
//    private lateinit var view: LoginContract.View
//    private lateinit var repository: FeatureToggleRepository
//
//
//    @Before
//    fun setUp() {
//        view = Mockito.mock(LoginContract.View::class.java)
//        repository = Mockito.mock(FeatureToggleRepository::class.java)
//        presenter = FeatureTogglePresenter(view, repository)
//    }
//
//
//    @Test
//    fun onSuccessTest(){
//        var response = FeatureToggleObj()
//        response.features = ArrayList()
//
//        presenter.onSuccess(response)
//    }
//
//    @Test
//    fun onSuccessFeaturesNullTest(){
//        var response = FeatureToggleObj()
//        response.features = null
//
//        presenter.onSuccess(response)
//    }
//
//
//    @Test
//    fun callApiTest(){
//        view.callApiFeaureToggle()
//    }
//
//    @Test
//    fun onErrorTest(){
//        var erroMessage = ErrorMessage()
//        presenter.onError(erroMessage)
//    }
//
//}