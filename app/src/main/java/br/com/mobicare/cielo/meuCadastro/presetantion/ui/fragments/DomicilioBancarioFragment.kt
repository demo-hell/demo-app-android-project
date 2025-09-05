package br.com.mobicare.cielo.meuCadastro.presetantion.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroDomicilioBancario
import kotlinx.android.synthetic.main.item_domicilio_bancario_fragment.*

/**
 * Created by benhur.souza on 26/04/2017.
 */

class DomicilioBancarioFragment : BaseFragment() {

    private var banco: MeuCadastroDomicilioBancario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        banco = arguments?.getSerializable(BANCO) as MeuCadastroDomicilioBancario
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.item_domicilio_bancario_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        setBank()
    }

    fun setBank() {
        ImageUtils.loadImage(imageview_domicilio_bancario_header, banco?.imgUrl)
        showCommonBankInfo()
    }

    private fun showCommonBankInfo() {
        if (!banco?.name.isNullOrBlank()) {
            textview_item_solucao_contratada_value.visibility = View.VISIBLE
            textview_item_solucao_contratada_value.text = banco?.name
        } else {
            textview_item_solucao_contratada_value.visibility = View.GONE
        }

        textview_cadastro_domicilio_bancario_banco.text = banco?.code
        textview_cadastro_domicilio_bancario_agencia.text = banco?.branch

        val isConvivenciaUser = UserPreferences.getInstance().isConvivenciaUser
        if (!banco?.account.isNullOrBlank() && isConvivenciaUser) {
            layout_cadastro_domicilio_bancario_conta.visibility = View.VISIBLE
            textview_cadastro_domicilio_bancario_conta.text = banco?.account
        } else {
            layout_cadastro_domicilio_bancario_conta.visibility = View.GONE
        }
    }

    companion object {
        private val BANCO = "banco"

        fun newInstance(banco: MeuCadastroDomicilioBancario): DomicilioBancarioFragment {
            val fragmentFirst = DomicilioBancarioFragment()
            val args = Bundle()
            args.putSerializable(BANCO, banco)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}
