package br.com.mobicare.cielo.meusrecebimentosnew.adapter.viewholder

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_CODE
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_SUMMARY_ITEM
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_TITLE
import br.com.mobicare.cielo.commons.constants.ONE_HUNDRED
import br.com.mobicare.cielo.commons.constants.Text.TITLE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.databinding.ItemListaCardResumoOperacoesBinding
import br.com.mobicare.cielo.databinding.LayoutSummaryViewItemBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.meusRecebimentos.presentation.ui.helpers.Codes
import br.com.mobicare.cielo.meusRecebimentos.presentation.ui.helpers.MeusRecebimentosHelper
import br.com.mobicare.cielo.meusrecebimentosnew.models.summaryview.Item
import br.com.mobicare.cielo.meusrecebimentosnew.visaodetalhada.VisaoDetalhadaMeusRecebimentosActivity
import br.com.mobicare.cielo.meusrecebimentosnew.visaosumarizada.PARAM_QUICKFILTER
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter

class SummaryViewMyReceiptsViewHolder(
    var binding: LayoutSummaryViewItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        code: Int,
        item: Item,
        title: String?,
        quickFilter: QuickFilter?,
        context: Context
    ) {
        setupTitleVisibility(item)
        var isShowDetailView = determinateDetailVisibility(code, item)
        handleDetailVisibility()
        populateFields(code, item, context)
        handleDetailViewClick(isShowDetailView, code, title, quickFilter, context)
    }

    private fun setupTitleVisibility(item: Item) {
        binding.apply {
            titleItemView?.visibility = View.GONE
            root.tag = item
        }
    }

    private fun determinateDetailVisibility(code: Int, item: Item): Boolean {
        var isShowDetailView = true

        when (code) {
            Codes.VENDA_CREDITO, Codes.DEBITO, Codes.PARCELADO, Codes.CANCELAMENTO_VENDA,
            Codes.REVERSAO_CANCELAMENTO, Codes.CHARGEBACK, Codes.REVERSAO_CHARGEBACK,
            Codes.DEBITO_SESSAO, Codes.CREDITO_SESSAO, Codes.ESTORNO_DEBITO_SESSAO,
            Codes.ESTORNO_CREDITO_SESSAO -> {
                isShowDetailView = true
            }

            Codes.ALUGUEL_POS -> isShowDetailView = false
            else -> {}
        }

        if (item.quantity == null || item.quantity == ZERO) {
            isShowDetailView = false
        }

        if (!isShowDetailView || item.links.isNullOrEmpty()) {
            isShowDetailView = false
            binding.resumoOperacoesImagemClick.gone()
        }
        return isShowDetailView
    }

    private fun handleDetailVisibility() {
            binding.contentFieldsLayout.removeAllViews()
            binding.contentFieldsLayout.requestLayout()
            binding.resumoOperacoesImagemClick.requestLayout()
    }

    private fun populateFields(code: Int, item: Item, context: Context) {
        MeusRecebimentosHelper.generateHashMap(code, item).forEach { itField ->
            if (itField.first.equals(TITLE)) {
                binding.titleItemView.text = itField.second
                binding.titleItemView.visibility = View.VISIBLE
            } else {
                ItemListaCardResumoOperacoesBinding.inflate(LayoutInflater.from(context)).let {
                    it.meusRecebimentosDetalheChave.text = itField.first
                    it.meusRecebimentosDetalheValor.text = itField.second
                    binding.contentFieldsLayout.addView(it.root)
                }
            }
        }
    }

    private fun handleDetailViewClick(
        isShowDetailView: Boolean,
        code: Int,
        title: String?,
        quickFilter: QuickFilter?,
        context: Context
    ) {
        if (isShowDetailView) {
            binding.root.setOnClickListener {
                val tagItem = it.tag as Item
                quickFilter?.let {
                    val filter = QuickFilter.Builder().from(it)
                    var titleForIntent = if (code == ONE_HUNDRED) null else title
                    filter.transactionTypeCode(tagItem.transactionTypeCode)
                    filter.merchantId(tagItem.merchantId)
                    tagItem.paymentTypeCode?.let {
                        var paymentTypes = ArrayList<Int>()
                        paymentTypes.add(it)
                        filter.paymentType(paymentTypes)
                    }
                    tagItem.cardBrandCode?.let {
                        var cardBrandCodes = ArrayList<Int>()
                        cardBrandCodes.add(it)
                        filter.cardBrand(cardBrandCodes)
                    }
                    tagItem.paymentDate?.let {
                        filter.initialDate(it)
                        filter.finalDate(it)
                    }
                    startDetailedViewActivity(tagItem, filter.build(), code, titleForIntent, context)
                }
            }
        } else {
            binding.root.setOnClickListener(null)
        }
    }

    private fun startDetailedViewActivity(
        tagItem: Item,
        filter: QuickFilter,
        code: Int,
        title: String?,
        context: Context
    ) {
        val intent = Intent(context, VisaoDetalhadaMeusRecebimentosActivity::class.java)
        intent.putExtra(ARG_PARAM_SUMMARY_ITEM, tagItem)
        intent.putExtra(PARAM_QUICKFILTER, filter)
        intent.putExtra(ARG_PARAM_CODE, code)
        intent.putExtra(ARG_PARAM_TITLE, title)
        context.startActivity(intent)
    }
}
