package br.com.mobicare.cielo.arv.presentation

import android.app.Activity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.navigation.Navigation
import br.com.cielo.libflue.highlightManager.extension.drawHighlight
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.presentation.home.utils.ArvNavigation
import br.com.mobicare.cielo.arv.utils.ArvConstants.ARV_ANTICIPATION
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_SUBCATEGORY_NAME
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.dp
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.databinding.ActivityArvNavigationFlowBinding
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.jetbrains.anko.startActivity

class ArvNavigationFlowActivity : BaseLoggedActivity(), CieloNavigation, ArvNavigation {
    private lateinit var binding: ActivityArvNavigationFlowBinding

    private var isBack = true

    private var bundle: Bundle? = null
    private var navigation: CieloNavigationListener? = null
    private var arvNavigation: ArvNavigation.Listener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArvNavigationFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleArvAnticipationBundleVerification()
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

    override fun saveData(bundle: Bundle) {
        this.bundle?.putAll(bundle) ?: run {
            this.bundle = bundle
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp().not()) {
            finish()
        }

        return true
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigation = listener
    }

    override fun setArvNavigationListener(listener: ArvNavigation.Listener) {
        arvNavigation = listener
    }

    override fun getSavedData() = bundle

    override fun onBackPressed() {
        if (isBack) {
            navigation?.onBackButtonClicked()
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun setupToolbar(
        title: String,
        isCollapsed: Boolean,
    ) {
        binding.apply {
            tvTitle.text = title
            navigationIcon.setOnClickListener {
                navigation?.onBackPressed()
                onBackPressed()
            }
            helpIcon.setOnClickListener {
                startHelpCenter(ConfigurationDef.TAG_HELP_CENTER_ARV)
                navigation?.onHelpButtonClicked()
            }
            waIcon.apply {
                gone()
                setOnClickListener {
                    arvNavigation?.onArvWhatsAppButtonClicked()
                }
            }
            if (isCollapsed) {
                root.transitionToEnd()
            } else {
                root.transitionToStart()
            }
        }
    }

    override fun showHelpButton(isShow: Boolean) {
        binding.helpIcon.visible(isShow)
    }

    override fun showArvWhatsAppButton(show: Boolean) {
        binding.waIcon.visible(show)
    }

    override fun setHighlightOnArvWhatsAppButton() {
        binding.waIcon.drawHighlight(
            this,
            title = getString(R.string.anticipation_whatsapp_tooltip),
        )
    }

    override fun showContainerButton(isShow: Boolean) {
        binding.containerButton.visible(isShow)
    }

    override fun setTextFirstButton(text: String) {
        binding.btnFirst.text = text
    }

    override fun setTextButton(text: String) {
        binding.btnSecond.text = text
    }

    override fun showButton(isShow: Boolean) {
        binding.btnSecond.visible(isShow)
    }

    override fun showFirstButton(isShow: Boolean) {
        binding.btnFirst.visible(isShow)
    }

    override fun enableButton(isEnabled: Boolean) {
        binding.btnSecond.isEnabled = isEnabled
    }

    private fun handleArvAnticipationBundleVerification() {
        if (intent.hasExtra(ARV_ANTICIPATION)) {
            handleArvAnticipationBundleSaving()
        }
    }

    private fun handleArvAnticipationBundleSaving() {
        val arvAnticipation =
            intent.getParcelableExtra<ArvAnticipation>(ARV_ANTICIPATION) as ArvAnticipation
        val bundle =
            Bundle().apply {
                putParcelable(ARV_ANTICIPATION, arvAnticipation)
            }
        saveData(bundle)
    }

    private fun setupListeners() {
        binding.apply {
            btnSecond.setOnClickListener {
                navigation?.onButtonClicked(btnSecond.text.toString())
            }

            btnFirst.setOnClickListener {
                navigation?.onFirstButtonClicked(btnFirst.text.toString())
            }
        }
    }

    override fun goToHome() {
        backToHome()
        finishAndRemoveTask()
    }

    override fun startHelpCenter(tagKey: String) {
        startActivity<CentralAjudaSubCategoriasEngineActivity>(
            ConfigurationDef.TAG_KEY_HELP_CENTER to tagKey,
            ARG_PARAM_SUBCATEGORY_NAME to getString(R.string.text_title_arv_toolbar),
            CentralAjudaSubCategoriasEngineActivity.NOT_CAME_FROM_HELP_CENTER to true,
        )
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
        callbackBack: () -> Unit,
    ) {
        showHandlerView()
        binding.customHandlerView.apply {
            this.title = title
            this.titleStyle = titleStyle
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

    private fun showHandlerView(isBackAction: Boolean = true) {
        if (isBackAction) {
            isBack = false
        }

        hideSoftKeyboard()
        binding.containerView.gone()
        binding.customHandlerView.visible()
    }

    private fun closeHandlerView(isBackAction: Boolean = true) {
        if (isBackAction) {
            isBack = true
        }

        binding.containerView.visible()
        binding.customHandlerView.gone()
    }

    override fun showCustomHandlerViewWithHelp(
        contentImage: Int,
        message: String,
        title: String,
        messageMargin: Int,
        titleAlignment: Int,
        messageAlignment: Int,
        labelFirstButton: String,
        labelSecondButton: String,
        isShowFirstButton: Boolean,
        isShowSecondButton: Boolean,
        callbackFirstButton: () -> Unit,
        callbackSecondButton: () -> Unit,
    ) {
        showHandlerView(isBackAction = false)
        binding.customHandlerView.apply {
            this.title = title
            this.message = message
            this.labelOutlined = labelFirstButton
            this.labelContained = labelSecondButton
            this.labelColorButtonOutlined = R.color.brand_400

            this.isShowButtonBack = true
            this.isShowHeaderImage = true
            this.isShowButtonOutlined = isShowFirstButton
            this.isShowButtonContained = isShowSecondButton

            this.contentImage = contentImage
            this.headerImage = R.drawable.ic_chargeback_help_24

            this.titleAlignment = titleAlignment
            this.messageAlignment = messageAlignment

            this.setMessageMargin(messageMargin.dp())

            this.setBackClickListener {
                finishAndRemoveTask()
            }

            this.setHeaderClickListener {
                startHelpCenter(ConfigurationDef.TAG_HELP_CENTER_ARV)
            }

            this.setButtonOutlinedClickListener {
                callbackFirstButton.invoke()
            }

            this.setButtonContainedClickListener {
                callbackSecondButton.invoke()
            }
        }
    }

    override fun showAnimatedLoading(
        @StringRes message: Int?,
    ) {
        binding.arvLoadingView.startAnimation(message = message ?: R.string.wait_a_moment_message)
    }

    override fun hideAnimatedLoading() {
        binding.arvLoadingView.hideAnimationStart()
    }

    override fun changeAnimatedLoadingText(message: Int?) {
        binding.animatedProgressView.setMessage(message)
    }

    override fun hideAnimatedLoadingWithoutMfa() {
        binding.animatedProgressView.hideAnimationStart()
    }

    override fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, ZERO)
    }

    override fun showAnimatedLoadingSuccess(
        message: Int?,
        onAction: () -> Unit,
    ) {
        binding.arvLoadingView.showAnimationSuccess {
            onAction.invoke()
        }
    }
}
