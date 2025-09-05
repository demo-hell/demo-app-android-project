package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixInfringementRepository
import br.com.mobicare.cielo.pixMVVM.utils.PixInfringementFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PostPixInfringementUseCaseTest {

    private val repository = mockk<PixInfringementRepository>()
    private val postPixInfringementUseCase = PostPixInfringementUseCase(repository)

    private val resultSuccessCreateInfringement =
        CieloDataResult.Success(PixInfringementFactory.pixCreateInfringementResponse)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    @Test
    fun `it should create infringement calling remote data source only once`() = runBlocking {
        coEvery {
            repository.postInfringement(any())
        } returns resultSuccessCreateInfringement

        postPixInfringementUseCase(PixInfringementFactory.pixCreateInfringementRequest)

        coVerify(exactly = ONE) { repository.postInfringement(any()) }
    }

    @Test
    fun `it should create infringement`() = runBlocking {
        coEvery {
            repository.postInfringement(any())
        } returns resultSuccessCreateInfringement

        val result =
            postPixInfringementUseCase(PixInfringementFactory.pixCreateInfringementRequest)

        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

        val dataResult = result as CieloDataResult.Success

        val actualInfringement = dataResult.value
        val expectedInfringement = resultSuccessCreateInfringement.value

        assertEquals(expectedInfringement.id, actualInfringement.id)
        assertEquals(expectedInfringement.creationDate, actualInfringement.creationDate)
    }

    @Test
    fun `it should return a network error when create infringement`() = runBlocking {
        coEvery {
            repository.postInfringement(any())
        } returns resultError

        val result =
            postPixInfringementUseCase(PixInfringementFactory.pixCreateInfringementRequest)

        assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)

        assertEquals(resultError, result)
    }

    @Test
    fun `it should return a empty error when create infringement`() = runBlocking {
        coEvery {
            repository.postInfringement(any())
        } returns resultEmpty

        val result =
            postPixInfringementUseCase(PixInfringementFactory.pixCreateInfringementRequest)

        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)

        assertEquals(resultEmpty, result)
    }

}