package br.com.mobicare.cielo.onboarding.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.onboarding.domains.entities.OnboardingObj
import kotlinx.android.synthetic.main.fragment_onboarding_pages.*

/**
 * Created by gustavon on 20/10/17.
 */
class OnBoardingPagesFragment : BaseFragment() {

    var title: String = "Acompanhe suas vendas"
    var subtitle: String = "Acesse seu extrato em tempo real e consulte suas vendas. Você ainda pode visualizar e enviar os seus comprovantes para seus clientes."
    var image: Int = R.drawable.device_one

    companion object {
        var TITLE: String = "TITLE"
        var SUBTITLE: String = "SUBTITLE"
        var IMAGE: String = "IMAGEM"

        fun getInstance(page: OnboardingObj.Page, image : Int): OnBoardingPagesFragment {
            val fragment = OnBoardingPagesFragment()
            val bundle = Bundle()
            bundle.putString(TITLE, page.title)
            bundle.putString(SUBTITLE, page.subtitle)
            bundle.putInt(IMAGE, image)
            fragment.arguments = bundle

            return fragment
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_onboarding_pages, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val bundle = this.arguments
        if (bundle != null) {
            title = bundle.getString(TITLE)!!
            subtitle = bundle.getString(SUBTITLE)!!
            image = bundle.getInt(IMAGE)
        }


        title_onboarding.setText(title)
        subtitle_onboarding.setText(subtitle)
        image_device.setImageResource(image)

    }


}