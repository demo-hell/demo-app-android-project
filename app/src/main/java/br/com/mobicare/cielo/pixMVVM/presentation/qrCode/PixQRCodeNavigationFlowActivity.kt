package br.com.mobicare.cielo.pixMVVM.presentation.qrCode

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.cielo.libflue.screen.HandlerViewFluiV2
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.databinding.ActivityPixNewDecodeQrCodeNavigationFlowBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.setNavGraphStartDestination
import br.com.mobicare.cielo.extensions.visible

class PixQRCodeNavigationFlowActivity :
    BaseLoggedActivity(),
    CieloNavigation {
    private var binding: ActivityPixNewDecodeQrCodeNavigationFlowBinding? = null
    private var bundle: Bundle? = null
    private var navigationListener: CieloNavigationListener? = null

    private val argsData: NavArgs.Data by lazy {
        NavArgs.Data(
            isReadingQRCode = intent.getBooleanExtra(NavArgs.IS_READING_QR_CODE_ARGS, false),
            currentBalance = intent.getDoubleExtra(NavArgs.CURRENT_BALANCE, ZERO_DOUBLE),
        )
    }

    private val startDestination get(): @LayoutRes Int =
        if (argsData.isReadingQRCode) {
            R.id.pixDecodeQRCodeFragment
        } else {
            R.id.pixCopyPasteFragment
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bundle = savedInstanceState

        ActivityPixNewDecodeQrCodeNavigationFlowBinding.inflate(layoutInflater).also {
            binding = it
            setContentView(it.root)
        }

        setupNavGraphStartDestination()
        initializeCollapsingToolbarLayout()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }

    override fun showAnimatedLoading(
        @StringRes message: Int?,
    ) {
        binding?.animatedProgressView?.startAnimation(
            message ?: R.string.wait_animated_loading_start_message,
        )
    }

    override fun hideAnimatedLoading() {
        binding?.animatedProgressView?.apply {
            hideAnimationStart()
            gone()
        }
    }

    override fun showContent(isShow: Boolean) {
        binding?.collapsingToolbarLayout?.visible(isShow)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean = binding?.collapsingToolbarLayout?.onCreateOptionsMenu(menu) ?: false

    override fun onOptionsItemSelected(item: MenuItem): Boolean = binding?.collapsingToolbarLayout?.onOptionsItemSelected(item) ?: false

    override fun configureCollapsingToolbar(configurator: CieloCollapsingToolbarLayout.Configurator) {
        binding?.collapsingToolbarLayout?.configure(configurator)
    }

    override fun getData() = argsData

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

    private fun setupNavGraphStartDestination() {
        setNavGraphStartDestination(
            navHostFragmentId = R.id.navHostPixDecodeQRCodeFragment,
            navGraphId = R.navigation.nav_graph_pix_decode_qr_code,
            startDestinationId = startDestination,
        )
    }

    private fun initializeCollapsingToolbarLayout() {
        binding?.collapsingToolbarLayout?.initialize(this)
    }

    object NavArgs {
        const val IS_READING_QR_CODE_ARGS = "IS_READING_QR_CODE_ARGS"
        const val CURRENT_BALANCE = "CURRENT_BALANCE"

        data class Data(
            val isReadingQRCode: Boolean,
            val currentBalance: Double,
        )
    }
}
