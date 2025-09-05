package br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.CardPhotoDescriptionActivity
import kotlinx.android.synthetic.main.fragment_activation_card_start.*
import org.jetbrains.anko.startActivity

class AccountCardActivationFWDFragment : BaseFragment() {

    var screenPath: String? = null
        get() = arguments?.getString(SCREEN_PATH)

    companion object {

        const val SCREEN_PATH = "br.com.cielo.cardActivation.screenPath"

        fun create(currentScreenPath: String): AccountCardActivationFWDFragment {

            val params = Bundle()

            val startCardActivationFragment = AccountCardActivationFWDFragment()
            if (currentScreenPath.isNotEmpty()) {
                params.putString(SCREEN_PATH, currentScreenPath)
                startCardActivationFragment.arguments = params
            }

            return startCardActivationFragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fwd_check_account,
                container, false)

    }

    override fun onResume() {
        super.onResume()

        buttonUnlockCreditCard.setOnClickListener {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAPBUTTON),
                action = listOf(currentScreenPath()),
                label = listOf(Label.UNLOCK_CARD_LABEL)
            )

            requireActivity().startActivity<CardPhotoDescriptionActivity>(
                    CardPhotoDescriptionActivity.RESEND_DOC to false)
        }

        Analytics.trackScreenView(
            screenName = currentScreenPath(),
            screenClass = this.javaClass
        )
    }

    fun currentScreenPath(): String = "$screenPath/${Action.CARD_FWD_SITE}"

}