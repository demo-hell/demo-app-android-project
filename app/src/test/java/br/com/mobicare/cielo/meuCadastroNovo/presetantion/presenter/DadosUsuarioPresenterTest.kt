package br.com.mobicare.cielo.meuCadastroNovo.presetantion.presenter


import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.ContactPreference
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.GetUserAdditionalInfo
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.PcdType
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.TimeOfDay
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.TypeOfCommunication
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.MeuCadastroContract
import io.mockk.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test

class DadosUsuarioPresenterTest {

    private lateinit var presenter: DadosUsuarioPresenter
    private val view: MeuCadastroContract.DadosUsuarioView = mockk(relaxed = true)
    private val repository: MeuCadastroContract.MeuCadastroRepository = mockk()
    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    @Before
    fun setUp() {
        presenter = DadosUsuarioPresenter(view, repository, uiScheduler, ioScheduler)
    }

    @After
    fun tearDown() {
        presenter.onCleared()
    }

    @Test
    fun `getAdditionalInfo should show additional info`() {
        // Given
        val timeOfDay = TimeOfDay("code", "description")
        val typeOfCommunication = arrayListOf(TypeOfCommunication("code", "description"))
        val contactPreference = ContactPreference("code", "description")
        val pcdType = PcdType("code", "description")

        val response = GetUserAdditionalInfo(
            timeOfDay = timeOfDay,
            typeOfCommunication = typeOfCommunication,
            contactPreference = contactPreference,
            pcdType = pcdType
        )

        every {
            repository.getAdditionalInfo()
        } returns Observable.just(response)

        // When
        presenter.getAdditionalInfo()

        // Then
        verify {
            view.showAdditionalInfo(
                typeOfCommunication,
                contactPreference,
                timeOfDay,
                pcdType
            )
        }
    }

    @Test
    fun `getAdditionalInfo should handle error and show error`() {
        // Given
        every {
            repository.getAdditionalInfo()
        } returns Observable.error(Exception("API call failed"))

        // When
        presenter.getAdditionalInfo()

        // Then
        verify { view.error() }
    }
}
