package br.com.mobicare.cielo.pix.ui.extract.account.management

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Text.OTP
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
import br.com.mobicare.cielo.databinding.FragmentTransitoryAccountManagementBinding
import br.com.mobicare.cielo.extensions.toHomePix
import br.com.mobicare.cielo.idOnboarding.router.IDOnboardingRouter
import br.com.mobicare.cielo.mfa.router.MfaRouteHandler
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.PIX_RETURN_FRAGMENT_WHEN_FINISHED_OPERATION
import br.com.mobicare.cielo.pix.constants.PIX_USAGE_TERMS_URL
import br.com.mobicare.cielo.pix.domain.PixMerchantResponse
import org.jetbrains.anko.browse
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.HttpURLConnection

const val PATH_DEFAULT = "-1"

class PixTransitoryAccountManagementFragment : BaseFragment(), CieloNavigationListener,
    PixTransitoryAccountManagementContract.View {

    private val presenter: PixTransitoryAccountManagementPresenter by inject {
        parametersOf(this)
    }

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    private val mfaRouteHandler: MfaRouteHandler by inject {
        parametersOf(activity ?: requireActivity())
    }

    private val isReturnFragmentWhenFinishedOperation by lazy {
        arguments?.getBoolean(PIX_RETURN_FRAGMENT_WHEN_FINISHED_OPERATION, false) ?: false
    }

    private var navigation: CieloNavigation? = null
    private var mIDORouter: IDOnboardingRouter? = null

    private var _binding: FragmentTransitoryAccountManagementBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransitoryAccountManagementBinding.inflate(inflater, container, false)
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
        setupOnResume()
    }

    override fun onPause() {
        super.onPause()
        setupOnPause()
    }

    private fun setupOnResume() {
        presenter.onResume()

        mIDORouter?.onResume()
        mIDORouter?.activity = activity

        restartMfa()
        setupView()
        setupMfaRouterHandler()
    }

    private fun restartMfa(){
        mfaRouteHandler.onPause()
        mfaRouteHandler.onResume()
    }

    private fun setupOnPause() {
        presenter.onPause()
        mIDORouter?.onPause()
        mfaRouteHandler.onPause()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.screen_account_management_toolbar))
            navigation?.showContainerButton()
            navigation?.showHelpButton()
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupView() {
        presenter.getMerchant()
        setupListeners()
    }

    private fun setupListeners() {
        binding?.includeAuthorization?.include?.checkBoxPix?.setOnCheckedChangeListener { _, selected ->
            binding?.btnActiveFreeMovement?.isEnabled = selected
        }

        binding?.includeAuthorization?.containerReadPixTerms?.setOnClickListener {
            requireActivity().browse(PIX_USAGE_TERMS_URL)
        }

        binding?.btnActiveFreeMovement?.setOnClickListener {
            changeAccountType()
        }
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

    private fun startDigitalIdentityMigration() {
        mIDORouter = IDOnboardingRouter(
            activity = requireActivity(),
            showLoadingCallback = {
                showLoading()
            },
            hideLoadingCallback = {
                hideLoading()
            },
            isShowWarning = false
        ).showOnboarding()
    }

    private fun changeAccountType() {
        presenter.getUserInformation()
    }

    private fun backToExtract(dialog: Dialog?) {
        dialog?.dismiss()
        if (isReturnFragmentWhenFinishedOperation)
            onBackButtonClicked()
        else
            findNavController().navigate(
                    PixTransitoryAccountManagementFragmentDirections.actionPixTransitoryAccountManagementFragmentToPixExtractFragment()
            )
    }

    private fun bottomSheetSuccessChangePixAccount() {
        bottomSheetGenericFlui(
            nameTopBar = EMPTY,
            R.drawable.ic_validado_transfer,
            getString(R.string.screen_account_management_success_change_account_title),
            getString(R.string.screen_account_management_success_change_account_message),
            nameBtn1Bottom = getString(R.string.text_close),
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
                        dialog.dismiss()
                        navigateToHomePix()
                    }

                    override fun onSwipeClosed() {
                        dismiss()
                        navigateToHomePix()
                    }

                    override fun onCancel() {
                        dismiss()
                        navigateToHomePix()
                    }
                }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showContent(true)
    }

    override fun onHideMerchant() {
        binding?.containerBankAccount?.progressBarBank?.gone()
    }

    override fun onShowLoadingMerchant() {
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

    override fun onShowIDOnboarding() {
        bottomSheetGenericFlui(
            EMPTY,
            R.drawable.img_preenchimento_dados,
            getString(R.string.screen_account_management_digital_identity_migration_title),
            getString(R.string.screen_account_management_digital_identity_migration_subtitle),
            getString(R.string.back),
            getString(R.string.screen_account_management_digital_identity_migration_btn),
            statusNameTopBar = false,
            statusTitle = true,
            statusSubTitle = true,
            statusImage = true,
            statusBtnClose = false,
            statusBtnFirst = true,
            statusBtnSecond = true,
            statusView1Line = true,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = true
        ).apply {
            onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnFirst(dialog: Dialog) {
                    dismiss()
                }

                override fun onBtnSecond(dialog: Dialog) {
                    dismiss()
                    startDigitalIdentityMigration()
                }

                override fun onSwipeClosed() {
                    dismiss()
                }

                override fun onCancel() {
                    dismiss()
                }
            }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun onNotAdmin() {
        bottomSheetGenericFlui(
            EMPTY,
            R.drawable.img_perfil_impedido,
            getString(R.string.screen_account_management_digital_identity_migration_not_admin_title),
            getString(R.string.screen_account_management_digital_identity_migration_not_admin_subtitle),
            getString(R.string.text_close),
            getString(R.string.text_close),
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
            isFullScreen = true
        ).apply {
            onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {

                override fun onBtnSecond(dialog: Dialog) {
                    backToExtract(dialog)
                }

                override fun onSwipeClosed() {
                    backToExtract(this@apply.dialog)
                }

                override fun onCancel() {
                    backToExtract(this@apply.dialog)
                }
            }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun onValidateMFA() {
        mfaRouteHandler.runWithMfaToken {
            validationTokenWrapper.generateOtp(showAnimation = true) { otpCode ->
                presenter.changePixAccount(otpCode)
            }
        }
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
        if (error?.code != HttpURLConnection.HTTP_FORBIDDEN.toString() || error.errorCode.contains(
                OTP
            )
        )
            navigation?.showErrorBottomSheet(
                textButton = getString(R.string.back),
                isFullScreen = false
            )
    }

    private fun navigateToHomePix() {
        if (isReturnFragmentWhenFinishedOperation)
            onBackButtonClicked()
        else
            requireActivity().toHomePix()
    }
}