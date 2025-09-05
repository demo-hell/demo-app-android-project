package br.com.mobicare.cielo.pix.ui.mylimits

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
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
import br.com.mobicare.cielo.databinding.ActivityPixMyLimitsNavigationFlowBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.PIX_CONTACT_ADD_SUCCESS_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_MY_LIMITS_IS_HOME_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_SHOW_DIALOG_ADD_NEW_RELIABLE_CONTACT_ARGS

class PixMyLimitsNavigationFlowActivity : BaseLoggedActivity(), CieloNavigation {

    private val isHome: Boolean by lazy {
        intent?.extras?.getBoolean(PIX_MY_LIMITS_IS_HOME_ARGS) ?: false
    }
    private val contactAddSuccess: Boolean by lazy {
        intent?.extras?.getBoolean(PIX_CONTACT_ADD_SUCCESS_ARGS) ?: false
    }
    private var binding: ActivityPixMyLimitsNavigationFlowBinding? = null
    private var navigationListener: CieloNavigationListener? = null
    private var isShowHelpMenu: Boolean = true
    private var bundle: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPixMyLimitsNavigationFlowBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupToolbar(binding?.toolbar?.toolbarMain as Toolbar, EMPTY)

        setNavigationGraph()
        configureListeners()
        bundle = savedInstanceState
    }

    private fun setNavigationGraph() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph_pix_my_limits)
        val destination = if (isHome)
            R.id.pixMyLimitsFragment
        else
            R.id.pixMyLimitsTrustedDestinationsFragment
        navGraph.setStartDestination(destination)

        navController.setGraph(navGraph, bundleOf(PIX_SHOW_DIALOG_ADD_NEW_RELIABLE_CONTACT_ARGS to contactAddSuccess))
    }

    override fun onSupportNavigateUp(): Boolean {
        if (Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp().not())
            this.finish()

        return true
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

    override fun getSavedData() = this.bundle

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
            binding?.toolbar?.textToolbarMainTitle?.text = title
        }
    }

    override fun setColorBackgroundButton(colorRes: Int) {
        binding?.containerButton?.setBackgroundColor(ContextCompat.getColor(this@PixMyLimitsNavigationFlowActivity, colorRes))
    }

    private fun configureListeners() {
        binding?.apply{
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
        binding?.containerView?.visible()
        if (isShow)
            binding?.progressView.gone()
    }

    override fun showErrorBottomSheet(
        textButton: String?,
        @StringRes textMessage: Int?,
        error: ErrorMessage?,
        title: String?,
        isFullScreen: Boolean
    ) {
        hideSoftKeyboard()
        val textBtn = textButton ?: getString(R.string.ok)
        val titleError = title ?: getString(R.string.text_title_generic_error)
        val textMessageError = textMessage?: R.string.business_error

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

                override fun onCancel() {
                    dismiss()
                    navigationListener?.onActionSwipe()
                }
            }
        }.show(supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun showWarningBottomSheet(
        image: Int,
        message: String,
        title: String?,
        bt1Title: String?,
        bt2Title: String?,
        isShowBt1: Boolean,
        isShowBt2: Boolean,
        bt1Callback: () -> Unit,
        bt2Callback: () -> Unit,
        closeCallback: () -> Unit,
        isFullScreen: Boolean,
        isPhone: Boolean
    ) {
        hideSoftKeyboard()
        val btn = bt2Title ?: getString(R.string.ok)
        val titleError = title ?: getString(R.string.text_title_generic_error)

        bottomSheetGenericFlui(
            image = image,
            title = titleError,
            subtitle = message,
            nameBtn1Bottom = bt1Title ?: btn,
            nameBtn2Bottom = btn,
            statusNameTopBar = false,
            statusBtnClose = false,
            statusBtnFirst = isShowBt1,
            statusBtnSecond = isShowBt2,
            statusView2Line = false,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = isFullScreen,
            isPhone = isPhone
        ).apply {
            onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnFirst(dialog: Dialog) {
                    dismiss()
                    bt1Callback.invoke()
                }

                override fun onBtnSecond(dialog: Dialog) {
                    dismiss()
                    bt2Callback.invoke()
                }

                override fun onSwipeClosed() {
                    closeCallback.invoke()
                }

                override fun onCancel() {
                    closeCallback.invoke()
                }
            }
        }.show(supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun showHelpButton(isShow: Boolean) {
        this.isShowHelpMenu = isShow
        this.invalidateOptionsMenu()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}