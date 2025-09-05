package br.com.mobicare.cielo.taxaPlanos.componentes.taxas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.cielo.libflue.inputtext.CieloTextInputView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.configureItemDecoration
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import kotlinx.android.synthetic.main.fragment_bandeiras.*
import kotlinx.android.synthetic.main.layout_bandeiras.view.*

private const val ARG_BANDEIRAS = "ARG_BANDEIRAS"

class BandeirasFragment : BaseFragment() {

    private var bandeiras: ArrayList<BandeiraModelView>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.fragment_bandeiras, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
        configureListeners()
        loadArguments()
        this.bandeiras?.let {
            loadBandeiras(it)
        }
    }

    private fun configureRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        this.lvBandeiras?.layoutManager = layoutManager
        this.lvBandeiras?.setHasFixedSize(true)
        this.lvBandeiras?.configureItemDecoration(requireContext(),
                layoutManager,
                R.drawable.shape_item_technical)
    }

    private fun configureListeners() {
        this.filtroInputView?.let {
            it.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    super.onTextChanged(s, start, before, count)
                    this@BandeirasFragment.bandeiras?.filter { it.nomeBandeira.toLowerCasePTBR().contains(s.toString().toLowerCasePTBR()) }?.let {
                        this@BandeirasFragment.loadBandeiras(it)
                    }
                }
            })
        }
    }

    private fun loadArguments() {
        this.arguments?.getParcelableArrayList<BandeiraModelView>(ARG_BANDEIRAS)?.let {
            this.bandeiras = it
        }
    }

    private fun loadBandeiras(bandeiras: List<BandeiraModelView>) {
        bandeiras.let { itBandeiras ->
            val adapter = DefaultViewListAdapter(itBandeiras, R.layout.layout_bandeiras)
            adapter.setBindViewHolderCallback(object : DefaultViewListAdapter.OnBindViewHolderPositon<BandeiraModelView> {
                override fun onBind(item: BandeiraModelView, holder: DefaultViewHolderKotlin, position: Int, lastPositon: Int) {
                    holder.mView.ivBandeira?.let { itImagem ->
                        ImageUtils
                                .loadImage(itImagem, item.iconeBandeira, R.drawable.ic_generic_brand)
                    }
                    holder.mView.tvNomeBandeira?.text = item.nomeBandeira
                    holder.mView.setOnClickListener {
                        TaxasPorBandeiraBottomSheet
                                .create(item)
                                .show(childFragmentManager, TaxasPorBandeiraBottomSheet::class.java.simpleName)
                    }
                }
            })
            this.lvBandeiras?.adapter = adapter
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(bandeiras: ArrayList<BandeiraModelView>) =
                BandeirasFragment().apply {
                    arguments = Bundle().apply {
                        putParcelableArrayList(ARG_BANDEIRAS, bandeiras)
                    }
                }
    }
}