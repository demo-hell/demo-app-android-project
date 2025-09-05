package br.com.mobicare.cielo.p2m.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.navigation.Navigation
import br.com.cielo.libflue.screen.HandlerViewBuilderFlui
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.WhatsApp
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.databinding.ActivityP2mNavigationFlowBinding
import br.com.mobicare.cielo.extensions.visible

class P2MFlowActivity : CollapsingToolbarBaseActivity(), CieloNavigation {

    private var _binding: ActivityP2mNavigationFlowBinding? = null
    private val binding get() = _binding!!

    private var bundle: Bundle? = null
    private var navigationListener: CieloNavigationListener? = null

    private var isBack = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle = savedInstanceState
        _binding = ActivityP2mNavigationFlowBinding.inflate(layoutInflater)

        setCollapsingToolbarContentView(binding.root)
        setupListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        if (Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp().not())
            this.finish()

        return true
    }

    private fun setupListeners() {
        buttonFooterAction.apply {
            setOnClickListener {
                navigationListener?.onButtonClicked(text.toString())
            }
        }
    }

    override fun onBackPressed() {
        if (isBack) {
            navigationListener?.onBackButtonClicked()
            super.onBackPressed()
        }
    }

    override fun configureCollapsingToolbar(configurator: Configurator) {
        updateConfiguration(configurator)
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }

    override fun showButton(isShow: Boolean) {
        buttonFooterAction.visible(isShow)
    }

    override fun setTextButton(text: String) {
        buttonFooterAction.text = text
    }

    override fun enableButton(isEnabled: Boolean) {
        buttonFooterAction.isEnabled = isEnabled
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
        HandlerViewBuilderFlui.Builder(this)
            .title(title)
            .titleStyle(R.style.bold_montserrat_20_brand_600_spacing_4)
            .titleAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .message(message)
            .messageStyle(R.style.regular_ubuntu_16_cloud_400_spacing_6)
            .messageAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .contentImage(contentImage)
            .labelContained(labelSecondButton)
            .labelOutlined(labelFirstButton)
            .isShowButtonBack(isShowButtonBack)
            .isShowButtonOutlined(isShowFirstButton)
            .isShowButtonContained(isShowSecondButton)
            .isShowHeaderImage(isShowButtonClose)
            .containedClickListener(object : HandlerViewBuilderFlui.ContainedOnClickListener {
                override fun onClick(dialog: Dialog?) {
                    dialog?.dismiss()
                    callbackSecondButton.invoke()
                }
            })
            .outlinedClickListener(object : HandlerViewBuilderFlui.OutlinedOnClickListener {
                override fun onClick(dialog: Dialog?) {
                    dialog?.dismiss()
                    callbackFirstButton.invoke()
                }
            })
            .headerClickListener(object : HandlerViewBuilderFlui.HeaderOnClickListener {
                override fun onClick(dialog: Dialog?) {
                    dialog?.dismiss()
                    callbackClose.invoke()
                }
            })
            .finishClickListener(object : HandlerViewBuilderFlui.FinishOnClickListener {
                override fun onClick(dialog: Dialog?) {
                    dialog?.dismiss()
                    callbackClose.invoke()
                }
            })
            .build()
            .show(supportFragmentManager, this.javaClass.name)
    }

    override fun onHelpButtonClicked() {
        Utils.openBrowser(this, WhatsApp.LINK_TO_SALES_WHATS_APP)
    }

    override fun showAnimatedLoading(@StringRes message: Int?) {
        binding.animatedProgressView.showAnimationStart(message)

    }

    override fun showAnimatedLoadingSuccess(@StringRes message: Int?, onAction: () -> Unit) {
        binding.animatedProgressView.showAnimationSuccess(message = message, onAction = {
            onAction.invoke()
        })
    }

    override fun showAnimatedLoadingAlert(@StringRes message: Int?, onAction: () -> Unit) {
        binding.animatedProgressView.showAnimationAlert(message = message, onAction = {
            onAction.invoke()
        })
    }

    override fun showAnimatedLoadingError(@StringRes message: Int?, onAction: () -> Unit) {
        binding.animatedProgressView.showAnimationError(message = message, onAction = {
            onAction.invoke()
        })
    }

    override fun hideAnimatedLoading() {
        binding.animatedProgressView.hideAnimationStart()
    }

    override fun changeAnimatedLoadingText(@StringRes message: Int?) {
        binding.animatedProgressView.setMessage(message)
    }

}
