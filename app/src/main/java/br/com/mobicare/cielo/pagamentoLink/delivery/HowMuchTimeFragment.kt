package br.com.mobicare.cielo.pagamentoLink.delivery

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_PAYMENT_LINK_DTO
import br.com.mobicare.cielo.commons.listener.EngineNextActionListener
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO
import kotlinx.android.synthetic.main.fragment_how_much_time.*

class HowMuchTimeFragment : BaseFragment(), EngineNextActionListener, View.OnClickListener {

    private var actionJorneyListner: ActivityStepCoordinatorListener? = null
    private var paymentLinkDTO: PaymentLinkDTO? = null

    companion object {
        fun newInstance(extras: Bundle) = HowMuchTimeFragment().apply { this.arguments = extras }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_how_much_time, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            init()
    }

    @SuppressLint("NewApi")
    private fun init() {
        this.actionJorneyListner?.setTitle("Entrega Loggi")
        this.actionJorneyListner?.setButtonName(getString(R.string.coil_button_next))
        this.actionJorneyListner?.enableNextButton(false)
        this.editTextTimeMinutes?.setMaxLength(4)
        this.arguments?.getParcelable<PaymentLinkDTO>(ARG_PARAM_PAYMENT_LINK_DTO)?.let {
            paymentLinkDTO = it
            populateFieds()
            textViewShortTerm.setOnClickListener(this)
            checkboxTerm.setOnCheckedChangeListener { buttonView, isChecked ->
                this.actionJorneyListner?.enableNextButton(isChecked)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActivityStepCoordinatorListener) {
            this.actionJorneyListner = context
            this.actionJorneyListner?.setTitle(getString(R.string.acceptance_term_toolbar_title))
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.actionJorneyListner = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            textViewShortTerm.id -> {
                startActivity(Intent(context, InfoActivity::class.java))
            }
        }
    }

    override fun onClicked() {
        this.paymentLinkDTO?.let { payDto ->
            if (!editTextTimeMinutes.getText().isBlank())

                try {
                    payDto.dalayTime = editTextTimeMinutes.getText().toInt()
                }
                catch(error: Throwable) {
                    editTextTimeMinutes.setError("Valor inválido")
                    return@let
                }

//            actionJorneyListner?.onNextStep(false, Bundle().apply {
//                putParcelable(ARG_PARAM_PAYMENT_LINK_DTO, payDto)
//            }, Pair(EnginePagamentoPorLinkFragment.SEND_OBJECT_TIME_CONFIGURATION_STEP,
//                    EnginePagamentoPorLinkFragment.SEND_OBJECT_ADDRESS_CONFIGURATION_STEP)
//            )
        }
    }

    private fun populateFieds() {
        this.paymentLinkDTO?.dalayTime?.let {
            this.editTextTimeMinutes?.setText(it.toString())
            this.checkboxTerm?.isChecked = true
            this.actionJorneyListner?.enableNextButton(true)
        }
    }
}