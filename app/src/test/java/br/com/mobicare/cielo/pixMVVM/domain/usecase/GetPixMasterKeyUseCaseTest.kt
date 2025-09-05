package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.asSuccess
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixClaimType
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixKeysRepository
import br.com.mobicare.cielo.pixMVVM.utils.PixKeysFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetPixMasterKeyUseCaseTest {
    private val repository = mockk<PixKeysRepository>()

    private val response = PixKeysFactory.pixAllKeysResponse
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val emptyResult = CieloDataResult.Empty()
    private val successResult = CieloDataResult.Success(response)
    private val getPixMasterKeyUseCase = GetPixMasterKeyUseCase(repository)

    @Test
    fun `it should call method getAllKeys of repository only once`() = runBlocking {
        // given
        coEvery { repository.getAllKeys() } returns successResult

        // when
        getPixMasterKeyUseCase()

        // then
        coVerify(exactly = 1) { repository.getAllKeys() }
    }

    @Test
    fun `it should return MasterKeyFound result when response has at least one main key`() = runBlocking {
        // given
        val keyItemsWithOneMainKey = PixKeysFactory.WithMasterKey.keyItems

        val successResultWithOneMainKey = CieloDataResult.Success(
            response.copy(
                keys = response.keys?.copy(
                    keys = keyItemsWithOneMainKey
                )
            )
        )

        val expectedData = GetPixMasterKeyUseCase.Data(
            keys = keyItemsWithOneMainKey,
            masterKey = PixKeysFactory.WithMasterKey.masterKey,
            shouldShowAlert = true
        )

        coEvery { repository.getAllKeys() } returns successResultWithOneMainKey

        // when
        val result = getPixMasterKeyUseCase()

        // then
        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

        assertThat(result.asSuccess.value)
            .isInstanceOf(GetPixMasterKeyUseCase.Result.MasterKeyFound::class.java)

        assertEquals(
            expectedData,
            (result.asSuccess.value as GetPixMasterKeyUseCase.Result.MasterKeyFound).data
        )
    }

    @Test
    fun `it should return MasterKeyNotFound result when response has no main key`() = runBlocking {
        // given
        val keyItemsWithNoMainKey = PixKeysFactory.WithoutMasterKey.keyItems

        val successResultWithNoMainKey = CieloDataResult.Success(
            response.copy(
                keys = response.keys?.copy(
                    keys = keyItemsWithNoMainKey
                )
            )
        )

        val expectedData = GetPixMasterKeyUseCase.Data(
            keys = keyItemsWithNoMainKey,
            shouldShowAlert = true
        )

        coEvery { repository.getAllKeys() } returns successResultWithNoMainKey

        // when
        val result = getPixMasterKeyUseCase()

        // then
        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

        assertThat(result.asSuccess.value)
            .isInstanceOf(GetPixMasterKeyUseCase.Result.MasterKeyNotFound::class.java)

        assertEquals(
            expectedData,
            (result.asSuccess.value as GetPixMasterKeyUseCase.Result.MasterKeyNotFound).data
        )
    }

    @Test
    fun `it should return NoKeysFound result when response has no keys`() = runBlocking {
        // given
        val successResultWithoutKeys = CieloDataResult.Success(
            response.copy(
                keys = response.keys?.copy(
                    keys = null
                )
            )
        )

        coEvery { repository.getAllKeys() } returns successResultWithoutKeys

        // when
        val result = getPixMasterKeyUseCase()

        // then
        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

        assertThat(result.asSuccess.value)
            .isInstanceOf(GetPixMasterKeyUseCase.Result.NoKeysFound::class.java)
    }

    private suspend fun runAssertShouldShowAlertTest(claimType: PixClaimType, expectedResult: Boolean) {
        // given
        val keyItemsWithOneSpecificClaimType = response.keys?.keys!!.mapIndexed { index, keyItem ->
            keyItem.copy(
                claimType = if (index == 0) claimType.name else PixClaimType.NOT_ALLOWED.name
            )
        }

        val successResultWithOneSpecificClaimType = CieloDataResult.Success(
            response.copy(
                keys = response.keys?.copy(
                    keys = keyItemsWithOneSpecificClaimType
                )
            )
        )

        coEvery { repository.getAllKeys() } returns successResultWithOneSpecificClaimType

        // when
        val result = getPixMasterKeyUseCase()

        // then
        assertEquals(
            expectedResult,
            (result.asSuccess.value as GetPixMasterKeyUseCase.Result.MasterKeyNotFound)
                .data
                .shouldShowAlert
        )
    }

    @Test
    fun `it should set shouldShowAlert as true when response has at least one key with PORTABILITY claim type`() = runBlocking {
        runAssertShouldShowAlertTest(PixClaimType.PORTABILITY,  expectedResult = true)
    }

    @Test
    fun `it should set shouldShowAlert as true when response has at least one key with OWNERSHIP claim type`() = runBlocking {
        runAssertShouldShowAlertTest(PixClaimType.OWNERSHIP,  expectedResult = true)
    }

    @Test
    fun `it should set shouldShowAlert as false when response has neither PORTABILITY or OWNERSHIP claim type`() = runBlocking {
        runAssertShouldShowAlertTest(PixClaimType.NOT_ALLOWED,  expectedResult = false)
    }

    @Test
    fun `it should return a network error on getPixMasterKeyUseCase call`() = runBlocking {
        // given
        coEvery { repository.getAllKeys() } returns errorResult

        // when
        val result = getPixMasterKeyUseCase()

        // then
        assertEquals(errorResult, result)
    }

    @Test
    fun `it should return an empty result on getPixMasterKeyUseCase call`() = runBlocking {
        // given
        coEvery { repository.getAllKeys() } returns emptyResult

        // when
        val result = getPixMasterKeyUseCase()

        // then
        assertEquals(emptyResult, result)
    }
}