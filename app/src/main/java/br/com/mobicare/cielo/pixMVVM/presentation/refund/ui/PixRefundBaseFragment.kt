package br.com.mobicare.cielo.pixMVVM.presentation.refund.ui

import android.app.Dialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.DrawableRes
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.toPixHomeExtract
import br.com.mobicare.cielo.pixMVVM.presentation.extract.PixNewExtractNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.presentation.refund.PixRefundNavigationFlowActivity
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef

open class PixRefundBaseFragment : BaseFragment() {

    private val navArgsData by lazy {
        navigation?.getData() as? PixRefundNavigationFlowActivity.NavArgs.Data
    }

    protected var navigation: CieloNavigation? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigation = requireActivity() as? CieloNavigation
    }

    protected fun showMessageScreen(
        title: String,
        message: String,
        @DrawableRes imageRes: Int,
        primaryButtonText: String,
        onPrimaryButtonTap: (Dialog?) -> Unit,
        secondaryButtonText: String? = null,
        onSecondaryButtonTap: ((Dialog?) -> Unit)? = null,
        onDismissTap: ((Dialog?) -> Unit)? = null,
        showCloseIconButton: Boolean = false,
    ) {
        doWhenResumed {
            navigation?.showHandlerViewV2(
                title = title,
                message = message,
                illustration = imageRes,
                isShowBackButton = false,
                isShowIconButtonEndHeader = showCloseIconButton,
                labelPrimaryButton = primaryButtonText,
                labelSecondaryButton = secondaryButtonText.orEmpty(),
                onPrimaryButtonClickListener = object : HandlerViewBuilderFluiV2.HandlerViewListener {
                    override fun onClick(dialog: Dialog?) = onPrimaryButtonTap(dialog)
                },
                onSecondaryButtonClickListener = onSecondaryButtonTap?.let {
                    object : HandlerViewBuilderFluiV2.HandlerViewListener {
                        override fun onClick(dialog: Dialog?) = it.invoke(dialog)
                    }
                },
                onBackButtonClickListener = object : HandlerViewBuilderFluiV2.HandlerViewListener {
                    override fun onClick(dialog: Dialog?) = onDismissTap?.invoke(dialog) ?: onCloseTap(dialog)
                },
                onIconButtonEndHeaderClickListener = object : HandlerViewBuilderFluiV2.HandlerViewListener {
                    override fun onClick(dialog: Dialog?) = onDismissTap?.invoke(dialog) ?: onCloseTap(dialog)
                }
            )
        }
    }

    protected fun onMenuOptionSelected(menuItem: MenuItem) {
        if (menuItem.itemId == R.id.menuActionHelp) {
            requireActivity().openFaq(
                tag = ConfigurationDef.TAG_HELP_CENTER_PIX,
                subCategoryName = getString(R.string.cielo_facilita_central_de_ajuda_pix)
            )
        }
    }

    protected fun onCloseTap(dialog: Dialog? = null) {
        dialog?.dismiss()
    }

    protected fun navigateToPixHomeExtract(dialog: Dialog?) {
        requireActivity().toPixHomeExtract(
            PixNewExtractNavigationFlowActivity.NavArgs.PROFILE_TYPE to navArgsData?.profileType,
            PixNewExtractNavigationFlowActivity.NavArgs.PIX_ACCOUNT to navArgsData?.pixAccount
        )
    }

}