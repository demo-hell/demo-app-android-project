package br.com.mobicare.cielo.mfa.primeiroAcesso

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment

class PrimeiroAcessoMfaFragment : BaseFragment(), CieloNavigationListener {

    private var cieloNavigation: CieloNavigation? = null
    var title: String? = null
    var passOrAt: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_primeiro_acesso_mfa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //ga verification
        UserPreferences.getInstance().saveKeepStatusMfa(true)
         UserPreferences.getInstance().keepStatusMfa?.let { stMfa->
                 title = if (stMfa) MFA_NOVO_TOKEN else MFA_NOVO_TOKEN_TROCA
         }

        validationShowOnboarding()
        configureNavigation()
    }

    fun validationShowOnboarding() {
        val isToShowMfaOnboarding = UserPreferences.getInstance()
            .isToShowOnboarding(UserPreferences.ONBOARDING.MFA)

        passOrAt = if(isToShowMfaOnboarding) MFA_PASSIVO else MFA_ATIVO
        gaOnboard()

    }

    override fun onBackButtonClicked(): Boolean {
        requireActivity().finish()
        return false
    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.setTextToolbar("Token")
            this.cieloNavigation?.setTextButton("Configurar")
            this.cieloNavigation?.showButton(true)
            this.cieloNavigation?.enableButton(true)
            this.cieloNavigation?.showHelpButton(false)
            this.cieloNavigation?.setNavigationListener(this)
        }
    }

    override fun onButtonClicked(labelButton: String) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.primeiroAcessoMfaFragment, true)
            .build()
        findNavController()
            .navigate(
                PrimeiroAcessoMfaFragmentDirections
                    .actionPrimeiroAcessoMfaFragmentToSelecioneBancoMfaFragment(),
                navOptions
            )
        gaOnboardButtonConfigurar()
    }

    //ga
    fun gaOnboard() {
        passOrAt?.let { st ->
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, MFA_NOVO_TOKEN),
                action = listOf(Action.ONBOARDING, st),
                label = listOf(MFA_VISUALIZAR_ONBOARDING)
            )
        }
    }

    fun gaOnboardButtonConfigurar() {
        passOrAt?.let { st ->
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, MFA_NOVO_TOKEN),
                action = listOf(Action.ONBOARDING, st, Action.CLIQUE, Action.BOTAO),
                label = listOf(MFA_CONFIGURACAO)
            )
        }

    }
    //end ga
}