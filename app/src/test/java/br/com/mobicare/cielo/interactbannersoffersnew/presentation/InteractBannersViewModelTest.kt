package br.com.mobicare.cielo.interactbannersoffersnew.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.interactBannersOffersNew.domain.useCase.DeleteLocalInteractBannersOffersUseCase
import br.com.mobicare.cielo.interactBannersOffersNew.domain.useCase.GetLocalInteractBannersOffersUseCase
import br.com.mobicare.cielo.interactBannersOffersNew.domain.useCase.GetRemoteInteractBannersOffersUseCase
import br.com.mobicare.cielo.interactBannersOffersNew.domain.useCase.SaveLocalInteractBannersOffersUseCase
import br.com.mobicare.cielo.interactBannersOffersNew.presentation.InteractBannersViewModel
import br.com.mobicare.cielo.interactBannersOffersNew.utils.BannerControl
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersUiState
import br.com.mobicare.cielo.interactbannersoffersnew.utils.InteractBannerNewFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class InteractBannersViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val getLocalInteractBannersOffersUseCase = mockk<GetLocalInteractBannersOffersUseCase>()
    private val getRemoteInteractBannersOffersUseCase =
        mockk<GetRemoteInteractBannersOffersUseCase>()
    private val saveLocalInteractBannersOffersUseCase =
        mockk<SaveLocalInteractBannersOffersUseCase>()
    private val deleteLocalInteractBannersOffersUseCase =
        mockk<DeleteLocalInteractBannersOffersUseCase>()
    private val getFeatureTogglePreferenceUseCase = mockk<GetFeatureTogglePreferenceUseCase>()

    private lateinit var viewModel: InteractBannersViewModel

    private val completeResponse = InteractBannerNewFactory.getCompleteResponse()
    private val leaderboardHomeCompleteResponse = completeResponse.take(4)
    private val rectangleHomeCompleteResponse =
        mutableListOf(completeResponse[4], completeResponse[5])
    private val leaderboardReceivablesCompleteResponse = mutableListOf(completeResponse[0])
    private val leaderboardServicesCompleteResponse = mutableListOf(completeResponse[1])
    private val leaderboardOthersCompleteResponse = mutableListOf(completeResponse[2])
    private val leaderboardFeesAndPlansCompleteResponse = mutableListOf(completeResponse[3])

    private val dataResultSuccess = CieloDataResult.Success(completeResponse)

    @Before
    fun setup() {
        viewModel = InteractBannersViewModel(
            getRemoteInteractBannersOffersUseCase,
            getLocalInteractBannersOffersUseCase,
            saveLocalInteractBannersOffersUseCase,
            deleteLocalInteractBannersOffersUseCase,
            getFeatureTogglePreferenceUseCase
        )

        coEvery { getFeatureTogglePreferenceUseCase(key = FeatureTogglePreference.INTERACT_BANNERS) } returns
                CieloDataResult.Success(true)
        coEvery { saveLocalInteractBannersOffersUseCase(completeResponse) } returns
                CieloDataResult.Success(true)

    }

    @Test
    fun `getHiringOffers should update InteractBannersUiState from API with OnShowBannerByControl and return max 4 data when BannerControl is LeaderboardHome`() =
        runTest {
            coEvery { getRemoteInteractBannersOffersUseCase() } returns dataResultSuccess
            viewModel.getHiringOffers(true, BannerControl.LeaderboardHome)
            dispatcherRule.advanceUntilIdle()
            viewModel.interactBannersStateMutableLiveData.value.let {
                assert(it is InteractBannersUiState.OnShowBannerByControl && leaderboardHomeCompleteResponse == it.offers)
            }
        }

    @Test
    fun `getHiringOffers should update InteractBannersUiState from API with OnShowBannerByControl and return max 2 data when BannerControl is RectangleHome`() =
        runTest {
            coEvery { getRemoteInteractBannersOffersUseCase() } returns dataResultSuccess
            viewModel.getHiringOffers(true, BannerControl.RectangleHome)
            dispatcherRule.advanceUntilIdle()
            viewModel.interactBannersStateMutableLiveData.value.let {
                assert(it is InteractBannersUiState.OnShowBannerByControl && rectangleHomeCompleteResponse == it.offers)
            }
        }

    @Test
    fun `getHiringOffers should update InteractBannersUiState from API with OnShowBannerByControl and return max 1 data when BannerControl is LeaderboardReceivables`() =
        runTest {
            coEvery { getRemoteInteractBannersOffersUseCase() } returns dataResultSuccess
            viewModel.getHiringOffers(true, BannerControl.LeaderboardReceivables)
            dispatcherRule.advanceUntilIdle()
            viewModel.interactBannersStateMutableLiveData.value.let {
                assert(it is InteractBannersUiState.OnShowBannerByControl && leaderboardReceivablesCompleteResponse == it.offers)
            }
        }

    @Test
    fun `getHiringOffers should update InteractBannersUiState from API with OnShowBannerByControl and return max 1 data when BannerControl is LeaderboardServices`() =
        runTest {
            coEvery { getRemoteInteractBannersOffersUseCase() } returns dataResultSuccess
            viewModel.getHiringOffers(true, BannerControl.LeaderboardServices)
            dispatcherRule.advanceUntilIdle()
            viewModel.interactBannersStateMutableLiveData.value.let {
                assert(it is InteractBannersUiState.OnShowBannerByControl && leaderboardServicesCompleteResponse == it.offers)
            }
        }


    @Test
    fun `getHiringOffers should update InteractBannersUiState from API with OnShowBannerByControl and return max 1 data when BannerControl is LeaderboardOthers`() =
        runTest {
            coEvery { getRemoteInteractBannersOffersUseCase() } returns dataResultSuccess
            viewModel.getHiringOffers(true, BannerControl.LeaderboardOthers)
            dispatcherRule.advanceUntilIdle()
            viewModel.interactBannersStateMutableLiveData.value.let {
                assert(it is InteractBannersUiState.OnShowBannerByControl && leaderboardOthersCompleteResponse == it.offers)
            }
        }

    @Test
    fun `getHiringOffers should update InteractBannersUiState from API with OnShowBannerByControl and return max 1 data when BannerControl is LeaderboardFeesAndPlans`() =
        runTest {
            coEvery { getRemoteInteractBannersOffersUseCase() } returns dataResultSuccess
            viewModel.getHiringOffers(true, BannerControl.LeaderboardFeesAndPlans)
            dispatcherRule.advanceUntilIdle()
            viewModel.interactBannersStateMutableLiveData.value.let {
                assert(it is InteractBannersUiState.OnShowBannerByControl && leaderboardFeesAndPlansCompleteResponse == it.offers)
            }
        }

    @Test
    fun `getHiringOffers should update InteractBannersUiState from userPreferences with OnShowBannerByControl and return max 4 data when BannerControl is LeaderboardHome`() =
        runTest {
            coEvery { getLocalInteractBannersOffersUseCase() } returns dataResultSuccess
            viewModel.getHiringOffers(false, BannerControl.LeaderboardHome)
            dispatcherRule.advanceUntilIdle()
            viewModel.interactBannersStateMutableLiveData.value.let {
                assert(it is InteractBannersUiState.OnShowBannerByControl && leaderboardHomeCompleteResponse == it.offers)
            }
        }

    @Test
    fun `getHiringOffers should update InteractBannersUiState from userPreferences with OnShowBannerByControl and return max 2 data when BannerControl is RectangleHome`() =
        runTest {
            coEvery { getLocalInteractBannersOffersUseCase() } returns dataResultSuccess
            viewModel.getHiringOffers(false, BannerControl.RectangleHome)
            dispatcherRule.advanceUntilIdle()
            viewModel.interactBannersStateMutableLiveData.value.let {
                assert(it is InteractBannersUiState.OnShowBannerByControl && rectangleHomeCompleteResponse == it.offers)
            }
        }

    @Test
    fun `getHiringOffers should update InteractBannersUiState from userPreferences with OnShowBannerByControl and return max 1 data when BannerControl is LeaderboardReceivables`() =
        runTest {
            coEvery { getLocalInteractBannersOffersUseCase() } returns dataResultSuccess
            viewModel.getHiringOffers(false, BannerControl.LeaderboardReceivables)
            dispatcherRule.advanceUntilIdle()
            viewModel.interactBannersStateMutableLiveData.value.let {
                assert(it is InteractBannersUiState.OnShowBannerByControl && leaderboardReceivablesCompleteResponse == it.offers)
            }
        }

    @Test
    fun `getHiringOffers should update InteractBannersUiState from userPreferences with OnShowBannerByControl and return max 1 data when BannerControl is LeaderboardServices`() =
        runTest {
            coEvery { getLocalInteractBannersOffersUseCase() } returns dataResultSuccess
            viewModel.getHiringOffers(false, BannerControl.LeaderboardServices)
            dispatcherRule.advanceUntilIdle()
            viewModel.interactBannersStateMutableLiveData.value.let {
                assert(it is InteractBannersUiState.OnShowBannerByControl && leaderboardServicesCompleteResponse == it.offers)
            }
        }


    @Test
    fun `getHiringOffers should update InteractBannersUiState from userPreferences with OnShowBannerByControl and return max 1 data when BannerControl is LeaderboardOthers`() =
        runTest {
            coEvery { getLocalInteractBannersOffersUseCase() } returns dataResultSuccess
            viewModel.getHiringOffers(false, BannerControl.LeaderboardOthers)
            dispatcherRule.advanceUntilIdle()
            viewModel.interactBannersStateMutableLiveData.value.let {
                assert(it is InteractBannersUiState.OnShowBannerByControl && leaderboardOthersCompleteResponse == it.offers)
            }
        }

    @Test
    fun `getHiringOffers should update InteractBannersUiState from userPreferences with OnShowBannerByControl and return max 1 data when BannerControl is LeaderboardFeesAndPlans`() =
        runTest {
            coEvery { getLocalInteractBannersOffersUseCase() } returns dataResultSuccess
            viewModel.getHiringOffers(false, BannerControl.LeaderboardFeesAndPlans)
            dispatcherRule.advanceUntilIdle()
            viewModel.interactBannersStateMutableLiveData.value.let {
                assert(it is InteractBannersUiState.OnShowBannerByControl && leaderboardFeesAndPlansCompleteResponse == it.offers)
            }
        }
}