package br.com.mobicare.cielo.accessManager.invite.receive.ui.legacy.invite

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.databinding.ActivityLegacyUserInviteReceiveNavigationBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible

class LegacyUserInviteReceiveNavigationActivity : BaseActivity(), CieloNavigation {

    private var binding: ActivityLegacyUserInviteReceiveNavigationBinding? = null
    private var navigationListener: CieloNavigationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLegacyUserInviteReceiveNavigationBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        with(intent?.extras ?: Bundle()) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController.setGraph(
                R.navigation.nav_graph_legacy_user_receive_invite,
                this
            )
        }
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        this.navigationListener = listener
    }

    override fun showLoading(
        isShow: Boolean,
        @StringRes message: Int?,
        vararg messageArgs: String
    ) {
        hideSoftKeyboard()
        binding?.apply {
            if (isShow) {
                messageProgressView.showLoading(message, *messageArgs)
                containerView.gone()
                errorView.gone()
            } else {
                containerView.visible()
                messageProgressView.hideLoading(successMessage = message, messageArgs = messageArgs)
            }
        }

    }

    override fun showContent(
        isShow: Boolean,
        @StringRes loadingSuccessMessage: Int?,
        loadingSuccessCallback: (() -> Unit)?,
        vararg messageArgs: String
    ) {
        binding?.apply {
            containerView.visible(isShow)
            if (isShow) {
                messageProgressView.hideLoading(
                    successMessage = loadingSuccessMessage,
                    loadingSuccessCallback,
                    *messageArgs
                )
                errorView.gone()
            }
        }

    }

    override fun showCustomBottomSheet(
        image: Int?,
        title: String?,
        message: String?,
        bt1Title: String?,
        bt2Title: String?,
        bt1Callback: (() -> Boolean)?,
        bt2Callback: (() -> Boolean)?,
        closeCallback: (() -> Unit)?,
        isCancelable: Boolean,
        isPhone: Boolean
    ) {
        lifecycleScope.launchWhenResumed {
            bottomSheetGenericFlui(
                image = image ?: R.drawable.ic_generic_error_image,
                title = title ?: getString(R.string.generic_error_title),
                subtitle = message ?: getString(R.string.error_generic),
                nameBtn1Bottom = bt1Title ?: "",
                nameBtn2Bottom = bt2Title ?: getString(R.string.ok),
                txtTitleStyle = TxtTitleStyle.TXT_TITLE_DARK_BLUE,
                txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_FLUI_BOTTOM_SHEET,
                statusBtnFirst = bt1Title != null,
                btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE_TXT_BLUE,
                isCancelable = isCancelable,
                isFullScreen = true,
                isPhone = isPhone
            ).apply {
                onClick =
                    object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {

                        override fun onBtnFirst(dialog: Dialog) {
                            if (bt1Callback?.invoke() != true) dismiss()
                        }

                        override fun onBtnSecond(dialog: Dialog) {
                            if (bt2Callback?.invoke() != true) dismiss()
                        }

                        override fun onSwipeClosed() {
                            closeCallback?.invoke()
                        }

                        override fun onCancel() {
                            closeCallback?.invoke()
                        }
                    }
            }.show(
                supportFragmentManager,
                getString(R.string.bottom_sheet_generic)
            )
        }
    }
}