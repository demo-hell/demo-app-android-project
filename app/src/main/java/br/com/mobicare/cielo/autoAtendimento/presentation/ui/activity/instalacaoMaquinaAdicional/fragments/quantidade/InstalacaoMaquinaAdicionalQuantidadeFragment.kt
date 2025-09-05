package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.quantidade

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView
import br.com.mobicare.cielo.autoAtendimento.analytics.AutoAtendimentoAnalytics
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.analytics.Action.CONTINUAR
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_AMOUNT_MACHINE
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_MACHINE_ITEM
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_NAME_MACHINE
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THIRTY_NINE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.listener.EngineNextActionListener
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.parcelable
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.InstalacaoMaquinaAdicionalQuantidadeFragmentBinding
import br.com.mobicare.cielo.machine.domain.MachineItemOfferResponse
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class InstalacaoMaquinaAdicionalQuantidadeFragment : BaseFragment(),
    InstalacaoMaquinaAdicionalQuantidadeContract.View, EngineNextActionListener {

    val presenter: InstalacaoMaquinaAdicionalQuantidadePresenter by inject {
        parametersOf(this)
    }

    private val analytics: AutoAtendimentoAnalytics by inject()

    private var actionListner: ActivityStepCoordinatorListener? = null

    private var _binding: InstalacaoMaquinaAdicionalQuantidadeFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActivityStepCoordinatorListener) {
            actionListner = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = InstalacaoMaquinaAdicionalQuantidadeFragmentBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logScreenView()
        loadData()
        configureViews()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onDetach() {
        actionListner = null
        super.onDetach()
    }

    private fun loadData() {
        arguments?.parcelable<MachineItemOfferResponse>(ARG_PARAM_MACHINE_ITEM)?.let {
            presenter.setData(it)
        }
    }

    private fun logScreenView() {
        analytics.logScreenView(AutoAtendimentoAnalytics.SCREEN_VIEW_REQUEST_MACHINE_QUANTITY)
    }

    private fun configureViews() {
        binding.apply {
            minusButton.setOnClickListener {
                presenter.minusButtonClicked()
            }
            plusButton.setOnClickListener {
                presenter.plusButtonClicked()
            }
        }
    }

    private fun configureSpacer() {
        view?.parent?.let {
            if (it is FrameLayout) {
                it.parent?.let { itLayout ->
                    if (itLayout is ScrollView) {
                        itLayout.post {
                            val lp = binding.spaceView.layoutParams
                            var diff = itLayout.height - it.height
                            if (diff <= ZERO) diff = THIRTY_NINE
                            lp?.height = diff
                            binding.spaceView.layoutParams = lp
                            binding.spaceView.requestLayout()
                        }
                    }
                }
            }
        }
    }

    override fun loadImage(url: String) {
        Picasso
            .get()
            .load(url)
            .into(binding.machineImageView, object : Callback {
                override fun onSuccess() {
                    configureSpacer()
                }
                override fun onError(e: Exception?) {
                    e?.printStackTrace()
                }
            })
    }

    override fun setEnableMinusButton(isEnable: Boolean) {
        binding.minusButton.isEnabled = isEnable
    }

    override fun setEnablePlusButton(isEnable: Boolean) {
        binding.plusButton.isEnabled = isEnable
    }

    override fun setAmount(amount: Int) {
        binding.amountText.text = amount.toString()
    }

    override fun setTitle(title: String) {
        binding.machineNameTextView.text = title
    }

    override fun setRentalAmount(value: Double) {
        binding.rentalAmountText.text = value.toPtBrRealString()
    }

    override fun setNotification(text: String) {
        binding.notificationText.text = text
    }

    override fun onClicked() {
        presenter.onNextButtonClicked()
    }

    override fun goToNextScreen(title: String?, value: Double?, amount: Int) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SOLICITAR_MAQUININHA),
            action = listOf(Action.SOLICITAR_MAQUININHA, title.orEmpty()),
            label = listOf(Label.BOTAO, String.format(PASSO_FORMAT, ONE), CONTINUAR)
        )

        analytics.logRequestMachineAddPaymentInfo(
            machineName = title.orEmpty(),
            value = value ?: ZERO_DOUBLE,
            quantity = amount
        )

        actionListner?.onNextStep(false, Bundle().apply {
            putInt(ARG_PARAM_AMOUNT_MACHINE, amount)
            putString(ARG_PARAM_NAME_MACHINE, title.orEmpty())
        })
    }

    override fun isEnabledNextButton(isEnabled: Boolean) {
        actionListner?.enableNextButton(isEnabled)
    }

    companion object {
        fun create(bundle: Bundle?) = InstalacaoMaquinaAdicionalQuantidadeFragment().apply {
            arguments = bundle
        }
    }

}
