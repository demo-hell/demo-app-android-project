package br.com.mobicare.cielo.pix.ui.mylimits.trusteddestinations.add

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.cielo.libflue.alert.CieloAskQuestionDialogFragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.commons.constants.Text.OTP
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.databinding.FragmentPixMyLimitsAddNewTrustedDestinationBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.*
import br.com.mobicare.cielo.pix.domain.ManualPayee
import br.com.mobicare.cielo.pix.domain.ValidateKeyResponse
import br.com.mobicare.cielo.pix.ui.mylimits.PixMyLimitsNavigationFlowActivity
import br.com.mobicare.cielo.pix.ui.transfer.amount.PixEnterTransferAmountBottomSheet
import br.com.mobicare.cielo.pix.ui.transfer.amount.PixEnterTransferAmountContract
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.HttpURLConnection

class PixMyLimitsAddNewTrustedDestinationFragment : BaseFragment(), CieloNavigationListener,
    PixMyLimitsAddNewTrustedDestinationContract.View, AllowMeContract.View {

    private val presenter: PixMyLimitsAddNewTrustedDestinationPresenter by inject {
        parametersOf(this)
    }

    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    private val isKey: Boolean by lazy {
        arguments?.getBoolean(PIX_IS_KEY_ARGS, false) ?: false
    }

    private val keyInformation: ValidateKeyResponse? by lazy {
        arguments?.getParcelable(PIX_KEY_INFORMATION_ARGS)
    }

    private val manualTransferPayee: ManualPayee? by lazy {
        arguments?.getParcelable(PIX_PAYEE_ARGS)
    }

    private var navigation: CieloNavigation? = null
    private var isAnimation = true

    private var _binding: FragmentPixMyLimitsAddNewTrustedDestinationBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentPixMyLimitsAddNewTrustedDestinationBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupView()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        presenter.onGetTrustedDestinationInformation(isKey, keyInformation, manualTransferPayee)
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.pix_text_my_limits_add_new_trusted_destination_toolbar))
            navigation?.setTextButton(getString(R.string.pix_text_my_limits_add_new_trusted_destination))
            navigation?.showContainerButton(isShow = true)
            navigation?.showButton(isShow = true)
            navigation?.showFirstButton()
            navigation?.setNavigationListener(this)
            navigation?.enableButton(false)
        }
    }

    private fun setupView() {
        setLimit()
        setupInformative()
        onClickListenerCancel()
        onClickListenerSetLimit()
    }

    private fun setupInformative() {
        binding?.containerInformative?.tvInformative?.text =
            getString(R.string.pix_text_my_limits_add_new_trusted_destination_informative)
    }

    private fun onClickListenerCancel() {
        binding?.tvCancel?.setOnClickListener {
            CieloAskQuestionDialogFragment
                .Builder()
                .title(getString(R.string.pix_text_my_limits_add_new_trusted_destination_cancel_title))
                .message(getString(R.string.pix_text_my_limits_add_new_trusted_destination_cancel_message))
                .cancelTextButton(getString(R.string.text_pix_transfer_cancel))
                .positiveTextButton(getString(R.string.back))
                .setCancelButtonBackgroundResource(ResourcesCompat.ID_NULL)
                .build().let {
                    it.onCancelButtonClickListener = View.OnClickListener {
                        showTrustedDestinations()
                    }
                    it.show(
                        childFragmentManager,
                        PixMyLimitsAddNewTrustedDestinationFragment::class.java.simpleName
                    )
                }
        }
    }

    private fun onClickListenerSetLimit() {
        binding?.tvValue?.setOnClickListener {
            setLimit()
        }
    }

    private fun setLimit() {
        PixEnterTransferAmountBottomSheet.onCreate(
            object : PixEnterTransferAmountContract.Result {
                override fun onAmount(amount: Double) {
                    binding?.tvValue?.text = amount.toPtBrRealString()

                    val isGreaterThanZero = amount > ZERO_DOUBLE
                    navigation?.enableButton(isGreaterThanZero)
                    binding?.tvGreaterThanZero?.visible(isGreaterThanZero.not())
                }
            },
            balance = DEFAULT_BALANCE,
            amount = getAmount(),
            title = getString(R.string.pix_text_my_limits_add_new_trusted_destination_value_title),
            message = getString(R.string.pix_text_my_limits_add_new_trusted_destination_value_message)
        ).show(childFragmentManager, tag)
    }

    private fun getAmount(): Double = binding?.tvValue?.text?.toString()?.moneyToDoubleValue()
        ?: ZERO_DOUBLE

    private fun getFingerPrint(isAnimation: Boolean = true) {
        this.isAnimation = isAnimation

        val mAllowMeContextual = allowMePresenter.init(requireContext())
        allowMePresenter.collect(
            mAllowMeContextual = mAllowMeContextual,
            requireActivity(),
            mandatory = true,
            hasAnimation = true
        )
    }

    private fun showAlert(message: String) {
        CieloAlertDialogFragment
            .Builder()
            .title(getString(R.string.dialog_title))
            .message(message)
            .closeTextButton(getString(R.string.dialog_button))
            .build().showAllowingStateLoss(
                requireActivity().supportFragmentManager,
                getString(R.string.text_cieloalertdialog)
            )
    }

    private fun showTrustedDestinations(contactAddSuccess: Boolean = false) {
        requireActivity().finish()
        requireActivity().startActivity<PixMyLimitsNavigationFlowActivity>(
            PIX_MY_LIMITS_IS_HOME_ARGS to false,
            PIX_CONTACT_ADD_SUCCESS_ARGS to contactAddSuccess
        )
    }

    private fun success() {
        bottomSheetGenericFlui(
            nameTopBar = EMPTY,
            R.drawable.ic_img_email,
            getString(R.string.pix_text_my_limits_add_new_trusted_destination_success_title),
            getString(R.string.pix_text_my_limits_add_new_trusted_destination_success_message),
            nameBtn1Bottom = getString(R.string.entendi),
            nameBtn2Bottom = getString(R.string.entendi),
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
                        showTrustedDestinations(true)
                    }

                    override fun onSwipeClosed() {
                        dismiss()
                        showTrustedDestinations(true)
                    }

                    override fun onCancel() {
                        dismiss()
                        showTrustedDestinations(true)
                    }
                }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    private fun stopAnimationAllowMe(onAction: () -> Unit = {}) {
        if (isAnimation)
            onAction.invoke()
        else
            Handler(Looper.getMainLooper()).postDelayed({
                validationTokenWrapper.playAnimationError(callbackValidateToken =
                object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                    override fun callbackTokenError() {
                        onAction.invoke()
                    }
                })
            }, ANIMATION_TIME)
    }

    override fun onSetTrustedDestinationInformation(
        name: String?,
        document: String?,
        documentType: String?,
        bank: String?,
        branch: String?,
        account: String?
    ) {
        binding?.containerDestinationInformation?.apply {
            tvDestinationNameTitle.text =
                getString(R.string.pix_text_my_limits_add_new_trusted_destination_name)
            tvDestinationName.text = name

            tvDestinationKeyTitle.text = documentType
            tvDestinationKey.text = document

            tvDestinationBank.text = bank
            tvDestinationAgencyAndAccount.text = getString(
                R.string.agency_and_account_bank_value,
                branch,
                account
            )
            tvDestinationAgencyAndAccount.visible()
            typedKeyGroup.gone()
        }
    }

    override fun onButtonClicked(labelButton: String) {
        getFingerPrint(isAnimation = true)
    }

    override fun successCollectToken(result: String) {
        validationTokenWrapper.generateOtp(showAnimation = isAnimation) { otpCode ->
            presenter.onAddNewTrustedDestination(otpCode, getAmount(), result)
        }
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
        stopAnimationAllowMe(onAction = { showAlert(errorMessage) })
    }

    override fun getSupportFragmentManagerInstance(): FragmentManager {
        return childFragmentManager
    }

    override fun stopAction() {
        stopAnimationAllowMe(onAction = {
            dialogLocationActivation(
                requireActivity(),
                childFragmentManager
            )
        })
    }

    override fun onErrorAddNewTrustedDestination(
        onGenericError: () -> Unit,
        onOTPError: () -> Unit
    ) {
        validationTokenWrapper.playAnimationError(callbackValidateToken =
        object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
            override fun callbackTokenError() {
                onGenericError.invoke()
            }
        })
    }

    override fun onErrorAddNewTrustedDestinationOTP() {
        bottomSheetGenericFlui(
            EMPTY,
            R.drawable.ic_lock_error,
            getString(R.string.text_pix_transfer_error_otp_title),
            getString(R.string.text_pix_error_otp_message),
            nameBtn1Bottom = getString(R.string.back),
            nameBtn2Bottom = getString(R.string.text_pix_transfer_error_btn_try_again),
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
            this.onClick =
                object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnSecond(dialog: Dialog) {
                        dialog.dismiss()
                        getFingerPrint(isAnimation = false)
                    }

                    override fun onBtnFirst(dialog: Dialog) {
                        dialog.dismiss()
                    }
                }
        }.show(childFragmentManager, this.tag)
    }

    override fun showError(error: ErrorMessage?) {
        if (error?.code != HttpURLConnection.HTTP_FORBIDDEN.toString() || error.errorCode.contains(
                OTP
            )
        ) {
            navigation?.showErrorBottomSheet(
                textButton = getString(R.string.back),
                error = processErrorMessage(
                    error,
                    getString(R.string.business_error),
                    getString(R.string.text_pix_generic_error_message)
                ),
                title = getString(R.string.text_pix_generic_error_title),
                isFullScreen = false
            )
        }
    }

    override fun onSuccessAddNewTrustedDestination() {
        validationTokenWrapper.playAnimationSuccess(
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenSuccess() {
                    success()
                }
            })
    }
}