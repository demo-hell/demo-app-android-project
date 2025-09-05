package br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.screen.HandlerViewBuilderFlui
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.getParcelableCustom
import br.com.mobicare.cielo.databinding.ActivityPredictiveBatteryNavigationFlowBinding
import br.com.mobicare.cielo.deeplink.model.DeepLinkModel
import br.com.mobicare.cielo.deeplink.utils.DeepLinkConstants
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.utils.PredictiveBatteryConstants.PREDICTIVE_BATTERY_SERIAL_NUMBER_ARGS

class PredictiveBatteryNavigationFlowActivity : BaseLoggedActivity(), CieloNavigation {

    private var binding: ActivityPredictiveBatteryNavigationFlowBinding? = null

    private var bundle: Bundle? = null
    private var navigationListener: CieloNavigationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle = savedInstanceState
        binding = ActivityPredictiveBatteryNavigationFlowBinding.inflate(layoutInflater)
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

    override fun getData(): Any? {
        return intent.getParcelableCustom<DeepLinkModel>(
            DeepLinkConstants.DEEP_LINK_MODEL_ARGS
        ) ?: intent.getStringExtra(PREDICTIVE_BATTERY_SERIAL_NUMBER_ARGS)
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