package br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.offer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.bottomsheet.CieloMessageBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.util.extensions.onAccessibilityStart
import br.com.cielo.libflue.util.imageUtils.setCustomDrawable
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values
import br.com.mobicare.cielo.commons.bottomsheet.callhelpcenter.CallHelpCenterBottomSheet
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.processErrorMessage
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Offer
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OfferResponse
import br.com.mobicare.cielo.databinding.FragmentOfferTapOnPhoneBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.formatRate
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneAnalytics
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneGA4
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneAccount
import br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.offer.accountSelector.AccountSelectorBottomSheet
import br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.offer.accountSelector.AccountSelectorContract
import br.com.mobicare.cielo.tapOnPhone.utils.TapOnPhoneConstants.REFERENCE_TAP_ON_PHONE
import br.com.mobicare.cielo.tapOnPhone.utils.TapOnPhoneTransactionType
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class TapOnPhoneAccreditationOfferFragment : BaseFragment(), CieloNavigationListener,
    TapOnPhoneAccreditationOfferContract.View {

    private var accounts: List<TapOnPhoneAccount>? = null
    private var selectedAccount: TapOnPhoneAccount? = null
    private var tapOnPhoneOffer: OfferResponse? = null
    private var sessionId: String? = null
    private var navigation: CieloNavigation? = null

    private var binding: FragmentOfferTapOnPhoneBinding? = null

    private val presenter: TapOnPhoneAccreditationOfferPresenter by inject {
        parametersOf(this)
    }

    private val analytics: TapOnPhoneAnalytics by inject()
    private val ga4: TapOnPhoneGA4 by inject()

    private val getOfferTap get() = tapOnPhoneOffer?.offer?.products?.firstOrNull { it.reference == REFERENCE_TAP_ON_PHONE }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentOfferTapOnPhoneBinding.inflate(inflater, container, false)
        .also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        tapOnPhoneOffer?.let {
            setupView()
            return
        }
        presenter.getOfferData()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.showBackIcon()
            navigation?.showHelpButton()
            navigation?.showCloseButton()
            navigation?.setNavigationListener(this)
        }
    }

    override fun onLoadDataSuccess(
        sessionId: String,
        offer: OfferResponse,
        accounts: List<TapOnPhoneAccount>
    ) {
        analytics.logCallbackSuccess(flow = TapOnPhoneAnalytics.OFFER_LOAD)
        analytics.logScreenView(TapOnPhoneAnalytics.OFFER_SCREEN_PATH, javaClass)
        ga4.logScreenView(TapOnPhoneGA4.SCREEN_VIEW_OFFER)
        doWhenResumed {
            this.sessionId = sessionId
            this.tapOnPhoneOffer = offer
            this.accounts = accounts
            this.selectedAccount = accounts.firstOrNull()

            setupView()
        }
    }

    private fun setupView() {
        setupOffer()
        setupOfferRetryButton()
        setupAccountSelectorBS()
        setupAccount()
        setupAccountRetryButton()
        setupListeners()
    }

    override fun showError(error: ErrorMessage?) {
        doWhenResumed {
            analytics.logCallbackError(
                TapOnPhoneAnalytics.OFFER_LOAD,
                error?.errorMessage.orEmpty(),
                error?.code.orEmpty()
            )
            ga4.logException(
                screenName = TapOnPhoneGA4.SCREEN_VIEW_ACCREDITATION_OFFER_LOAD,
                errorMessage = error?.errorMessage.orEmpty(),
                errorCode = error?.code.orEmpty()
            )
            showAccountsError()
            showOfferError()
            navigation?.showAnimatedLoadingError(
                onAction = {
                    showErrorHandler(error)
                    analytics.logScreenView(
                        TapOnPhoneAnalytics.ACCREDITATION_OFFER_LOAD_ERROR_SCREEN_PATH,
                        javaClass
                    )
                })
        }
    }

    override fun onShowCallCenter(error: ErrorMessage) {
        doWhenResumed {
            analytics.logOrderRequestCallback(
                isError = true,
                errorMessage = error.errorMessage,
                errorCode = error.code
            )
            ga4.logException(
                screenName = TapOnPhoneGA4.SCREEN_VIEW_ACCREDITATION_OFFER_LOAD,
                errorMessage = error.errorMessage,
                errorCode = error.code
            )
            navigation?.showAnimatedLoadingError(
                onAction = {
                    analytics.logScreenView(
                        TapOnPhoneAnalytics.ACCREDITATION_CANNOT_PROCEED_SCREEN_PATH,
                        javaClass
                    )
                    navigation?.showCustomHandler(
                        contentImage = R.drawable.ic_90_celular_atencao,
                        titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                        title = getString(R.string.tap_on_phone_bs_required_data_field_title),
                        message = getString(R.string.tap_on_phone_bs_required_data_field_description),
                        isShowFirstButton = true,
                        isShowHeaderImage = false,
                        labelFirstButton = getString(R.string.tap_on_phone_bs_required_data_field_label_button_secondary),
                        labelSecondButton = getString(R.string.text_call_center_action),
                        firstButtonCallback = {
                            navigation?.goToHome()
                        },
                        secondButtonCallback = {
                            CallHelpCenterBottomSheet.newInstance().show(childFragmentManager, tag)
                            requireActivity().finish()
                        },
                        headerCallback = {
                            navigation?.goToHome()
                        },
                        finishCallback = {
                            navigation?.goToHome()
                        }
                    )
                })
        }
    }

    private fun showErrorHandler(error: ErrorMessage?) {
        doWhenResumed {
            navigation?.showCustomErrorHandler(
                title = getString(R.string.tap_on_phone_initialize_terminal_generic_error_title),
                error = processErrorMessage(
                    error,
                    getString(R.string.error_generic),
                    getString(R.string.tap_on_phone_initialize_terminal_generic_error_message)
                ),
                labelSecondButton = getString(R.string.entendi),
                secondButtonCallback = ::finishScreen,
                finishCallback = ::finishScreen,
                headerCallback = ::finishScreen,
                isBack = true
            )
        }
    }

    private fun finishScreen() {
        navigation?.goToHome()
    }

    override fun showLoading() {
        doWhenResumed {
            navigation?.showAnimatedLoading(R.string.tap_on_phone_loading_offer)
        }
    }

    override fun hideLoading() {
        doWhenResumed {
            navigation?.hideAnimatedLoading()
        }
    }

    override fun showLoadingOffers() {
        hideOfferError()
        binding?.apply {
            offer.feesContainer.gone()
            btnSeeMoreDetails.gone()
            tvReceivingObservation.gone()
            btnSeeMoreDetailsShimmer.visible()
            btnSeeMoreDetailsShimmer.startShimmer()
            offer.shimmerContainer.visible()
            offer.shimmerContainer.startShimmer()
            containerReceivingAgree.isEnabled = false
            btnContinue.isEnabled = false
        }
    }

    override fun hideLoadingOffers() {
        binding?.apply {
            btnSeeMoreDetailsShimmer.stopShimmer()
            btnSeeMoreDetailsShimmer.gone()
            offer.shimmerContainer.stopShimmer()
            offer.shimmerContainer.gone()
            offer.feesContainer.visible()
            btnSeeMoreDetails.visible()
            containerReceivingAgree.isEnabled = true
        }
    }

    private fun hideOfferError() {
        binding?.apply {
            offer.offerErrorGroup.gone()
            btnReloadOffer.gone()
            btnReloadOffer.isEnabled = false
        }
    }

    override fun showOfferError() {
        binding?.apply {
            offer.offerContentGroup.gone()
            btnSeeMoreDetails.gone()
            offer.offerErrorGroup.visible()
            btnReloadOffer.isEnabled = true
            btnReloadOffer.visible()
        }
    }

    override fun onShowOffer(offer: OfferResponse) {
        this.tapOnPhoneOffer = offer
        setupOffer()
    }

    override fun showLoadingAccounts() {
        hideAccountError()
        binding?.apply {
            btnAccountsShimmer.visible()
            btnAccountsShimmer.startShimmer()
            account.shimmerContainer.visible()
            account.shimmerContainer.startShimmer()
        }
    }

    private fun hideAccountError() {
        binding?.apply {
            account.accountErrorGroup.gone()
            btnAccountsReload.gone()
            btnAccountsReload.isEnabled = false
        }
    }

    override fun hideLoadingAccounts() {
        binding?.apply {
            account.shimmerContainer.stopShimmer()
            account.shimmerContainer.gone()
            btnAccountsShimmer.stopShimmer()
            btnAccountsShimmer.gone()
        }
    }

    override fun onGetSessionIdSuccess(sessionId: String) {
        this.sessionId = sessionId
    }

    override fun onShowAccounts(accounts: List<TapOnPhoneAccount>) {
        this.accounts = accounts
        this.selectedAccount = accounts.first()
        setupAccount()
    }

    private fun showAccountsError() {
        binding?.apply {
            account.accountErrorGroup.visible()
            btnAccountsReload.isEnabled = true
            btnAccountsReload.visible()
        }
    }

    private fun setupAccountRetryButton() {
        binding?.btnAccountsReload?.setOnClickListener {
            presenter.getOfferData()
        }
    }

    private fun setupOffer() {
        binding?.apply {
            tapOnPhoneOffer?.let {
                val visaRates = getOfferTap?.brands?.firstOrNull { brand ->
                    brand.code == VISA_BRAND_CODE
                }?.conditions

                val debit = visaRates?.firstOrNull { brandRates ->
                    brandRates.type == TapOnPhoneTransactionType.DEBIT.name
                }?.flexibleTermPaymentMDR

                val credit = visaRates?.firstOrNull { brandRates ->
                    brandRates.type == TapOnPhoneTransactionType.CREDIT_IN_CASH.name
                }?.flexibleTermPaymentMDR

                val term = getOfferTap?.settlementTerm?.let { timing ->
                    resources.getQuantityString(
                        R.plurals.tap_one_phone_term_working_days,
                        timing,
                        timing
                    )
                }.orEmpty()

                offer.apply {
                    tvDebitTax.text = debit?.formatRate()
                    tvCreditTax.text = credit?.formatRate()
                    tvTerm.text = term
                    offerContentGroup.visible()
                }

                btnSeeMoreDetails.apply {
                    visible()
                    isEnabled = true
                }

                btnContinue.isEnabled = true

                groupAutomaticReceiptOptional.visible(presenter.isEnabledAutomaticReceiptOptional())

                setupReceivingSection(it.offer)
                setupReceivingObservation(it.offer)
            }
        }
    }

    private fun setupReceivingSection(offer: Offer?) {
        binding?.apply {
            tvReceivingTitle.text = getString(R.string.tap_on_phone_receiving_title)
            containerReceivingAgree.contentDescription =
                getString(R.string.tap_on_phone_receiving_content_description_checked)
        }
    }

    private fun setupReceivingObservation(offer: Offer?) {
        binding?.tvReceivingObservation?.apply {
            offer?.settlementTerm?.let { timing ->
                text = resources.getString(R.string.tap_on_phone_receiving_observation, timing)
            }
        }
    }

    private fun setupOfferRetryButton() {
        binding?.apply {
            btnReloadOffer.setOnClickListener {
                presenter.reloadOffer(cbReceivingAgree.isChecked)
            }
        }
    }

    private fun setupListeners() {
        binding?.apply {
            btnContinue.setOnClickListener(::onClickContinue)
            containerReceivingAgree.setOnClickListener(::onReceivingAgreeClick)
            containerReceivingAgree.onAccessibilityStart(::onReceivingAgreeAccessibilityStart)
        }
        setupMoreDetailsListener()
    }

    private fun onReceivingAgreeAccessibilityStart(info: AccessibilityNodeInfoCompat) {
        info.contentDescription = getString(
            if (binding?.cbReceivingAgree?.isChecked == true)
                R.string.tap_on_phone_receiving_content_description_checked
            else
                R.string.tap_on_phone_receiving_content_description_unchecked,
        )
    }

    private fun onReceivingAgreeClick(v: View) {
        binding?.apply {
            cbReceivingAgree.isChecked.not().let { isChecked ->
                cbReceivingAgree.isChecked = isChecked
                presenter.reloadOffer(isChecked)
            }
        }
    }

    private fun onClickContinue(v: View) {
        val labelBtnContinue = binding?.btnContinue?.getText() ?: EMPTY

        analytics.logScreenActions(
            flowName = TapOnPhoneAnalytics.OFFER,
            labelName = labelBtnContinue
        )
        ga4.logAccreditationAddPaymentInfo(
            screenName = TapOnPhoneGA4.SCREEN_VIEW_OFFER,
            bankName = selectedAccount?.bankName.orEmpty()
        )

        if (presenter.isShowBSAlertDeviceIncompatibility(requireActivity()))
            showBSAlertDeviceIncompatibility()
        else
            navigateToTermAndCondition()
    }

    private fun showBSAlertDeviceIncompatibility() {
        ga4.logException(
            screenName = TapOnPhoneGA4.SCREEN_VIEW_OFFER,
            errorMessage = TapOnPhoneGA4.YOUR_PHONE_IS_NOT_COMPATIBLE,
            errorCode = EMPTY
        )
        CieloMessageBottomSheet.create(
            CieloBottomSheet.HeaderConfigurator(
                title = getString(R.string.tap_on_phone_title_bs_alert_device_compatibility),
                showCloseButton = true
            ),
            CieloMessageBottomSheet.Message(
                text = getString(R.string.tap_on_phone_message_bs_alert_device_compatibility)
            ),
            CieloBottomSheet.ButtonConfigurator(
                title = getString(R.string.entendi),
                onTap = { bs ->
                    navigateToTermAndCondition()
                    bs.dismiss()
                }
            )
        ).show(childFragmentManager, tag)
    }

    private fun navigateToTermAndCondition() {
        val sessionId = sessionId ?: return
        val offer = tapOnPhoneOffer ?: return
        val account = selectedAccount ?: return

        doWhenResumed {
            findNavController().safeNavigate(
                TapOnPhoneAccreditationOfferFragmentDirections.actionTapOnPhoneAccreditationOfferFragmentToTapOnPhoneTermAndConditionFragment(
                    offer, account, sessionId
                )
            )
        }
    }

    private fun setupMoreDetailsListener() {
        binding?.btnSeeMoreDetails?.setOnClickListener {
            analytics.logScreenActions(
                flowName = TapOnPhoneAnalytics.OFFER,
                labelName = binding?.btnSeeMoreDetails?.text.toString()
            )
            ga4.logClick(
                screenName = TapOnPhoneGA4.SCREEN_VIEW_OFFER,
                contentType = GoogleAnalytics4Values.LINK,
                contentName = binding?.btnSeeMoreDetails?.text.toString()
            )
            findNavController().navigate(
                TapOnPhoneAccreditationOfferFragmentDirections.actionTapOnPhoneAccreditationOfferFragmentToTapOnPhoneInstallmentFeesFragment(
                    getOfferTap?.brands?.toTypedArray() ?: arrayOf(),
                    getOfferTap?.settlementTerm ?: ZERO
                )
            )
        }
    }

    private fun setupAccountSelectorBS() {
        binding?.apply {
            btnAccounts.setOnClickListener {
                accounts?.let {
                    AccountSelectorBottomSheet.create(
                        it,
                        selectedAccount,
                        object : AccountSelectorContract.Result {
                            override fun onAccountConfirm(account: TapOnPhoneAccount) {
                                ga4.logClick(
                                    screenName = TapOnPhoneGA4.SCREEN_VIEW_OFFER,
                                    contentName = TapOnPhoneGA4.CONFIRM,
                                    contentComponent = account.bankName
                                )
                                selectedAccount = account
                                setupAccount()
                            }

                        }).show(childFragmentManager, null)

                    analytics.logScreenActions(
                        flowName = TapOnPhoneAnalytics.OFFER,
                        labelName = btnAccounts.text.toString()
                    )
                    ga4.logClick(
                        screenName = TapOnPhoneGA4.SCREEN_VIEW_OFFER,
                        contentType = GoogleAnalytics4Values.LINK,
                        contentName = btnAccounts.text.toString()
                    )
                }
            }
        }
    }

    private fun setupAccount() {
        binding?.account?.apply {
            selectedAccount?.let {
                tvBankName.text = it.bankName
                ImageUtils.loadImage(
                    bankIcon,
                    it.imgSource,
                    R.drawable.ic_generic_brand
                )
                tvBankBranchNumber.text = it.agency
                tvBankAccountNumber.text = ACCOUNT_FORMAT.format(it.account, it.accountDigit)

                accountContentGroup.visible()
                binding?.btnAccounts.visible()
                binding?.btnAccounts?.isEnabled = true

                binding?.btnContinue?.isEnabled = true
            } ?: run {
                accounts?.let {
                    selectedAccount = accounts?.first()
                } ?: presenter.getOfferData()

            }
        }
    }

    override fun onBackButtonClicked(): Boolean {
        analytics.logScreenActions(
            flowName = TapOnPhoneAnalytics.OFFER,
            labelName = Action.VOLTAR
        )
        return super.onBackButtonClicked()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    private companion object {
        const val ACCOUNT_FORMAT = "%s-%s"
        const val VISA_BRAND_CODE = "1"
    }

}