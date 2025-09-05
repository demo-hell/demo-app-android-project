package br.com.mobicare.cielo.taxaPlanos.componentes.taxas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.component.accordeon.CieloTaxasBandeiraAccordeon
import br.com.mobicare.cielo.databinding.LayoutTaxasPorBandeiraBottomSheetBinding
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TaxasPorBandeiraBottomSheet : BottomSheetDialogFragment() {

    private var binding: LayoutTaxasPorBandeiraBottomSheetBinding? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        return LayoutTaxasPorBandeiraBottomSheetBinding.inflate(
                inflater,
                container,
                false
        ).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadRates()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun loadRates() {
        arguments?.getParcelable<BandeiraModelView>(ARG_BANDEIRA)?.let {
            binding?.apply {
                llTaxasLayout.removeAllViews()
                tvNomeBandeira.text = it.nomeBandeira
                if (it.nomeBandeira.contains(PIX, true)) {
                    if (it.taxas.flatMap { taxa -> taxa.values }
                            .any { value -> value.first.equals(PIX_RATE_TAX, ignoreCase = true) }) {
                        tvLabelPix.apply {
                            fromHtml(R.string.txt_label_pix_bottomsheet)
                            visible()
                        }
                        tvLabelMDR.gone()
                        tvLabelRR.gone()
                    } else {
                        tvLabelPix.gone()
                        tvLabelMDR.gone()
                        tvLabelRR.gone()
                    }
                } else {
                    tvLabelPix.gone()
                    tvLabelMDR.apply {
                        fromHtml(R.string.txt_label_mdr_bottomsheet)
                        visible()
                    }
                    tvLabelRR.apply {
                        fromHtml(R.string.txt_label_rr_bottomsheet)
                        visible()
                    }
                }

                it.taxas.forEach {
                    llTaxasLayout.addView(CieloTaxasBandeiraAccordeon(requireContext()).apply {
                        this.setData(it)
                    })
                }
            }
        }
    }

    companion object {
        private const val ARG_BANDEIRA = "ARG_BANDEIRA"
        private const val PIX_RATE_TAX = "Tarifa"
        private const val PIX = "PIX"

        @JvmStatic
        fun create(bandeira: BandeiraModelView) =
                TaxasPorBandeiraBottomSheet().apply {
                    this.arguments = Bundle().apply {
                        this.putParcelable(ARG_BANDEIRA, bandeira)
                    }
                }
    }
}