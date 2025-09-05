package br.com.mobicare.cielo.commons.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import br.com.mobicare.cielo.R
import kotlinx.android.synthetic.main.fragment_info_cielo.*

class CieloInfoFragment : androidx.fragment.app.Fragment() {


    private var title: String? = null
        get() = arguments?.getString(CIELO_INFO_TITLE)

    private var description: String? = null
        get() = arguments?.getString(CIELO_INFO_DESCRIPTION)

    private var drawableId: Int? = null
        get() = arguments?.getInt(CIELO_INFO_DRAWABLE)

    companion object {

        const val CIELO_INFO_TITLE = "br.com.cielo.commons.infoTitleKey"
        const val CIELO_INFO_DESCRIPTION = "br.com.cielo.commons.infoDescriptionKey"
        const val CIELO_INFO_DRAWABLE = "br.com.cielo.commons.infoImageKey"

        fun create(title: String,
                   description: String,
                   @DrawableRes drawableId: Int): CieloInfoFragment {
            val cieloInfoFragment = CieloInfoFragment()

            val bundleParams = Bundle()
            bundleParams.putString(CIELO_INFO_TITLE, title)
            bundleParams.putString(CIELO_INFO_DESCRIPTION, description)
            bundleParams.putInt(CIELO_INFO_DRAWABLE, drawableId)

            cieloInfoFragment.arguments = bundleParams

            return cieloInfoFragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_info_cielo, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawableId?.run {
            textInfoTitle.text = title
            textInfoDescription.text = description
            imageInfoContent.setImageResource(this)
        }

    }
}