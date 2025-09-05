package br.com.mobicare.cielo.coil.presentation.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.cielo.libflue.util.ONE
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.COILS
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.SCREEN_VIEW_REQUEST_MATERIALS_COIL_CONFIRM_QUANTITY
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.SuppliesAcitivytContract
import br.com.mobicare.cielo.coil.domains.CoilOptionObj
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.constants.ONE_FLOAT
import br.com.mobicare.cielo.commons.constants.TWO_THOUSAND
import br.com.mobicare.cielo.commons.constants.ZERO_COMMA_FIVE_FLOAT
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentCoilAmountBinding
import kotlinx.android.synthetic.main.fragment_coil_amount.*
import org.koin.android.ext.android.inject

class CoilAmountFragment : BaseFragment() {

    private var binding: FragmentCoilAmountBinding? = null

    private lateinit var actionListener: ActivityStepCoordinatorListener
    private lateinit var title: String

    private val ga4: SelfServiceAnalytics by inject()
    private var coilOption: CoilOptionObj = CoilOptionObj()
    private var callBack: SuppliesAcitivytContract.View? = null

    companion object {
        fun create(
            listener: ActivityStepCoordinatorListener,
            coilOptionObj: CoilOptionObj,
            callBackView: SuppliesAcitivytContract.View
        ): CoilAmountFragment {
            return CoilAmountFragment().apply {
                listener.setTitle(coilOptionObj.title)
                actionListener = listener
                title = coilOptionObj.title
                callBack = callBackView
                coilOption = coilOptionObj
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCoilAmountBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun onResume() {
        super.onResume()
        logScreenView()
    }

    private fun setupView() {
        binding?.apply {
            buttonNext.isEnabled = false
            buttonNext.alpha = ZERO_COMMA_FIVE_FLOAT
            Handler().postDelayed({
                if (isAttached()) {
                    buttonNext.isEnabled = true
                    buttonNext.alpha = ONE_FLOAT
                    buttonNext.setOnClickListener {
                        sendTagEvent()
                        sendCoilToCallBack()
                        actionListener.onNextStep(false)
                    }
                }
            }, TWO_THOUSAND)
        }
    }

    private fun sendTagEvent() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SOLICITAR_MATERIAIS),
            action = listOf(BOBINA, this.title),
            label = listOf(Label.BOTAO, "${button_next.text}")
        )
    }

    private fun logScreenView() = ga4.logScreenView(SCREEN_VIEW_REQUEST_MATERIALS_COIL_CONFIRM_QUANTITY)

    private fun sendCoilToCallBack(){

        val coilOptions = ArrayList<CoilOptionObj>(ONE)
        coilOption.tagService = COILS
        coilOptions.add(coilOption)
        callBack?.listStickers(coilOptions)
    }
}