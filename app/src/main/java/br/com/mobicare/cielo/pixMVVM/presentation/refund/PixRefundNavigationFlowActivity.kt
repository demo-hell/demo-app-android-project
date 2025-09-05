package br.com.mobicare.cielo.pixMVVM.presentation.refund

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import androidx.annotation.StringRes
import br.com.cielo.libflue.screen.HandlerViewBuilderFlui
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.cielo.libflue.screen.HandlerViewFluiV2
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.parcelable
import br.com.mobicare.cielo.commons.utils.serializable
import br.com.mobicare.cielo.databinding.ActivityPixRefundNavigationFlowBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pixMVVM.domain.enums.ProfileType
import br.com.mobicare.cielo.pixMVVM.domain.model.OnBoardingFulfillment
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail

class PixRefundNavigationFlowActivity : BaseLoggedActivity(), CieloNavigation {

    private lateinit var binding: ActivityPixRefundNavigationFlowBinding

    private var bundle: Bundle? = null
    private var navigationListener: CieloNavigationListener? = null

    private val navArgsData: NavArgs.Data by lazy {
        NavArgs.Data(
            transferDetail = intent.parcelable(NavArgs.TRANSFER_DETAIL_ARGS),
            profileType = intent?.serializable(NavArgs.PROFILE_TYPE_ARGS),
            pixAccount = intent?.parcelable(NavArgs.PIX_ACCOUNT_ARGS),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle = savedInstanceState
        binding = ActivityPixRefundNavigationFlowBinding.inflate(layoutInflater)
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
        onDismiss: ((Dialog?) -> Unit)?
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
            .labelContained(labelFirstButton)
            .labelOutlined(labelSecondButton)
            .isShowButtonBack(isShowButtonBack)
            .isShowButtonContained(isShowFirstButton)
            .isShowButtonOutlined(isShowSecondButton)
            .isShowHeaderImage(isShowButtonClose)
            .containedClickListener(object : HandlerViewBuilderFlui.ContainedOnClickListener {
                override fun onClick(dialog: Dialog?) {
                    dialog?.dismiss()
                    callbackFirstButton.invoke()
                }
            })
            .outlinedClickListener(object : HandlerViewBuilderFlui.OutlinedOnClickListener {
                override fun onClick(dialog: Dialog?) {
                    dialog?.dismiss()
                    callbackSecondButton.invoke()
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
            .show(supportFragmentManager, javaClass.name)
    }

    object NavArgs {
        const val TRANSFER_DETAIL_ARGS = "TRANSFER_DETAIL_ARGS"
        const val PROFILE_TYPE_ARGS = "PROFILE_TYPE_ARGS"
        const val PIX_ACCOUNT_ARGS = "PIX_ACCOUNT_ARGS"

        data class Data(
            val transferDetail: PixTransferDetail?,
            val profileType: ProfileType?,
            val pixAccount: OnBoardingFulfillment.PixAccount? = null,
        )
    }

}