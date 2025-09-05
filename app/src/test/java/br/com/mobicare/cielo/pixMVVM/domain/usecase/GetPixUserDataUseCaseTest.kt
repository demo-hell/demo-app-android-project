package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.login.domains.entities.EstabelecimentoObj
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.pixMVVM.presentation.home.utils.UserDataUiResult
import com.google.common.truth.Truth.assertThat
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class GetPixUserDataUseCaseTest {
    private val menuPreference = mockk<MenuPreference>()

    private val establishment = EstabelecimentoObj(
        ec = "1234567",
        tradeName = "Lojinha do Pix",
        cnpj = "80.497.050/0001-65"
    )
    private val getPixUserDataUseCase = GetPixUserDataUseCase(menuPreference)

    @Test
    fun `it should call method getEstablishment of menuPreference only once`() {
        // given
        every { menuPreference.getEstablishment() } returns establishment

        // when
        getPixUserDataUseCase()

        // then
        verify(exactly = 1) { menuPreference.getEstablishment() }
    }

    @Test
    fun `it should return WithMerchantAndDocument result when merchant and document are not empty`() {
        // given
        every { menuPreference.getEstablishment() } returns establishment

        // when
        val result = getPixUserDataUseCase()

        // then
        assertThat(result).isInstanceOf(UserDataUiResult.WithMerchantAndDocument::class.java)
    }

    @Test
    fun `it should return WithMerchant result when merchant is not empty and document is empty`() {
        // given
        every { menuPreference.getEstablishment() } returns establishment.copy(cnpj = "")

        // when
        val result = getPixUserDataUseCase()

        // then
        assertThat(result).isInstanceOf(UserDataUiResult.WithMerchant::class.java)
    }

    @Test
    fun `it should return WithDocument result when merchant is empty and document is not empty`() {
        // given
        every { menuPreference.getEstablishment() } returns establishment.copy(ec = "")

        // when
        val result = getPixUserDataUseCase()

        // then
        assertThat(result).isInstanceOf(UserDataUiResult.WithDocument::class.java)
    }

    @Test
    fun `it should return WithOnlyOptionalUserName result when merchant and document are empty`() {
        // given
        every { menuPreference.getEstablishment() } returns establishment.copy(ec = "", cnpj = "")

        // when
        val result = getPixUserDataUseCase()

        // then
        assertThat(result).isInstanceOf(UserDataUiResult.WithOnlyOptionalUserName::class.java)
    }

    private fun EstabelecimentoObj.copy(ec: String? = null, cnpj: String? = null) =
        EstabelecimentoObj(
            ec = ec ?: this.ec,
            cnpj = cnpj ?: this.cnpj,
            tradeName = this.tradeName,
            hierarchyLevel = this.hierarchyLevel,
            hierarchyLevelDescription = this.hierarchyLevelDescription
        )
}