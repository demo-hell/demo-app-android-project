package br.com.mobicare.cielo.meusrecebimentosnew.adapter.viewholder

import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.constants.Text.TITLE
import br.com.mobicare.cielo.databinding.ItemListaCardResumoOperacoesBinding
import br.com.mobicare.cielo.databinding.ItemListaCardResumoOperacoesTooltipBinding
import br.com.mobicare.cielo.databinding.LayoutSummaryViewItemBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.meusRecebimentos.presentation.ui.helpers.MeusRecebimentosDetalheHelper
import br.com.mobicare.cielo.meusrecebimentosnew.models.detailsummaryview.Receivable
import br.com.mobicare.cielo.meusrecebimentosnew.visaodetalhada.VisaoDetalhadaMeusRecebimentosActivity

class DetailViewOfMyReceiptsViewHolder(
    var binding: LayoutSummaryViewItemBinding
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(code: Int, item: Receivable, activity: VisaoDetalhadaMeusRecebimentosActivity) {

        binding.apply {
            resumoOperacoesImagemClick?.gone()
            titleItemView.gone()
            contentFieldsLayout.removeAllViews()
        }

        val itemsForLayout = MeusRecebimentosDetalheHelper.generateHashMap(code, item, activity)
        itemsForLayout.forEach { itField ->
            val itemViewBinding =
                ItemListaCardResumoOperacoesBinding.inflate(LayoutInflater.from(activity))
            if (itField.first.equals(TITLE)) {
                itemViewBinding.apply {
                    meusRecebimentosDetalheChave.text = itField.second
                    meusRecebimentosDetalheChave.setTextColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.colorPrimary
                        )
                    )
                }
                itemViewBinding.meusRecebimentosDetalheValor.gone()
            } else {
                itemViewBinding.apply {
                    meusRecebimentosDetalheChave.text = itField.first
                    meusRecebimentosDetalheValor.text = itField.second
                    itField.third?.let { colorId ->
                        meusRecebimentosDetalheValor.setTextColor(colorId)
                    }
                }
            }
            binding.contentFieldsLayout.addView(itemViewBinding.root)
        }
        createTooltip(item, activity)
    }

    private fun createTooltip(item: Receivable, activity: VisaoDetalhadaMeusRecebimentosActivity) {
        if (item.statusCode != null) {
            val tooltipBinding =
                ItemListaCardResumoOperacoesTooltipBinding.inflate(LayoutInflater.from(activity))
            LayoutInflater.from(activity).inflate(
                R.layout.item_lista_card_resumo_operacoes_tooltip, null, false
            )

            when (item.statusCode) {
                Text.PIX_TRANSF_NEGADA_STATUS_CODE_16 -> {
                    tooltipBinding.tooltip.text =
                        activity.getString(R.string.tooltip_transf_negada_banco)
                    binding.contentFieldsLayout.addView(tooltipBinding.root)
                }

                Text.PIX_TRANSF_NAO_REALIZADA_STATUS_CODE_17 -> {
                    tooltipBinding.tooltip.text =
                        activity.getString(R.string.tooltip_transf_nao_realizada)
                    binding.contentFieldsLayout.addView(tooltipBinding.root)
                }
            }
        }
    }
}