package br.com.mobicare.cielo.posVirtual.presentation

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.window.OnBackInvokedDispatcher
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import br.com.cielo.libflue.screen.HandlerViewBuilderFlui
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.databinding.ActivityPosVirtualNavigationFlowBinding
import br.com.mobicare.cielo.extensions.visible

class PosVirtualNavigationFlowActivity : CollapsingToolbarBaseActivity(), CieloNavigation {

    private var _binding: ActivityPosVirtualNavigationFlowBinding? = null
    private val binding get() = _binding!!

    private var bundle: Bundle? = null
    private var navigationListener: CieloNavigationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle = savedInstanceState
        _binding = ActivityPosVirtualNavigationFlowBinding.inflate(layoutInflater)

        setCollapsingToolbarContentView(binding.root)
        setupListeners()
    }

    private fun setupListeners() {
        buttonFooterAction.apply {
            setOnClickListener {
                navigationListener?.onButtonClicked(text.toString())
            }
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

    override fun onBackPressed() {
        val value = navigationListener?.onBackButtonClicked() ?: false
        if (value.not()) super.onBackPressed()
    }

    override fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, ZERO)
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
            .titleStyle(R.style.bold_montserrat_20_cloud_600_spacing_8)
            .titleAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .message(message)
            .messageStyle(R.style.regular_montserrat_16_cloud_600_spacing_4)
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
            .backClickListener(object: HandlerViewBuilderFlui.BackOnClickListener {
                override fun onClick(dialog: Dialog?) {
                    dialog?.dismiss()
                    callbackBack.invoke()
                }
            })
            .finishClickListener(object : HandlerViewBuilderFlui.FinishOnClickListener {
                override fun onClick(dialog: Dialog?) {
                    dialog?.dismiss()
                    callbackBack.invoke()
                }
            })
            .build()
            .show(supportFragmentManager, this.javaClass.name)
    }

}