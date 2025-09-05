package br.com.mobicare.cielo.pixMVVM.presentation.router

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
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
import br.com.mobicare.cielo.databinding.ActivityPixRouterNavigationFlowBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.moveToHome
import br.com.mobicare.cielo.extensions.setNavGraphStartDestination
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.mfa.router.MfaRouteHandler
import br.com.mobicare.cielo.pixMVVM.analytics.PixAnalytics
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.IS_SHOW_DATA_QUERY_ARGS

class PixRouterNavigationFlowActivity : BaseLoggedActivity(), CieloNavigation {

    private var binding: ActivityPixRouterNavigationFlowBinding? = null

    private var navigationListener: CieloNavigationListener? = null
    private var isShowHelpMenu: Boolean = true
    private var bundle: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPixRouterNavigationFlowBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setNavGraphStartDestination(
            navHostFragmentId = R.id.nav_host_pix_fragment,
            navGraphId = R.navigation.nav_graph_pix_router,
            startDestinationId = R.id.pixRouterFragment,
        )

        setupToolbar(binding?.toolbar?.toolbarMain as Toolbar, EMPTY)
        configureListeners()
        bundle = savedInstanceState
    }

    override fun onSupportNavigateUp(): Boolean {
        if (Navigation.findNavController(this, R.id.nav_host_pix_fragment).navigateUp().not())
            this.finish()

        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        this.bundle?.let {
            outState.clear()
            outState.putAll(it)
        }
    }

    override fun saveData(bundle: Bundle) {
        this.bundle?.putAll(bundle) ?: run {
            this.bundle = bundle
        }
    }

    override fun getSavedData() = this.bundle

    override fun getData(): Any = intent?.getBooleanExtra(IS_SHOW_DATA_QUERY_ARGS, false) ?: false

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
            showToolbar(true)
            binding?.toolbar?.textToolbarMainTitle?.text = title
        }
    }

    override fun setColorBackgroundButton(colorRes: Int) {
        binding?.apply {
            containerButton.setBackgroundColor(
                ContextCompat.getColor(
                    this@PixRouterNavigationFlowActivity,
                    colorRes
                )
            )
        }
    }

    private fun configureListeners() {
        binding?.apply {
            this.btnNext.setOnClickListener {
                navigationListener?.onButtonClicked(btnNext.getText())
            }

            this.btnCancel.setOnClickListener {
                navigationListener?.onFirstButtonClicked(btnCancel.getText())
            }
        }

    }

    override fun onBackPressed() {
        this.navigationListener?.onBackButtonClicked()
        super.onBackPressed()
    }

    override fun goToHome() = finish()

    override fun setTextFirstButton(text: String) {
        binding?.btnCancel?.setText(text)
    }

    override fun setTextButton(text: String) {
        binding?.btnNext?.setText(text)
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        this.navigationListener = listener
    }

    override fun showContainerButton(isShow: Boolean) {
        binding?.containerButton.visible(isShow)
    }

    override fun showFirstButton(isShow: Boolean) {
        binding?.btnCancel.visible(isShow)
    }

    override fun showButton(isShow: Boolean) {
        binding?.btnNext.visible(isShow)
    }

    override fun enableButton(isEnabled: Boolean) {
        binding?.btnNext?.isEnabled = isEnabled
    }

    override fun showLoading(isShow: Boolean) {
        binding?.progressView.visible()
        if (isShow)
            binding?.containerView?.gone()
    }

    override fun showContent(isShow: Boolean) {
        binding?.apply {
            if (isShow) {
                progressView.gone()
                hideAnimatedLoading()
            }
            containerView.visible()
        }
    }

    override fun showErrorBottomSheet(
        textButton: String?,
        @StringRes textMessage: Int?,
        error: ErrorMessage?,
        title: String?,
        isFullScreen: Boolean
    ) {
        this.hideSoftKeyboard()
        val textBtn = textButton ?: getString(R.string.ok)
        val titleError = title ?: getString(R.string.text_title_generic_error)
        val textMessageError = textMessage ?: R.string.business_error

        bottomSheetGenericFlui(
            EMPTY,
            R.drawable.ic_07,
            titleError,
            messageError(error, this, textMessageError),
            textBtn,
            textBtn,
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
            isFullScreen = isFullScreen
        ).apply {
            onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
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

    override fun showErrorBottomSheet(
        textButton: String?,
        title: String?,
        subtitle: String?,
        callToActionButton: () -> Unit,
        callToActionSwiped: () -> Unit,
        isFullScreen: Boolean
    ) {

        this.hideSoftKeyboard()

        val textTitle = title ?: getString(R.string.text_title_generic_error)
        val textSubtitle = subtitle ?: getString(R.string.text_message_generic_error)

        bottomSheetGenericFlui(
            EMPTY,
            R.drawable.ic_07,
            textTitle,
            textSubtitle,
            EMPTY,
            getString(R.string.edit_block_close_button_label),
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
            isFullScreen = isFullScreen
        ).apply {
            onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    dismiss()
                    callToActionButton.invoke()
                }

                override fun onSwipeClosed() {
                    dismiss()
                    callToActionSwiped.invoke()
                }
            }
        }.show(supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun showError(error: ErrorMessage?) {
        this.hideSoftKeyboard()
        binding?.containerView.gone()
        binding?.progressView.gone()

        with(binding?.errorView) {
            this?.visible()
            this?.cieloErrorMessage = messageError(error, this@PixRouterNavigationFlowActivity)
            this?.errorButton?.setText(getString(R.string.back))
            this?.cieloErrorTitle = getString(R.string.text_title_generic_error)
            this?.errorHandlerCieloViewImageDrawable = R.drawable.ic_generic_error_image
            this?.configureActionClickListener {
                navigationListener?.onClickSecondButtonError()
            }
        }
    }

    override fun successTermPix() {
        PixAnalytics.run {
            logScreenView(PixAnalytics.ScreenView.ADHERENCE_REQUEST_SUCCESS)
            logAdherencePurchase()
        }
        bottomSheetGenericFlui(
            nameTopBar = EMPTY,
            R.drawable.ic_pix_solicitacao_enviada,
            getString(R.string.banner_success_email_title),
            getString(R.string.banner_success_email_subtitle),
            nameBtn1Bottom = EMPTY,
            getString(R.string.text_close),
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
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE
        ).apply {
            this.onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    dialog.dismiss()
                    moveToHome()
                }

                override fun onSwipeClosed() {
                    requireActivity().finish()
                }
            }
        }.show(supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun showHelpButton(isShow: Boolean) {
        this.isShowHelpMenu = isShow
        this.invalidateOptionsMenu()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MfaRouteHandler.MFA_ACTIVITY_REQUEST_CODE && resultCode != Activity.RESULT_OK) {
            finish()
        }
    }

    override fun showToolbar(isShow: Boolean) {
        super.showToolbar(isShow)
        binding?.toolbar?.root.visible(isShow)
    }

    override fun showAnimatedLoading(@StringRes message: Int?) {
        binding?.animatedProgressView?.startAnimation(
            message = message ?: R.string.wait_animated_loading_start_message,
            isUpdateMessage = false
        )
    }

    override fun hideAnimatedLoading() {
        binding?.animatedProgressView.gone()
    }

    override fun showCustomHandler(
        contentImage: Int,
        headerImage: Int,
        message: String,
        title: String,
        titleAlignment: Int,
        messageAlignment: Int,
        labelFirstButton: String,
        labelSecondButton: String,
        isShowFirstButton: Boolean,
        isShowSecondButton: Boolean,
        firstButtonCallback: () -> Unit,
        secondButtonCallback: () -> Unit,
        headerCallback: () -> Unit,
        finishCallback: () -> Unit,
        isBack: Boolean,
        isShowButtonBack: Boolean,
        isShowHeaderImage: Boolean
    ) {
        binding?.customHandlerView?.apply {
            this.title = title
            this.message = message
            this.labelOutlined = labelFirstButton
            this.labelContained = labelSecondButton

            this.isShowButtonBack = isShowButtonBack
            this.isShowHeaderImage = isShowHeaderImage
            this.isShowButtonOutlined = isShowFirstButton
            this.isShowContainerButton = isShowSecondButton

            this.headerImage = headerImage
            this.contentImage = contentImage

            this.titleAlignment = titleAlignment
            this.messageAlignment = messageAlignment

            this.setBackClickListener {
                closeHandlerView()
                finishCallback.invoke()
            }

            this.setHeaderClickListener {
                closeHandlerView()
                headerCallback.invoke()
            }

            this.setButtonOutlinedClickListener {
                closeHandlerView()
                firstButtonCallback.invoke()
            }

            this.setButtonContainedClickListener {
                closeHandlerView()
                secondButtonCallback.invoke()
            }
        }

        showHandlerView()
    }

    private fun showHandlerView() {
        hideSoftKeyboard()
        binding?.apply {
            toolbar.root.gone()
            containerView.gone()
            customHandlerView.visible()
        }
    }

    private fun closeHandlerView() {
        binding?.apply {
            toolbar.root.visible()
            containerView.visible()
            customHandlerView.gone()
        }
    }

}