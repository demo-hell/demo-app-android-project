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
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.UnlockCreditCardActivity
import kotlinx.android.synthetic.main.fragment_activation_card_start.*
import org.jetbrains.anko.startActivity

class StartCardActivationFragment : BaseFragment() {

    private  var mIssuer: String = ""

    var screenPath: String? = null
        get() = arguments?.getString(SCREEN_PATH)

    companion object {

        const val SCREEN_PATH = "br.com.cielo.cardActivation.screenPath"
        const val SCREEN_ISSUER = "br.com.cielo.cardActivation.issuer"

        fun create(currentScreenPath: String, issuer: String): StartCardActivationFragment {

            val params = Bundle()
            val startCardActivationFragment = StartCardActivationFragment()
            if (currentScreenPath.isNotEmpty()) {
                params.putString(SCREEN_PATH, currentScreenPath)
            }
            params.putString(SCREEN_ISSUER, issuer)
            startCardActivationFragment.arguments = params

            return startCardActivationFragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_activation_card_start,
                container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.arguments?.let {
            mIssuer = it.getString(SCREEN_ISSUER, "")
        }

    }

    override fun onResume() {
        super.onResume()

        buttonUnlockCreditCard.setOnClickListener {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAPBUTTON),
                action = listOf(currentScreenPath()),
                label = listOf(Label.UNLOCK_CARD_LABEL)
            )

                requireActivity().startActivity<UnlockCreditCardActivity>(
                        SCREEN_PATH to currentScreenPath(),
                        SCREEN_ISSUER to  mIssuer)
        }

        Analytics.trackScreenView(
            screenName = currentScreenPath(),
            screenClass = this.javaClass
        )
    }

    fun currentScreenPath(): String = "$screenPath/${Action.UNLOCK_CARD_SCREEN}"

}