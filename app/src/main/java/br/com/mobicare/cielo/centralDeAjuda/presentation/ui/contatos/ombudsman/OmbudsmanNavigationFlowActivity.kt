package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos.ombudsman

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
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
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.EMPTY
import kotlinx.android.synthetic.main.activity_ombudsman_navigation_flow.*

class OmbudsmanNavigationFlowActivity : BaseLoggedActivity(), CieloNavigation {

    private var navigationListener: CieloNavigationListener? = null
    private var isShowHelpMenu: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ombudsman_navigation_flow)

        setupToolbar(toolbar as Toolbar)
        configureListeners()
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

    override fun setTextToolbar(title: String) {
        if (title.isNotBlank()) {
            val toolbarTitleTextView = findViewById<AppCompatTextView>(R.id.textToolbarMainTitle)
            toolbarTitleTextView?.text = title
        }
    }

    override fun setColorBackgroundButton(colorRes: Int) {
        container_button?.setBackgroundColor(ContextCompat.getColor(this, colorRes))
    }

    private fun configureListeners() {
        this.btn_next?.setOnClickListener {
            this.navigationListener?.onButtonClicked(btn_next.getText())
        }
    }

    override fun onBackPressed() {
        this.navigationListener?.onBackButtonClicked()
        super.onBackPressed()
    }

    override fun setTextButton(text: String) {
        this.btn_next?.setText(text)
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        this.navigationListener = listener
    }

    override fun showButton(isShow: Boolean) {
        this.container_button?.visible(isShow)
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

    override fun showErrorBottomSheet(textButton: String?, @StringRes textMessage: Int?, error: ErrorMessage?, title: String?, isFullScreen: Boolean) {
        this.hideSoftKeyboard()
        val textBtn = textButton ?: getString(R.string.ok)
        val textMessageError = textMessage?: R.string.business_error

        bottomSheetGenericFlui(
                nameTopBar = EMPTY,
                R.drawable.ic_07,
                getString(R.string.text_title_generic_error),
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

    override fun showHelpButton(isShow: Boolean) {
        this.isShowHelpMenu = isShow
        this.invalidateOptionsMenu()
    }
}
