package br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.ui.BaseFragment

class ProcessingCardActivationFWDFragment : BaseFragment() {

    var screenPath: String? = null
        get() = arguments?.getString(SCREEN_PATH)

    companion object {

        const val SCREEN_PATH = "br.com.cielo.cardActivation.screenPath"

        fun create(currentScreenPath: String): ProcessingCardActivationFWDFragment {

            val params = Bundle()

            val startCardActivationFragment = ProcessingCardActivationFWDFragment()
            if (currentScreenPath.isNotEmpty()) {
                params.putString(SCREEN_PATH, currentScreenPath)
                startCardActivationFragment.arguments = params
            }

            return startCardActivationFragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fwd_card_processing,
                container, false)

    }

    override fun onResume() {
        super.onResume()

//        buttonUnlockCreditCard.setOnClickListener {
//
//            Analytics.trackEvent(
//                category = listOf(Category.APP_CIELO, Category.TAPBUTTON),
//                action = listOf(currentScreenPath()),
//                label = listOf(Label.UNLOCK_CARD_LABEL)
//            )
//
//            val browserIntent = Intent(
//                    Intent.ACTION_VIEW,
//                    Uri.parse("https://minhaconta2.cielo.com.br/login/"))
//            startActivity(browserIntent)
//
//
//        }

        Analytics.trackScreenView(
            screenName = currentScreenPath(),
            screenClass = this.javaClass
        )
    }

    fun currentScreenPath(): String = "$screenPath/${Action.CARD_FWD_SITE}"

}