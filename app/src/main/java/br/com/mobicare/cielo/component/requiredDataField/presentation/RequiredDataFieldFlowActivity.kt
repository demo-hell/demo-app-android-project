package br.com.mobicare.cielo.component.requiredDataField.presentation

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import br.com.cielo.libflue.screen.HandlerViewBuilderFlui
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.commons.utils.getParcelableCustom
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.component.requiredDataField.presentation.model.UiRequiredDataField
import br.com.mobicare.cielo.component.requiredDataField.presentation.ui.RequiredDataFieldFragment
import br.com.mobicare.cielo.databinding.ActivityRequiredDataFieldFlowBinding
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible

class RequiredDataFieldFlowActivity : CollapsingToolbarBaseActivity(), CieloNavigation {

    private lateinit var binding: ActivityRequiredDataFieldFlowBinding

    private var navigationListener: CieloNavigationListener? = null

    private val data by lazy {
        intent?.getParcelableCustom<UiRequiredDataField>(RequiredDataFieldFragment.REQUIRED_DATA_ARG)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequiredDataFieldFlowBinding.inflate(layoutInflater)
        setCollapsingToolbarContentView(binding.root, showAppBarLayout = true)
        setStartDestination()
    }

    private fun setStartDestination() {
        data?.let { data ->
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
                .navController
                .setGraph(
                    R.navigation.nav_graph_required_data_field,
                    bundleOf(RequiredDataFieldFragment.REQUIRED_DATA_ARG to data)
                )
        } ?: finish()
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }

    override fun configureCollapsingToolbar(configurator: Configurator) {
        updateConfiguration(configurator)
    }

    override fun showButton(isShow: Boolean) {
        buttonFooterAction.visible(isShow)
    }

    private fun showHandlerView() {
        hideSoftKeyboard()
        showAppBarLayout(false)
        showFooterView(false)
        binding.apply {
            navHostFragment.gone()
            customHandlerView.visible()
        }
    }

    private fun closeHandlerView() {
        showAppBarLayout(true)
        showFooterView(true)
        binding.apply {
            customHandlerView.gone()
            navHostFragment.visible()
        }
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
        checkFragmentManager {
            binding.customHandlerView.apply {
                this.title = title
                this.message = message
                this.labelOutlined = labelFirstButton
                this.labelContained = labelSecondButton

                this.isShowButtonBack = isShowButtonBack
                this.isShowHeaderImage = isShowHeaderImage
                this.isShowButtonOutlined = isShowFirstButton
                this.isShowContainerButton = isShowSecondButton

                this.headerImage = headerImage
                this.contentImage = contentImage

                this.titleAlignment = titleAlignment
                this.messageAlignment = messageAlignment

                this.titleStyle = R.style.bold_montserrat_20_cloud_600_spacing_8

                this.setBackClickListener {
                    closeHandlerView()
                    finishCallback.invoke()
                }

                this.setHeaderClickListener {
                    closeHandlerView()
                    headerCallback.invoke()
                }

                this.setButtonOutlinedClickListener {
                    closeHandlerView()
                    firstButtonCallback.invoke()
                }

                this.setButtonContainedClickListener {
                    closeHandlerView()
                    secondButtonCallback.invoke()
                }
            }
            showHandlerView()
        }
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

    private fun checkFragmentManager(onAction: () -> Unit) {
        doWhenResumed(
            action = {
                onAction.invoke()
            },
            errorCallback = { goToHome() }
        )
    }

    override fun goToHome() {
        backToHome()
        finishAndRemoveTask()
    }

    companion object {

        fun launch(context: Context, data: UiRequiredDataField) =
            Intent(context, RequiredDataFieldFlowActivity::class.java).putExtra(
                RequiredDataFieldFragment.REQUIRED_DATA_ARG,
                data
            )

    }

}