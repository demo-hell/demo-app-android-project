package br.com.mobicare.cielo.arv.presentation.router

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.utils.ArvConstants.ARV_ANTICIPATION
import br.com.mobicare.cielo.arv.utils.UiArvRouterState
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentArvRouterBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.safeNavigate
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArvRouterFragment : BaseFragment(), CieloNavigationListener {
    private val viewModel: ArvRouterViewModel by viewModel()
    private var binding: FragmentArvRouterBinding? = null

    private var navigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentArvRouterBinding.inflate(inflater, container, false)
        .also {
            binding = it
        }.root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        getInformation()
        setupObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.showHelpButton(isShow = false)
            navigation?.setupToolbar(isCollapsed = false)
            navigation?.showContainerButton(isShow = false)
        }
    }

    private fun getInformation() {
        viewModel.handleInitialFlow(getArvAnticipation())
    }

    private fun setupObserver() {
        viewModel.arvRouterLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiArvRouterState.ShowOnboarding -> onShowOnboarding()
                is UiArvRouterState.ShowHome -> onShowHome()
                is UiArvRouterState.ShowArvSingleAnticipation -> onShowArvSingleAnticipation(uiState.arvAnticipation)
                is UiArvRouterState.ShowUnavailableService -> onShowUnavailableService()
            }
        }
    }

    private fun getArvAnticipation(): ArvAnticipation? {
        val savedBundle = navigation?.getSavedData()
        return savedBundle?.getParcelable<ArvAnticipation>(ARV_ANTICIPATION)?.copy(isFromCardHomeFlow = true)
    }

    private fun onShowOnboarding() {
        findNavController().safeNavigate(
            ArvRouterFragmentDirections
                .actionArvRouterFragmentToArvOnboardingFragment(),
        )
    }

    private fun onShowHome() {
        findNavController().safeNavigate(
            ArvRouterFragmentDirections
                .actionArvRouterFragmentToArvHomeFragment(),
        )
    }

    private fun onShowArvSingleAnticipation(arvAnticipation: ArvAnticipation) {
        findNavController().safeNavigate(
            ArvRouterFragmentDirections.actionArvRouterFragmentToArvSingleAnticipationFragment(arvAnticipation, null),
        )
    }

    private fun onShowUnavailableService() {
        doWhenResumed {
            navigation?.showCustomHandlerViewWithHelp(
                contentImage = R.drawable.img_maintenance_mfa,
                title = getString(R.string.anticipation_Unavailable_service_title),
                message = getString(R.string.anticipation_Unavailable_service_message),
                labelSecondButton = getString(R.string.go_to_initial_screen),
                callbackSecondButton = {
                    navigation?.goToHome()
                },
            )
        }
    }
}
