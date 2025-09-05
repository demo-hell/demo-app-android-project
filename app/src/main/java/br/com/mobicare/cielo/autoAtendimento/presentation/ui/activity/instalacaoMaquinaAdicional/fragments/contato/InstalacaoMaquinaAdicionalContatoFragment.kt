package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.contato

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.autoAtendimento.analytics.AutoAtendimentoAnalytics
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.engine.getNameMachine
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.analytics.Action.CONTINUAR
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_NAME_MACHINE
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_PERSON_NAME
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_PERSON_PHONE_NUMBER
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.helpers.EditTextHelper
import br.com.mobicare.cielo.commons.listener.EngineNextActionListener
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.afterTextChangesNotEmptySubscribe
import br.com.mobicare.cielo.commons.utils.phone
import br.com.mobicare.cielo.databinding.InstalacaoMaquinaAdicionalContatoFragmentBinding
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class InstalacaoMaquinaAdicionalContatoFragment : BaseFragment(),
    InstalacaoMaquinaAdicionalContatoContract.View, EngineNextActionListener {

    val presenter: InstalacaoMaquinaAdicionalContatoPresenter by inject {
        parametersOf(this)
    }

    private val analytics: AutoAtendimentoAnalytics by inject()

    private var actionListener: ActivityStepCoordinatorListener? = null

    private var _binding: InstalacaoMaquinaAdicionalContatoFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActivityStepCoordinatorListener) {
            actionListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = InstalacaoMaquinaAdicionalContatoFragmentBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logScreenView()
        setData()
        configureFields()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onDetach() {
        actionListener = null
        super.onDetach()
    }

    private fun logScreenView() {
        analytics.logScreenView(AutoAtendimentoAnalytics.SCREEN_VIEW_REQUEST_MACHINE_CONTACT)
    }

    private fun setData() {
        arguments?.let { arg ->
            val name = arg.getString(ARG_PARAM_PERSON_NAME) ?: return
            val phoneNumber = arg.getString(ARG_PARAM_PERSON_PHONE_NUMBER) ?: return

            presenter.setData(name, phoneNumber)
        }
    }

    private fun configureFields() {
        binding.apply {
            EditTextHelper.phoneField(tilNumeroCelular, tieNumeroCelular)
            tieNomeContato.afterTextChangesNotEmptySubscribe {
                tilNomeContato.error = null
                tilNomeContato.isErrorEnabled = false
            }
        }
    }

    override fun onClicked() {
        presenter.onNextButtonClicked(
            binding.tieNomeContato.text.toString(),
            binding.tieNumeroCelular.text.toString()
        )
    }

    override fun onShowNameError(errorMessage: String?) {
        binding.tilNomeContato.error = errorMessage
    }

    override fun onShowPhoneNumberError(errorMessage: String?) {
        binding.tilNumeroCelular.error = errorMessage
    }

    override fun goToNextScreen(nome: String, numeroTelefone: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SOLICITAR_MAQUININHA),
            action = listOf(Action.SOLICITAR_MAQUININHA, getNameMachine().orEmpty()),
            label = listOf(Label.BOTAO, String.format(PASSO_FORMAT, THREE), CONTINUAR)
        )

        actionListener?.onNextStep(false, Bundle().apply {
            putString(ARG_PARAM_PERSON_NAME, nome)
            putString(ARG_PARAM_PERSON_PHONE_NUMBER, numeroTelefone)
            putString(ARG_PARAM_NAME_MACHINE, getNameMachine().orEmpty())
        })
    }

    override fun setPersonData(nome: String, numeroTelefone: String) {
        binding.apply {
            tieNomeContato.setText(nome)
            tieNumeroCelular.setText(numeroTelefone.phone())
        }
    }

    companion object {
        fun create(bundle: Bundle?) = InstalacaoMaquinaAdicionalContatoFragment().apply {
            arguments = bundle
        }
    }

}
