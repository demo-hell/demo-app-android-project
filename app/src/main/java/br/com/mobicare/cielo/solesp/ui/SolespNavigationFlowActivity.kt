package br.com.mobicare.cielo.solesp.ui

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import br.com.cielo.libflue.screen.HandlerViewBuilderFlui
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.databinding.ActivitySolespNavigationFlowBinding
import br.com.mobicare.cielo.extensions.moveToHome
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import kotlinx.android.synthetic.main.toolbar_solesp.view.*

class SolespNavigationFlowActivity : BaseLoggedActivity(), CieloNavigation {

    private var binding: ActivitySolespNavigationFlowBinding? = null

    private var bundle: Bundle? = null
    private var navigationListener: CieloNavigationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySolespNavigationFlowBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        configureListeners()

        bundle = savedInstanceState
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }

    override fun setTextButton(text: String) {
        binding?.btnNext?.text = text
    }

    override fun enableButton(isEnabled: Boolean) {
        binding?.btnNext?.isEnabled = isEnabled
    }

    override fun showHelpButton(isShow: Boolean) {
        binding?.toolbar?.icButtonRight.visible(isShow)
    }

    override fun showLoading(isShow: Boolean) {
        binding?.apply {
            btnNext.visible(isShow.not())
            if (isShow) loadingProgress.showAnimationStart(R.string.txt_loading_solesp_send_request)
            else loadingProgress.hideAnimationStart()
        }
    }

    override fun showSuccess() {
        showHandler(
            R.string.txt_title_success_bottom_sheet_solesp_send_request,
            R.string.txt_message_success_bottom_sheet_solesp_send_request,
            R.drawable.ic_23
        )
    }

    override fun showError(onAction: () -> Unit) {
        showHandler(
            R.string.txt_title_error_bottom_sheet_solesp_send_request,
            R.string.txt_message_error_bottom_sheet_solesp_send_request,
            R.drawable.ic_07,
            onAction
        )
    }

    private fun showHandler(
        @StringRes title: Int,
        @StringRes message: Int,
        @DrawableRes icon: Int,
        onClickTryAgain: (() -> Unit)? = null
    ) {
        HandlerViewBuilderFlui.Builder(this)
            .title(getString(title))
            .message(getString(message))
            .messageStyle(R.style.Paragraph_400_regular_16_display_400)
            .contentImage(icon)
            .labelContained(getString(R.string.go_to_home_pix))
            .labelOutlined(getString(R.string.text_try_again_label))
            .isShowButtonBack(false)
            .isShowButtonOutlined(onClickTryAgain != null)
            .isShowHeaderImage(false)
            .containedClickListener(object : HandlerViewBuilderFlui.ContainedOnClickListener {
                override fun onClick(dialog: Dialog?) {
                    finishScreen(dialog, true)
                }
            })
            .outlinedClickListener(object : HandlerViewBuilderFlui.OutlinedOnClickListener {
                override fun onClick(dialog: Dialog?) {
                    onClickTryAgain?.invoke()
                    finishScreen(dialog, false)
                }
            })
            .build()
            .show(supportFragmentManager, this.javaClass.name)
    }

    private fun finishScreen(dialog: Dialog?, closeActivity: Boolean) {
        dialog?.dismiss()
        if (closeActivity) {
            moveToHome()
            finish()
        }
    }

    private fun configureListeners() {
        binding?.apply {
            toolbar.root.apply {
                icArrowLeft.setOnClickListener {
                    onBackPressed()
                }
            }
            btnNext.setOnClickListener {
                navigationListener?.onButtonClicked(btnNext.text.toString())
            }
        }
    }

}