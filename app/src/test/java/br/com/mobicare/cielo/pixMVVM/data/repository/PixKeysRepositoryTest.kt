package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.asSuccess
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixKeysRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.utils.PixKeysFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PixKeysRepositoryTest {
    private val remoteDataSource = mockk<PixKeysRemoteDataSource>()
    private val repository = PixKeysRepositoryImpl(remoteDataSource)

    private val pixAllKeysResponse = PixKeysFactory.pixAllKeysResponse

    private val successResult = CieloDataResult.Success(pixAllKeysResponse)
    private val successResultValidateKey = CieloDataResult.Success(PixKeysFactory.pixValidateKey)

    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val emptyResult = CieloDataResult.Empty()

    @Test
    fun `it should call method getAllKeys of remote data source only once`() =
        runBlocking {
            // given
            coEvery { remoteDataSource.getAllKeys() } returns successResult

            // when
            repository.getAllKeys()

            // then
            coVerify(exactly = ONE) { remoteDataSource.getAllKeys() }
        }

    @Test
    fun `it should return the correct PixKeysResponse on getAllKeys call successfully`() =
        runBlocking {
            // given
            coEvery { remoteDataSource.getAllKeys() } returns successResult

            // when
            val result = repository.getAllKeys()

            // then
            assertEquals(successResult, result)

            val actual = result.asSuccess.value
            val expected = pixAllKeysResponse

            assertEquals(expected, actual)
        }

    @Test
    fun `it should return a network error on getAllKeys call`() =
        runBlocking {
            // given
            coEvery { remoteDataSource.getAllKeys() } returns errorResult

            // when
            val result = repository.getAllKeys()

            // then
            assertEquals(errorResult, result)
        }

    @Test
    fun `it should call method getValidateKEy of remote data source only once`() =
        runBlocking {
            coEvery { remoteDataSource.getValidateKey(any(), any()) } returns successResultValidateKey

            repository.getValidateKey(PixKeysFactory.key, PixKeysFactory.keyType)

            coVerify(exactly = ONE) { remoteDataSource.getValidateKey(any(), any()) }
        }

    @Test
    fun `it should return the validate key response`() =
        runBlocking {
            coEvery { remoteDataSource.getValidateKey(any(), any()) } returns successResultValidateKey

            val result = repository.getValidateKey(PixKeysFactory.key, PixKeysFactory.keyType)

            assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

            val actualResponse = (result as CieloDataResult.Success).value
            val expectedResponse = successResultValidateKey.value

            assertEquals(expectedResponse.accountNumber, actualResponse.accountNumber)
            assertEquals(expectedResponse.accountType, actualResponse.accountType)
            assertEquals(expectedResponse.branch, actualResponse.branch)
            assertEquals(expectedResponse.claimType, actualResponse.claimType)
            assertEquals(expectedResponse.creationDate, actualResponse.creationDate)
            assertEquals(expectedResponse.endToEndId, actualResponse.endToEndId)
            assertEquals(expectedResponse.key, actualResponse.key)
            assertEquals(expectedResponse.keyType, actualResponse.keyType)
            assertEquals(expectedResponse.ownerDocument, actualResponse.ownerDocument)
            assertEquals(expectedResponse.ownerName, actualResponse.ownerName)
            assertEquals(expectedResponse.ownerTradeName, actualResponse.ownerTradeName)
            assertEquals(expectedResponse.ownerType, actualResponse.ownerType)
            assertEquals(expectedResponse.ownershipDate, actualResponse.ownershipDate)
            assertEquals(expectedResponse.participant, actualResponse.participant)
            assertEquals(expectedResponse.participantName, actualResponse.participantName)
        }

    @Test
    fun `it should return a network error on getValidateKey call`() =
        runBlocking {
            coEvery { remoteDataSource.getValidateKey(any(), any()) } returns errorResult

            val result = repository.getValidateKey(PixKeysFactory.key, PixKeysFactory.keyType)

            assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)

            assertEquals(errorResult, result)
        }

    @Test
    fun `it should return a empty error on getValidateKey call`() =
        runBlocking {
            coEvery { remoteDataSource.getValidateKey(any(), any()) } returns emptyResult

            val result = repository.getValidateKey(PixKeysFactory.key, PixKeysFactory.keyType)

            assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)

            assertEquals(emptyResult, result)
        }
}
