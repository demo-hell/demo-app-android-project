package br.com.mobicare.cielo.login.firstAccess.presentation

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.lifecycle.lifecycleScope
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.databinding.ActivityFirstAccessNavigationFlowBinding
import br.com.mobicare.cielo.extensions.gone

class FirstAccessNavigationFlowActivity : BaseActivity() , CieloNavigation {
    private var navigationListener: CieloNavigationListener? = null
    private var _binding: ActivityFirstAccessNavigationFlowBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityFirstAccessNavigationFlowBinding.inflate(layoutInflater).also { _binding = it }
        setContentView(binding?.root)
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }

    override fun onDestroy() {
        super.onDestroy()
        navigationListener = null
        _binding = null
    }

    override fun showAnimatedLoading(@StringRes message: Int?) {
        binding.apply {
            animatedProgressView.startAnimation(
                message ?: R.string.wait_animated_loading_start_message
            )
        }
    }

    override fun hideAnimatedLoading() {
        binding.apply {
            animatedProgressView.hideAnimationStart()
            animatedProgressView.gone()
        }
    }

    override fun hideKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, ZERO)
    }

    override fun showCustomBottomSheet(
        @DrawableRes image: Int?,
        title: String?,
        message: String?,
        bt1Title: String?,
        bt2Title: String?,
        bt1Callback: (() -> Boolean)?,
        bt2Callback: (() -> Boolean)?,
        closeCallback: (() -> Unit)?,
        isCancelable: Boolean,
        isPhone: Boolean,
        titleBlack: Boolean
    ) {
        lifecycleScope.launchWhenResumed {
            bottomSheetGenericFlui(
                image = image ?: R.drawable.ic_generic_error_image,
                title = title ?: getString(R.string.generic_error_title),
                subtitle = message ?: getString(R.string.error_generic),
                nameBtn1Bottom = bt1Title ?: EMPTY,
                nameBtn2Bottom = bt2Title ?: getString(R.string.entendi),
                txtTitleStyle = if (titleBlack) TxtTitleStyle.TXT_TITLE_DARK_BLACK else TxtTitleStyle.TXT_TITLE_DARK_BLUE,
                txtSubtitleStyle = if (titleBlack) TxtSubTitleStyle.TXT_SUBTITLE_BLACK_CENTER else TxtSubTitleStyle.TXT_SUBTITLE_FLUI_BOTTOM_SHEET,
                statusBtnFirst = bt1Title != null,
                btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
                btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
                isCancelable = isCancelable,
                isFullScreen = false,
                isPhone = isPhone
            ).apply {
                onClick =
                    object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {

                        override fun onBtnFirst(dialog: Dialog) {
                            if (bt1Callback?.invoke() != true) dismiss()
                        }

                        override fun onBtnSecond(dialog: Dialog) {
                            if (bt2Callback?.invoke() != true) dismiss()
                        }

                        override fun onSwipeClosed() {
                            closeCallback?.invoke()
                        }

                        override fun onCancel() {
                            closeCallback?.invoke()
                        }
                    }
            }.show(
                supportFragmentManager,
                getString(R.string.bottom_sheet_generic)
            )
        }
    }

    override fun showCustomHandlerView(
        @DrawableRes contentImage: Int,
        @DrawableRes headerImage: Int,
        @StyleRes titleStyle: Int,
        @StyleRes messageStyle: Int,
        messageHandler: String,
        titleHandler: String,
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
            illustration = contentImage
            title = titleHandler
            message = messageHandler
            messageTextAppearance = R.style.medium_montserrat_16_neutral_800
            labelPrimaryButton = labelFirstButton
            isShowBackButton = false
            onPrimaryButtonClickListener =
                object : HandlerViewBuilderFluiV2.HandlerViewListener {
                    override fun onClick(dialog: Dialog?) {
                        dialog?.dismiss()
                        callbackFirstButton.invoke()
                    }
                }

            onIconButtonEndHeaderClickListener =
                object : HandlerViewBuilderFluiV2.HandlerViewListener {
                    override fun onClick(dialog: Dialog?) {
                        dialog?.dismiss()
                        callbackBack.invoke()
                    }
                }
        }.build().show(supportFragmentManager, this.javaClass.name)
    }
}