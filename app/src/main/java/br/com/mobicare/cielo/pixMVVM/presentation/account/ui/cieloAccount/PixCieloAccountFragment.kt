package br.com.mobicare.cielo.pixMVVM.presentation.account.ui.cieloAccount

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.cielo.libflue.util.imageUtils.setCustomDrawable
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.spannable.htmlTextFormat
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationToken
import br.com.mobicare.cielo.databinding.FragmentPixCieloAccountBinding
import br.com.mobicare.cielo.pixMVVM.presentation.account.ui.PixAccountBaseFragment
import br.com.mobicare.cielo.pixMVVM.presentation.account.utils.PixProfileUiState
import br.com.mobicare.cielo.pixMVVM.presentation.account.utils.PixReceiptMethodUiState
import br.com.mobicare.cielo.pixMVVM.presentation.account.viewmodel.PixAccountChangeViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.account.viewmodel.PixReceiptMethodViewModel
import br.com.mobicare.cielo.pixMVVM.utils.PixConstants
import org.jetbrains.anko.browse
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PixCieloAccountFragment : PixAccountBaseFragment() {
    private val accountViewModel: PixReceiptMethodViewModel by sharedViewModel()
    private val accountChangeViewModel: PixAccountChangeViewModel by viewModel()

    private var _binding: FragmentPixCieloAccountBinding? = null
    private val binding get() = requireNotNull(_binding)

    override val toolbarTitle get() = getString(R.string.pix_account_cielo_account_title)

    override val footerButtonConfigurator get() =
        FooterButtonConfigurator(
            text = getString(R.string.pix_account_activate_modality),
            onTap = ::onActivateModalityTap,
            isEnabled = false,
        )

    private val onBoardingFulfillment get() = accountViewModel.onBoardingFulfillment

    private val onErrorCloseTap get() =
        object : HandlerViewBuilderFluiV2.HandlerViewListener {
            override fun onClick(dialog: Dialog?) = requireActivity().finish()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return FragmentPixCieloAccountBinding
            .inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        getOnBoardingFulfillment()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun getOnBoardingFulfillment() {
        if (accountViewModel.isSuccessState) {
            handleSuccessUIState()
        } else {
            accountViewModel.getOnBoardingFulfillment()
        }
    }

    private fun onActivateModalityTap() {
        getToken {
            accountChangeViewModel.changeProfile(token = it, settlementActive = false)
        }
    }

    private fun setupObservers() {
        setupObserverUIState()
        setupObserverProfileState()
    }

    private fun setupObserverUIState() {
        accountViewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PixReceiptMethodUiState.Loading -> handleLoadingState()
                is PixReceiptMethodUiState.Success -> handleSuccessUIState()
                is PixReceiptMethodUiState.Error -> handleErrorState()
            }
        }
    }

    private fun setupObserverProfileState() {
        accountChangeViewModel.profileState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PixProfileUiState.Success -> handleSuccessProfileState()
                is PixProfileUiState.Error -> showGenericErrorScreen()
            }
        }
    }

    private fun handleLoadingState() {
        navigation?.showAnimatedLoading()
    }

    private fun handleSuccessUIState() {
        navigation?.run {
            setupViews()
            setupDocument()
            setupTerms()

            showContent()
        }
    }

    private fun handleErrorState() {
        navigation?.showHandlerViewV2(
            title = getString(R.string.commons_generic_error_title),
            message = getString(R.string.commons_generic_error_message),
            illustration = R.drawable.ic_07,
            isShowBackButton = false,
            isShowIconButtonEndHeader = false,
            labelPrimaryButton = getString(R.string.text_close),
            onPrimaryButtonClickListener = onErrorCloseTap,
            onBackButtonClickListener = onErrorCloseTap,
            onIconButtonEndHeaderClickListener = onErrorCloseTap,
        )
    }

    private fun handleSuccessProfileState() {
        navigation?.hideAnimatedLoading()

        handlerValidationToken.playAnimationSuccess(
            callbackAnimationSuccess =
                object : HandlerValidationToken.CallbackAnimationSuccess {
                    override fun onSuccess() {
                        showSuccessScreen(
                            illustration = R.drawable.img_14_estrelas,
                            titleText = getString(R.string.pix_account_success_title),
                            messageText = getString(R.string.pix_account_cielo_account_success_message),
                            noteText =
                                getString(
                                    R.string.pix_account_success_note,
                                    onBoardingFulfillment?.documentType?.name,
                                    onBoardingFulfillment?.document,
                                ),
                            primaryButtonText = getString(R.string.text_close),
                            onPrimaryButtonClick = ::navigateToPixHome,
                        )
                    }
                },
        )
    }

    private fun setupDocument() {
        binding.tvDocument.text =
            getString(
                R.string.pix_account_cielo_account_document,
                onBoardingFulfillment?.documentType,
                onBoardingFulfillment?.document,
            ).htmlTextFormat()
    }

    private fun setupTerms() {
        binding.apply {
            btTerms.setOnClickListener(::onTermsTap)
            llAcceptTerms.setOnClickListener {
                cbAcceptTerms.isChecked = cbAcceptTerms.isChecked.not()
                footerButton.isButtonEnabled = cbAcceptTerms.isChecked
            }
        }
    }

    private fun setupViews() {
        binding.llTerms.setCustomDrawable {
            solidColor = R.color.cloud_100
            radius = R.dimen.dimen_8dp
        }
    }

    private fun onTermsTap(v: View) {
        requireActivity().browse(PixConstants.PIX_TERMS_OF_USE_URL)
    }
}
