package br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.fragment.SuccessFragment
import br.com.mobicare.cielo.commons.utils.SCREEN_CURRENT_PATH
import br.com.mobicare.cielo.commons.utils.addInFrame

class CardSuccessFragment : BaseFragment(), SuccessFragment.SuccessEndActionListener {

    lateinit var onReturnDigitalAccount: () -> Unit

    companion object {
        fun newInstance(screenPath: String, title: String, description: String, button: String, onReturnDigitalAccount: () -> Unit): CardSuccessFragment {

            val cardSuccessFragment = CardSuccessFragment()

            cardSuccessFragment.onReturnDigitalAccount = onReturnDigitalAccount

            if (screenPath.isNotEmpty()) {
                val params = Bundle()
                params.putString(SCREEN_CURRENT_PATH, screenPath)
                params.putString(TITLE, title)
                params.putString(DESCRIPTION, description)
                params.putString(BUTTON, button)
                cardSuccessFragment.arguments = params
            }

            return cardSuccessFragment
        }

        const val DESCRIPTION = "br.com.cielo.description"
        const val TITLE = "br.com.cielo.tile"
        const val BUTTON = "br.com.cielo.button"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_success_card, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val successFragment = SuccessFragment.create(
            this.requireArguments().getString(SCREEN_CURRENT_PATH)!!,
            this.requireArguments().getString(TITLE)!!,
            this.requireArguments().getString(DESCRIPTION)!!,
            this.requireArguments().getString(BUTTON)!!)


        successFragment.successClickListener = this
        successFragment.addInFrame(fragmentManager, R.id.frameSuccessContent)
    }

    override fun onEndSucessClick() {
        onReturnDigitalAccount()
    }

}