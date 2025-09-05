package br.com.mobicare.cielo.pixMVVM.presentation.extract

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.cielo.libflue.screen.HandlerViewFluiV2
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.parcelable
import br.com.mobicare.cielo.commons.utils.serializable
import br.com.mobicare.cielo.databinding.ActivityPixNewExtractNavigationFlowBinding
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pixMVVM.domain.enums.ProfileType
import br.com.mobicare.cielo.pixMVVM.domain.model.OnBoardingFulfillment

class PixNewExtractNavigationFlowActivity : BaseLoggedActivity(), CieloNavigation {
    private lateinit var binding: ActivityPixNewExtractNavigationFlowBinding

    private var bundle: Bundle? = null
    private var navigationListener: CieloNavigationListener? = null

    val data: NavArgs.Data by lazy {
        NavArgs.Data(
            profileType = intent?.serializable(NavArgs.PROFILE_TYPE),
            pixAccount = intent?.parcelable(NavArgs.PIX_ACCOUNT),
            settlementScheduled = intent?.parcelable(NavArgs.SETTLEMENT_SCHEDULED)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle = savedInstanceState
        binding = ActivityPixNewExtractNavigationFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeCollapsingToolbarLayout()
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }

    override fun onBackPressed() {
        val value = navigationListener?.onBackButtonClicked() ?: false
        if (value) {
            returnToHome()
        } else {
            super.onBackPressed()
        }
    }

    override fun configureCollapsingToolbar(configurator: CieloCollapsingToolbarLayout.Configurator) {
        binding.cieloCollapsingToolbarLayout.configure(configurator)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return binding.cieloCollapsingToolbarLayout.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            binding.cieloCollapsingToolbarLayout.onOptionsItemSelected(item)
        }
    }

    override fun showContent(isShow: Boolean) {
        binding.apply {
            if (isShow) hideAnimatedLoading()
            customHandlerView.gone()
            cieloCollapsingToolbarLayout.visible(isShow)
        }
    }

    override fun showAnimatedLoading(message: Int?) {
        binding.apply {
            cieloCollapsingToolbarLayout.gone()
            customHandlerView.gone()
            animatedProgressView.startAnimation(
                message = message ?: R.string.wait_animated_loading_start_message,
                isUpdateMessage = false,
            )
        }
    }

    override fun hideAnimatedLoading() {
        binding.animatedProgressView.gone()
    }

    override fun saveData(bundle: Bundle) {
        this.bundle?.putAll(bundle) ?: run {
            this.bundle = bundle
        }
    }

    override fun clearData() {
        bundle = null
    }

    override fun getSavedData() = bundle

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
        isShowHeaderImage: Boolean,
    ) {
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
        HandlerViewBuilderFluiV2.Builder(this).apply {
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
        }
            .build()
            .show(supportFragmentManager, this.javaClass.name)
    }

    private fun initializeCollapsingToolbarLayout() {
        binding.cieloCollapsingToolbarLayout.initialize(this)
    }

    private fun showHandlerView() {
        hideSoftKeyboard()
        binding.apply {
            hideAnimatedLoading()
            cieloCollapsingToolbarLayout.gone()
            customHandlerView.visible()
        }
    }

    private fun closeHandlerView() {
        binding.apply {
            cieloCollapsingToolbarLayout.visible()
            customHandlerView.gone()
        }
    }

    private fun returnToHome() {
        backToHome()
        finishAndRemoveTask()
    }

    object NavArgs {
        const val PROFILE_TYPE = "PROFILE_TYPE"
        const val PIX_ACCOUNT = "PIX_ACCOUNT"
        const val SETTLEMENT_SCHEDULED = "SETTLEMENT_SCHEDULED"
        const val SCHEDULED_PIX_WAS_CANCELED = "SCHEDULED_PIX_WAS_CANCELED"

        data class Data(
            val profileType: ProfileType?,
            val pixAccount: OnBoardingFulfillment.PixAccount? = null,
            val settlementScheduled: OnBoardingFulfillment.SettlementScheduled? = null
        )
    }
}
