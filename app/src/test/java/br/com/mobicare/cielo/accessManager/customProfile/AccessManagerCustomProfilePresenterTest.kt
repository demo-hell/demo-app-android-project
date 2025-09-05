package br.com.mobicare.cielo.accessManager.customProfile

import br.com.mobicare.cielo.accessManager.AccessManagerRepository
import br.com.mobicare.cielo.accessManager.model.AccessManagerCustomProfileDetailResponse
import br.com.mobicare.cielo.accessManager.model.AccessManagerCustomProfileResponse
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.pix.constants.ACTIVE
import com.nhaarman.mockito_kotlin.mock
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test

class AccessManagerCustomProfilePresenterTest {

    private lateinit var view: AccessManagerCustomProfileContract.View
    private lateinit var repository: AccessManagerRepository
    private lateinit var uiScheduler: Scheduler
    private lateinit var ioScheduler: Scheduler
    private lateinit var presenter: AccessManagerCustomProfilePresenter

    @Before
    fun setup(){
        view = mockk(relaxed = true)
        repository = mockk(relaxed = true)
        uiScheduler = Schedulers.trampoline()
        ioScheduler = Schedulers.trampoline()

        presenter = AccessManagerCustomProfilePresenter(
            repository, view, uiScheduler, ioScheduler
        )
    }

    @After
    fun teardown(){
        presenter.onPause()
    }

    @Test
    fun `getCustomActiveProfiles should call showCustomProfiles with token when API call succeeds`() {
        //Given
        val response: List<AccessManagerCustomProfileResponse> = mock()

        every {
            repository.getProfiles(Text.CUSTOM, ACTIVE)
        } returns Observable.just(response)

        // When
        presenter.getCustomActiveProfiles()

        // Then
        verify { view.showCustomProfiles(response)}
        verify (inverse = true) { view.showErrorProfile()}
    }

    @Test
    fun `getCustomActiveProfiles should call showErrorProfile with token when API call fails`() {
        //Given
        val response: List<AccessManagerCustomProfileResponse> = mock()

        every {
            repository.getProfiles(Text.CUSTOM, ACTIVE)
        } returns Observable.error(Exception("API call failed"))

        // When
        presenter.getCustomActiveProfiles()

        // Then
        verify { view.showErrorProfile()}
        verify (inverse = true) { view.showCustomProfiles(response)}
    }

    @Test
    fun `getDetailCustomProfile should call getDetailSuccess with token when API call succeeds`() {
        //Given
        val response: AccessManagerCustomProfileDetailResponse = mock()
        val profileId = "profileId"

        every {
            repository.getProfileDetail(profileId)
        } returns Observable.just(response)

        // When
        presenter.getDetailCustomProfile(profileId)

        // Then
        verify { view.getDetailSuccess(response)}
        verify (inverse = true) { view.showErrorProfile()}
    }

    @Test
    fun `getDetailCustomProfile should call showErrorProfile with token when API call fails`() {
        //Given
        val response: AccessManagerCustomProfileDetailResponse = mock()
        val profileId = "profileId"

        every {
            repository.getProfileDetail(profileId)
        } returns Observable.error(Exception("API call failed"))

        // When
        presenter.getDetailCustomProfile(profileId)

        // Then
        verify { view.showErrorProfile()}
        verify (inverse = true) { view.getDetailSuccess(response)}
    }
}