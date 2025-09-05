package br.com.mobicare.cielo.commons.ui.widget

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import br.com.mobicare.cielo.R
import kotlinx.android.synthetic.main.fragment_info_card_cielo.*

class CieloCardInfoFragment : androidx.fragment.app.Fragment() {


    private var title: String? = null
        get() = arguments?.getString(TITLE)

    private var description: String? = null
        get() = arguments?.getString(DESCRIPTION)

    private var buttonLabel: String? = null
        get() = arguments?.getString(BUTTON_LABEL)

    private var infoLogo: Int? = null
        get() = arguments?.getInt(INFO_LOGO)


    var onActionClickListener: OnActionButtonClick? = null

    interface OnActionButtonClick {
        fun onClick()
    }


    companion object {
        const val TITLE = "br.com.cielo.common.title"
        const val DESCRIPTION = "br.com.cielo.common.description"
        const val BUTTON_LABEL = "br.com.cielo.common.buttonLabel"
        const val INFO_LOGO = "br.com.cielo.common.infoLogo"

        fun create(title: String, @DrawableRes infoLogo: Int,
                   description: String, buttonLabel: String): CieloCardInfoFragment {
            val cieloCardInfoFrag = CieloCardInfoFragment()

            val args = Bundle()

            args.putString(TITLE, title)
            args.putInt(INFO_LOGO, infoLogo)
            args.putString(DESCRIPTION, description)
            args.putString(BUTTON_LABEL, buttonLabel)

            cieloCardInfoFrag.arguments = args

            return cieloCardInfoFrag
        }
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_info_card_cielo, container, false)
    }

    override fun onResume() {
        super.onResume()

        infoLogo?.run {
            imageInfoLogo.setImageResource(this)
        }

        title?.run {
            textInfoTitle.text = SpannableStringBuilder.valueOf(this)
        }

        description?.run {
            textDescriptionInfo.text = SpannableStringBuilder.valueOf(this)
        }

        buttonLabel?.run {
            buttonInfo.setText(this)
        }

        buttonInfo.setOnClickListener {
            onActionClickListener?.onClick()
        }
    }
}