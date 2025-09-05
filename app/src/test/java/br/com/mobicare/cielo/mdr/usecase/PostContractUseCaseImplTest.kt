package br.com.mobicare.cielo.mdr.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.mdr.data.usecase.PostContractUseCaseImpl
import br.com.mobicare.cielo.mdr.domain.repository.MdrRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PostContractUseCaseImplTest {
    private lateinit var useCase: PostContractUseCaseImpl
    private val mdrRepository: MdrRepository = mockk()

    @Before
    fun setUp() {
        useCase = PostContractUseCaseImpl(mdrRepository)
    }

    @Test
    fun `invoke with isAccepted true calls postContractDecision`() =
        runTest {
            coEvery { mdrRepository.postContractDecision(any(), any(), any()) } returns CieloDataResult.Empty()

            val result = useCase.invoke("123", 1, true)

            assertEquals(CieloDataResult.Empty(), result)
        }

    @Test
    fun `invoke with isAccepted false calls postContractDecision`() =
        runTest {
            coEvery { mdrRepository.postContractDecision(any(), any(), any()) } returns CieloDataResult.Empty()

            val result = useCase.invoke("123", 1, false)

            assertEquals(CieloDataResult.Empty(), result)
        }

    @Test
    fun `invoke with exception returns CieloDataResult Empty and logs error to FirebaseCrashlytic`() =
        runTest {
            val exceptionMessage = "Error message"
            coEvery { mdrRepository.postContractDecision(any(), any(), any()) } throws Exception(exceptionMessage)
            mockkStatic(FirebaseCrashlytics::class)
            every { FirebaseCrashlytics.getInstance().log(any()) } just Runs

            val result = useCase.invoke("12344", 1, true)

            assertEquals(CieloDataResult.Empty(), result)
            verify { FirebaseCrashlytics.getInstance().log(exceptionMessage) }
        }
}
