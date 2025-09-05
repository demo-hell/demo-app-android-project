package br.com.mobicare.cielo.interactbannersoffersnew.data.repository


import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.interactBannersOffersNew.data.datasource.InteractBannerNewRemoteDataSource
import br.com.mobicare.cielo.interactBannersOffersNew.data.datasource.local.InteractBannerNewLocalDataSource
import br.com.mobicare.cielo.interactBannersOffersNew.data.repository.InteractBannerNewRepositoryImpl
import br.com.mobicare.cielo.interactbannersoffersnew.utils.InteractBannerNewFactory
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class InteractBannerNewRepositoryImplTest {
    private val remoteDataSource = mockk<InteractBannerNewRemoteDataSource>()
    private val localDataSource = mockk<InteractBannerNewLocalDataSource>()
    private val repository = InteractBannerNewRepositoryImpl(remoteDataSource, localDataSource)

    private val completeResponse = InteractBannerNewFactory.getCompleteResponse()
    private val successResult = CieloDataResult.Success(completeResponse)
    private val apiError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `getRemoteInteractBannersOffers should return success on successful API response`() =
        runTest {
            coEvery { remoteDataSource.getRemoteInteractBannersOffers() } returns successResult

            val result = repository.getRemoteInteractBannersOffers()

            assertEquals(successResult, result)
        }

    @Test
    fun `getRemoteInteractBannersOffers should return empty result on API response`() =
        runTest {
            coEvery { remoteDataSource.getRemoteInteractBannersOffers() } returns CieloDataResult.Empty()

            val result = repository.getRemoteInteractBannersOffers()

            assert(result is CieloDataResult.Empty)
        }

    @Test
    fun `getRemoteInteractBannersOffers should return network error when getting data`() =
        runTest {
            coEvery { remoteDataSource.getRemoteInteractBannersOffers() } returns apiError

            val result = repository.getRemoteInteractBannersOffers()

            assertEquals(apiError, result)
        }

    @Test
    fun `getLocalInteractBannersOffers should return success on successful get from userPreferences`() =
        runTest {
            coEvery { localDataSource.getLocalInteractBannersOffers() } returns successResult

            val result = repository.getLocalInteractBannersOffers()

            assertEquals(successResult, result)
        }

    @Test
    fun `getLocalInteractBannersOffers should return empty result on getting data from userPreferences`() =
        runTest {
            coEvery { localDataSource.getLocalInteractBannersOffers() } returns CieloDataResult.Empty()

            val result = repository.getLocalInteractBannersOffers()

            assert(result is CieloDataResult.Empty)
        }

    @Test
    fun `getLocalInteractBannersOffers should return error when getting data`() =
        runTest {
            coEvery { localDataSource.getLocalInteractBannersOffers() } returns apiError

            val result = repository.getLocalInteractBannersOffers()

            assertEquals(apiError, result)
        }


}