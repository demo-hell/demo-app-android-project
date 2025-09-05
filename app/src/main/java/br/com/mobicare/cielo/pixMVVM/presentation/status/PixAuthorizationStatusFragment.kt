package br.com.mobicare.cielo.pixMVVM.presentation.status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.commons.utils.convertToBrDateFormat
import br.com.mobicare.cielo.commons.utils.getNewErrorMessage
import br.com.mobicare.cielo.databinding.FragmentPixAuthorizationStatusBinding
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.pix.constants.*
import org.jetbrains.anko.browse
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDateTime

class PixAuthorizationStatusFragment : BaseFragment(), CieloNavigationListener {

    private val viewModel: PixAuthorizationStatusViewModel by viewModel()

    private var binding: FragmentPixAuthorizationStatusBinding? = null
    private var navigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentPixAuthorizationStatusBinding.inflate(
        inflater, container, false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupObserver()
        setupListener()

        getPixAuthorizationStatus()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onBackButtonClicked(): Boolean {
        requireActivity().finish()
        return super.onBackButtonClicked()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = (requireActivity() as CieloNavigation).also {
                it.configureCollapsingToolbar(
                    CollapsingToolbarBaseActivity.Configurator(
                        show = false
                    )
                )
            }
        }
    }

    private fun setupObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PixAuthorizationStatusUiState.Loading -> handleLoading()
                is PixAuthorizationStatusUiState.Success -> handleSuccess(state)
                is PixAuthorizationStatusUiState.Error -> handleError()
            }
        }
    }

    private fun setupListener() {
        binding?.linkContractAuthorization?.setOnClickListener {
            requireActivity().browse(PIX_USAGE_TERMS_URL)
        }
    }

    private fun getPixAuthorizationStatus() = viewModel.getPixAuthorizationStatus()

    private fun handleLoading() {
        navigation?.showAnimatedLoading()
    }

    private fun handleSuccess(state: PixAuthorizationStatusUiState.Success) {
        configureToolbarTitle()
        configureDate(state.data.beginTime)

        when (state) {
            is PixAuthorizationStatusUiState.Active -> configureActiveStatus()
            is PixAuthorizationStatusUiState.WaitingActivation -> configureWaitingActivationStatus()
            is PixAuthorizationStatusUiState.Pending -> configurePendingStatus()
        }
    }

    private fun configureToolbarTitle() {
        navigation?.run {
            hideAnimatedLoading()
            configureCollapsingToolbar(
                CollapsingToolbarBaseActivity.Configurator(
                    show = true,
                    toolbarTitle = getString(R.string.pix_authorization_status_title),
                    toolbarTitleAppearance = CollapsingToolbarBaseActivity.ToolbarTitleAppearance(
                        collapsed = R.style.CollapsingToolbar_Collapsed_BlackBold,
                        expanded = R.style.CollapsingToolbar_Expanded_BlackBold
                    )
                )
            )
        }
    }

    private fun configureDate(beginTime: LocalDateTime?) {
        binding?.tvDateValue?.text = beginTime?.convertToBrDateFormat()
    }

    private fun configureActiveStatus() {
        binding?.apply {
            tvStatusValue.text = getString(R.string.pix_status_active)
            ivStatus.setImageResource(R.drawable.ic_check_rounded)
        }
    }

    private fun configureWaitingActivationStatus() {
        binding?.tvStatusValue?.text = getString(R.string.pix_status_waiting_activation)
    }

    private fun configurePendingStatus() {
        binding?.tvStatusValue?.text = getString(R.string.pix_status_pending)
    }

    private fun handleError() {
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.img_10_erro,
            title = getString(R.string.commons_generic_error_title),
            message = requireContext().getNewErrorMessage(
                newMessage = R.string.commons_generic_error_message
            ),
            labelSecondButton = getString(R.string.text_try_again_label),
            labelFirstButton = getString(R.string.back),
            isShowButtonClose = true,
            isShowSecondButton = true,
            titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
            messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
            titleStyle = R.style.bold_montserrat_20_cloud_600_spacing_8,
            callbackClose = ::navigateToHome,
            callbackSecondButton = ::getPixAuthorizationStatus,
        )
    }

    private fun navigateToHome() {
        requireActivity().backToHome()
    }

}