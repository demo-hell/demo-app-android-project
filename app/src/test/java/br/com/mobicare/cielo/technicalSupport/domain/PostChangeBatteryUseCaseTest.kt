package br.com.mobicare.cielo.technicalSupport.domain

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.technicalSupport.domain.repository.PredictiveBatteryRepository
import br.com.mobicare.cielo.technicalSupport.domain.useCase.PostChangeBatteryUseCase
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.utils.PredictiveBatteryFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostChangeBatteryUseCaseTest {

    private val repository = mockk<PredictiveBatteryRepository>()
    private lateinit var postChangeBatteryUseCase: PostChangeBatteryUseCase

    private val resultPredictiveBatteryPostChangeBatterySuccess = CieloDataResult.Success(
        PredictiveBatteryFactory.batteryResponseSuccess
    )
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    @Before
    fun setup() {
        postChangeBatteryUseCase = PostChangeBatteryUseCase(repository)
    }

    @Test
    fun `it should call postChangeBattery of repository only once`() = runTest {
        coEvery {
            repository.postChangeBattery(any())
        } returns resultPredictiveBatteryPostChangeBatterySuccess

        postChangeBatteryUseCase(PredictiveBatteryFactory.batteryRequestWithChargeBatteryIsTrue)

        coVerify(exactly = ONE) { repository.postChangeBattery(any()) }
    }

    @Test
    fun `it should return a Success when post changeBattery with parameter chargeBattery is true`() =
        runTest {
            coEvery { repository.postChangeBattery(any()) } returns resultPredictiveBatteryPostChangeBatterySuccess

            val result =
                postChangeBatteryUseCase(PredictiveBatteryFactory.batteryRequestWithChargeBatteryIsTrue)

            assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

            val resultSuccess = result as CieloDataResult.Success
            val actualID = resultSuccess.value.id
            val expectedID = PredictiveBatteryFactory.batteryResponseID

            assertEquals(expectedID, actualID)
        }

    @Test
    fun `it should return a Success when post changeBattery with parameter chargeBattery is false`() =
        runTest {
            coEvery { repository.postChangeBattery(any()) } returns resultPredictiveBatteryPostChangeBatterySuccess

            val result =
                postChangeBatteryUseCase(PredictiveBatteryFactory.batteryRequestWithChargeBatteryIsFalse)

            assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

            val resultSuccess = result as CieloDataResult.Success
            val actualID = resultSuccess.value.id
            val expectedID = PredictiveBatteryFactory.batteryResponseID

            assertEquals(expectedID, actualID)
        }

    @Test
    fun `it should return a Error when post changeBattery with parameter chargeBattery is true`() =
        runTest {
            coEvery { repository.postChangeBattery(any()) } returns resultError

            val result =
                postChangeBatteryUseCase(PredictiveBatteryFactory.batteryRequestWithChargeBatteryIsTrue)

            assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)
            assertEquals(resultError, result)
        }

    @Test
    fun `it should return a Error when post changeBattery with parameter chargeBattery is false`() =
        runTest {
            coEvery { repository.postChangeBattery(any()) } returns resultError

            val result =
                postChangeBatteryUseCase(PredictiveBatteryFactory.batteryRequestWithChargeBatteryIsFalse)

            assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)
            assertEquals(resultError, result)
        }

    @Test
    fun `it should return a Empty when post changeBattery with parameter chargeBattery is true`() =
        runTest {
            coEvery { repository.postChangeBattery(any()) } returns resultEmpty

            val result =
                postChangeBatteryUseCase(PredictiveBatteryFactory.batteryRequestWithChargeBatteryIsTrue)

            assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)
            assertEquals(resultEmpty, result)
        }

    @Test
    fun `it should return a Empty when post changeBattery with parameter chargeBattery is false`() =
        runTest {
            coEvery { repository.postChangeBattery(any()) } returns resultEmpty

            val result =
                postChangeBatteryUseCase(PredictiveBatteryFactory.batteryRequestWithChargeBatteryIsFalse)

            assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)
            assertEquals(resultEmpty, result)
        }

}