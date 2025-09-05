package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action.FECHAR
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.meuCadastroNovo.domain.Bank
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.adapter.DadosContaABandeiradapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dc_details_brand.*
import kotlinx.android.synthetic.main.item_dados_conta.view.*
import kotlinx.android.synthetic.main.item_tipo_conta.*
import kotlinx.android.synthetic.main.item_tipo_conta.view.*

/**
 * @author Enzo teles
 * */
class DetailBrandBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {

    companion object {
        val BANK = "bank"
        fun newInstance(bank: Bank): DetailBrandBottomSheet {
            return DetailBrandBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(BANK, bank)

                }
            }
        }
    }

    //variable
    lateinit var banks: Bank
    lateinit var adapter: DadosContaABandeiradapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dc_details_brand, container, false)
    }

    @SuppressLint("StringFormatMatches")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            banks = it.getParcelable(BANK)!!
        }
        iniView()
    }

    override fun onResume() {
        super.onResume()
        gaSendSeeBank("abrir")
    }

    /**
     * método para popular a view
     * */
    private fun iniView() {

        //onclicklistener
        dc_btn_close.setOnClickListener(this)
        dc_btn_banner_close.setOnClickListener(this)

        banks.let {bnk->


            if (bnk.digitalAccount){
                dc_tv_num_agencia_impl.visibility = View.GONE
                dc_tv_num_agencia.visibility = View.GONE
                dc_tv_tipo_conta_impl.text = "Digital"
                dc_tv_name_bank.text = "Conta Digital"
            }else{
                dc_tv_num_agencia_impl.visibility = View.VISIBLE
                dc_tv_num_agencia.visibility = View.VISIBLE
                dc_tv_num_agencia_impl.text = if(bnk.agencyDigit.isNullOrEmpty()) bnk.agency else "${bnk.agency}-${bnk.agencyDigit}"
                dc_tv_tipo_conta_impl.text = if(bnk.savingsAccount) "Poupança" else "Corrente"
                dc_tv_name_bank.text = bnk.name
            }

           dc_tv_num_conta_impl.text = if(bnk.accountDigit.isNullOrEmpty()) bnk.accountNumber else "${bnk.accountNumber}-${bnk.accountDigit}"

            //BindingUtils.loadImage(dc_iv_brand, bnk.imgSource)
            Picasso.get()
                    .load(bnk.imgSource)
                    .into(dc_iv_brand, object : Callback {
                        override fun onSuccess() {
                            progress_dc.gone()
                            dc_iv_brand.visible()
                        }

                        override fun onError(e: Exception?) {
                            e?.printStackTrace()
                        }

                    })

            adapter = DadosContaABandeiradapter(bnk.brands, true)
            rv_dc_brands.adapter = adapter
            rv_dc_brands.layoutManager = GridLayoutManager(context, 5)

        }
    }

    /**
     *onCreateDialog
     * @param savedInstanceState
     * @return dialog
     * */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)
        changeDialog(dialog)

        dialog.setOnKeyListener { dialog, keyCode, _ ->
            if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                gaSendSeeBank(FECHAR)
                dialog.dismiss()
                return@setOnKeyListener true
            } else {
                return@setOnKeyListener false
            }

        }
        return dialog
    }

    /**
     * método para vericar quando o dialog muda de estado
     * @param dialog
     * */
    private fun changeDialog(dialog: Dialog) {
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(
                    R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState >= 4) {
                        gaSendSeeBank(FECHAR)
                        dismiss()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }
    }


    /**
     * Método que pega o click da tela de details
     * @param v
     * */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.dc_btn_close -> {
                gaSendSeeBank(FECHAR)
                dismiss()
            }
            R.id.dc_btn_banner_close -> {
                gaSendSeeBank(FECHAR)
                dismiss()
            }
        }
    }


    private fun gaSendSeeBank(labelButton: String) {
       if(isVisible) {
           Analytics.trackEvent(
               category = listOf(Category.APP_CIELO, "meu-cadastro"),
               action = listOf("contas", "modal"),
               label = listOf(Label.BOTAO, labelButton),
           )
        }
    }
}