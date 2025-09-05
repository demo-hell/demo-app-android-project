package br.com.mobicare.cielo.pix.ui.mylimits.timemanagement

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
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
import br.com.mobicare.cielo.commons.utils.getErrorMessage
import br.com.mobicare.cielo.databinding.FragmentPixMyLimitsTimeManagementBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.domain.PixTimeManagementResponse
import br.com.mobicare.cielo.pix.enums.PixStatusTimeManagementEnum
import br.com.mobicare.cielo.pix.enums.PixTimeManagementEnum
import kotlinx.android.synthetic.main.layout_pix_my_limits_time_management_change.view.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PixMyLimitsTimeManagementFragment : BaseFragment(), CieloNavigationListener,
    PixMyLimitsTimeManagementContract.View {

    private val presenter: PixMyLimitsTimeManagementPresenter by inject {
        parametersOf(this)
    }

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    private var navigation: CieloNavigation? = null
    private var nightTimeStart: String? = null
    private var oldNightTimeStart: String? = null

    private var _binding: FragmentPixMyLimitsTimeManagementBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPixMyLimitsTimeManagementBinding.inflate(inflater, container, false)
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
        presenter.getNightTime()
    }

    private fun setupView() {
        binding?.containerPixMyLimitsChange?.btnApplyChange?.isEnabled = false
        setupListeners()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.showContainerButton(isShow = false)
            navigation?.setTextToolbar(getString(R.string.text_toolbar_pix_my_limits_time_management))
            navigation?.setNavigationListener(this)
        }
    }

    private fun updateNightTime(isAnimation: Boolean = false) {
        validationTokenWrapper.generateOtp(showAnimation = isAnimation) { otpCode ->
            presenter.onUpdateNightTime(
                otp = otpCode,
                nightTimeStart = nightTimeStart
            )
        }
    }

    private fun setupListeners() {
        onCheckedChangeListener()
        applyTimeChangeListener()
    }

    private fun onCheckedChangeListener() {
        binding?.containerPixMyLimitsChange?.apply {
            radioGroupChange.setOnCheckedChangeListener { group, _ ->
                btnApplyChange.isEnabled = isEnabledButtonApplyChange(group.checkedRadioButtonId)
                updateNightTimeValue(group.checkedRadioButtonId)
            }
        }
    }

    private fun applyTimeChangeListener() {
        binding?.containerPixMyLimitsChange?.btnApplyChange?.setOnClickListener {
            updateNightTime(isAnimation = true)
        }
    }

    private fun isEnabledButtonApplyChange(radioButtonId: Int): Boolean {
        var isEnable = false
        binding?.containerPixMyLimitsChange?.apply {
            isEnable = if (oldNightTimeStart.equals(PixTimeManagementEnum.TEN.time))
                radioButtonId != radioGroupChange.rb22Hours.id
            else
                radioButtonId != radioGroupChange.rb20Hours.id
        }
        return isEnable
    }

    private fun isEnableRadioButton(status: String?) =
        status != PixStatusTimeManagementEnum.PENDING.name

    private fun updateNightTimeValue(radioButtonId: Int) {
        binding?.containerPixMyLimitsChange?.apply {
            nightTimeStart = if (radioButtonId == radioGroupChange.rb22Hours.id)
                PixTimeManagementEnum.TEN.time
            else
                PixTimeManagementEnum.EIGHT.time
        }
    }

    private fun setupRadioButtonTimeManager() {
        binding?.containerPixMyLimitsChange?.apply {
            if (nightTimeStart.equals(PixTimeManagementEnum.EIGHT.time)) {
                rb22Hours.isChecked = false
                rb20Hours.isChecked = true
            } else {
                rb22Hours.isChecked = true
                rb20Hours.isChecked = false
            }
        }
    }

    private fun setupStatus(status: String?, nightTime: String?) {
        binding?.containerPixMyLimitsChange?.apply {
            val isEnableRadioButton = isEnableRadioButton(status)
            rb22Hours.isEnabled = isEnableRadioButton
            rb20Hours.isEnabled = isEnableRadioButton
            btnApplyChange.isEnabled = false

            tvPendingChange.visible(isEnableRadioButton.not())

            if (isEnableRadioButton.not()) {
                tvPendingChange.text = HtmlCompat.fromHtml(
                    getString(
                        R.string.text_pix_my_limit_night_time_pending_alert,
                        PixTimeManagementEnum.findByTime(
                            nightTime
                        ).displayHour
                    ),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
        }
    }

    private fun setupDescriptionTimeManager(nightTimeResponse: PixTimeManagementResponse) {
        binding?.containerPixMyLimitsActual?.apply {
            tvMyLimitsDaytimeValue.text = nightTimeResponse.actualDayTimeDescription
            tvMyLimitsNighttimeValue.text = nightTimeResponse.actualNightTimeDescription
        }
    }

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showContent(true)
    }

    override fun onSuccessGetNightTime(nightTimeResponse: PixTimeManagementResponse) {
        nightTimeStart = nightTimeResponse.nighttimeStart
        oldNightTimeStart = nightTimeResponse.nighttimeStart

        setupRadioButtonTimeManager()
        setupStatus(
            nightTimeResponse.lastRequest?.status,
            nightTimeResponse.lastRequest?.nighttimeStart
        )
        setupDescriptionTimeManager(nightTimeResponse)
    }

    override fun onErrorGetNightTime(errorMessage: ErrorMessage?) {
        navigation?.showWarningBottomSheet(
            message = requireActivity().getErrorMessage(
                errorMessage,
                getString(R.string.text_pix_error_try_again)
            ),
            title = getString(R.string.text_pix_generic_error_title),
            bt2Title = getString(R.string.text_error_update),
            bt2Callback = { presenter.getNightTime() },
            closeCallback = {
                findNavController().navigate(
                    PixMyLimitsTimeManagementFragmentDirections.actionPixMyLimitsTimeManagementFragmentToPixMyLimitsFragment()
                )
            },
            isFullScreen = false
        )
    }

    override fun onSuccessUpdateNightTime() {
        validationTokenWrapper.playAnimationSuccess(callbackValidateToken =
        object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
            override fun callbackTokenSuccess() {
                navigation?.showWarningBottomSheet(
                    image = R.drawable.ic_validado_transfer,
                    message = getString(R.string.text_pix_night_time_success_message),
                    title = getString(R.string.text_pix_night_time_success_title),
                    bt2Title = getString(R.string.entendi),
                    bt2Callback = { presenter.getNightTime() },
                    closeCallback = {
                        presenter.getNightTime()
                    },
                    isFullScreen = true
                )
            }
        })
    }

    override fun onErrorUpdateNightTime(onGenericError: () -> Unit) {
        validationTokenWrapper.playAnimationError(callbackValidateToken =
        object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
            override fun callbackTokenError() {
                onGenericError.invoke()
            }
        })
    }

    override fun showError(error: ErrorMessage?) {
        navigation?.showWarningBottomSheet(
            message = requireActivity().getErrorMessage(
                error,
                getString(R.string.text_pix_error_in_processing)
            ),
            title = getString(R.string.text_pix_generic_error_title),
            bt2Title = getString(R.string.back),
            isFullScreen = false
        )
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}