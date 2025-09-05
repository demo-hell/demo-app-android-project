package br.com.mobicare.cielo.pix.ui.mylimits.transactions

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.cielo.libflue.util.ZERO
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
import br.com.mobicare.cielo.databinding.FragmentPixMyLimitsTransactionsBinding
import br.com.mobicare.cielo.pix.constants.ANIMATION_TIME
import br.com.mobicare.cielo.pix.constants.DEFAULT_BALANCE
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.domain.LimitsRequest
import br.com.mobicare.cielo.pix.domain.PixMyLimitsResponse
import br.com.mobicare.cielo.pix.enums.BeneficiaryTypeEnum
import br.com.mobicare.cielo.pix.enums.PixLimitTypeEnum
import br.com.mobicare.cielo.pix.enums.PixTimeManagementEnum
import br.com.mobicare.cielo.pix.helpers.pixLimitDayTimeRangeText
import br.com.mobicare.cielo.pix.helpers.pixLimitNightTimeRangeText
import br.com.mobicare.cielo.pix.ui.mylimits.transactions.helpers.PixMyLimitsTransactionTypeViewSelector
import br.com.mobicare.cielo.pix.ui.transfer.amount.PixEnterTransferAmountBottomSheet
import br.com.mobicare.cielo.pix.ui.transfer.amount.PixEnterTransferAmountContract
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.HttpURLConnection

class PixMyLimitsTransactionsFragment : BaseFragment(), CieloNavigationListener,
    PixMyLimitsTransactionsContract.View, AllowMeContract.View {

    private val presenter: PixMyLimitsTransactionsPresenter by inject {
        parametersOf(this)
    }

    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }

    private val args: PixMyLimitsTransactionsFragmentArgs by navArgs()
    private lateinit var beneficiaryType: BeneficiaryTypeEnum

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    private val listLimitsRequest: MutableList<LimitsRequest> = arrayListOf()

    private var binding: FragmentPixMyLimitsTransactionsBinding? = null
    private var navigation: CieloNavigation? = null
    private var isAnimation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        beneficiaryType = args.pixBeneficiaryType
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentPixMyLimitsTransactionsBinding.inflate(
        inflater, container, false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupTitle()
        setupView()
        setupListeners()
    }

    private fun setupTitle() {
        binding?.apply {
            tvMyLimitsTitle.text = getString(
                when (beneficiaryType) {
                    BeneficiaryTypeEnum.CPF -> R.string.text_pix_my_limits_title_transaction_cpf
                    BeneficiaryTypeEnum.CNPJ -> R.string.text_pix_my_limits_title_transaction_cnpj
                }
            )
        }
    }

    private fun setupView() {
        binding?.apply {
            containerPixMyLimitsDaytime.tvMyLimitsDaytimeValue.text =
                getString(R.string.text_pix_enter_amount_value_zero)
            containerPixMyLimitsDaytime.tvMyLimitsDaytimeTotalValue.text =
                getString(R.string.text_pix_enter_amount_value_zero)
            containerPixMyLimitsNighttime.tvMyLimitsNighttimeValue.text =
                getString(R.string.text_pix_enter_amount_value_zero)
            containerPixMyLimitsNighttime.tvMyLimitsNighttimeTotalValue.text =
                getString(R.string.text_pix_enter_amount_value_zero)
            containerPixMyLimitsMonthly.tvMyLimitsMonthlyValue.text =
                getString(R.string.text_pix_enter_amount_value_zero)
            tvMyLimitsOptions.text = HtmlCompat.fromHtml(
                getString(R.string.text_my_limits_transaction_options),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        onGetMyLimits()
    }

    fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.showContainerButton(isShow = false)
            navigation?.setTextToolbar(
                getString(
                    when (beneficiaryType) {
                        BeneficiaryTypeEnum.CNPJ -> R.string.text_toolbar_pix_my_limits_pix_pj
                        BeneficiaryTypeEnum.CPF -> R.string.text_toolbar_pix_my_limits_pix_pf
                    }
                )
            )
            navigation?.setNavigationListener(this)
        }
    }

    private fun changeAmount(title: String?, changeAmount: Double, type: String) {
        PixEnterTransferAmountBottomSheet.onCreate(
            object : PixEnterTransferAmountContract.Result {
                override fun onAmount(amount: Double) {
                    val isUpdate = amount.compareTo(changeAmount) != ZERO
                    if (amount > ZERO_DOUBLE && isUpdate)
                        updateAmount(type, amount)
                }
            },
            balance = DEFAULT_BALANCE,
            amount = changeAmount,
            title = title,
            textButton = getString(R.string.text_pix_my_limit_adjust_limit),
            isDismiss = false
        ).show(childFragmentManager, tag)
    }

    private fun updateAmount(type: String, amount: Double) {
        listLimitsRequest.find { it.type == type }?.value = amount
        getFingerPrint(isAnimation = true)
    }

    private fun setupListeners() {
        binding?.apply {
            containerPixMyLimitsDaytime.tvMyLimitsDaytimeValue.setOnClickListener {
                changeAmount(
                    getString(R.string.text_pix_new_limit_daytime),
                    getAmountDayTime(),
                    PixLimitTypeEnum.DAYTIME_TRANSACTION_LIMIT.name
                )
            }
            containerPixMyLimitsDaytime.tvMyLimitsDaytimeTotalValue.setOnClickListener {
                changeAmount(
                    getString(R.string.text_pix_new_limit_daytime_total),
                    getAmountDayTimeTotal(),
                    PixLimitTypeEnum.TOTAL_DAYTIME_TRANSACTION_LIMIT.name
                )
            }
            containerPixMyLimitsNighttime.tvMyLimitsNighttimeValue.setOnClickListener {
                changeAmount(
                    getString(R.string.text_pix_new_limit_nighttime),
                    getAmountNightTime(),
                    PixLimitTypeEnum.NIGHTTIME_TRANSACTION_LIMIT.name
                )
            }
            containerPixMyLimitsNighttime.tvMyLimitsNighttimeTotalValue.setOnClickListener {
                changeAmount(
                    getString(R.string.text_pix_new_limit_nighttime_total),
                    getAmountNightTimeTotal(),
                    PixLimitTypeEnum.TOTAL_NIGHTTIME_TRANSACTION_LIMIT.name
                )
            }
            containerPixMyLimitsMonthly.tvMyLimitsMonthlyValue.setOnClickListener {
                changeAmount(
                    getString(R.string.text_pix_new_limit_monthly),
                    getAmountMonthly(),
                    PixLimitTypeEnum.TOTAL_MONTH_TRANSACTION_LIMIT.name
                )
            }
        }
    }

    private fun getAmountDayTime(): Double =
        binding?.containerPixMyLimitsDaytime?.tvMyLimitsDaytimeValue?.text?.toString()
            ?.moneyToDoubleValue()
            ?: ZERO_DOUBLE

    private fun getAmountDayTimeTotal(): Double =
        binding?.containerPixMyLimitsDaytime?.tvMyLimitsDaytimeTotalValue?.text?.toString()
            ?.moneyToDoubleValue()
            ?: ZERO_DOUBLE

    private fun getAmountNightTime(): Double =
        binding?.containerPixMyLimitsNighttime?.tvMyLimitsNighttimeValue?.text?.toString()
            ?.moneyToDoubleValue()
            ?: ZERO_DOUBLE

    private fun getAmountNightTimeTotal(): Double =
        binding?.containerPixMyLimitsNighttime?.tvMyLimitsNighttimeTotalValue?.text?.toString()
            ?.moneyToDoubleValue()
            ?: ZERO_DOUBLE

    private fun getAmountMonthly(): Double =
        binding?.containerPixMyLimitsMonthly?.tvMyLimitsMonthlyValue?.text?.toString()
            ?.moneyToDoubleValue()
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

    private fun onGetMyLimits() {
        presenter.run {
            getMyLimits(beneficiaryType = beneficiaryType)
            getNightTime()
        }
    }

    private fun setMyLimits(type: String?, value: Double?) {
        listLimitsRequest.add(LimitsRequest(type = type, value = value))
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

    private fun bottomSheetSuccessUpdateLimit() {
        bottomSheetGenericFlui(
            nameTopBar = EMPTY,
            R.drawable.ic_validado_transfer,
            getString(R.string.text_pix_send_adjust_limit),
            getString(R.string.text_pix_detail_adjust_limit),
            nameBtn1Bottom = EMPTY,
            nameBtn2Bottom = getString(R.string.entendi),
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
            isFullScreen = true,
            isPhone = false
        ).apply {
            this.onClick =
                object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnSecond(dialog: Dialog) {
                        dialog.dismiss()
                        onGetMyLimits()
                    }

                    override fun onSwipeClosed() {
                        dismiss()
                        onGetMyLimits()
                    }

                    override fun onCancel() {
                        dismiss()
                        onGetMyLimits()
                    }
                }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    private fun bottomSheetErrorUpdateLimit(error: ErrorMessage?) {
        val errorMessage = processErrorMessage(
            error,
            getString(R.string.business_error),
            getString(R.string.text_pix_error_try_again)
        )
        if (error?.code != HttpURLConnection.HTTP_FORBIDDEN.toString() || error.errorCode.contains(
                OTP
            )
        ) {
            bottomSheetGenericFlui(
                nameTopBar = EMPTY,
                R.drawable.ic_generic_error_image,
                getString(R.string.text_pix_error_try_again),
                errorMessage.message,
                nameBtn1Bottom = EMPTY,
                nameBtn2Bottom = getString(R.string.back),
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
                isFullScreen = false,
                isPhone = false
            ).apply {
                this.onClick =
                    object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                        override fun onBtnSecond(dialog: Dialog) {
                            dialog.dismiss()
                            onGetMyLimits()
                        }

                        override fun onSwipeClosed() {
                            dismiss()
                            onGetMyLimits()
                        }

                        override fun onCancel() {
                            dismiss()
                            onGetMyLimits()
                        }
                    }
            }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
        }
    }

    override fun onShowMyLimits(limitsResponse: PixMyLimitsResponse) {
        limitsResponse.limits?.let { limits ->
            listLimitsRequest.clear()
            for (limit in limits) {
                setMyLimits(type = limit.type, value = limit.accountLimit)
                binding?.let {
                    PixMyLimitsTransactionTypeViewSelector(
                        limit = limit,
                        binding = it,
                        fragmentManager = childFragmentManager
                    ).invoke()
                }
            }
        }
    }

    private fun checkBottomSheetToken() {
        val activitySupport = requireActivity().supportFragmentManager
        val simpleName = BottomSheetValidationTokenWrapper::class.java.simpleName
        removeFragment(activitySupport.findFragmentByTag(simpleName), activitySupport)
        removeFragment(parentFragmentManager.findFragmentByTag(simpleName), activitySupport)
    }

    private fun removeFragment(fragment: Fragment?, activitySupport: FragmentManager) {
        fragment?.let {
            activitySupport.beginTransaction().remove(it).commit()
            parentFragmentManager.beginTransaction().remove(it).commit()
        }
    }

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showContent(true)
    }

    override fun onErrorGetLimits(errorMessage: ErrorMessage?) {
        val error = processErrorMessage(
            errorMessage,
            getString(R.string.business_error),
            getString(R.string.text_pix_error_try_again)
        )

        bottomSheetGenericFlui(
            EMPTY,
            R.drawable.ic_generic_error_image,
            getString(R.string.text_pix_my_limits_error_title),
            error.message,
            nameBtn1Bottom = EMPTY,
            nameBtn2Bottom = getString(R.string.text_error_update),
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
            isFullScreen = false
        ).apply {
            this.onClick =
                object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnSecond(dialog: Dialog) {
                        dialog.dismiss()
                        toBack()
                    }

                    override fun onSwipeClosed() {
                        toBack()
                    }
                }

        }.show(childFragmentManager, this.tag)
    }

    private fun toBack() {
        findNavController().navigate(PixMyLimitsTransactionsFragmentDirections.actionPixMylimitsTransactionsToPixMyLimitsFragment())
    }

    override fun successCollectToken(result: String) {
        checkBottomSheetToken()

        validationTokenWrapper.generateOtp(showAnimation = true) { otpCode ->
            presenter.onUpdateLimit(
                otp = otpCode,
                listLimits = listLimitsRequest,
                fingerprint = result,
                beneficiaryType = beneficiaryType
            )
        }
    }

    override fun onSuccessUpdateLimit() {
        validationTokenWrapper.playAnimationSuccess(
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenSuccess() {
                    bottomSheetSuccessUpdateLimit()
                }
            })
    }

    override fun onErrorUpdateLimit(errorMessage: ErrorMessage?) {
        bottomSheetErrorUpdateLimit(errorMessage)
    }

    override fun onSuccessGetNightTime(timeManagement: PixTimeManagementEnum?) {
        timeManagement?.let {
            binding?.apply {
                containerPixMyLimitsDaytime.tvMyLimitsHoursRange.text =
                    requireContext().pixLimitDayTimeRangeText(timeManagement)
                containerPixMyLimitsNighttime.tvMyLimitsHoursRange.text =
                    requireContext().pixLimitNightTimeRangeText(timeManagement)
            }
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

    override fun onErrorUpdateLimits(onGenericError: () -> Unit) {
        validationTokenWrapper.playAnimationError(callbackValidateToken =
        object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
            override fun callbackTokenError() {
                onGenericError.invoke()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }
}