package br.com.mobicare.cielo.chargeback.presentation

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.navigation.Navigation
import br.com.cielo.libflue.util.extensions.gone
import br.com.cielo.libflue.util.extensions.visible
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.dp
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.databinding.ActivityChargebackNavigationFlowBinding
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef


class ChargebackNavigationFlowActivity : BaseLoggedActivity(), CieloNavigation {

    private lateinit var binding: ActivityChargebackNavigationFlowBinding
    private var navigation: CieloNavigationListener? = null
    private var isShowHelpMenu: Boolean = false
    private var bundle: Bundle? = null
    private var isBack = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChargebackNavigationFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding.toolbar)
        changeStatusBarColor()
        setupListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        bundle?.let {
            outState.apply {
                clear()
                putAll(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isShowHelpMenu) {
            menuInflater.inflate(R.menu.menu_faq_chargeback_white, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_help -> openFaqChargeback()
            android.R.id.home -> onBackPressedDispatcher.onBackPressed()
        }

        return true
    }

    override fun saveData(bundle: Bundle) {
        this.bundle?.putAll(bundle) ?: run {
            this.bundle = bundle
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp().not())
            this.finish()
        return true
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigation = listener
    }

    override fun getSavedData() = bundle

    private fun setupListeners() {
        binding.apply {
            btnSecond.setOnClickListener {
                navigation?.onButtonClicked(binding.btnSecond.text.toString())
            }
            btnFirst.setOnClickListener {
                navigation?.onFirstButtonClicked(btnFirst.getText())
            }
        }
    }

    private fun openFaqChargeback() {
        openFaq(
            tag = ConfigurationDef.TAG_HELP_CENTER_CONTESTACAO,
            subCategoryName = getString(R.string.text_values_chargeback_faq_label)
        )
    }

    override fun showHelpButton(isShow: Boolean) {
        isShowHelpMenu = isShow
        invalidateOptionsMenu()
    }


    override fun onBackPressed() {
        if (isBack) {
            this.navigation?.onBackButtonClicked()
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun showContainerButton(isShow: Boolean) {
        binding.containerButton.visible(isShow)
    }

    override fun showButton(isShow: Boolean) {
        binding.btnSecond.visible(isShow)
    }

    override fun setTextFirstButton(text: String) {
        binding.btnFirst.setText(text)
    }

    override fun showFirstButton(isShow: Boolean) {
        binding.btnFirst.visible(isShow)
    }

    override fun setTextButton(text: String) {
        binding.btnSecond.text = text
    }

    override fun enableButton(isEnabled: Boolean) {
        binding.btnSecond.isEnabled = isEnabled
    }

    override fun showCustomHandlerView(
        @DrawableRes contentImage: Int,
        @DrawableRes headerImage: Int,
        @StyleRes titleStyle: Int,
        @StyleRes messageStyle: Int,
        message: String,
        title: String,
        messageMargin: Int,
        titleAlignment: Int,
        messageAlignment: Int,
        labelFirstButton: String,
        labelSecondButton: String,
        isShowButtonBack: Boolean,
        isShowButtonClose: Boolean,
        isShowFirstButton: Boolean,
        isShowSecondButton: Boolean,
        callbackFirstButton: () -> Unit,
        callbackSecondButton: () -> Unit,
        callbackClose: () -> Unit,
        callbackBack: () -> Unit
    ) {
        showHandlerView()
        binding.customHandlerView.apply {
            this.title = title
            this.message = message
            this.labelOutlined = labelFirstButton
            this.labelContained = labelSecondButton

            this.isShowButtonBack = isShowButtonBack
            this.isShowHeaderImage = isShowButtonClose
            this.isShowButtonOutlined = isShowFirstButton
            this.isShowButtonContained = isShowSecondButton

            this.headerImage = headerImage
            this.contentImage = contentImage

            this.titleAlignment = titleAlignment
            this.messageAlignment = messageAlignment

            if (titleStyle != ONE_NEGATIVE) this.titleStyle = titleStyle
            if (messageStyle != ONE_NEGATIVE) this.messageStyle = messageStyle

            this.setMessageMargin(messageMargin.dp())

            this.setBackClickListener {
                closeHandlerView()
                callbackBack.invoke()
            }

            this.setHeaderClickListener {
                closeHandlerView()
                callbackClose.invoke()
            }

            this.setButtonOutlinedClickListener {
                closeHandlerView()
                callbackFirstButton.invoke()
            }

            this.setButtonContainedClickListener {
                closeHandlerView()
                callbackSecondButton.invoke()
            }
        }
    }

    private fun showHandlerView() {
        isBack = false
        hideSoftKeyboard()
        binding.containerView.gone()
        binding.customHandlerView.visible()
    }

    private fun closeHandlerView() {
        isBack = true
        binding.containerView.visible()
        binding.customHandlerView.gone()
    }

    override fun setTextToolbar(title: String) {
        super.setTextToolbar(title)
        binding.toolbar.title = title
    }

}