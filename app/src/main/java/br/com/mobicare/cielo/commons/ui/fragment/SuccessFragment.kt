package br.com.mobicare.cielo.commons.ui.fragment

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.StartCardActivationFragment
import kotlinx.android.synthetic.main.view_success.*

class SuccessFragment : BaseFragment() {

    var successClickListener: SuccessEndActionListener? = null

    var title: String? = null
        get() = arguments?.getString(TITLE_KEY)

    var description: String? = null
        get() = arguments?.getString(DESCRIPTION_KEY)

    var buttonDescription: String? = null
        get() = arguments?.getString(BUTTON_KEY)

    var screenPath: String? = null
        get() = arguments?.getString(StartCardActivationFragment.SCREEN_PATH)

    companion object {

        const val TITLE_KEY = "br.com.cielo.title"
        const val DESCRIPTION_KEY = "br.com.cielo.description"
        const val BUTTON_KEY = "br.com.cielo.button"

        fun create(currentPath: String,
                   title: String,
                   description: String,
                   button: String): SuccessFragment {

            val params = Bundle()

            params.putString(TITLE_KEY, title)
            params.putString(DESCRIPTION_KEY, description)
            params.putString(BUTTON_KEY, button)
            params.putString(StartCardActivationFragment.SCREEN_PATH, currentPath)

            val successFragment = SuccessFragment()
            successFragment.arguments = params

            return successFragment
        }

    }

    interface SuccessEndActionListener {
        fun onEndSucessClick()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.view_success, container, false)
    }


    override fun onResume() {
        super.onResume()

        textSuccessTitle.text = SpannableStringBuilder.valueOf(title)
        textSuccessDescription.text = SpannableStringBuilder.valueOf(description)
        buttonEndAction.setText(SpannableStringBuilder.valueOf(buttonDescription).toString())

        buttonEndAction.setOnClickListener {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAPBUTTON),
                action = listOf(screenPath.toString()),
                label = listOf(buttonEndAction.text.toString())
            )
            successClickListener?.onEndSucessClick()
        }
    }
}
