package br.com.mobicare.cielo.balcaoRecebiveis

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.balcaoRecebiveis.fragment.BalcaoRecebiveisBottomSheetAdquirentes
import br.com.mobicare.cielo.balcaoRecebiveis.fragment.BalcaoRecebiveisBottomSheetBancos
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.merchant.domain.entity.MerchantResponseRegisterGet
import com.redmadrobot.inputmask.helper.Mask
import com.redmadrobot.inputmask.model.CaretString
import kotlinx.android.synthetic.main.activity_fluxo_navegacao_superlink.toolbar
import kotlinx.android.synthetic.main.authorization_history_activity.*
import kotlinx.android.synthetic.main.item_comp_sanfona_cpf_detalhes.view.*

/**
 * @author Enzo Teles
 * Thursday, Set 28, 2020
 * */
class AuthorizationHistoryActivity: BaseLoggedActivity(){

    var merchatGet: MerchantResponseRegisterGet?= null
    var listMerchant = ArrayList<MerchantResponseRegisterGet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authorization_history_activity)
        setupToolbar(toolbar as Toolbar, getString(R.string.title_bs_balcao))

        initView()
        populateView()
    }

    private fun initView() {
        intent?.extras?.let {
            merchatGet = it.getParcelable("merchantGet")
            merchatGet?.let { it1 -> listMerchant.add(it1) }
        }
    }

    private fun populateView() {

        rv_list_cpf.layoutManager = LinearLayoutManager(this.baseContext)
        rv_list_cpf?.setHasFixedSize(true)
        val adapter = DefaultViewListAdapter(listMerchant, R.layout.item_comp_sanfona_cpf_detalhes)
        adapter.setBindViewHolderCallback(object: DefaultViewListAdapter.OnBindViewHolder<MerchantResponseRegisterGet> {
            var isClickSetinhaDown = false
            override fun onBind(item: MerchantResponseRegisterGet, holder: DefaultViewHolderKotlin) {
                listOpen(holder)
                if(item.document?.length!! == 11){
                    val cpf = cpfMaskFormatter(item.document).formattedText.string
                    holder.mView.textView.text = "CPF $cpf"
                }else{
                    val cnpj = item.document?.let { cnpjMaskFormatter(it).formattedText.string }
                    holder.mView.textView.text = "CNPJ $cnpj"
                }

                holder.mView.text_br_history_de_value.text = item.optinDate?.convertToBrDateFormat()
                holder.mView.text_br_history_user_value.text = if(!item.email.isNullOrEmpty()) item.email else item.user
                holder.mView.text_br_history_cc_value.text = if(item.cieloCustomer == true) "sim" else "não"
                holder.mView.text_br_history_st_value.text = "Aprovado"
                holder.mView.ll_setinha_down.setOnClickListener {
                    if(isClickSetinhaDown == false){
                        listOpen(holder)
                    }else{
                        listClose(holder)
                    }
                }

                holder.mView.ll_bandeiras.setOnClickListener{
                    holder.mView.ll_bandeiras.isEnabled = false
                    Handler().postDelayed({
                        //doSomethingHere()
                        holder.mView.ll_bandeiras.isEnabled = true
                    }, 500)
                        merchatGet?.arrangements?.let {
                            val ftsucessBS = BalcaoRecebiveisBottomSheetBancos.newInstance(it, "Bandeiras autorizadas")
                            ftsucessBS.show(supportFragmentManager, "BalcaoRecebiveisBottomSheetBancos")

                    }


                }
                holder.mView.ll_credenciadoras.setOnClickListener{
                    holder.mView.ll_credenciadoras.isEnabled = false
                    Handler().postDelayed({
                        //doSomethingHere()
                        holder.mView.ll_credenciadoras.isEnabled = true
                    }, 500)
                        merchatGet?.adquirers?.let {
                            val ftsucessBS = BalcaoRecebiveisBottomSheetAdquirentes.newInstance(it, "Credenciadoras autorizadas")
                            ftsucessBS.show(supportFragmentManager, "BalcaoRecebíveisBottomSheetListItens")
                        }
                }
            }

            private fun listClose(holder: DefaultViewHolderKotlin) {
                holder.mView.layout_body.visibility = View.GONE
                isClickSetinhaDown = false
                holder.mView.iv_setinha_down.setBackgroundResource(R.drawable.ic_setinha_down)
            }

            private fun listOpen(holder: DefaultViewHolderKotlin) {
                holder.mView.layout_body.visibility = View.VISIBLE
                isClickSetinhaDown = true
                holder.mView.iv_setinha_down.setBackgroundResource(R.drawable.ic_setinha_up)
            }
        })
        adapter.onItemClickListener = object : DefaultViewListAdapter.OnItemClickListener<MerchantResponseRegisterGet> {
            override fun onItemClick(item: MerchantResponseRegisterGet) {
            }
        }
        rv_list_cpf.adapter = adapter
    }

    /**
     * method to put mask in the cpf field
     * */
    fun cpfMaskFormatter(inputCpf: String): Mask.Result {
        val cpfMask = Mask(CPF_MASK_FORMAT)
        return cpfMask.apply(CustomCaretString.forward(inputCpf))
    }

    /**
     * method to put mask in the cpf field
     * */
    fun cnpjMaskFormatter(inputCnpj: String): Mask.Result {
        val cnpjMask = Mask(CNPJ_MASK_COMPLETE_FORMAT)
        return cnpjMask.apply(CustomCaretString.forward(inputCnpj))
    }
}