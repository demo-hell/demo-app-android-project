package br.com.mobicare.cielo.pixMVVM.presentation.key

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.LayoutRes
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import androidx.annotation.StringRes
import br.com.cielo.libflue.screen.HandlerViewBuilderFlui
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.cielo.libflue.screen.HandlerViewFluiV2
import br.com.mobicare.cielo.commons.constants.ONE_DOUBLE
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.databinding.ActivityPixKeyNavigationFlowBinding
import br.com.mobicare.cielo.extensions.setNavGraphStartDestination
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pixMVVM.presentation.key.enums.PixKeyTypeButton

class PixKeyNavigationFlowActivity : BaseLoggedActivity(), CieloNavigation {

    private lateinit var binding: ActivityPixKeyNavigationFlowBinding

    private var bundle: Bundle? = null
    private var navigationListener: CieloNavigationListener? = null

    private val argsData: NavArgs.Data by lazy {
        NavArgs.Data(
            keyTypeId = intent.getIntExtra(NavArgs.KEY_TYPE_ARGS, ONE_NEGATIVE),
            currentBalance = intent.getDoubleExtra(NavArgs.CURRENT_BALANCE_ARGS, -ONE_DOUBLE)
        )
    }

    private val startDestinationId
        get(): @LayoutRes Int = when (argsData.keyTypeId) {
            PixKeyTypeButton.BANK_ACCOUNT.ordinal -> R.id.pixBankAccountSearchFragment
            else -> R.id.pixInsertAllKeysFragment
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle = savedInstanceState
        binding = ActivityPixKeyNavigationFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavGraphStartDestination()
        initializeCollapsingToolbarLayout()
    }

    private fun setupNavGraphStartDestination() {
        setNavGraphStartDestination(
            navHostFragmentId = R.id.navHostFragment,
            navGraphId = R.navigation.nav_graph_pix_key,
            startDestinationId = startDestinationId,
            args = Bundle().apply {
                putInt(NavArgs.KEY_TYPE_ARGS, argsData.keyTypeId)
            }
        )
    }

    private fun initializeCollapsingToolbarLayout() {
        binding.collapsingToolbarLayout.initialize(this)
    }

    override fun getData() = argsData

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return binding.collapsingToolbarLayout.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return binding.collapsingToolbarLayout.onOptionsItemSelected(item)
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }

    override fun configureCollapsingToolbar(configurator: CieloCollapsingToolbarLayout.Configurator) {
        binding.collapsingToolbarLayout.configure(configurator)
    }

    override fun showAnimatedLoading(@StringRes message: Int?) {
        binding.apply {
            collapsingToolbarLayout.gone()
            animatedProgressView.startAnimation(
                message ?: R.string.wait_animated_loading_start_message
            )
        }
    }

    override fun hideAnimatedLoading() {
        binding.apply {
            animatedProgressView.hideAnimationStart()
            animatedProgressView.gone()
            collapsingToolbarLayout.visible()
        }
    }

    override fun showContent(isShow: Boolean) {
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
        const val KEY_TYPE_ARGS = "KEY_TYPE_ARGS"
        const val CURRENT_BALANCE_ARGS = "CURRENT_BALANCE_ARGS"

        data class Data(
            val keyTypeId: Int,
            val currentBalance: Double
        )
    }

}