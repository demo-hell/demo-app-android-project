package br.com.mobicare.cielo.newRecebaRapido.presentation

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.navigation.Navigation
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.databinding.ActivityReceiveAutomaticBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible

class ReceiveAutomaticActivity : CollapsingToolbarBaseActivity(), CieloNavigation {
    private var _binding: ActivityReceiveAutomaticBinding? = null
    private val binding get() = _binding!!
    private var bundle: Bundle? = null
    private var navigationListener: CieloNavigationListener? = null
    private var isBack = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle = savedInstanceState
        _binding = ActivityReceiveAutomaticBinding.inflate(layoutInflater)

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
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun startHelpCenter(tagKey: String) {
        this.openFaq(
            tag = tagKey,
            subCategoryName = getString(R.string.receive_auto_title)
        )
    }

    override fun configureCollapsingToolbar(configurator: Configurator) {
        updateConfiguration(configurator.copy(
            toolbarTitleAppearance = ToolbarTitleAppearance(
                collapsed = R.style.CollapsingToolbar_Collapsed_BlackBold,
                expanded = R.style.CollapsingToolbar_Expanded_BlackBold
            )
        ))
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }

    override fun showLoading(
        isShow: Boolean,
        @StringRes message: Int?,
        vararg messageArgs: String,
    ) {
        showAppBarLayout(false)
        message?.let {
            binding.loading.apply {
                startAnimation(it, false)
                visible()
            }}
    }

    override fun hideLoading() {
        binding.loading.apply {
            hideAnimationStart()
            gone()
        }
        showAppBarLayout(true)
    }

    override fun showFirstButton(isShow: Boolean) {
        binding.btnFirst.visible(isShow)
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
        HandlerViewBuilderFluiV2.Builder(this).apply {
            this.title = title
            this.message = message
            this.illustration = contentImage
            this.labelPrimaryButton = labelSecondButton
            this.labelSecondaryButton = labelFirstButton
            this.isShowBackButton = isShowButtonBack
            this.isShowIconButtonEndHeader = isShowButtonClose
            this.onPrimaryButtonClickListener =
                object : HandlerViewBuilderFluiV2.HandlerViewListener {
                    override fun onClick(dialog: Dialog?) {
                        dialog?.dismiss()
                        callbackSecondButton.invoke()
                    }
                }
            this.onSecondaryButtonClickListener =
                object : HandlerViewBuilderFluiV2.HandlerViewListener {
                    override fun onClick(dialog: Dialog?) {
                        dialog?.dismiss()
                        callbackFirstButton.invoke()
                    }
                }
            this.onIconButtonEndHeaderClickListener =
                object : HandlerViewBuilderFluiV2.HandlerViewListener {
                    override fun onClick(dialog: Dialog?) {
                        dialog?.dismiss()
                        callbackClose.invoke()
                    }
                }
        }

            .build()
            .show(supportFragmentManager, this.javaClass.name)
    }

    override fun showAnimatedLoading(@StringRes message: Int?) {
        binding.animatedProgressView.showAnimationStart(message)
        showAppBarLayout(false)
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
        showAppBarLayout(true)
        binding.animatedProgressView.hideAnimationStart()
    }

    override fun changeAnimatedLoadingText(@StringRes message: Int?) {
        binding.animatedProgressView.setMessage(message)
    }

}