package br.com.mobicare.cielo.pixMVVM.presentation.account.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.enum.CieloCardInformationType
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.cielo.libflue.screen.HandlerViewFluiV2
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationToken
import br.com.mobicare.cielo.databinding.LayoutPixFooterRoundedButtonBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.toHomePix
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.koin.android.ext.android.inject

abstract class PixAccountBaseFragment : BaseFragment() {

    abstract val toolbarTitle: String

    protected val handlerValidationToken: HandlerValidationToken by inject()

    private var _footerBinding: LayoutPixFooterRoundedButtonBinding? = null
    private val footerBinding get() = requireNotNull(_footerBinding)

    open val footerButtonConfigurator: FooterButtonConfigurator? = null

    protected var navigation: CieloNavigation? = null

    protected val footerButton get() = footerBinding.button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _footerBinding = LayoutPixFooterRoundedButtonBinding.inflate(inflater, container, false)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeNavigation()
        configureCollapsingToolbar()
        configureFooterButton()
    }

    override fun onDestroyView() {
        _footerBinding = null
        super.onDestroyView()
    }

    private fun initializeNavigation() {
        navigation = requireActivity() as? CieloNavigation
    }

    private fun configureCollapsingToolbar() {
        navigation?.configureCollapsingToolbar(
            CieloCollapsingToolbarLayout.Configurator(
                toolbar = CieloCollapsingToolbarLayout.Toolbar(
                    title = toolbarTitle,
                    menu = CieloCollapsingToolbarLayout.ToolbarMenu(
                        menuRes = R.menu.menu_help,
                        onOptionsItemSelected = ::onMenuOptionSelected
                    )
                ),
                footerView = footerButtonConfigurator?.run { footerBinding.root }
            )
        )
    }

    protected fun getToken(onSuccess: (String) -> Unit) {
        handlerValidationToken.getToken(
            childFragmentManager,
            object : HandlerValidationToken.CallbackToken {
                override fun onSuccess(token: String) = onSuccess(token)
                override fun onError() = onTokenError(onSuccess)
            }
        )
    }

    private fun onTokenError(onSuccess: (String) -> Unit, error: NewErrorMessage? = null) {
        handlerValidationToken.playAnimationError(
            error,
            object : HandlerValidationToken.CallbackAnimationError {
                override fun onTryAgain() = getToken(onSuccess)
            }
        )
    }

    protected fun showGenericErrorScreen(
        @StringRes buttonTextRes: Int = R.string.entendi
    ) {
        handlerValidationToken.hideAnimation(
            callbackStopAnimation = object : HandlerValidationToken.CallbackStopAnimation {
                override fun onStop() {
                    showErrorScreen(
                        title = getString(R.string.commons_generic_error_title),
                        message = getString(R.string.commons_generic_error_message),
                        buttonText = getString(buttonTextRes),
                    )
                }
            }
        )
    }

    private fun configureFooterButton() {
        footerButtonConfigurator?.let { configurator ->
            footerButton.text = configurator.text
            footerButton.isButtonEnabled = configurator.isEnabled
            footerButton.setOnClickListener { configurator.onTap() }
        }
    }

    private fun onMenuOptionSelected(menuItem: MenuItem) {
        if (menuItem.itemId == R.id.menuActionHelp) {
            requireActivity().openFaq(
                tag = ConfigurationDef.TAG_HELP_CENTER_PIX,
                subCategoryName = getString(R.string.cielo_facilita_central_de_ajuda_pix)
            )
        }
    }

    protected fun showErrorScreen(
        title: String,
        message: String,
        buttonText: String,
        onClose: ((Dialog?) -> Unit)? = null
    ) {
        doWhenResumed {
            navigation?.showHandlerViewV2(
                title = title,
                message = message,
                messageTextAppearance = R.style.medium_montserrat_16_neutral_600,
                illustration = R.drawable.ic_07,
                isShowBackButton = false,
                isShowIconButtonEndHeader = true,
                labelPrimaryButton = buttonText,
                onPrimaryButtonClickListener = onHandlerViewListenerAction {
                    onClose?.invoke(it) ?: it?.dismiss()
                },
                onBackButtonClickListener = onHandlerViewListenerAction {
                    onClose?.invoke(it) ?: it?.dismiss()
                },
                onIconButtonEndHeaderClickListener = onHandlerViewListenerAction {
                    onClose?.invoke(it) ?: it?.dismiss()
                },
            )
        }
    }

    protected fun showSuccessScreen(
        @DrawableRes illustration: Int,
        titleText: String,
        messageText: String,
        noteText: String? = null,
        primaryButtonText: String,
        onPrimaryButtonClick: (Dialog?) -> Unit,
        secondaryButtonText: String? = null,
        onSecondaryButtonClick: ((Dialog?) -> Unit)? = null,
        showCloseButton: Boolean = false
    ) {
        doWhenResumed {
            navigation?.showHandlerViewV2(
                title = titleText,
                message = messageText,
                messageTextAppearance = R.style.medium_montserrat_16_neutral_600,
                illustration = illustration,
                isShowBackButton = false,
                isShowIconButtonEndHeader = showCloseButton,
                labelPrimaryButton = primaryButtonText,
                labelSecondaryButton = secondaryButtonText.orEmpty(),
                cardInformationData = noteText?.let {
                    HandlerViewFluiV2.CardInformationData(
                        type = CieloCardInformationType.NOTE,
                        text = it
                    )
                },
                onPrimaryButtonClickListener = onHandlerViewListenerAction { onPrimaryButtonClick(it) },
                onSecondaryButtonClickListener = onSecondaryButtonClick?.let { callback ->
                    onHandlerViewListenerAction { dialog -> callback.invoke(dialog) }
                },
                onBackButtonClickListener = onHandlerViewListenerAction { navigateToPixHome(it) },
                onIconButtonEndHeaderClickListener = onHandlerViewListenerAction { navigateToPixHome(it) },
            )
        }
    }

    protected fun navigateToPixHome(dialog: Dialog?) {
        dialog?.dismiss()
        requireActivity().toHomePix()
    }

    private fun onHandlerViewListenerAction(action: (Dialog?) -> Unit) =
        object : HandlerViewBuilderFluiV2.HandlerViewListener {
            override fun onClick(dialog: Dialog?) { action(dialog) }
        }

    data class FooterButtonConfigurator(
        val text: String,
        val onTap: () -> Unit,
        val isEnabled: Boolean = true
    )

}