package br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chat.presentation.utils.ChatApollo
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_fwd_problem.*


class CardProblemFWDFragment : BaseFragment() {

    var screenPath: String? = null
        get() = arguments?.getString(SCREEN_PATH)

    companion object {

        const val SCREEN_PATH = "br.com.cielo.cardActivation.screenPath"

        fun create(currentScreenPath: String): CardProblemFWDFragment {

            val params = Bundle()

            val startCardActivationFragment = CardProblemFWDFragment()
            if (currentScreenPath.isNotEmpty()) {
                params.putString(SCREEN_PATH, currentScreenPath)
                startCardActivationFragment.arguments = params
            }

            return startCardActivationFragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fwd_problem,
                container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonAcessChat.setOnClickListener {
            ChatApollo.callChatNotLogin(requireActivity())
        }

        textCallPhone.setOnClickListener {

            //Utils.callPhone(requireActivity(), getString(R.string.text_my_cards_call_phone_number))
            val ftsucessBS = CallCentralBSfrag.newInstance()
            ftsucessBS.show(requireFragmentManager(),
                    "CallCentralBSfrag")
        }
    }


    override fun onResume() {
        super.onResume()

        Analytics.trackScreenView(
            screenName = currentScreenPath(),
            screenClass = this.javaClass
        )
    }

    fun currentScreenPath(): String = "$screenPath/${Action.CARD_FWD_SITE}"

}