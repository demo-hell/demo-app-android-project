package br.com.mobicare.cielo.pix.ui.home.account

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.BrandCardHelper
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.processErrorMessage
import br.com.mobicare.cielo.databinding.FragmentFreeMovementAccountManagementBinding
import br.com.mobicare.cielo.mfa.router.MfaRouteHandler
import br.com.mobicare.cielo.pix.constants.*
import br.com.mobicare.cielo.pix.domain.PixMerchantResponse
import br.com.mobicare.cielo.pix.ui.extract.PixExtractNavigationFlowActivity
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

const val PATH_DEFAULT = "-1"

class PixFreeMovementAccountManagementFragment : BaseFragment(), CieloNavigationListener,
    PixFreeMovementAccountManagementContract.View {

    private val presenter: PixFreeMovementAccountManagementPresenter by inject {
        parametersOf(this)
    }
    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }
    private val mfaRouteHandler: MfaRouteHandler by inject {
        parametersOf(activity ?: requireActivity())
    }
    private val balance: Double? by lazy {
        arguments?.getDouble(PIX_BALANCE_ARGS)
    }

    private var navigation: CieloNavigation? = null

    private var _binding: FragmentFreeMovementAccountManagementBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFreeMovementAccountManagementBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()

        restartMfa()
        setupView()
        setupMfaRouterHandler()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
        mfaRouteHandler.onPause()
    }

    private fun restartMfa() {
        mfaRouteHandler.onPause()
        mfaRouteHandler.onResume()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = (requireActivity() as CieloNavigation).also {
                it.setNavigationListener(this@PixFreeMovementAccountManagementFragment)
                it.configureCollapsingToolbar(
                    CieloCollapsingToolbarLayout.Configurator(
                        layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
                        toolbar = CieloCollapsingToolbarLayout.Toolbar(
                            title = getString(R.string.screen_account_management_toolbar),
                        )
                    )
                )
            }
        }
    }

    private fun setupView() {
        presenter.getMerchant()
        setupListeners()
    }

    private fun setupListeners() {
        binding?.btnActiveAutomaticTransfer?.setOnClickListener {
            balance?.let { itBalance ->
                if (itBalance > ZERO_DOUBLE) showInformationDialog()
                else {
                    validationTokenWrapper.generateOtp(showAnimation = true) { otpCode ->
                        presenter.changePixAccount(otpCode)
                    }
                }
            }
        }
    }

    private fun showInformationDialog() {
        CieloDialog.create(
            title = getString(R.string.text_pix_dialog_migration_to_free_movement_title),
            message = getString(R.string.text_pix_dialog_migration_to_free_movement_subtitle)
        ).setTitleTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .setMessageTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .setPrimaryButton(getString(R.string.entendi))
            .show(
                childFragmentManager,
                PixFreeMovementAccountManagementFragment::class.java.simpleName
            )
    }

    private fun tryAgainBankAccount() {
        binding?.containerBankAccount?.includeTryAgain?.containerTryAgain?.setOnClickListener {
            presenter.getMerchant()
        }
    }

    private fun setupMfaRouterHandler() {
        mfaRouteHandler.showLoadingCallback = { show ->
            if (show)
                showLoading()
            else
                hideLoading()
        }
    }

    private fun bottomSheetSuccessChangePixAccount() {
        bottomSheetGenericFlui(
            nameTopBar = EMPTY,
            R.drawable.ic_validado_transfer,
            getString(R.string.screen_account_management_success_change_to_transitory_account_title),
            getString(R.string.screen_account_management_success_change_to_transitory_account_subtitle),
            nameBtn2Bottom = getString(R.string.text_close),
            statusNameTopBar = false,
            statusTitle = true,
            statusSubTitle = true,
            statusImage = true,
            statusBtnClose = false,
            statusBtnFirst = false,
            statusBtnSecond = true,
            statusView1Line = true,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = true,
            isPhone = false
        ).apply {
            this.onClick =
                object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnSecond(dialog: Dialog) {
                        finishFlow(this@apply)
                    }

                    override fun onSwipeClosed() {
                        finishFlow(this@apply)
                    }

                    override fun onCancel() {
                        finishFlow(this@apply)
                    }
                }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    private fun finishFlow(bottomSheet: BottomSheetFluiGenericFragment) {
        bottomSheet.dismiss()
        redirectToExtract()
        requireActivity().finish()
    }

    private fun redirectToExtract() =
        requireActivity().startActivity<PixExtractNavigationFlowActivity>(
            IS_HOME_PIX_ARGS to true, IS_POSSIBLE_CHANGE_PIX_ARGS to true
        )

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showContent(true)
    }

    override fun onHideMerchantLoading() {
        binding?.containerBankAccount?.progressBarBank?.gone()
    }

    override fun onShowMerchantLoading() {
        binding?.containerBankAccount?.progressBarBank?.visible()
        binding?.containerBankAccount?.containerBank?.gone()
        binding?.containerBankAccount?.includeTryAgain?.root?.gone()
    }

    override fun onSuccessMerchant(merchant: PixMerchantResponse) {
        binding?.containerBankAccount?.containerBank?.visible()
        binding?.containerBankAccount?.tvBankName?.text = merchant.nonPixAccount?.bankName
        binding?.containerBankAccount?.tvBankAgency?.text = HtmlCompat.fromHtml(
            getString(R.string.pix_bank_account_agency, merchant.nonPixAccount?.agency ?: EMPTY),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        val account = "${merchant.nonPixAccount?.account}-${merchant.nonPixAccount?.accountDigit}"

        binding?.containerBankAccount?.tvBankAccount?.text =
            HtmlCompat.fromHtml(
                getString(R.string.pix_bank_account_account, account),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

        BrandCardHelper.getLoadBrandImageGeneric(merchant.nonPixAccount?.bank ?: PATH_DEFAULT)
            .let { itUrl ->
                ImageUtils.loadImage(
                    binding?.containerBankAccount?.ivFlag,
                    itUrl,
                    R.drawable.bank_000
                )
            }
    }

    override fun onErrorMerchant(errorMessage: ErrorMessage?) {
        val error = processErrorMessage(
            errorMessage,
            getString(R.string.business_error),
            getString(R.string.screen_account_management_error_merchant)
        )
        binding?.containerBankAccount?.includeTryAgain?.root?.visible()
        binding?.containerBankAccount?.includeTryAgain?.tvErrorLoadingPix?.text = error.message
        tryAgainBankAccount()
    }

    override fun onErrorChangePixAccount(onFirstAction: () -> Unit) {
        validationTokenWrapper.playAnimationError(callbackValidateToken =
        object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
            override fun callbackTokenError() {
                onFirstAction.invoke()
            }
        })
    }

    override fun onSuccessChangePixAccount() {
        validationTokenWrapper.playAnimationSuccess(
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenSuccess() {
                    bottomSheetSuccessChangePixAccount()
                }
            })
    }

    override fun showError(error: ErrorMessage?) {
        navigation?.showErrorBottomSheet(
            title = getString(R.string.text_pix_generic_bs_error_title),
            textMessage = R.string.text_pix_error_in_processing,
            textButton = getString(R.string.back),
            isFullScreen = false
        )
    }
}