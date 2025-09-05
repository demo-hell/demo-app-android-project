package br.com.mobicare.cielo.idOnboarding.updateUser.p1Policy.benefits

import androidx.annotation.VisibleForTesting
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.removeNonNumbers
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.pix.constants.EMPTY

class IDOnboardingBenefitsInfoP1PolicyPresenter(
    private val menuPreference: MenuPreference,
    private val userPreferences: UserPreferences,
) : IDOnboardingBenefitsInfoP1PolicyContract.Presenter {

    override fun fetchTradeName() = menuPreference.getEstablishment()?.tradeName ?: EMPTY

    override fun fetchCnpj() = menuPreference.getEstablishment()?.cnpj ?: EMPTY

    override fun saveNewCpfToShowOnLogin(newCpf: String?) {
        newCpf?.let { storeCPF(it) }
    }

    @VisibleForTesting
    fun storeCPF(cpf: String) {
        userPreferences.apply {
            keepUserName(cpf.removeNonNumbers())
            keepLogin(true, EMPTY, cpf)
            isStepTwo(true)
        }
    }
}

