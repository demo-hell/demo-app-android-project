package br.com.mobicare.cielo.interactbannersoffersnew.domain.usecase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.interactBannersOffersNew.data.repository.InteractBannerNewRepositoryImpl
import br.com.mobicare.cielo.interactBannersOffersNew.domain.useCase.GetRemoteInteractBannersOffersUseCase
import br.com.mobicare.cielo.interactbannersoffersnew.utils.InteractBannerNewFactory
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetRemoteInteractBannersOffersUseCaseTest {
    private val repository = mockk<InteractBannerNewRepositoryImpl>()
    private val getRemoteInteractBannersOffersUseCase =
        GetRemoteInteractBannersOffersUseCase(repository)

    private val completeResponse = InteractBannerNewFactory.getCompleteResponse()
    private val successResult = CieloDataResult.Success(completeResponse)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }


    @Test
    fun `invoke should return list of HiringOffers on successful API response`() = runTest {
        coEvery { repository.getRemoteInteractBannersOffers() } returns successResult

        val result = getRemoteInteractBannersOffersUseCase()

        assertEquals(successResult, result)
    }

    @Test
    fun `invoke should return API error result on error response`() = runBlocking {
        coEvery { repository.getRemoteInteractBannersOffers() } returns resultError

        val result = getRemoteInteractBannersOffersUseCase()

        assertEquals(resultError, result)
    }

    @Test
    fun `invoke should return empty result on empty API response`() = runBlocking {
        coEvery { repository.getRemoteInteractBannersOffers() } returns CieloDataResult.Empty()

        val result = getRemoteInteractBannersOffersUseCase()

        assertTrue(result is CieloDataResult.Empty)
    }
}