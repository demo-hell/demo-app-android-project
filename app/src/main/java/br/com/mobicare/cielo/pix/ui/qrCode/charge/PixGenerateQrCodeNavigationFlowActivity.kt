package br.com.mobicare.cielo.pix.ui.qrCode.charge

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
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
import br.com.mobicare.cielo.databinding.ActivityPixQrCodeNavigationFlowBinding
import br.com.mobicare.cielo.databinding.ToolbarMainBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.PIX_MY_KEYS_ARGS
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixKeysResponse

class PixGenerateQrCodeNavigationFlowActivity : BaseLoggedActivity(), CieloNavigation {

    private var navigationListener: CieloNavigationListener? = null
    private var isShowHelpMenu: Boolean = true
    private var bundle: Bundle? = null

    private lateinit var binding: ActivityPixQrCodeNavigationFlowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPixQrCodeNavigationFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: ToolbarMainBinding = binding.toolbar
        setupToolbar(toolbar.toolbarMain, EMPTY)

        configureListeners()
        bundle = savedInstanceState
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

    override fun getData(): Any? = intent?.getParcelableArrayListExtra<PixKeysResponse.KeyItem>(PIX_MY_KEYS_ARGS)

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

    override fun showHelpButton(isShow: Boolean) {
        this.isShowHelpMenu = isShow
        this.invalidateOptionsMenu()
    }

    override fun setTextToolbar(title: String) {
        if (title.isNotBlank()) {
            val toolbarTitleTextView = findViewById<AppCompatTextView>(R.id.textToolbarMainTitle)
            toolbarTitleTextView?.text = title
        }
    }

    override fun setColorBackgroundButton(colorRes: Int) {
        binding.containerButton.setBackgroundColor(ContextCompat.getColor(this, colorRes))
    }

    private fun configureListeners() {
        binding.btnNext.setOnClickListener {
            this.navigationListener?.onButtonClicked(binding.btnNext.getText())
        }

        binding.btnCancel.setOnClickListener {
            this.navigationListener?.onFirstButtonClicked(binding.btnCancel.getText())
        }
    }

    override fun onBackPressed() {
        this.navigationListener?.onBackButtonClicked()
        super.onBackPressed()
    }

    override fun setTextFirstButton(text: String) {
        binding.btnCancel.setText(text)
    }

    override fun setTextButton(text: String) {
        binding.btnNext.setText(text)
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }

    override fun showContainerButton(isShow: Boolean) {
        binding.containerButton.visible(isShow)
    }

    override fun showFirstButton(isShow: Boolean) {
        binding.btnCancel.visible(isShow)
    }

    override fun showButton(isShow: Boolean) {
        binding.btnNext.visible(isShow)
    }

    override fun enableButton(isEnabled: Boolean) {
        binding.btnNext.isEnabled = isEnabled
    }

    override fun showLoading(isShow: Boolean) {
        binding.progressView.visible()
        if (isShow)
            binding.containerView.gone()
    }

    override fun showContent(isShow: Boolean) {
        binding.containerView.visible()
        if (isShow)
            binding.progressView.gone()
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
                    navigationListener?.onActionSwipe()
                }
            }
        }.show(supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }
}