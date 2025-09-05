package br.com.mobicare.cielo.eventTracking.presentation

import android.os.Bundle
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.databinding.EventTrackingFlowActivityBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible

class EventTrackingNavigationFlowActivity : BaseLoggedActivity(), CieloNavigation {

    private var navigationListener: CieloNavigationListener? = null
    private var _binding: EventTrackingFlowActivityBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventTrackingFlowActivityBinding.inflate(layoutInflater).also { _binding = it }
        setContentView(binding.root)

        changeStatusBarColor()
    }

    override fun showBackButton(isShow: Boolean) {
        binding.navigationIcon.visible(isShow)
    }

    override fun showHelpButton(isShow: Boolean) {
        binding.helpIcon.visible(isShow)
        binding.root.getConstraintSet(R.id.start)?.apply {
            this.visible(binding.helpIcon.id, isShow)
        }
        binding.root.getConstraintSet(R.id.end)?.apply {
            this.visible(binding.helpIcon.id, isShow)
        }
    }

    override fun setupToolbar(title: String, isCollapsed: Boolean, subtitle: String?) {
        binding.apply {
            myRequestsTitle.text = title
            if (!subtitle.isNullOrEmpty()) {
                root.getConstraintSet(R.id.start)?.let { startConstraintSet ->
                    startConstraintSet.visible(myRequestsSubtitle.id)
                }
                myRequestsSubtitle.text = subtitle
            } else {
                root.getConstraintSet(R.id.start)?.let { startConstraintSet ->
                    startConstraintSet.gone(myRequestsSubtitle.id)
                }
            }
            navigationIcon.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            helpIcon.setOnClickListener {
                //doNothing
            }

            if (isCollapsed) {
                root.transitionToEnd()
            } else {
                root.transitionToStart()
            }
        }
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }

    override fun onDestroy() {
        super.onDestroy()
        navigationListener = null
        _binding = null
    }
}