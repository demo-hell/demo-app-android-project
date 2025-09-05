package br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.getParcelableCustom
import br.com.mobicare.cielo.databinding.ActivityRaD2ToD1MigrationFlowBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers

class MigrationD2toD1NavigationFlowActivity : BaseLoggedActivity(), CieloNavigation {

    private lateinit var binding: ActivityRaD2ToD1MigrationFlowBinding

    private var bundle: Bundle? = null
    private var navigationListener: CieloNavigationListener? = null

    private val navArgsData: NavArgs.Data by lazy {
        NavArgs.Data(
            migrationOffer = intent.getParcelableCustom<HiringOffers>(NavArgs.MIGRATION_OFFER_ARGS)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle = savedInstanceState
        binding = ActivityRaD2ToD1MigrationFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeCollapsingToolbarLayout()
    }

    override fun getData() = navArgsData

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return binding.collapsingToolbarLayout.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return binding.collapsingToolbarLayout.onOptionsItemSelected(item)
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }

    private fun initializeCollapsingToolbarLayout() {
        binding.collapsingToolbarLayout.initialize(this)
    }

    override fun configureCollapsingToolbar(configurator: CieloCollapsingToolbarLayout.Configurator) {
        binding.collapsingToolbarLayout.configure(configurator)
    }

    override fun showAnimatedLoading(@StringRes message: Int?) {
        binding.apply {
            animatedProgressView.startAnimation(
                message ?: R.string.wait_animated_loading_start_message
            )
            collapsingToolbarLayout.gone()
        }
    }

    override fun hideAnimatedLoading() {
        binding.apply {
            animatedProgressView.hideAnimationStart()
            animatedProgressView.gone()
        }
    }

    override fun showContent(isShow: Boolean) {
        if (isShow) hideAnimatedLoading()
        binding.collapsingToolbarLayout.visible(isShow)
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

    object NavArgs {
        const val MIGRATION_OFFER_ARGS = "MIGRATION_OFFER_ARGS"

        data class Data(
            val migrationOffer: HiringOffers? = null
        )
    }

}