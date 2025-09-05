package br.com.mobicare.cielo.posVirtual.presentation.router

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.HelpCenter.PHONE_CALL_CENTER
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.registerForActivityResultCustom
import br.com.mobicare.cielo.component.impersonate.data.model.response.MerchantResponse
import br.com.mobicare.cielo.component.impersonate.presentation.ImpersonateNavigationFlowActivity
import br.com.mobicare.cielo.component.impersonate.presentation.model.ImpersonateUI
import br.com.mobicare.cielo.databinding.FragmentPosVirtualRouterBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.posVirtual.domain.model.PosVirtual
import br.com.mobicare.cielo.posVirtual.presentation.router.views.PosVirtualCanceledHandlerView
import br.com.mobicare.cielo.posVirtual.utils.UIPosVirtualRouterState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PosVirtualRouterFragment : BaseFragment(), CieloNavigationListener {

    private val viewModel: PosVirtualRouterViewModel by viewModel()

    private var binding: FragmentPosVirtualRouterBinding? = null
    private var navigation: CieloNavigation? = null
    private var impersonateActivityResult: ActivityResultLauncher<Intent>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPosVirtualRouterBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupObserver()
        registerImpersonateActivity()
        verifyEligibility()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun verifyEligibility() = viewModel.getEligibility(requireContext())

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.apply {
                configureCollapsingToolbar(
                    CollapsingToolbarBaseActivity.Configurator(
                        show = false
                    )
                )
                showButton(false)
            }
        }
    }

    private fun registerImpersonateActivity() {
        impersonateActivityResult = registerForActivityResultCustom(::onImpersonateResult)
    }

    private fun setupObserver() {
        viewModel.routerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIPosVirtualRouterState.Loading -> showLoading(state.isLoading)
                is UIPosVirtualRouterState.Success -> handleSuccessState(state)
                is UIPosVirtualRouterState.Error -> handleErrorState(state)
                else -> showGenericErrorMessage()
            }
        }
    }

    private fun handleSuccessState(state: UIPosVirtualRouterState.Success) {
        when (state) {
            is UIPosVirtualRouterState.StatusPending -> showStatusPendingMessage()
            is UIPosVirtualRouterState.StatusCanceled -> showStatusCanceledMessage()
            is UIPosVirtualRouterState.StatusFailed -> showStatusFailedMessage()
            is UIPosVirtualRouterState.StatusSuccess -> navigateToHome(state.data)
            is UIPosVirtualRouterState.ImpersonateRequired -> performImpersonate(state.data.merchantId)
        }
    }

    private fun handleErrorState(state: UIPosVirtualRouterState.Error) {
        when (state) {
            is UIPosVirtualRouterState.AccreditationRequired -> navigateToAccreditation()
            is UIPosVirtualRouterState.OnBoardingRequired -> navigateToOnBoarding()
            is UIPosVirtualRouterState.GenericError -> showGenericErrorMessage(state.message)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.loadingViewFlui?.apply {
            if (isLoading)
                startAnimation(R.string.wait_a_moment)
            else
                hideAnimationStart()
        }
    }

    private fun navigateToAccreditation() {
        doWhenResumed {
            findNavController().navigate(
                PosVirtualRouterFragmentDirections
                    .actionPosVirtualRouterToPosVirtualAccreditation()
            )
        }
    }

    private fun navigateToOnBoarding() {
        doWhenResumed {
            findNavController().navigate(
                PosVirtualRouterFragmentDirections.actionPosVirtualRouterToPosVirtualOnboarding()
            )
        }
    }

    private fun navigateToHome(data: PosVirtual) {
        doWhenResumed {
            data.products?.let { products ->
                findNavController().navigate(
                    PosVirtualRouterFragmentDirections.actionPosVirtualRouterToPosVirtualHome(
                        products.toTypedArray(),
                        data.merchantId.orEmpty()
                    )
                )
            } ?: showGenericErrorMessage()
        }
    }

    private fun performImpersonate(merchantId: String?) {
        impersonateActivityResult?.launch(
            ImpersonateNavigationFlowActivity.launch(
                requireContext(),
                args = ImpersonateUI(
                    subTitle = getString(R.string.pos_virtual_impersonating_subtitle),
                    merchants = listOf(
                        MerchantResponse(id = merchantId)
                    )
                )
            )
        )
    }

    private fun onImpersonateResult(result: ActivityResult) {
        val data = viewModel.data

        if (data != null && result.resultCode == Activity.RESULT_OK) {
            navigateToHome(data)
        } else {
            requireActivity().finish()
        }
    }

    private fun showStatusPendingMessage() {
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.img_44_aguarde,
            title = getString(R.string.pos_virtual_eligibility_pending_title),
            message = getString(R.string.pos_virtual_eligibility_pending_message),
            labelSecondButton = getString(R.string.go_to_initial_screen),
            callbackSecondButton = ::finish,
            callbackBack = ::finish
        )
    }

    private fun showStatusCanceledMessage() {
        PosVirtualCanceledHandlerView(requireContext())
            .invoke(
                onMakeCallClick = ::makeCall,
                onFinishClick = ::finish
            )
            .show(requireActivity().supportFragmentManager, javaClass.name)
    }

    private fun showStatusFailedMessage() {
        // TODO: A ser definido
        showGenericErrorMessage()
    }

    private fun showGenericErrorMessage(message: String? = null) {
        navigation?.showCustomHandlerView(
            title = getString(R.string.commons_generic_error_title),
            message = message ?: getString(R.string.commons_generic_error_message),
            labelFirstButton = getString(R.string.back),
            labelSecondButton = getString(R.string.text_try_again_label),
            isShowFirstButton = true,
            isShowButtonClose = true,
            callbackSecondButton = ::verifyEligibility,
            callbackFirstButton = ::finish,
            callbackClose = ::finish,
            callbackBack = ::finish
        )
    }

    private fun makeCall() = requireActivity().apply {
        Utils.callPhone(
            activity = this,
            phone = PHONE_CALL_CENTER,
            skipRationaleRequest = false
        )
    }

    private fun finish() = requireActivity().finish()

}