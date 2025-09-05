package br.com.mobicare.cielo.accessManager

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.navigation.Navigation
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.messageError
import br.com.mobicare.cielo.databinding.ActivityAccessManagerNavigationBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.isAvailable
import br.com.mobicare.cielo.extensions.showBottomSheet
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.EMPTY

class AccessManagerNavigationActivity : BaseLoggedActivity(), CieloNavigation {

    private var bundle: Bundle? = null
    private var navigationListener: CieloNavigationListener? = null
    private var isShowHelpMenu: Boolean = true

    private var binding: ActivityAccessManagerNavigationBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccessManagerNavigationBinding.inflate(layoutInflater)
        savedInstanceState?.let {
            this.bundle = it
        }
        binding?.apply {
            setContentView(root)
            setupToolbar(toolbar.root, "")
        }

        changeStatusBarColor()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        bundle?.let {
            outState.clear()
            outState.putAll(it)
        }
    }

    override fun saveData(bundle: Bundle) {
        this.bundle?.putAll(bundle) ?: run {
            this.bundle = bundle
        }
    }

    override fun getSavedData() = bundle

    override fun onSupportNavigateUp(): Boolean {
        if (Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp().not())
            this.finish()

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isShowHelpMenu)
            menuInflater.inflate(R.menu.menu_common_faq, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_help -> {
                this.navigationListener?.onHelpButtonClicked()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun setTextToolbar(title: String) {
        if (title.isNotBlank()) {
            val toolbarTitleTextView = findViewById<AppCompatTextView>(R.id.textToolbarMainTitle)
            toolbarTitleTextView.text = title
        }
    }

    override fun onBackPressed() {
        this.navigationListener?.onBackButtonClicked()
        super.onBackPressed()
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }

    override fun showLoading(
        isShow: Boolean,
        @StringRes message: Int?,
        vararg messageArgs: String
    ) {
        binding?.apply {
            if (isShow) {
                messageProgressView.showLoading(message, *messageArgs)
                containerView.gone()
                errorView.gone()
            } else {
                containerView.visible()
                messageProgressView.hideLoading(
                    successMessage = message,
                    messageArgs = *messageArgs
                )
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

    override fun showErrorBottomSheet(
        textButton: String?,
        @StringRes textMessage: Int?,
        error: ErrorMessage?,
        title: String?,
        isFullScreen: Boolean
    ) {
        if (isAvailable()) {
            hideSoftKeyboard()
            binding?.messageProgressView?.hideLoading()

            val titleText = title ?: getString(R.string.generic_error_title)
            val textBtn = textButton ?: getString(R.string.ok)
            val textMessageError = textMessage ?: R.string.business_error

            bottomSheetGenericFlui(
                EMPTY,
                R.drawable.ic_07,
                titleText,
                messageError(error, this, textMessageError),
                textBtn,
                getString(R.string.text_try_again_label),
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
                isFullScreen = isFullScreen
            ).apply {
                onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnFirst(dialog: Dialog) {
                        dismiss()
                        navigationListener?.onRetry()
                    }

                    override fun onBtnSecond(dialog: Dialog) {
                        dismiss()
                        navigationListener?.onClickSecondButtonError()
                    }

                    override fun onSwipeClosed() {
                        dismiss()
                        navigationListener?.onActionSwipe()
                    }
                }
            }.show(supportFragmentManager, getString(R.string.bottom_sheet_generic))
        }
    }

    override fun showError(error: ErrorMessage?) {
        binding?.apply {
            hideSoftKeyboard()
            containerView.gone()
            messageProgressView.hideLoading()

            errorView.run {
                visible()
                cieloErrorMessage = messageError(error, this@AccessManagerNavigationActivity)
                errorButton?.setText(getString(R.string.back))
                cieloErrorTitle = getString(R.string.generic_error_title)
                errorHandlerCieloViewImageDrawable = R.drawable.ic_generic_error_image
                configureActionClickListener {
                    navigationListener?.onRetry()
                }
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
        isPhone: Boolean,
        titleBlack: Boolean
    ) {
        showBottomSheet(
            image,
            title,
            message,
            bt1Title,
            bt2Title,
            bt1Callback,
            bt2Callback,
            closeCallback,
            isCancelable,
            isPhone,
            titleBlack
        )
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
        showBottomSheet(
            image,
            title,
            message,
            bt1Title,
            bt2Title,
            bt1Callback,
            bt2Callback,
            closeCallback,
            isCancelable,
            isPhone
        )
    }

    override fun showHelpButton(isShow: Boolean) {
        isShowHelpMenu = isShow
        invalidateOptionsMenu()
    }

    override fun onPause() {
        super.onPause()
        navigationListener?.onPauseActivity()
    }
}