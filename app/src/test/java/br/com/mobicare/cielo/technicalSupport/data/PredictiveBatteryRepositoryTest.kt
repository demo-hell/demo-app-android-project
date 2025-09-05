package br.com.mobicare.cielo.technicalSupport.data

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.technicalSupport.data.repository.PredictiveBatteryRepositoryImpl
import br.com.mobicare.cielo.technicalSupport.domain.dataSource.PredictiveBatteryDataSource
import br.com.mobicare.cielo.technicalSupport.domain.repository.PredictiveBatteryRepository
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
class PredictiveBatteryRepositoryTest {

    private val remoteDataSource = mockk<PredictiveBatteryDataSource>()
    private lateinit var repository: PredictiveBatteryRepository

    private val resultPredictiveBatteryPostChangeBatterySuccess = CieloDataResult.Success(
        PredictiveBatteryFactory.batteryResponseSuccess
    )
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    @Before
    fun setup() {
        repository = PredictiveBatteryRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `it should call postChangeBattery of remote data source only once`() = runTest {
        coEvery {
            remoteDataSource.postChangeBattery(any())
        } returns resultPredictiveBatteryPostChangeBatterySuccess

        repository.postChangeBattery(PredictiveBatteryFactory.batteryRequestWithChargeBatteryIsTrue)

        coVerify(exactly = ONE) { remoteDataSource.postChangeBattery(any()) }
    }

    @Test
    fun `it should return a Success when post changeBattery with parameter chargeBattery is true`() = runTest {
        coEvery { remoteDataSource.postChangeBattery(any()) } returns resultPredictiveBatteryPostChangeBatterySuccess

        val result =
            repository.postChangeBattery(PredictiveBatteryFactory.batteryRequestWithChargeBatteryIsTrue)

        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

        val resultSuccess = result as CieloDataResult.Success
        val actualID = resultSuccess.value.id
        val expectedID = PredictiveBatteryFactory.batteryResponseID

        assertEquals(expectedID, actualID)
    }

    @Test
    fun `it should return a Success when post changeBattery with parameter chargeBattery is false`() = runTest {
        coEvery { remoteDataSource.postChangeBattery(any()) } returns resultPredictiveBatteryPostChangeBatterySuccess

        val result =
            repository.postChangeBattery(PredictiveBatteryFactory.batteryRequestWithChargeBatteryIsFalse)

        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

        val resultSuccess = result as CieloDataResult.Success
        val actualID = resultSuccess.value.id
        val expectedID = PredictiveBatteryFactory.batteryResponseID

        assertEquals(expectedID, actualID)
    }

    @Test
    fun `it should return a Error when post changeBattery with parameter chargeBattery is true`() = runTest {
        coEvery { remoteDataSource.postChangeBattery(any()) } returns resultError

        val result =
            repository.postChangeBattery(PredictiveBatteryFactory.batteryRequestWithChargeBatteryIsTrue)

        assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)
        assertEquals(resultError, result)
    }

    @Test
    fun `it should return a Error when post changeBattery with parameter chargeBattery is false`() = runTest {
        coEvery { remoteDataSource.postChangeBattery(any()) } returns resultError

        val result =
            repository.postChangeBattery(PredictiveBatteryFactory.batteryRequestWithChargeBatteryIsFalse)

        assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)
        assertEquals(resultError, result)
    }

    @Test
    fun `it should return a Empty when post changeBattery with parameter chargeBattery is true`() = runTest {
        coEvery { remoteDataSource.postChangeBattery(any()) } returns resultEmpty

        val result =
            repository.postChangeBattery(PredictiveBatteryFactory.batteryRequestWithChargeBatteryIsTrue)

        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)
        assertEquals(resultEmpty, result)
    }

    @Test
    fun `it should return a Empty when post changeBattery with parameter chargeBattery is false`() = runTest {
        coEvery { remoteDataSource.postChangeBattery(any()) } returns resultEmpty

        val result =
            repository.postChangeBattery(PredictiveBatteryFactory.batteryRequestWithChargeBatteryIsFalse)

        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)
        assertEquals(resultEmpty, result)
    }

}