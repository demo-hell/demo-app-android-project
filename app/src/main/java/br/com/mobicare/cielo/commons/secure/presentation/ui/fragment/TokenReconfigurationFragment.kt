package br.com.mobicare.cielo.commons.secure.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics
import kotlinx.android.synthetic.main.fragment_token_reconfiguration.*

class TokenReconfigurationFragment : BaseFragment(), CieloNavigationListener {

    private var cieloNavigation: CieloNavigation? = null
    var title: String? = null
    var passOrAt: String? = MFA_ATIVO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(
        R.layout.fragment_token_reconfiguration,
        container, false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //ga verification
        UserPreferences.getInstance().saveKeepStatusMfa(false)
        UserPreferences.getInstance().keepStatusMfa?.let { stMfa->
                title = if (stMfa) MFA_NOVO_TOKEN else MFA_NOVO_TOKEN_TROCA
        }
        configureNavigation()
        configureListeners()
        validationShowOnboarding()
        gaOnboard()
    }

    fun validationShowOnboarding() {
        val isToShowMfaOnboarding = UserPreferences.getInstance()
            .isToShowOnboarding(UserPreferences.ONBOARDING.MFA)

        passOrAt = if(isToShowMfaOnboarding) MFA_PASSIVO else MFA_ATIVO

    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.setTextToolbar(getString(R.string.text_token))
            this.cieloNavigation?.showButton(false)
            this.cieloNavigation?.showHelpButton(false)
            this.cieloNavigation?.setNavigationListener(this)
            this.cieloNavigation?.enableButton(false)
            this.cieloNavigation?.setNavigationListener(this)
        }
    }

    private fun configureListeners() {
        this.alertCieloViewReconfigurationToken?.configureActionClickListener {
            gaOnboardButtonConfigurar()
            TokenReconfigurationOptionBottomSheet()
                .setListener(object : TokenReconfigurationOptionBottomSheet.Listener {
                    override fun onChoice(hasMobilePhone: Boolean) {
                        when (hasMobilePhone) {
                            false -> goToBankSelect()
                            true -> goToValidationPreviousToken()
                        }
                    }
                })
                .show(this.childFragmentManager, "TokenReconfigurationOptionBottomSheet")
        }
    }

    private fun goToBankSelect() {
        findNavController()
            .navigate(TokenReconfigurationFragmentDirections.actionTokenReconfigurationFragmentToSelecioneBancoMfaFragment())
    }

    private fun goToValidationPreviousToken() {
        findNavController()
            .navigate(TokenReconfigurationFragmentDirections.actionTokenReconfigurationFragmentToValidationPreviousTokenFragment())
    }

    override fun onBackButtonClicked() : Boolean {
        requireActivity().finish()
        return false
    }

    //ga
    fun gaOnboard() {
        title?.let { t ->
            passOrAt?.let { st ->
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, t),
                    action = listOf(Action.ONBOARDING, st),
                    label = listOf(MFA_VISUALIZAR_ONBOARDING)
                )

                Analytics.GoogleAnalytics4Tracking.trackEvent(
                    eventName = ScreenView.SCREEN_VIEW_EVENT,
                    isLoginOrImpersonateFlow = true,
                    eventsMap = mapOf(
                        ScreenView.SCREEN_NAME to MFA_SCREEN_VIEW,
                        Navigation.FIREBASE_SCREEN to this.javaClass.simpleName.toLowerCasePTBR(),
                    )
                )
            }
        }
    }

    fun gaOnboardButtonConfigurar() {
        title?.let { t ->
            passOrAt?.let { st ->
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, t),
                    action = listOf(Action.ONBOARDING, st, Action.CLIQUE, Action.BOTAO),
                    label = listOf(MFA_CONFIGURACAO)
                )

                Analytics.GoogleAnalytics4Tracking.trackEvent(
                    eventName = Click.CLICK_EVENT,
                    eventsMap = mapOf(
                        ScreenView.SCREEN_NAME to MFA_SCREEN_VIEW,
                        Navigation.CONTENT_COMPONENT to MFA_CONFIGURE_O_TOKEN,
                        Navigation.CONTENT_TYPE to HomeAnalytics.BUTTON,
                        Navigation.CONTENT_NAME to MFA_CONFIGURACAO,
                        Navigation.FIREBASE_SCREEN to this.javaClass.simpleName.toLowerCasePTBR(),
                    )
                )
            }
        }

    }

    //end ga

}