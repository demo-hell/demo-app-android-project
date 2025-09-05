package br.com.mobicare.cielo.turboRegistration.presentation.registration

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ONE
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.viewBinding
import br.com.mobicare.cielo.databinding.FragmentRegistrationBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.turboRegistration.RegistrationUpdateViewModel
import br.com.mobicare.cielo.turboRegistration.analytics.TurboRegistrationAnalytics
import br.com.mobicare.cielo.turboRegistration.utils.RegistrationResource
import br.com.mobicare.cielo.turboRegistration.utils.RegistrationStepError
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegistrationFragment : BaseFragment(), CieloNavigationListener {

    private val binding: FragmentRegistrationBinding by viewBinding()
    private val viewModel: RegistrationUpdateViewModel by activityViewModels()
    private lateinit var navigation: CieloNavigation
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doWhenResumed {
            TurboRegistrationAnalytics.screenViewSelfRegistrationLoading()
        }
        setupNavigation()
        makeScreen()
        viewModel.updateFromStep(UserPreferences.getInstance().turboRegistrationErrorStep)
        addObserver()
    }

    private fun addObserver() {
        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is RegistrationResource.Success -> onResultSuccess()
                is RegistrationResource.Error -> onResultError(result.error)
                else -> {}
            }
        }
    }

    private fun onResultSuccess() {
        val resultIntent = android.content.Intent()
        doWhenResumed {
            TurboRegistrationAnalytics.screenViewSelfRegistrationDone()
        }
        requireActivity().setResult(Activity.RESULT_OK, resultIntent)
        requireActivity().finish()
    }

    private fun onResultError(cieloAPIException: CieloAPIException) {
        TurboRegistrationAnalytics.exceptionSelfRegistrationError(
            cieloAPIException.newErrorMessage.message,
            cieloAPIException.newErrorMessage.httpCode
        )
        findNavController().safeNavigate(RegistrationFragmentDirections.actionNavRegistrationToNavError(cieloAPIException.newErrorMessage.message))
    }

    private fun makeScreen() {
        binding.lottieAnimation.apply {
            setAnimation(ANIMATION_LOADING_PATH)
            playAnimation()
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation.setNavigationListener(this)
            navigation.setupToolbar(title = EMPTY, isCollapsed = false, subtitle = EMPTY)
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.updateStep.collectLatest { step ->
                    val isStepFinalAndIsLegalEntity = step == RegistrationStepError.BANK.ordinal && UserPreferences.getInstance().isLegalEntity
                    navigation.onStepChanged(if (isStepFinalAndIsLegalEntity) step - ONE else step, true)
                    binding.tvWait.text = getString(R.string.sending_data)
                }
            }
            navigation.showBackButton(isShow = false)
        }
    }

    companion object {
        private const val ANIMATION_LOADING_PATH = "lottie/loading_fundo_branco.json"
    }
}