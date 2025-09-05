package br.com.mobicare.cielo.pix.ui.keys.myKeys

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
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
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.messageError
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.EMPTY
import kotlinx.android.synthetic.main.activity_pix_my_keys_navigation_flow.*

class PixMyKeysNavigationFlowActivity : BaseLoggedActivity(), CieloNavigation {

    private var navigationListener: CieloNavigationListener? = null
    private var isShowHelpMenu: Boolean = true
    private var bundle: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pix_my_keys_navigation_flow)

        setupToolbar(toolbar as Toolbar, "")

        with(intent?.extras ?: Bundle()) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController.setGraph(R.navigation.nav_graph_pix_my_keys, this)
        }

        configureListeners()
        bundle = savedInstanceState
    }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        this.bundle?.let {
            outState.clear()
            outState.putAll(it)
        }
    }

    override fun saveData(bundle: Bundle) {
        this.bundle?.let {
            it.putAll(bundle)
        } ?: run {
            this.bundle = bundle
        }
    }

    override fun getSavedData() = this.bundle

    override fun setTextToolbar(title: String) {
        if (title.isNotBlank()) {
            val toolbarTitleTextView = findViewById<AppCompatTextView>(R.id.textToolbarMainTitle)
            toolbarTitleTextView.text = title
        }
    }

    override fun setColorBackgroundButton(colorRes: Int) {
        container_button?.setBackgroundColor(ContextCompat.getColor(this, colorRes))
    }

    private fun configureListeners() {
        this.btn_next?.setOnClickListener {
            this.navigationListener?.onButtonClicked(btn_next.getText())
        }
        this.btn_cancel?.setOnClickListener {
            this.navigationListener?.onFirstButtonClicked(btn_cancel.getText())
        }
    }

    override fun onBackPressed() {
        this.navigationListener?.onBackButtonClicked()
        super.onBackPressed()
    }

    override fun setTextFirstButton(text: String) {
        this.btn_cancel?.setText(text)
    }

    override fun setTextButton(text: String) {
        this.btn_next?.setText(text)
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        this.navigationListener = listener
    }

    override fun showContainerButton(isShow: Boolean) {
        this.container_button?.visible(isShow)
    }

    override fun showFirstButton(isShow: Boolean) {
        this.btn_cancel.visible(isShow)
    }

    override fun showButton(isShow: Boolean) {
        this.btn_next?.visible(isShow)
    }

    override fun enableButton(isEnabled: Boolean) {
        this.btn_next?.isEnabled = isEnabled
    }

    override fun showLoading(isShow: Boolean) {
        this.progressView?.visible()
        if (isShow)
            this.container_view?.gone()
    }

    override fun showContent(isShow: Boolean) {
        this.container_view?.visible()
        if (isShow)
            this.progressView?.gone()
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
            this.onClick =
                object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {

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

    override fun showHelpButton(isShow: Boolean) {
        this.isShowHelpMenu = isShow
        this.invalidateOptionsMenu()
    }
}