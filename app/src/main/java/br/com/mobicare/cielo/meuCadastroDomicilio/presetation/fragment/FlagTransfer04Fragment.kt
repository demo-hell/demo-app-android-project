package br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.DomicilioFlagVo
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.FlagTransferActionListener
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.activity.FlagTransferEngineActivity
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.FlagTransfer01Fragment.Companion.BANK
import br.com.mobicare.cielo.meuCadastroNovo.domain.Bank
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.ft_fragment_01.*
import kotlinx.android.synthetic.main.ft_fragment_02_item_flag.view.*
import kotlinx.android.synthetic.main.ft_fragment_04.*


/**
 * create by Enzo Teles
 * */

class FlagTransfer04Fragment : BaseFragment(), FlagTransferActionListener, View.OnClickListener {

    private var bankSelected: Bank? = null
    private var listBrands: ArrayList<DomicilioFlagVo>? = null

    private var actionListner: ActivityStepCoordinatorListener? = null
    private lateinit var adapter: DefaultViewListAdapter<DomicilioFlagVo>
    var listener: FlagTransferEngineActivity? = null
    var isCheck = false

    companion object {
        const val BRANDSELECTED = "bankSelected"

        fun newInstance(bundles: Bundle, actionListner: ActivityStepCoordinatorListener?) =
            FlagTransfer04Fragment().apply {
                arguments = bundles
                this.actionListner = actionListner
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.ft_fragment_04, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener?.onButtonName(getString(R.string.item_dc_transferir_bandeiras))
        listener?.onButtonStatus()

        arguments?.let {
            //populate object in screen
            listBrands = it.getParcelableArrayList(BRANDSELECTED)
            bankSelected = it.getParcelable(BANK)

            initView()
        } ?: run {
            ft_error.gone()
        }

        actionListner?.setTitle(getString(R.string.tv_item_tx_brands_account))
    }

    /**
     * método para popular o adapter
     * */
    fun initView() {
        listBrands?.let {
            ll_ver_mais?.visibility =
                if (it.filter { it.checked }.size <= 4) View.GONE else View.VISIBLE
        } ?: run {
            ll_ver_mais?.visibility = View.GONE
        }

        ll_ver_mais?.setOnClickListener(this)

        bankSelected?.apply {
            if (name.equals(VISA_NET)) {
                text_view_bank_item?.text = "$code - ${getString(R.string.txt_name_bank)}"
            } else {
                val bankName = if (name.isNullOrBlank()) "" else "- $name"
                text_view_bank_item?.text = "$code $bankName"
            }
        }

        recycler_view_flags_full?.layoutManager = GridLayoutManager(requireActivity(), 4)

        if (isCheck.not()) {
            val listFirstBrands = ArrayList<DomicilioFlagVo>()

            listBrands?.forEach { brands ->
                if (listFirstBrands.size <= 3 && brands.checked)
                    listFirstBrands.add(brands)
            }

            adapter = DefaultViewListAdapter(listFirstBrands, R.layout.ft_fragment_02_item_flag)

        } else {
            listBrands?.let { brands ->
                adapter = DefaultViewListAdapter(
                    brands.filter { it.checked },
                    R.layout.ft_fragment_02_item_flag
                )

            }
        }

        adapter.setBindViewHolderCallback(object :
            DefaultViewListAdapter.OnBindViewHolder<DomicilioFlagVo> {
            override fun onBind(item: DomicilioFlagVo, holder: DefaultViewHolderKotlin) {

                holder.itemView.setBackgroundResource(R.drawable.ft_frag_02_item_selector_uncheck)

                Picasso.get()
                    .load(item.imgSource)
                    .into(holder.itemView.image_view_flag)

            }
        })
        recycler_view_flags_full.adapter = adapter
    }


    /**
     * método que envia o banco para o segundo passo
     * */
    override fun validade(bundle: Bundle?) {
    }

    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.ll_ver_mais -> {
                if (!isCheck) {
                    iv_ver_mais.rotation = -90f
                    tv_ver_mais.text = getString(R.string.tv_item_tx_bandeiras_ver_menos_min)
                    isCheck = true
                    initView()
                } else {
                    iv_ver_mais.rotation = 90f
                    tv_ver_mais.text = getString(R.string.tv_item_tx_bandeiras_ver_mais_min)
                    isCheck = false
                    initView()
                }

            }
        }
    }

}
