package br.com.mobicare.cielo.pixMVVM.presentation.infringement

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.screen.HandlerViewBuilderFlui
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.databinding.ActivityPixInfringementNavigationFlowBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pixMVVM.utils.PixConstants.PIX_ID_END_TO_END_ARGS

class PixInfringementNavigationFlowActivity : BaseLoggedActivity(), CieloNavigation {

    private var binding: ActivityPixInfringementNavigationFlowBinding? = null
    private var bundle: Bundle? = null
    private var navigationListener: CieloNavigationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bundle = savedInstanceState
        binding = ActivityPixInfringementNavigationFlowBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initializeCollapsingToolbarLayout()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }

    override fun getData(): Any? = intent?.getStringExtra(PIX_ID_END_TO_END_ARGS)

    override fun configureCollapsingToolbar(configurator: CieloCollapsingToolbarLayout.Configurator) {
        binding?.cieloCollapsingToolbarLayout?.configure(configurator)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return binding?.cieloCollapsingToolbarLayout?.onCreateOptionsMenu(menu)
            ?: super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return binding?.cieloCollapsingToolbarLayout?.onOptionsItemSelected(item)
            ?: super.onOptionsItemSelected(item)
    }

    override fun showAnimatedLoading(@StringRes message: Int?) {
        binding?.apply {
            animatedProgressView.startAnimation(
                message ?: R.string.wait_animated_loading_start_message
            )
            cieloCollapsingToolbarLayout.gone()
        }
    }

    override fun hideAnimatedLoading() {
        binding?.apply {
            animatedProgressView.hideAnimationStart()
            animatedProgressView.gone()
        }
    }

    override fun showContent(isShow: Boolean) {
        binding?.cieloCollapsingToolbarLayout?.visible()
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
            .titleStyle(R.style.bold_montserrat_20_cloud_800_spacing_8)
            .titleAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .message(message)
            .messageStyle(R.style.regular_montserrat_16_neutral_600_spacing_4)
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

    private fun initializeCollapsingToolbarLayout() {
        binding?.cieloCollapsingToolbarLayout?.initialize(this)
    }

}