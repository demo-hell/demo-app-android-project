package br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.ActivityBackActionListener
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.DomicilioFlagVo
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.FlagTransferActionListener
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.FlagTransferClickListener
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.FlagTransferQuantityListener
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroGA4.ScreenView.SCREEN_VIEW_MY_PROFILE_ACCOUNT_FLAG_TRANSFER
import br.com.mobicare.cielo.meuCadastroNovo.domain.Bank
import br.com.mobicare.cielo.mfa.resume.ARG_PARAM_MFA_ACCOUNT
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.ft_fragment_02.*
import kotlinx.android.synthetic.main.ft_fragment_02_item_flag.view.*
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroGA4 as ga4

const val BANK_SELECTED = "bankSelected"
const val BANK = "bank"
const val VISA_NET = "VISANET"

class FlagTransfer02Fragment : BaseFragment(), FlagTransferActionListener {

    private var actionListner: ActivityStepCoordinatorListener? = null
    private var quantityListener: FlagTransferQuantityListener? = null
    private var backActionListener: ActivityBackActionListener? = null
    private var clickListener: FlagTransferClickListener? = null

    private var bankSelected: ArrayList<DomicilioFlagVo>? = null
    private var bank: Bank? = null
    private lateinit var adapter: DefaultViewListAdapter<DomicilioFlagVo>

    companion object {
        fun newInstance(
            bundle: Bundle,
            actionListner: ActivityStepCoordinatorListener?,
            quantityListener: FlagTransferQuantityListener?,
            backActionListener: ActivityBackActionListener?,
            clickListener: FlagTransferClickListener?
        ) = FlagTransfer02Fragment().apply {
            this.arguments = bundle
            this.actionListner = actionListner
            this.quantityListener = quantityListener
            this.backActionListener = backActionListener
            this.clickListener = clickListener
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.ft_fragment_02, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actionListner?.setTitle(getString(R.string.tv_toolbar_tranfer))
        clickListener?.onButtonName(getString(R.string.text_avancar))
        clickListener?.onButtonStatus()
        clickListener?.showButtonHome()

        configSelectAll()
        configAlterBanck()
        configListScreen()
        showQuantitySelected()
        configRecycleView()
    }

    override fun onResume() {
        super.onResume()
        logScreenView()
    }

    private fun configAlterBanck() {
        text_view_bank_choose?.setOnClickListener {
            backActionListener?.onBack()
        }
    }

    private fun configListScreen() {
        arguments?.let {
            bank = it.getParcelable(BANK)

            bank?.apply {
                if (name.equals(VISA_NET)) {
                    text_view_bank_item?.text = "$code - ${getString(R.string.txt_name_bank)}"
                } else {
                    val bankName = if (name.isNullOrBlank()) "" else "- $name"
                    text_view_bank_item?.text = "$code $bankName"
                }
            }

            if (it.containsKey(BANK_SELECTED)) {
                bankSelected = it.getParcelableArrayList(BANK_SELECTED)

            } else
                bank?.brands?.forEach { brands ->

                    if (bankSelected.isNullOrEmpty())
                        bankSelected = ArrayList()

                    bankSelected?.add(
                        DomicilioFlagVo(
                            brands.code,
                            brands.imgSource,
                            brands.name,
                            false
                        )
                    )
                }
        }
    }

    private fun showQuantitySelected() {
        bankSelected?.let {
            val quantity = it.filter { domicilio ->
                domicilio.checked
            }.size
            quantityListener?.quantitychosen(quantity)
            clickListener?.onButtonSelected(quantity > 0)
        }
    }

    private fun configRecycleView() {
        activity?.let { recycler_view_flags.layoutManager = GridLayoutManager(it, 4) }
        bankSelected?.let { selected ->

            adapter = DefaultViewListAdapter(selected, R.layout.ft_fragment_02_item_flag)
            adapter.setBindViewHolderCallback(object :
                DefaultViewListAdapter.OnBindViewHolder<DomicilioFlagVo> {
                override fun onBind(item: DomicilioFlagVo, holder: DefaultViewHolderKotlin) {

                    if (item.checked)
                        holder.itemView.setBackgroundResource(R.drawable.ft_frag_02_item_selector_check)
                    else
                        holder.itemView.setBackgroundResource(R.drawable.ft_frag_02_item_selector_uncheck)


                    Picasso.get()
                        .load(item.imgSource)
                        .into(holder.itemView.image_view_flag)

                    holder.itemView.content_flag.setOnClickListener {
                        if (item.checked.not()) {
                            holder.itemView.setBackgroundResource(R.drawable.ft_frag_02_item_selector_check)
                            item.checked = true
                        } else {
                            holder.itemView.setBackgroundResource(R.drawable.ft_frag_02_item_selector_uncheck)
                            item.checked = false
                        }
                        showQuantitySelected()
                    }

                }
            })
            recycler_view_flags.adapter = adapter
        }

    }

    private fun configSelectAll() {
        text_view_flag_all_selected.setOnClickListener {
            bankSelected?.let {
                it.forEach { selected ->
                    selected.checked = true
                }
                quantityListener?.quantitychosen(it.size)
                clickListener?.onButtonSelected(it.size > 0)
                adapter.notifyDataSetChanged()
            }

        }
    }


    //region FlagTransferActionListener
    override fun validade(bundle: Bundle?) {

        val bundleRet = Bundle()
        bundleRet.putParcelableArrayList(BANK_SELECTED, bankSelected)
        actionListner?.onNextStep(false, bundleRet)
    }

    private fun logScreenView() {
        if(isAttached()) {
            ga4.logScreenView(SCREEN_VIEW_MY_PROFILE_ACCOUNT_FLAG_TRANSFER)
        }
    }
}