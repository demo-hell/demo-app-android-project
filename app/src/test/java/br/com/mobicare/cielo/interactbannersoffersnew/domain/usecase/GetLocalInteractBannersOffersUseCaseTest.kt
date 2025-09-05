package br.com.mobicare.cielo.interactbannersoffersnew.domain.usecase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.interactBannersOffersNew.data.repository.InteractBannerNewRepositoryImpl
import br.com.mobicare.cielo.interactBannersOffersNew.domain.useCase.GetLocalInteractBannersOffersUseCase
import br.com.mobicare.cielo.interactbannersoffersnew.utils.InteractBannerNewFactory
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GetLocalInteractBannersOffersUseCaseTest {
    private val repository = mockk<InteractBannerNewRepositoryImpl>()
    private val getLocalInteractBannersOffersUseCase =
        GetLocalInteractBannersOffersUseCase(repository)

    private val completeResponse = InteractBannerNewFactory.getCompleteResponse()
    private val successResult = CieloDataResult.Success(completeResponse)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }


    @Test
    fun `invoke should return list of HiringOffers on successful API response`() = runTest {
        coEvery { repository.getLocalInteractBannersOffers() } returns successResult

        val result = getLocalInteractBannersOffersUseCase()

        assertEquals(successResult, result)
    }

    @Test
    fun `invoke should return API error result on error response`() = runTest {
        coEvery { repository.getLocalInteractBannersOffers() } returns resultError

        val result = getLocalInteractBannersOffersUseCase()

        assertEquals(resultError, result)
    }

    @Test
    fun `invoke should return empty result on empty API response`() = runTest {
        coEvery { repository.getLocalInteractBannersOffers() } returns CieloDataResult.Empty()

        val result = getLocalInteractBannersOffersUseCase()

        assertTrue(result is CieloDataResult.Empty)
    }
}