package br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Text.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.helpers.InputTextHelper
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.PHONE_MASK_FORMAT
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.getScreenHeight
import br.com.mobicare.cielo.databinding.FragmentPredictiveBatteryBinding
import br.com.mobicare.cielo.databinding.LayoutButtonFooterBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.analytics.PredictiveBatteryAnalytics
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.analytics.PredictiveBatteryAnalytics.Companion.LOGICAL_NUMBER_INVALID
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.analytics.PredictiveBatteryAnalytics.Companion.SCREEN_VIEW_ACCEPT_SUPPORT
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.analytics.PredictiveBatteryAnalytics.Companion.SCREEN_VIEW_AVAILABLE_SERVICE
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.analytics.PredictiveBatteryAnalytics.Companion.SCREEN_VIEW_LOGICAL_NUMBER_INVALID
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.analytics.PredictiveBatteryAnalytics.Companion.SCREEN_VIEW_REFUSE_SUPPORT
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.analytics.PredictiveBatteryAnalytics.Companion.SCREEN_VIEW_REFUSE_SUPPORT_ERROR
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.analytics.PredictiveBatteryAnalytics.Companion.SCREEN_VIEW_REQUEST_EXCHANGE_ERROR
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.analytics.PredictiveBatteryAnalytics.Companion.SCREEN_VIEW_REQUEST_SENT
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.analytics.PredictiveBatteryAnalytics.Companion.SCREEN_VIEW_UNAVAILABLE_SERVICE
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.analytics.PredictiveBatteryAnalytics.Companion.UNAVAILABLE_SERVICE
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.presentation.viewModel.PredictiveBatteryViewModel
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.utils.UIPredictiveBatteryState
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PredictiveBatteryFragment : BaseFragment(), CieloNavigationListener {
    private var binding: FragmentPredictiveBatteryBinding? = null
    private var bindingFooter: LayoutButtonFooterBinding? = null

    private val ga4: PredictiveBatteryAnalytics by inject()
    private var navigation: CieloNavigation? = null
    private val viewModel: PredictiveBatteryViewModel by viewModel()

    private var isShowForm = false
    private val phoneNumber get() = binding?.itPhoneNumber?.getText().orEmpty()

    private val toolbarDefault
        get() =
            CieloCollapsingToolbarLayout.Configurator(
                layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
                toolbar =
                    CieloCollapsingToolbarLayout.Toolbar(
                        title = getString(R.string.technical_support_predictive_battery_title_toolbar),
                        showBackButton = true,
                    ),
                footerView = bindingFooter?.root,
            )

    private val toolbarBlank get() = CieloCollapsingToolbarLayout.Configurator(layoutMode = CieloCollapsingToolbarLayout.LayoutMode.BLANK)

    private val dataFromActivity get() = navigation?.getData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingFooter = LayoutButtonFooterBinding.inflate(inflater, container, false)

        return FragmentPredictiveBatteryBinding.inflate(
            inflater,
            container,
            false,
        ).also { binding = it }.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupObserver()
        setupButtonSend()
        setMinimumHeight()
        setupNavigation()
        setupView()
        showForm()

        viewModel.start(dataFromActivity)
    }

    override fun onResume() {
        super.onResume()

        if (isShowForm) logScreenView(SCREEN_VIEW_ACCEPT_SUPPORT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setMinimumHeight() {
        binding?.llContentFragment?.minimumHeight = requireActivity().getScreenHeight()
    }

    private fun setupObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIPredictiveBatteryState.ShowLoading -> showLoading(true)
                is UIPredictiveBatteryState.HideLoading -> showLoading(false)
                is UIPredictiveBatteryState.RefuseExchangeError -> onRefuseExchangeError(state.errorMessage)
                is UIPredictiveBatteryState.RequestExchangeError -> onRequestExchangeError(state.errorMessage)
                is UIPredictiveBatteryState.ServiceAvailable -> onServiceAvailable()
                is UIPredictiveBatteryState.SuccessRefuseExchange -> onSuccessRefuseExchange()
                is UIPredictiveBatteryState.SuccessRequestExchange -> onSuccessRequestExchange()
                is UIPredictiveBatteryState.UnavailableService -> onUnavailableService()
                is UIPredictiveBatteryState.ValidateLogicNumberError -> onValidateLogicNumberError()
            }
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
        }
    }

    private fun setupView() {
        InputTextHelper.phoneInput(
            inputText = binding?.itPhoneNumber,
            phoneMask = PHONE_MASK_FORMAT,
            afterTextChange = {
                enableButtonSend()
            },
        )
    }

    private fun setupButtonSend() {
        bindingFooter?.btnAction?.apply {
            setText(R.string.btn_send_terms)
            isEnabled = false
            setOnClickListener {
                logClickButton(SCREEN_VIEW_ACCEPT_SUPPORT, getString(R.string.btn_send_terms))
                viewModel.requestExchange(phoneNumber)
            }
        }
    }

    private fun enableButtonSend() {
        bindingFooter?.btnAction?.isEnabled = ValidationUtils.isValidPhoneNumber(phoneNumber)
        verifyInputTextPhoneNumber()
    }

    private fun verifyInputTextPhoneNumber() {
        val isShowError =
            phoneNumber.isNotEmpty() && ValidationUtils.isValidPhoneNumber(phoneNumber).not()

        binding?.apply {
            itPhoneNumber.showError(isShowError)
            tvMessageErrorItPhoneNumber.visible(isShowError)
        }
    }

    private fun showLoading(isShow: Boolean) {
        isShowForm = false
        showForm()
        binding?.loadingProgress?.apply {
            if (isShow) startAnimation(R.string.wait_animated_loading_start_message) else hideAnimationStart()
            visible(isShow)
        }
    }

    private fun onSuccessRefuseExchange() {
        logScreenView(SCREEN_VIEW_REFUSE_SUPPORT)
        showBottomSheetGoToHome(
            title = R.string.technical_support_predictive_battery_title_bs_success_refuse_exchange,
            message = R.string.technical_support_predictive_battery_message_bs_success_refuse_exchange,
            image = R.drawable.img_90_celular_atencao,
        )
    }

    private fun onSuccessRequestExchange() {
        logScreenView(SCREEN_VIEW_REQUEST_SENT)
        showBottomSheetGoToHome(
            title = R.string.technical_support_predictive_battery_title_bs_success_request_exchange,
            message = R.string.technical_support_predictive_battery_message_bs_success_request_exchange,
            image = R.drawable.img_151_habilitar_venda_whatsapp,
            screenPath = SCREEN_VIEW_REQUEST_SENT,
        )
    }

    private fun onRefuseExchangeError(error: NewErrorMessage?) {
        logScreenView(SCREEN_VIEW_REFUSE_SUPPORT_ERROR)
        logException(SCREEN_VIEW_REFUSE_SUPPORT_ERROR, error)
        showBottomSheetGoToHome(
            title = R.string.commons_generic_error_title,
            message = R.string.pos_virtual_error_message_generic,
            image = R.drawable.img_10_erro,
        )
    }

    private fun onRequestExchangeError(errorMessage: NewErrorMessage?) {
        logScreenView(SCREEN_VIEW_REQUEST_EXCHANGE_ERROR)
        logException(SCREEN_VIEW_REQUEST_EXCHANGE_ERROR, errorMessage)
        navigation?.showCustomHandlerView(
            title = getString(R.string.commons_generic_error_title),
            message = getString(R.string.pos_virtual_error_message_generic),
            contentImage = R.drawable.img_10_erro,
            isShowButtonClose = true,
            isShowFirstButton = true,
            labelFirstButton = getString(R.string.back),
            labelSecondButton = getString(R.string.text_try_again_label),
            callbackFirstButton = {
                logClickButton(
                    SCREEN_VIEW_REQUEST_EXCHANGE_ERROR,
                    getString(R.string.back),
                )
                setTrueInIsShowForm()
            },
            callbackSecondButton = {
                logClickButton(
                    SCREEN_VIEW_REQUEST_EXCHANGE_ERROR,
                    getString(R.string.text_try_again_label),
                )
                viewModel.requestExchange(phoneNumber)
            },
            callbackBack = ::setTrueInIsShowForm,
            callbackClose = ::goToHome,
        )
    }

    private fun onServiceAvailable() {
        logScreenView(SCREEN_VIEW_AVAILABLE_SERVICE)
        navigation?.showCustomHandlerView(
            title = getString(R.string.technical_support_predictive_battery_title_bs_service_available),
            message =
                getString(
                    R.string.technical_support_predictive_battery_message_bs_service_available,
                    viewModel.logicalNumber,
                ),
            contentImage = R.drawable.img_158_maquininha,
            isShowButtonClose = true,
            isShowFirstButton = true,
            labelFirstButton = getString(R.string.technical_support_predictive_battery_label_first_button_bs_service_available),
            labelSecondButton = getString(R.string.technical_support_predictive_battery_label_second_button_bs_service_available),
            callbackFirstButton = {
                logClickButton(
                    SCREEN_VIEW_AVAILABLE_SERVICE,
                    getString(R.string.technical_support_predictive_battery_label_first_button_bs_service_available),
                )
                viewModel.refuseExchange()
            },
            callbackSecondButton = {
                logClickButton(
                    SCREEN_VIEW_AVAILABLE_SERVICE,
                    getString(R.string.technical_support_predictive_battery_label_second_button_bs_service_available),
                )
                setTrueInIsShowForm()
            },
            callbackBack = ::goToHome,
            callbackClose = ::goToHome,
        )
    }

    private fun showForm() {
        navigation?.configureCollapsingToolbar(if (isShowForm) toolbarDefault else toolbarBlank)

        binding?.apply {
            llContentFragment.visible(isShowForm)
            tvLogicalNumber.text = viewModel.logicalNumber
        }
    }

    private fun onUnavailableService() {
        logScreenView(SCREEN_VIEW_UNAVAILABLE_SERVICE)
        logException(SCREEN_VIEW_UNAVAILABLE_SERVICE, UNAVAILABLE_SERVICE)
        showBottomSheetGoToHome(
            title = R.string.technical_support_predictive_battery_title_bs_unavailable_service,
            message = R.string.technical_support_predictive_battery_message_bs_unavailable_service,
            image = R.drawable.img_19_manuntencao,
        )
    }

    private fun onValidateLogicNumberError() {
        logScreenView(SCREEN_VIEW_LOGICAL_NUMBER_INVALID)
        logException(SCREEN_VIEW_LOGICAL_NUMBER_INVALID, LOGICAL_NUMBER_INVALID)
        showBottomSheetGoToHome(
            title = R.string.commons_generic_error_title,
            message = R.string.pos_virtual_error_message_generic,
            image = R.drawable.img_10_erro,
        )
    }

    private fun showBottomSheetGoToHome(
        @StringRes title: Int,
        @StringRes message: Int,
        @DrawableRes image: Int,
        screenPath: String = EMPTY,
    ) {
        navigation?.showCustomHandlerView(
            title = getString(title),
            message = getString(message),
            contentImage = image,
            labelSecondButton = getString(R.string.entendi),
            callbackSecondButton = {
                screenPath.takeIf { it.isNotEmpty() }
                    ?.let { logClickButton(it, getString(R.string.entendi)) }
                goToHome()
            },
            callbackBack = ::goToHome,
        )
    }

    private fun setTrueInIsShowForm() {
        isShowForm = true
        logScreenView(SCREEN_VIEW_ACCEPT_SUPPORT)
        showForm()
    }

    private fun goToHome() {
        requireActivity().finish()
    }

    private fun logScreenView(screenPath: String) = ga4.logScreenView(screenPath)

    private fun logClickButton(
        screenPath: String,
        labelButton: String,
    ) = ga4.logClickButton(screenPath, labelButton)

    private fun logException(
        screenPath: String,
        error: NewErrorMessage?,
    ) = ga4.logException(screenPath, error)

    private fun logException(
        screenPath: String,
        description: String,
    ) = ga4.logException(screenPath, description)
}
