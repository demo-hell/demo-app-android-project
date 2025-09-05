package br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import br.com.knowledge.capitulo7_mvp.FlagTransferBanksAdapter
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.FlagTransferActionListener
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.activity.FlagTransferEngineActivity
import br.com.mobicare.cielo.meuCadastroNovo.domain.Bank
import kotlinx.android.synthetic.main.ft_fragment_01.*


/**
 * create by Enzo Teles
 * */
class FlagTransfer01Fragment : BaseFragment(), FlagTransferActionListener {

    private var actionListner: ActivityStepCoordinatorListener? = null
    private var listBanks: ArrayList<Bank>? = null
    private var chosen: (Bank) -> Unit = {}

    lateinit var adapter: FlagTransferBanksAdapter
    lateinit var textTitleBandeiras: String
    lateinit var b: Bank

    var listener: FlagTransferEngineActivity? = null

    var bundleBank: Bundle? = null
    var listBanks03: ArrayList<Bank>? = null

    companion object {
        const val LISTBANK = "listBank"
        const val BANK = "bank"
        fun newInstance(
            listBanks: Bundle,
            actionListner: ActivityStepCoordinatorListener?,
            chosen: (Bank) -> Unit
        ) = FlagTransfer01Fragment().apply {
            arguments = listBanks
            this.chosen = chosen
            this.actionListner = actionListner
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(
        R.layout.ft_fragment_01, container,
        false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener?.hideButtonHome()
        listener?.onButtonName("Avançar")
        typefaceTextView25.text = textTitleBandeiras
        listener?.onButtonStatus()
        arguments?.let {
            //populate object in screen
            listBanks = it.getParcelableArrayList(LISTBANK)
            initAdapter()
        } ?: run {
            //if the object == null show error view
            ft_error.gone()
        }

        actionListner?.setTitle("Transferir bandeira")
    }

    /**
     * método para popular o adapter
     * */
    private fun initAdapter() {

        if (listBanks03 == null) {
            listener?.hideButtonHome()
            listBanks?.let { banks ->
                adapter = FlagTransferBanksAdapter(banks, this::itemBank)
                rv_bandeiras?.layoutManager = GridLayoutManager(context, 2)
                rv_bandeiras?.adapter = adapter
            }
        } else {
            listener?.showButtonHome()
            listBanks03?.let { banks ->
                adapter = FlagTransferBanksAdapter(banks, this::itemBank)
                rv_bandeiras?.layoutManager = GridLayoutManager(context, 2)
                rv_bandeiras?.adapter = adapter
            }
        }
    }


    /**
     * método que pega o banco selecionado pelo usuário
     * */
    private fun itemBank(bank: Bank) {
        listener?.onButtonSelected(true)
        bundleBank = Bundle().apply {
            putParcelable(BANK, bank)
            chosen(bank)
        }
    }

    /**
     * método que envia o banco para o segundo passo
     * */
    override fun validade(bundle: Bundle?) {
        actionListner?.onNextStep(false, bundleBank)
    }

}