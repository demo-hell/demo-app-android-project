package br.com.mobicare.cielo.idOnboarding.updateUser.p1Policy.benefits

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.login.domains.entities.EstabelecimentoObj
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class IDOnboardingBenefitsInfoP1PolicyPresenterTest {

    @Mock
    lateinit var menuPreference: MenuPreference

    @Mock
    lateinit var userPreferences: UserPreferences

    private lateinit var presenter: IDOnboardingBenefitsInfoP1PolicyPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        doReturn(
            EstabelecimentoObj(
                ec = "7866099010",
                tradeName = "CIA TESTE",
                cnpj = "33488798766"
            )
        ).whenever(menuPreference).getEstablishment()

        presenter = spy(IDOnboardingBenefitsInfoP1PolicyPresenter(menuPreference, userPreferences))
    }

    @Test
    fun `When fetchTradeName() is called and the tradeName value is empty, then the return must be empty as well`() {
        doReturn(
            EstabelecimentoObj(
                ec = "7866099010",
                tradeName = "",
                cnpj = "33488798766"
            )
        ).whenever(menuPreference).getEstablishment()

        assertEquals("", presenter.fetchTradeName())
    }

    @Test
    fun `When fetchTradeName() is called and its value is CIA TESTE, the return must be CIA TESTE`() {
        assertEquals("CIA TESTE", presenter.fetchTradeName())
    }

    @Test
    fun `When fetchCnpj() is called and value is 33488798766, the return must be 33488798766`() {
        assertEquals("33488798766", presenter.fetchCnpj())
    }

  @Test
    fun `When fetchCnpj() is called and the CNPJ value is null, then the return must be empty`() {
        doReturn(
            EstabelecimentoObj(
                ec = "7866099010",
                tradeName = "CIA TESTE",
                cnpj = null
            )
        ).whenever(menuPreference).getEstablishment()

        assertEquals("", presenter.fetchCnpj())
    }

    @Test
    fun `When saveNewCpfToShowOnLogin() is called and the new CPF is not empty should store this information`() {
        presenter.saveNewCpfToShowOnLogin("34009765654")

        val cpfCaptor = argumentCaptor<String>()

        verify(presenter).saveNewCpfToShowOnLogin(cpfCaptor.capture())
        verify(presenter).storeCPF(cpfCaptor.capture())

        assertEquals("34009765654", cpfCaptor.firstValue)
        assertEquals("34009765654", cpfCaptor.secondValue)
    }

    @Test
    fun `When saveNewCpfToShowOnLogin() is called and CPF is null should do nothing`() {
        presenter.saveNewCpfToShowOnLogin(null)

        val cpfCaptor = argumentCaptor<String>()

        verify(presenter).saveNewCpfToShowOnLogin(cpfCaptor.capture())
        verify(presenter, never()).storeCPF(anyString())

        assertEquals(null, cpfCaptor.firstValue)
    }
}