package br.com.mobicare.cielo.home.presentation.main.ui.fragment.home.adapters

import android.content.Context
import android.widget.LinearLayout.LayoutParams
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.COMMA
import br.com.mobicare.cielo.commons.constants.DOT
import br.com.mobicare.cielo.commons.constants.TWENTY_FOUR
import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.utils.roundToTwoDecimal
import br.com.mobicare.cielo.databinding.BrandsTaxHomeItemBinding
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.home.presentation.enum.ProductTypeEnum.CREDIT
import br.com.mobicare.cielo.home.presentation.enum.ProductTypeEnum.DEBIT
import br.com.mobicare.cielo.meuCadastroNovo.domain.Brand
import br.com.mobicare.cielo.pix.constants.EMPTY

private const val BRAND_CODE_PIX = 89

class HomeFeeAndPlansViewHolder(private val binding: BrandsTaxHomeItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        brand: Brand?,
        isFirstTimeOpening: Boolean,
        isError: Boolean,
        callToAction: (() -> Unit)?,
        context: Context?
    ) {
        binding.apply {
            if (isFirstTimeOpening) {
                showLoadingShimmer()
                return
            }

            if (isError) {
                showErrorView()
                setListener(callToAction, context)
                return
            }

            var credit = SIMPLE_LINE
            var debit = SIMPLE_LINE

            context?.let { itContext ->
                brand?.products?.forEach { itProduct ->
                    val percentageValue = itContext.getString(
                        R.string.x_per_cent_string,
                        itProduct.conditions[ZERO].mdr.roundToTwoDecimal().replace(DOT, COMMA)
                    )

                    when (itProduct.name.lowercase()) {
                        CREDIT.value -> credit = percentageValue
                        DEBIT.value -> debit = percentageValue
                    }
                }
            }

            ImageUtils.loadImage(ivBrand, brand?.imgSource, R.drawable.ic_generic_brand)

            includeLoading.root.gone()
            mainContainer.visible()

            tvCardName.text = brand?.name?.toLowerCasePTBR().capitalizePTBR()
            tvDebitValue.text = debit
            tvCreditValue.text = credit

            if(isPixBrand(brand?.code)) {
                tvCreditTitle.text = EMPTY
                tvCreditValue.text = EMPTY
            }
        }
    }

    private fun isPixBrand(brandCode: Int?) = brandCode == BRAND_CODE_PIX

    private fun setListener(callToAction: (() -> Unit)?, context: Context?) {
        binding.includeError.btTryAgain.setOnClickListener {
            context?.let { itContext ->
                itemView.layoutParams = itemView.layoutParams?.apply {
                    width = itContext.resources.getDimensionPixelSize(R.dimen.dimen_180dp)
                    height = LayoutParams.WRAP_CONTENT
                }
            }

            binding.includeError.root.gone()
            showLoadingShimmer()

            callToAction?.invoke()
        }
    }

    private fun showErrorView() {
        binding.root.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).also {
                it.marginEnd = TWENTY_FOUR
            }
        binding.includeLoading.root.visible()
        binding.includeError.root.visible()
    }

    private fun showLoadingShimmer() {
        binding.includeLoading.apply {
            root.visible()

            loadingShimmerFeeAndPlans.startShimmer()
        }
    }
}