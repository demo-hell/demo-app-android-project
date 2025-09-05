package br.com.mobicare.cielo.tapOnPhone.presentation

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.navigation.Navigation
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.databinding.ActivityTapOnPhoneNavigationFlowBinding
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneAnalytics
import br.com.mobicare.cielo.tapOnPhone.utils.TapOnPhoneConstants.TAP_ON_PHONE_ACTIVITY_WAS_OPENED_BY_POS_VIRTUAL_ARGS
import br.com.mobicare.cielo.tapOnPhone.utils.TapOnPhoneConstants.TAP_ON_PHONE_HAS_CARD_READER_ARGS
import org.koin.android.ext.android.inject

class TapOnPhoneNavigationFlowActivity : BaseLoggedActivity(), CieloNavigation {

    private lateinit var binding: ActivityTapOnPhoneNavigationFlowBinding

    private var navigation: CieloNavigationListener? = null
    private var bundle: Bundle? = null
    private var bundlePos: Bundle? = null

    private var isBack = true
    private var isShowHelpMenu: Boolean = false
    private var isShowCloseMenu: Boolean = false

    private val analytics: TapOnPhoneAnalytics by inject()

    private val hasCardReader
        get() = intent.getBooleanExtra(
            TAP_ON_PHONE_HAS_CARD_READER_ARGS,
            false
        )
    private val wasOpenedByPOSVirtual
        get() = intent.getBooleanExtra(
            TAP_ON_PHONE_ACTIVITY_WAS_OPENED_BY_POS_VIRTUAL_ARGS,
            false
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTapOnPhoneNavigationFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar)
        changeStatusBarColor()
        setupListeners()
        saveIntent()
    }

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

    private fun saveIntent() {
        bundlePos = Bundle()
        bundlePos?.putBoolean(TAP_ON_PHONE_HAS_CARD_READER_ARGS, hasCardReader)
        bundlePos?.putBoolean(
            TAP_ON_PHONE_ACTIVITY_WAS_OPENED_BY_POS_VIRTUAL_ARGS,
            wasOpenedByPOSVirtual
        )
    }

    private fun getPOSBundle(): Bundle =
        bundlePos ?: Bundle().apply {
            putBoolean(
                TAP_ON_PHONE_HAS_CARD_READER_ARGS,
                this@TapOnPhoneNavigationFlowActivity.hasCardReader
            )
            putBoolean(
                TAP_ON_PHONE_ACTIVITY_WAS_OPENED_BY_POS_VIRTUAL_ARGS,
                this@TapOnPhoneNavigationFlowActivity.wasOpenedByPOSVirtual
            )

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

    override fun getSavedData() = getPOSBundle()

    override fun getData() = getPOSBundle()

    override fun onSupportNavigateUp(): Boolean {
        if (Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp().not()) finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isShowHelpMenu || isShowCloseMenu) {
            menuInflater.inflate(R.menu.menu_common_faq_blue, menu)

            menu?.findItem(R.id.action_close)?.isVisible = isShowCloseMenu
            menu?.findItem(R.id.action_help)?.isVisible = isShowHelpMenu
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_help -> {
                navigation?.onHelpButtonClicked()
                return true
            }

            R.id.action_close -> {
                navigation?.onCloseButtonClicked()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (isBack) {
            navigation?.onBackButtonClicked()
            super.onBackPressed()
        }
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigation = listener
    }

    override fun showContainerButton(isShow: Boolean) {
        binding.containerButton.visible(isShow)
    }

    override fun showToolbar(isShow: Boolean) {
        binding.toolbar.visible(isShow)
    }

    override fun showBackIcon(isShow: Boolean) {
        setupBackIcon(isShow)
    }

    override fun showFirstButton(isShow: Boolean) {
        binding.btnFirst.visible(isShow)
    }

    override fun showButton(isShow: Boolean) {
        binding.btnSecond.visible(isShow)
    }

    override fun setTextFirstButton(text: String) {
        binding.btnFirst.setText(text)
    }

    override fun setTextButton(text: String) {
        binding.btnSecond.text = text
    }

    override fun setColorBackgroundAnimatedProgressView(colorRes: Int) {
        binding.animatedProgressView.setAnimationColor(colorRes)
    }

    override fun enableButton(isEnabled: Boolean) {
        binding.btnSecond.isEnabled = isEnabled
    }

    override fun showHelpButton(isShow: Boolean) {
        isShowHelpMenu = isShow
        invalidateOptionsMenu()
    }

    override fun showCloseButton(isShow: Boolean) {
        isShowCloseMenu = isShow
        invalidateOptionsMenu()
    }

    override fun showAnimatedLoading(message: Int?) {
        binding.animatedProgressView.showAnimationStart(message)
    }

    override fun showAnimatedLoadingSuccess(message: Int?, onAction: () -> Unit) {
        binding.animatedProgressView.showAnimationSuccess(message = message, onAction = {
            onAction()
        })
    }

    override fun showAnimatedLoadingAlert(message: Int?, onAction: () -> Unit) {
        binding.animatedProgressView.showAnimationAlert(message = message, onAction = {
            onAction()
        })
    }

    override fun showAnimatedLoadingError(message: Int?, onAction: () -> Unit) {
        binding.animatedProgressView.showAnimationError(message = message, onAction = {
            onAction()
        })
    }

    override fun hideAnimatedLoading() {
        binding.animatedProgressView.hideAnimationStart()
    }

    override fun changeAnimatedLoadingText(message: Int?) {
        binding.animatedProgressView.setMessage(message)
    }

    override fun nfcIsNotSupported(gaFlowDetails: String) {
        checkFragmentManager {
            showCustomHandler(
                title = getString(R.string.tap_on_phone_nfc_is_not_supported_title),
                message = getString(R.string.tap_on_phone_nfc_is_not_supported_subtitle),
                contentImage = R.drawable.ic_07,
                labelSecondButton = getString(R.string.tap_on_phone_not_eligible_text_button),
                secondButtonCallback = {
                    finish()
                },
                finishCallback = {
                    finish()
                },
                headerCallback = {
                    finish()
                },
                isBack = true,
                isShowHeaderImage = true,
            )
            if (gaFlowDetails.isNotEmpty()) {
                analytics.logNFCNotSupported(gaFlowDetails, javaClass)
            }
        }
    }

    override fun androidIsNotSupported(gaFlowDetails: String) {
        checkFragmentManager {
            showCustomHandler(
                title = getString(R.string.tap_on_phone_android_is_not_supported_title),
                message = getString(R.string.tap_on_phone_android_is_not_supported_subtitle),
                contentImage = R.drawable.ic_07,
                labelSecondButton = getString(R.string.tap_on_phone_not_eligible_text_button),
                secondButtonCallback = {
                    finish()
                },
                finishCallback = {
                    finish()
                },
                headerCallback = {
                    finish()
                },
                isBack = true,
                isShowHeaderImage = true
            )
            if (gaFlowDetails.isNotEmpty()) {
                analytics.logAndroidIsNotSupported(gaFlowDetails, javaClass)
            }
        }
    }

    override fun notEligibleForTapOnPhone() {
        checkFragmentManager {
            showCustomHandler(
                title = getString(R.string.tap_on_phone_not_eligible_title),
                message = getString(R.string.tap_on_phone_not_eligible_message),
                contentImage = R.drawable.img_50_nao_elegivel,
                labelSecondButton = getString(R.string.tap_on_phone_not_eligible_text_button),
                secondButtonCallback = {
                    finish()
                },
                finishCallback = {
                    finish()
                },
                headerCallback = {
                    finish()
                },
                isBack = true,
                isShowHeaderImage = true
            )
        }
    }

    override fun retryConnectCardReader(onAction: () -> Unit, btnLabel: Int) {
        checkFragmentManager {
            showCustomHandler(
                contentImage = R.drawable.ic_07,
                title = getString(R.string.tap_on_phone_initialize_card_reader_title),
                message = getString(R.string.tap_on_phone_initialize_card_reader_message),
                titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                messageAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                labelSecondButton = getString(btnLabel),
                secondButtonCallback = {
                    onAction()
                },
                finishCallback = {
                    goToHome()
                },
                headerCallback = {
                    goToHome()
                },
                isBack = true,
                isShowHeaderImage = true
            )
        }
    }

    override fun goToHome() {
        if (wasOpenedByPOSVirtual.not()) backToHome()
        finishAndRemoveTask()
    }

    private fun showHandlerView(isBackAction: Boolean) {
        hideSoftKeyboard()
        isBack = isBackAction
        binding.toolbar.gone()
        binding.containerView.gone()
        binding.customHandlerView.visible()
    }

    private fun closeHandlerView() {
        isBack = true
        binding.toolbar.visible()
        binding.containerView.visible()
        binding.customHandlerView.gone()
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
        showHandlerView(isBackAction = isBack)
        checkFragmentManager {
            binding.customHandlerView.apply {
                this.title = title
                this.message = message
                this.labelOutlined = labelFirstButton
                this.labelContained = labelSecondButton
                this.titleStyle = R.style.bold_montserrat_20_cloud_600_spacing_8

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
                    finishCallback()
                }

                this.setHeaderClickListener {
                    closeHandlerView()
                    headerCallback()
                }

                this.setButtonOutlinedClickListener {
                    closeHandlerView()
                    firstButtonCallback()
                }

                this.setButtonContainedClickListener {
                    closeHandlerView()
                    secondButtonCallback()
                }
            }
        }
    }

    override fun showCustomErrorHandler(
        contentImage: Int,
        headerImage: Int,
        error: ErrorMessage,
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
        checkFragmentManager {
            showCustomHandler(
                contentImage = contentImage,
                headerImage = headerImage,
                message = error.message,
                title = title,
                titleAlignment = titleAlignment,
                messageAlignment = messageAlignment,
                labelFirstButton = labelFirstButton,
                labelSecondButton = labelSecondButton,
                isShowFirstButton = isShowFirstButton,
                isShowSecondButton = isShowSecondButton,
                firstButtonCallback = {
                    finishCallback()
                },
                secondButtonCallback = {
                    secondButtonCallback()
                },
                headerCallback = {
                    headerCallback()
                },
                finishCallback = {
                    finishCallback()
                },
                isBack = isBack,
                isShowButtonBack = isShowButtonBack,
                isShowHeaderImage = isShowHeaderImage
            )
        }
    }

    private fun checkFragmentManager(onAction: () -> Unit) {
        doWhenResumed(
            action = {
                onAction()
            },
            errorCallback = { goToHome() }
        )
    }
}