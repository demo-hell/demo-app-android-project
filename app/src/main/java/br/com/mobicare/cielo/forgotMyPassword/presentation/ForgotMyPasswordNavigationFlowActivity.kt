package br.com.mobicare.cielo.forgotMyPassword.presentation

import android.os.Bundle
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.databinding.ActivityForgotMyPasswordNavigationFlowBinding
import br.com.mobicare.cielo.extensions.visible
import kotlinx.android.synthetic.main.toolbar_solesp.view.icArrowLeft

class ForgotMyPasswordNavigationFlowActivity : BaseLoggedActivity(), CieloNavigation {
    private var binding: ActivityForgotMyPasswordNavigationFlowBinding? = null
    private var bundle: Bundle? = null
    private var navigationListener: CieloNavigationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotMyPasswordNavigationFlowBinding.inflate(layoutInflater)
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
        binding?.btnNext?.isButtonEnabled = isEnabled
    }

    override fun showButton(isShow: Boolean) {
        binding?.btnNext?.visible(isShow)
    }

    override fun showToolbar(isShow: Boolean) {
        binding?.toolbar?.root.visible(isShow)
    }

    override fun showLoading(isShow: Boolean) {
        binding?.apply {
            btnNext.visible(isShow.not())
            if (isShow) loadingProgress.startAnimation(R.string.loading_message)
            else loadingProgress.hideAnimationStart()
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