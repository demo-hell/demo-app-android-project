package br.com.mobicare.cielo.component.impersonate.presentation

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import br.com.cielo.libflue.screen.HandlerViewBuilderFlui
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.commons.utils.getParcelableCustom
import br.com.mobicare.cielo.databinding.ActivityImpersonateNavigationFlowBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.component.impersonate.presentation.model.ImpersonateUI

class ImpersonateNavigationFlowActivity : CollapsingToolbarBaseActivity(), CieloNavigation {

    private var binding: ActivityImpersonateNavigationFlowBinding? = null

    private var bundle: Bundle? = null
    private var navigationListener: CieloNavigationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bundle = savedInstanceState
        binding = ActivityImpersonateNavigationFlowBinding.inflate(layoutInflater).also {
            setCollapsingToolbarContentView(it.root)
        }

        setupListeners()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }

    override fun configureCollapsingToolbar(configurator: Configurator) =
        updateConfiguration(configurator)

    override fun enableButton(isEnabled: Boolean) {
        buttonFooterAction.isEnabled = isEnabled
    }

    override fun setTextButton(text: String) {
        buttonFooterAction.text = text
    }

    override fun showButton(isShow: Boolean) {
        buttonFooterAction.visible(isShow)
    }

    override fun getData(): Any? {
        return intent.getParcelableCustom<ImpersonateUI>(
            IMPERSONATING_NAVIGATION_FLOW_ACTIVITY_ARGS
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
        callbackBack: () -> Unit
    ) {
        HandlerViewBuilderFlui.Builder(this)
            .title(title)
            .titleStyle(R.style.bold_montserrat_20_cloud_600_spacing_8)
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
            .backClickListener(object : HandlerViewBuilderFlui.BackOnClickListener {
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

    private fun setupListeners() {
        buttonFooterAction.apply {
            setOnClickListener {
                navigationListener?.onButtonClicked(text.toString())
            }
        }
    }

    companion object {

        private const val IMPERSONATING_NAVIGATION_FLOW_ACTIVITY_ARGS =
            "IMPERSONATING_NAVIGATION_FLOW_ACTIVITY_ARGS"

        fun launch(context: Context, args: ImpersonateUI) =
            Intent(context, ImpersonateNavigationFlowActivity::class.java).putExtra(
                IMPERSONATING_NAVIGATION_FLOW_ACTIVITY_ARGS,
                args
            )

    }

}