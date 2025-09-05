package br.com.mobicare.cielo.component.requiredDataField.presentation.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationToken
import br.com.mobicare.cielo.component.requiredDataField.presentation.factory.RequiredDataFieldViewFactory
import br.com.mobicare.cielo.component.requiredDataField.presentation.model.UiRequiredDataField
import br.com.mobicare.cielo.component.requiredDataField.presentation.viewmodel.RequiredDataFieldViewModel
import br.com.mobicare.cielo.component.requiredDataField.utils.RequiredDataFieldValueStore
import br.com.mobicare.cielo.component.requiredDataField.utils.UiRequiredDataFieldState
import br.com.mobicare.cielo.databinding.FragmentRequiredDataFieldBinding
import br.com.mobicare.cielo.databinding.LayoutRequiredDataFieldFooterBinding
import br.com.mobicare.cielo.extensions.moveToHome
import org.jetbrains.anko.longToast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class RequiredDataFieldFragment : BaseFragment(), CieloNavigationListener {

    private val viewModel: RequiredDataFieldViewModel by viewModel()
    private val args: RequiredDataFieldFragmentArgs by navArgs()
    private val handlerValidationToken: HandlerValidationToken by inject()

    private var binding: FragmentRequiredDataFieldBinding? = null
    private var footerBinding: LayoutRequiredDataFieldFooterBinding? = null
    private var navigation: CieloNavigation? = null

    private lateinit var uiRequiredDataField: UiRequiredDataField

    private val valueStore = RequiredDataFieldValueStore()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiRequiredDataField = args.requireddataarg
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): LinearLayoutCompat {
        footerBinding = LayoutRequiredDataFieldFooterBinding.inflate(inflater)

        return FragmentRequiredDataFieldBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupObserver()
        setupListener()
        buildFormFields()
    }

    override fun onDestroyView() {
        footerBinding = null
        binding = null
        super.onDestroyView()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = (requireActivity() as CieloNavigation).also {
                it.configureCollapsingToolbar(
                    CollapsingToolbarBaseActivity.Configurator(
                        toolbarTitle = getString(R.string.required_data_field_title),
                        toolbarTitleAppearance = CollapsingToolbarBaseActivity.ToolbarTitleAppearance(
                            collapsed = R.style.CollapsingToolbar_Collapsed_BlackBold,
                            expanded = R.style.CollapsingToolbar_Expanded_BlackBold,
                        ),
                        footerView = footerBinding?.root
                    )
                )
                it.showButton(false)
            }
        }
    }

    private fun setupListener() {
        footerBinding?.btnContinue?.setOnClickListener(::onContinueClick)
    }

    private fun setupObserver() {
        viewModel.requiredDataFieldState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiRequiredDataFieldState.Success -> handleSuccessResult(state.orderID)
                is UiRequiredDataFieldState.TokenError -> handleTokenError(state.error)
                is UiRequiredDataFieldState.InvalidDataError -> showInvalidDataError(state.message)
                is UiRequiredDataFieldState.GenericError -> showGenericError(state.error)
            }
        }
    }

    private fun buildFormFields() {
        binding?.apply {
            RequiredDataFieldViewFactory(
                layoutInflater,
                requiredResponse = uiRequiredDataField.requiredField,
                valueStore = valueStore,
                onFieldChanged = ::onFieldChanged
            )
                .create()
                .forEach { containerForm.addView(it) }
        }
    }

    private fun onFieldChanged(id: String, value: String) {
        footerBinding?.btnContinue?.isEnabled = valueStore.isValid
    }

    private fun onContinueClick(v: View) {
        if (valueStore.isValid) {
            getToken()
        } else {
            requireActivity().longToast(R.string.required_data_field_validation_error_check_fields)
        }
    }

    private fun getToken() {
        handlerValidationToken.getToken(
            childFragmentManager,
            object : HandlerValidationToken.CallbackToken {
                override fun onSuccess(token: String) {
                    viewModel.sendDataField(
                        requireContext(),
                        token,
                        valueStore.asFieldRequestList,
                        uiRequiredDataField.order
                    )
                }

                override fun onError() = handleTokenError()
            }
        )
    }

    private fun handleTokenError(error: NewErrorMessage? = null) {
        handlerValidationToken.playAnimationError(error,
            object : HandlerValidationToken.CallbackAnimationError {
                override fun onTryAgain() {
                    getToken()
                }
            }
        )
    }

    private fun handleSuccessResult(orderID: String) {
        handlerValidationToken.playAnimationSuccess(callbackAnimationSuccess =
        object : HandlerValidationToken.CallbackAnimationSuccess {
            override fun onSuccess() {
                navigation?.showCustomHandlerView(
                    contentImage = R.drawable.img_14_estrelas,
                    title = getString(R.string.required_data_field_title_bs_success_update_data),
                    message = getString(
                        R.string.required_data_field_message_bs_success_update_data,
                        orderID
                    ),
                    labelSecondButton = getString(R.string.go_to_initial_screen),
                    isShowButtonClose = false,
                    callbackSecondButton = ::navigateToHome,
                    callbackBack = ::navigateToHome
                )
            }
        })
    }

    private fun showGenericError(newErrorMessage: NewErrorMessage?) {
        handlerValidationToken.hideAnimation(callbackStopAnimation =
        object : HandlerValidationToken.CallbackStopAnimation {
            override fun onStop() {
                navigation?.showCustomHandler(
                    title = getString(R.string.commons_generic_error_title),
                    message = newErrorMessage?.message
                        ?: getString(R.string.commons_generic_error_message),
                    titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                    messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                    labelSecondButton = getString(R.string.text_try_again_label),
                    labelFirstButton = getString(R.string.back),
                    isShowFirstButton = true,
                    firstButtonCallback = ::navigateToHome,
                    headerCallback = ::navigateToHome,
                )
            }
        })
    }

    private fun showInvalidDataError(@StringRes message: Int?) {
        handlerValidationToken.hideAnimation(callbackStopAnimation =
        object : HandlerValidationToken.CallbackStopAnimation {
            override fun onStop() {
                navigation?.showCustomHandler(
                    title = getString(R.string.required_data_field_validation_error_title),
                    message = getString(message ?: R.string.commons_generic_error_message),
                    titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                    messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                    labelSecondButton = getString(R.string.text_try_again_label),
                    headerCallback = ::navigateToHome,
                )
            }
        })
    }

    private fun navigateToHome() {
        activity?.run {
            setResult(Activity.RESULT_OK)
            finish()
            moveToHome()
        }
    }

    companion object {
        const val REQUIRED_DATA_ARG = "REQUIRED_DATA_ARG"
    }

}