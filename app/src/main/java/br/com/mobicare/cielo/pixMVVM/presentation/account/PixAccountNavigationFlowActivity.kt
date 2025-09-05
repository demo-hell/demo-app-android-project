package br.com.mobicare.cielo.pixMVVM.presentation.account

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.screen.HandlerViewBuilderFlui
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.cielo.libflue.screen.HandlerViewFluiV2
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE_DOUBLE
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.databinding.ActivityPixAccountNavigationFlowBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.setNavGraphStartDestination
import br.com.mobicare.cielo.extensions.visible

class PixAccountNavigationFlowActivity :
    BaseLoggedActivity(),
    CieloNavigation {
    private lateinit var binding: ActivityPixAccountNavigationFlowBinding

    private var bundle: Bundle? = null
    private var navigationListener: CieloNavigationListener? = null

    private val navArgsData: NavArgs.Data by lazy {
        NavArgs.Data(
            currentBalance =
                if (intent.hasExtra(NavArgs.CURRENT_BALANCE_ARGS)) {
                    intent.getDoubleExtra(NavArgs.CURRENT_BALANCE_ARGS, -ONE_DOUBLE)
                } else {
                    null
                },
            accessedThroughPennyDrop = intent.getBooleanExtra(NavArgs.ACCESSED_THROUGH_PENNY_DROP_ARGS, false),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle = savedInstanceState
        binding = ActivityPixAccountNavigationFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavGraph()
        initializeCollapsingToolbarLayout()
    }

    override fun getData() = navArgsData

    override fun onCreateOptionsMenu(menu: Menu?): Boolean = binding.collapsingToolbarLayout.onCreateOptionsMenu(menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean = binding.collapsingToolbarLayout.onOptionsItemSelected(item)

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }

    private fun initializeCollapsingToolbarLayout() {
        binding.collapsingToolbarLayout.initialize(this)
    }

    override fun configureCollapsingToolbar(configurator: CieloCollapsingToolbarLayout.Configurator) {
        binding.collapsingToolbarLayout.configure(configurator)
    }

    override fun showAnimatedLoading(
        @StringRes message: Int?,
    ) {
        binding.apply {
            animatedProgressView.startAnimation(
                message ?: R.string.wait_animated_loading_start_message,
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

    override fun showHandlerViewV2(
        titleTextAppearance: Int,
        titleAlignment: Int,
        title: String,
        messageTextAppearance: Int,
        messageAlignment: Int,
        message: String,
        labelPrimaryButton: String,
        labelSecondaryButton: String,
        illustration: Int,
        cardInformationData: HandlerViewFluiV2.CardInformationData?,
        isShowBackButton: Boolean,
        isShowIconButtonEndHeader: Boolean,
        drawableIconButtonEndHeader: Int,
        iconButtonEndHeaderContentDescription: String,
        hasPhone: Boolean,
        onPrimaryButtonClickListener: HandlerViewBuilderFluiV2.HandlerViewListener?,
        onSecondaryButtonClickListener: HandlerViewBuilderFluiV2.HandlerViewListener?,
        onBackButtonClickListener: HandlerViewBuilderFluiV2.HandlerViewListener?,
        onIconButtonEndHeaderClickListener: HandlerViewBuilderFluiV2.HandlerViewListener?,
        onDismiss: ((Dialog?) -> Unit)?,
    ) {
        HandlerViewBuilderFluiV2
            .Builder(this)
            .apply {
                this.titleTextAppearance = titleTextAppearance
                this.titleAlignment = titleAlignment
                this.title = title
                this.messageTextAppearance = messageTextAppearance
                this.messageAlignment = messageAlignment
                this.message = message
                this.labelPrimaryButton = labelPrimaryButton
                this.labelSecondaryButton = labelSecondaryButton
                this.illustration = illustration
                this.cardInformationData = cardInformationData
                this.isShowBackButton = isShowBackButton
                this.isShowIconButtonEndHeader = isShowIconButtonEndHeader
                this.drawableIconButtonEndHeader = drawableIconButtonEndHeader
                this.iconButtonEndHeaderContentDescription = iconButtonEndHeaderContentDescription
                this.hasPhone = hasPhone
                this.onPrimaryButtonClickListener = onPrimaryButtonClickListener
                this.onSecondaryButtonClickListener = onSecondaryButtonClickListener
                this.onBackButtonClickListener = onBackButtonClickListener
                this.onIconButtonEndHeaderClickListener = onIconButtonEndHeaderClickListener
                this.onDismiss = onDismiss
            }.build()
            .show(supportFragmentManager, this.javaClass.name)
    }

    override fun showCustomHandlerView(
        contentImage: Int,
        headerImage: Int,
        titleStyle: Int,
        messageStyle: Int,
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
        callbackBack: () -> Unit,
    ) {
        HandlerViewBuilderFlui
            .Builder(this)
            .title(title)
            .titleStyle(R.style.bold_montserrat_20_cloud_800_spacing_8)
            .titleAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .message(message)
            .messageStyle(R.style.regular_montserrat_16_neutral_600_spacing_4)
            .messageAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .contentImage(contentImage)
            .labelContained(labelFirstButton)
            .labelOutlined(labelSecondButton)
            .isShowButtonBack(isShowButtonBack)
            .isShowButtonContained(isShowFirstButton)
            .isShowButtonOutlined(isShowSecondButton)
            .isShowHeaderImage(isShowButtonClose)
            .containedClickListener(
                object : HandlerViewBuilderFlui.ContainedOnClickListener {
                    override fun onClick(dialog: Dialog?) {
                        dialog?.dismiss()
                        callbackFirstButton.invoke()
                    }
                },
            ).outlinedClickListener(
                object : HandlerViewBuilderFlui.OutlinedOnClickListener {
                    override fun onClick(dialog: Dialog?) {
                        dialog?.dismiss()
                        callbackSecondButton.invoke()
                    }
                },
            ).headerClickListener(
                object : HandlerViewBuilderFlui.HeaderOnClickListener {
                    override fun onClick(dialog: Dialog?) {
                        dialog?.dismiss()
                        callbackClose.invoke()
                    }
                },
            ).backClickListener(
                object : HandlerViewBuilderFlui.BackOnClickListener {
                    override fun onClick(dialog: Dialog?) {
                        dialog?.dismiss()
                        callbackBack.invoke()
                    }
                },
            ).finishClickListener(
                object : HandlerViewBuilderFlui.FinishOnClickListener {
                    override fun onClick(dialog: Dialog?) {
                        dialog?.dismiss()
                        callbackBack.invoke()
                    }
                },
            ).build()
            .show(supportFragmentManager, javaClass.name)
    }

    private fun setupNavGraph() {
        setNavGraphStartDestination(
            navHostFragmentId = R.id.navHostFragment,
            navGraphId = R.navigation.nav_graph_pix_account,
            startDestinationId = if (navArgsData.accessedThroughPennyDrop) R.id.pixCieloAccountFragment else R.id.pixReceiptMethodFragment,
        )
    }

    object NavArgs {
        const val CURRENT_BALANCE_ARGS = "CURRENT_BALANCE_ARGS"
        const val ACCESSED_THROUGH_PENNY_DROP_ARGS = "ACCESSED_THROUGH_PENNY_DROP_ARGS"

        data class Data(
            val currentBalance: Double?,
            val accessedThroughPennyDrop: Boolean = false,
        )
    }
}
