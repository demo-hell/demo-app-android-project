package br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfService

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.domain.model.Supply
import br.com.mobicare.cielo.autoAtendimento.presentation.presenter.AutoAtendimentoContract
import br.com.mobicare.cielo.autoAtendimento.presentation.presenter.AutoAtendimentoPresenter
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.addInFrame
import kotlinx.android.synthetic.main.auto_atendimento.*
import kotlinx.android.synthetic.main.auto_atendimento_machine_fragment.*
import kotlinx.android.synthetic.main.fragment_receba_mais_failure.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class AutoAtendimentoFragment : BaseFragment(), AutoAtendimentoContract.View {


    private val paraVenderMais: AutoAtendimentoVenderMaisFragment by inject()

    val presenter: AutoAtendimentoPresenter by inject {
        parametersOf(this)
    }
    private val materias: AutoAtendimentoMateriasFragment by inject()


    companion object {
        val MATERIAL: String = "materias"
        val PRAVENDER: String = "para_vender"
        const val VALUE_ARRAY = "valuesArray"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.auto_atendimento, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        showProgress()
        presenter.setView(this)
        presenter.loadSuplies()


        card_view_machine.setOnClickListener {
            TODO()
//            MainActivity.mActivity?.changeFragment(RequestMachineFragment(), R.string.aut_title_new_machine)
        }
        configureToolbarActionListener?.changeTo(title =
        getString(R.string.text_services_navigation_label))
    }

    override fun responseListSuplies(supplies: List<Supply>) {

        if (isAttached()) {
            hideProgress()
            if (supplies.size != 0) {
                fg_at_materias.visible()
                //add fragment materias

                val arguments = Bundle()
                arguments.putParcelableArrayList(VALUE_ARRAY, supplies as ArrayList)
                materias.arguments = arguments
                materias.addInFrame(childFragmentManager, R.id.fg_at_materias)
//                manager.addFragment(R.id.fg_at_materias, materias, MATERIAL, false, supplies)
            } else {
                fg_at_materias.gone()
            }
            addFragmentFarol()
        }
    }

    private fun addFragmentFarol() {
        //add fragment vender mais
        paraVenderMais.addInFrame(childFragmentManager, R.id.fg_at_para_vender_mais)
//        manager.addFragment(R.id.fg_at_para_vender_mais, paraVenderMais, PRAVENDER, false)
    }

    override fun errorResponse(e: Throwable) {

        if (isAttached()) {
            val error = APIUtils.convertToErro(e)
            when {
                error.httpStatus >= 500 -> {
                    showError()
                    buttonUpdate.setOnClickListener {
                        showProgress()
                        presenter.loadSuplies()
                    }
                }
                error.httpStatus == 401 -> {
                    SessionExpiredHandler.userSessionExpires(requireContext())
                }
                error.httpStatus == 420 -> {
                    showError420()
                }
                else -> {
                    showError()
                    buttonUpdate.setOnClickListener {
                        showProgress()
                        presenter.loadSuplies()
                    }

                }
            }

        }

    }

    fun showError() {
        if (isAttached()) {
            layout_auto_atendimento.gone()
            cl_auto_atendimento.gone()
            ll_auto_atendimento_error.visible()
        }
    }

    fun showError420() {
        if (isAttached()) {
            hideProgress()
            fg_at_materias.gone()
            addFragmentFarol()
            //fg_at_para_vender_mais.w
            //activity?.showMessage(getString(R.string.aut_error_420), getString(R.string.aut_title))
        }
    }

    fun showProgress() {
        if (isAttached()) {
            layout_auto_atendimento.gone()
            cl_auto_atendimento.visible()
            ll_auto_atendimento_error.gone()
        }
    }

    fun hideProgress() {
        if (isAttached()) {
            layout_auto_atendimento.visible()
            cl_auto_atendimento.gone()
            ll_auto_atendimento_error.gone()
        }
    }


}