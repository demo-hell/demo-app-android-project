package br.com.mobicare.cielo.component.accordeon

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.transition.TransitionManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.NINETY_DOUBLE
import br.com.mobicare.cielo.commons.constants.NINETY_DOUBLE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.databinding.LayoutCieloTaxasBandeiraAccordeonBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.taxaPlanos.componentes.taxas.TaxasModelView
import br.com.mobicare.cielo.taxaPlanos.mapper.TaxAndBrandsMapper.INSTALLMENTS

class CieloTaxasBandeiraAccordeon @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = ZERO
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var isExpanded = false
    private var binding: LayoutCieloTaxasBandeiraAccordeonBinding? = null

    init {
        binding = LayoutCieloTaxasBandeiraAccordeonBinding.inflate(
                LayoutInflater.from(context),
                this,
                true
        )
        binding?.llItems.gone()
    }

    fun setData(data: TaxasModelView) {
        configureListeners()
        binding?.apply {
            tvNomeTaxa.text = data.name
            for (idx in data.values.indices) {
                val item = data.values[idx]
                llItems.addView(CieloKeyValue(context).apply {
                    val isSubtitle = item.first.contains(INSTALLMENTS)
                    setData(item.first, item.second, (idx + ONE == data.values.size).not() && isSubtitle.not(), if (isSubtitle) R.color.brand_500 else null)
                })
                llItems.requestLayout()
            }
        }
    }

    private fun configureListeners() {
        binding?.apply {
            setOnClickListener {
                TransitionManager.beginDelayedTransition(llItems)
                isExpanded = isExpanded.not()
                if (isExpanded) {
                    ivArrow.rotation = NINETY_DOUBLE_NEGATIVE
                    llItems.visible()
                    llItems.requestLayout()
                } else {
                    ivArrow.rotation = NINETY_DOUBLE
                    llItems.gone()
                }
                TransitionManager.endTransitions(llItems)
            }
        }
    }

}