package br.com.mobicare.cielo.contactCielo.domain

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.constants.SIX
import br.com.mobicare.cielo.commons.constants.ZERO_TEXT
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.contactCielo.data.mapper.ContactCieloWhatsappMapper
import br.com.mobicare.cielo.contactCielo.domain.model.ContactCieloWhatsapp
import br.com.mobicare.cielo.contactCielo.domain.useCase.GetLocalSegmentCodeUseCase
import br.com.mobicare.cielo.contactCielo.domain.useCase.GetRemoteSegmentCodeUseCase
import br.com.mobicare.cielo.contactCielo.domain.useCase.SaveLocalSegmentCodeUseCase
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class ContactCieloViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val context = mockk<Context>(relaxed = true)

    private val getRemoteSegmentCodeUseCase = mockk<GetRemoteSegmentCodeUseCase>()
    private val getLocalSegmentCodeUseCase = mockk<GetLocalSegmentCodeUseCase>()
    private val saveLocalSegmentCodeUseCase = mockk<SaveLocalSegmentCodeUseCase>()
    private val getFeatureTogglePreferenceUseCase = mockk<GetFeatureTogglePreferenceUseCase>()

    private lateinit var viewModel: ContactCieloViewModel

    private val completeList = listOf(
        ContactCieloWhatsappMapper.virtualManager,
        ContactCieloWhatsappMapper.generalDoubts
    )

    private val onlyDuvidasGeraisList = listOf(
        ContactCieloWhatsappMapper.generalDoubts
    )

    private val emptyListOfContactCieloWhatsapp = listOf<ContactCieloWhatsapp>()

    @Before
    fun setup() {
        mockkObject(CieloApplication)
        every { CieloApplication.Companion.context } returns context

        viewModel = ContactCieloViewModel(
            getLocalSegmentCodeUseCase,
            saveLocalSegmentCodeUseCase,
            getRemoteSegmentCodeUseCase,
            getFeatureTogglePreferenceUseCase
        )
    }

    @Test
    fun `contactInfoSource should return an list of ContactCieloWhatsapp with GerenteVirtual and DuvidasGerais when all featureToggle is enabled`() =
        runTest {

            coEvery { getFeatureTogglePreferenceUseCase(FeatureTogglePreference.CONTACT_CIELO) } returns
                    CieloDataResult.Success(true)
            coEvery { getFeatureTogglePreferenceUseCase(FeatureTogglePreference.CONTACT_CIELO_GERENTE_VIRTUAL) } returns
                    CieloDataResult.Success(true)
            coEvery { getLocalSegmentCodeUseCase() } returns CieloDataResult.Success(EMPTY) andThen CieloDataResult.Success(SIX.toString())
            coEvery { getRemoteSegmentCodeUseCase() } returns CieloDataResult.Success(SIX.toString())
            coEvery { saveLocalSegmentCodeUseCase(SIX.toString()) } just Runs

            viewModel.retrieveContactSourceInfo()
            dispatcherRule.advanceUntilIdle()

            assert(viewModel.contactInfoSource.value == completeList)
        }

    @Test
    fun `contactInfoSource should return an list of ContactCieloWhatsapp only DuvidasGerais when featureToggle CONTACT_CIELO is enabled and CONTACT_CIELO_GERENTE_VIRTUAL is disabled`() =
        runTest {

            coEvery { getFeatureTogglePreferenceUseCase(FeatureTogglePreference.CONTACT_CIELO) } returns
                    CieloDataResult.Success(true)
            coEvery { getFeatureTogglePreferenceUseCase(FeatureTogglePreference.CONTACT_CIELO_GERENTE_VIRTUAL) } returns
                    CieloDataResult.Success(false)
            coEvery { getLocalSegmentCodeUseCase() } returns CieloDataResult.Success(EMPTY) andThen CieloDataResult.Success(SIX.toString())
            coEvery { getRemoteSegmentCodeUseCase() } returns CieloDataResult.Success(SIX.toString())
            coEvery { saveLocalSegmentCodeUseCase(SIX.toString()) } just Runs

            viewModel.retrieveContactSourceInfo()
            dispatcherRule.advanceUntilIdle()

            assert(viewModel.contactInfoSource.value == onlyDuvidasGeraisList)
        }

    @Test
    fun `contactInfoSource should return an list of ContactCieloWhatsapp only DuvidasGerais when featureToggle CONTACT_CIELO is enabled and CONTACT_CIELO_GERENTE_VIRTUAL is enabled but segmentCode is not allowed`() =
        runTest {

            coEvery { getFeatureTogglePreferenceUseCase(FeatureTogglePreference.CONTACT_CIELO) } returns
                    CieloDataResult.Success(true)
            coEvery { getFeatureTogglePreferenceUseCase(FeatureTogglePreference.CONTACT_CIELO_GERENTE_VIRTUAL) } returns
                    CieloDataResult.Success(true)
            coEvery { getLocalSegmentCodeUseCase() } returns CieloDataResult.Success(EMPTY) andThen CieloDataResult.Success(ZERO_TEXT)
            coEvery { getRemoteSegmentCodeUseCase() } returns CieloDataResult.Success(ZERO_TEXT)
            coEvery { saveLocalSegmentCodeUseCase(ZERO_TEXT) } just Runs

            viewModel.retrieveContactSourceInfo()
            dispatcherRule.advanceUntilIdle()

            assert(viewModel.contactInfoSource.value == onlyDuvidasGeraisList)
        }

    @Test
    fun `contactInfoSource should return an empty list when featureToggle CONTACT_CIELO is disabled`() =
        runTest {

            coEvery { getFeatureTogglePreferenceUseCase(FeatureTogglePreference.CONTACT_CIELO) } returns
                    CieloDataResult.Success(false)
            coEvery { getFeatureTogglePreferenceUseCase(FeatureTogglePreference.CONTACT_CIELO_GERENTE_VIRTUAL) } returns
                    CieloDataResult.Success(false)
            coEvery { getLocalSegmentCodeUseCase() } returns CieloDataResult.Success(EMPTY) andThen CieloDataResult.Success(SIX.toString())
            coEvery { getRemoteSegmentCodeUseCase() } returns CieloDataResult.Success(SIX.toString())
            coEvery { saveLocalSegmentCodeUseCase(SIX.toString()) } just Runs

            viewModel.retrieveContactSourceInfo()
            dispatcherRule.advanceUntilIdle()

            assert(viewModel.contactInfoSource.value == emptyListOfContactCieloWhatsapp)
        }

    @Test
    fun `contactInfoSource should return an empty list when featureToggle CONTACT_CIELO is disabled even if CONTACT_CIELO_GERENTE_VIRTUAL is enabled`() =
        runTest {

            coEvery { getFeatureTogglePreferenceUseCase(FeatureTogglePreference.CONTACT_CIELO) } returns
                    CieloDataResult.Success(false)
            coEvery { getFeatureTogglePreferenceUseCase(FeatureTogglePreference.CONTACT_CIELO_GERENTE_VIRTUAL) } returns
                    CieloDataResult.Success(true)
            coEvery { getLocalSegmentCodeUseCase() } returns CieloDataResult.Success(EMPTY) andThen CieloDataResult.Success(SIX.toString())
            coEvery { getRemoteSegmentCodeUseCase() } returns CieloDataResult.Success(SIX.toString())
            coEvery { saveLocalSegmentCodeUseCase(SIX.toString()) } just Runs

            viewModel.retrieveContactSourceInfo()
            dispatcherRule.advanceUntilIdle()

            assert(viewModel.contactInfoSource.value == emptyListOfContactCieloWhatsapp)
        }
}