package br.com.mobicare.cielo.pix.ui.keys.myKeys.register.validation

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Text.OTP
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.getFormattedKey
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.processErrorMessage
import br.com.mobicare.cielo.commons.utils.serializable
import br.com.mobicare.cielo.commons.utils.showKeyboard
import br.com.mobicare.cielo.databinding.FragmentPixValidationCodeBinding
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.pix.constants.COUNTDOWN_TIME
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.INTERVAL_TIME
import br.com.mobicare.cielo.pix.constants.PIX_CLAIM_FLOW_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_CLAIM_ID_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_KEY_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_TYPE_KEY_ARGS
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.HttpURLConnection

class PixValidationCodeFragment :
    BaseFragment(),
    CieloNavigationListener,
    PixValidationCodeContract.View {
    private var binding: FragmentPixValidationCodeBinding? = null

    private val presenter: PixValidationCodePresenter by inject {
        parametersOf(this)
    }

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    private val keyType: PixKeyTypeEnum? by lazy {
        arguments?.serializable<PixKeyTypeEnum>(PIX_TYPE_KEY_ARGS)
    }

    private val key: String? by lazy {
        arguments?.getString(PIX_KEY_ARGS)
    }

    private val claimId: String? by lazy {
        arguments?.getString(PIX_CLAIM_ID_ARGS)
    }

    private val isClaimFlow: Boolean by lazy {
        arguments?.getBoolean(PIX_CLAIM_FLOW_ARGS) ?: false
    }

    private var navigation: CieloNavigation? = null
    private var isStartTimer = true
    private var isToKey = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentPixValidationCodeBinding.inflate(
        inflater,
        container,
        false,
    ).also { binding = it }.root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupTitle()
        sendNewCode()
        if (isClaimFlow) {
            presenter.onSendValidationCode(key, keyType, isClaimFlow)
        } else {
            if (isStartTimer) {
                countdown()
            }
            requireActivity().showKeyboard(binding?.inputTextCode)
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        isStartTimer = false
        presenter.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.text_toolbar_key_registration_pix))
            navigation?.setTextButton(getString(R.string.text_next_label))
            navigation?.showContainerButton(isShow = true)
            navigation?.showButton(isShow = true)
            navigation?.showHelpButton()
            navigation?.setNavigationListener(this)
        }
    }

    private fun countdown() {
        binding?.apply {
            tvTimerSendNewCode.visible()
            setupNewCode(R.color.display_200, R.drawable.ic_chevron_end_gray)
            tvSendNewCode.isEnabled = false
        }

        object : CountDownTimer(COUNTDOWN_TIME, INTERVAL_TIME) {
            override fun onTick(millisUntilFinished: Long) {
                val time = (millisUntilFinished / INTERVAL_TIME).toString()
                binding?.tvTimerSendNewCode?.fromHtml(R.string.text_pix_create_key_timer, time)
            }

            override fun onFinish() {
                binding?.apply {
                    tvTimerSendNewCode.gone()
                    setupNewCode(R.color.brand_400, R.drawable.ic_chevron_end_blue)
                    tvSendNewCode.isEnabled = true
                }
            }
        }.start()
    }

    private fun setupNewCode(
        @ColorRes textColor: Int,
        @DrawableRes icon: Int,
    ) {
        binding?.tvSendNewCode?.apply {
            setTextColor(ContextCompat.getColor(requireContext(), textColor))
            setCompoundDrawablesWithIntrinsicBounds(ZERO, ZERO, icon, ZERO)
        }
    }

    private fun setupTitle() {
        val formattedKey = if (isClaimFlow) key?.let { getFormattedKey(it, keyType?.name) } else key

        binding?.tvTitleValidationCode?.text =
            if (keyType == PixKeyTypeEnum.PHONE) {
                getString(
                    R.string.text_pix_create_key_insert_code_phone,
                    formattedKey.orEmpty(),
                )
            } else {
                getString(
                    R.string.text_pix_create_key_insert_code,
                    formattedKey.orEmpty(),
                )
            }
    }

    private fun showErrorInput(
        errorMessage: String = getString(R.string.text_pix_create_key_insert_code_error_input),
        isShow: Boolean = true,
    ) {
        binding?.inputTextCode?.apply {
            setError(errorMessage)
            setErrorImage(R.drawable.ic_alert_red)
            showErrorWithIcon(isShow)
        }
    }

    private fun sendNewCode() {
        binding?.tvSendNewCode?.setOnClickListener {
            presenter.onSendValidationCode(key, keyType, isClaimFlow)
        }
    }

    private fun toRegister(code: String?) {
        keyType?.let {
            findNavController().safeNavigate(
                PixValidationCodeFragmentDirections.actionPixValidationCodeFragmentToPixKeyRegistrationFragment(
                    it,
                    key.orEmpty(),
                    code.orEmpty(),
                ),
            )
        }
    }

    private fun toMyKeys() {
        findNavController().safeNavigate(
            PixValidationCodeFragmentDirections.actionPixValidationCodeFragmentToPixMyKeysFragment(
                false,
            ),
        )
    }

    private fun callClaim(isStartAnimation: Boolean = true) {
        val code = binding?.inputTextCode?.getText()

        validationTokenWrapper.generateOtp(showAnimation = isStartAnimation) { otpCode ->
            presenter.onRevokeClaim(otpCode, claimId, code)
        }
    }

    private fun bottomSheetSuccessClaims(
        @StringRes title: Int,
        @StringRes message: Int,
    ) {
        val formattedKey = key?.let { getFormattedKey(it, keyType?.name) }
        val finalMessage = getString(message, formattedKey)

        bottomSheetGenericFlui(
            nameTopBar = EMPTY,
            R.drawable.ic_validado_transfer,
            getString(title),
            finalMessage,
            nameBtn1Bottom = getString(R.string.text_pix_success_create_claims_action),
            nameBtn2Bottom = getString(R.string.back),
            statusNameTopBar = false,
            statusTitle = true,
            statusSubTitle = true,
            statusImage = true,
            statusBtnClose = false,
            statusBtnFirst = true,
            statusBtnSecond = false,
            statusView1Line = true,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = true,
            isPhone = false,
        ).apply {
            this.onClick =
                object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnFirst(dialog: Dialog) {
                        dialog.dismiss()
                        toMyKeys()
                    }

                    override fun onSwipeClosed() {
                        toMyKeys()
                    }
                }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun onButtonClicked(labelButton: String) {
        requireActivity().hideSoftKeyboard()
        if (isClaimFlow) {
            callClaim()
        } else {
            val isNull = binding?.inputTextCode?.getText().isNullOrEmpty()
            showErrorInput(isShow = isNull)
            if (isNull.not()) {
                toRegister(binding?.inputTextCode?.getText())
            }
        }
    }

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showContent(true)
    }

    override fun onSuccessSendCode() {
        countdown()
        requireActivity().showKeyboard(binding?.inputTextCode)
    }

    override fun showError(error: ErrorMessage?) {
        isToKey = isClaimFlow
        if (error?.code != HttpURLConnection.HTTP_FORBIDDEN.toString() ||
            error.errorCode.contains(
                OTP,
            )
        ) {
            navigation?.showErrorBottomSheet(
                textButton = getString(R.string.back),
                error =
                    processErrorMessage(
                        error,
                        getString(R.string.business_error),
                        getString(R.string.text_pix_create_key_insert_code_error_subtitle),
                    ),
                title = getString(R.string.text_pix_create_key_insert_code_error_title),
                isFullScreen = false,
            )
        }
    }

    override fun onBackButtonClicked(): Boolean {
        requireActivity().hideSoftKeyboard()
        return super.onBackButtonClicked()
    }

    override fun onSuccessRevokeClaim() {
        bottomSheetSuccessClaims(
            title = R.string.text_success_revoke_claim_title,
            message = R.string.text_success_revoke_claim_message,
        )
    }

    override fun onSuccessClaim(onAction: () -> Unit) {
        validationTokenWrapper.playAnimationSuccess(
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenSuccess() {
                    onAction.invoke()
                }
            },
        )
    }

    override fun onShowErrorClaim(error: ErrorMessage?) {
        isToKey = true
        validationTokenWrapper.playAnimationError(
            callbackValidateToken =
                object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                    override fun callbackTokenError() {
                        if (error?.code != HttpURLConnection.HTTP_FORBIDDEN.toString() || error.errorCode.contains(OTP)) {
                            navigation?.showErrorBottomSheet(
                                textButton = getString(R.string.back),
                                error =
                                    processErrorMessage(
                                        error,
                                        getString(R.string.business_error),
                                        getString(R.string.text_pix_create_key_insert_code_error_subtitle),
                                    ),
                                title = getString(R.string.text_error_confirm_claim_title),
                                isFullScreen = false,
                            )
                        }
                    }
                },
        )
    }

    override fun onClickSecondButtonError() {
        if (isToKey) {
            toMyKeys()
        }
    }

    override fun onActionSwipe() {
        if (isToKey) {
            toMyKeys()
        }
    }
}
