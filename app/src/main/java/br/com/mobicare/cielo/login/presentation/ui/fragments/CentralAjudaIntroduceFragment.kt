package br.com.mobicare.cielo.login.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.CentralAjudaActvity
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.fragments.CentralAjudaFragment
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_central_ajuda_introduce.*
import org.jetbrains.anko.startActivity

class CentralAjudaIntroduceFragment : BaseFragment() {

    lateinit var screenName: String

    companion object {
        fun newInstance(screenName: String): CentralAjudaIntroduceFragment =
            CentralAjudaIntroduceFragment().apply {
                this.screenName = screenName
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_central_ajuda_introduce, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.helpCenterButton?.setOnClickListener {
            gaSendButton("central de ajuda")
            requireActivity().startActivity<CentralAjudaActvity>(
                CentralAjudaFragment.SCREEN_NAME to screenName,
                CentralAjudaFragment.MERCHANT_ID to ""
            )
        }
    }

    private fun gaSendButton(labelButton: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, LOGIN_COMO_ACESSAR),
            action = listOf(Action.FORMULARIO),
            label = listOf(Label.BOTAO, labelButton.replace("\n", ""))
        )
    }

}