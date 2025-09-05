package br.com.mobicare.cielo.openFinance.presentation.manager

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
import br.com.mobicare.cielo.databinding.OpenFinanceFlowActivityBinding

class OpenFinanceFlowActivity : BaseLoggedActivity(), CieloNavigation {
    private var navigationListener: CieloNavigationListener? = null
    private var _binding: OpenFinanceFlowActivityBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = OpenFinanceFlowActivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initializeCollapsingToolbarLayout()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initializeCollapsingToolbarLayout() {
        binding?.cieloCollapsingToolbarLayout?.initialize(this)
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
            .isShowButtonContained(isShowSecondButton)
            .backgroundColorHandlerView(R.color.white)
            .isShowButtonOutlined(isShowFirstButton)
            .labelContained(labelSecondButton)
            .labelOutlined(labelFirstButton)
            .labelColorButtonContained(R.color.white)
            .isShowButtonOutlined(isShowFirstButton)
            .isShowHeaderImage(true)
            .contentImage(contentImage)
            .isShowButtonBack(isShowButtonBack)
            .headerImage(R.drawable.ic_symbol_close_brand_400_24_dp)
            .title(title)
            .titleStyle(R.style.bold_montserrat_20_cloud_600_spacing_8)
            .titleAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .message(message)
            .messageStyle(R.style.regular_montserrat_16_cloud_400_spacing_4)
            .messageAlignment(View.TEXT_ALIGNMENT_TEXT_START)
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
}