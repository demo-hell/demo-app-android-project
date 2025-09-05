package br.com.mobicare.cielo.component.impersonate.presentation.fragment.viewHolder

import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.component.impersonate.presentation.model.MerchantUI
import br.com.mobicare.cielo.databinding.LayoutImpersonateItemOptionEcBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible

class ImpersonateMerchantsViewHolder(
    private val binding: LayoutImpersonateItemOptionEcBinding,
) : RecyclerView.ViewHolder(binding.root) {

    private val context = binding.root.context

    fun bind(merchant: MerchantUI, onTap: (String) -> Unit) {
        binding.apply {
            if (merchant.name.isNullOrBlank().not()) {
                tvTitle.text = merchant.name
                tvSubtitle.text = formatSubtitle(merchant)
                tvSubtitle.visible()
            } else {
                tvTitle.text =
                    context.getString(R.string.super_link_impersonating_ec_number, merchant.id)
                tvSubtitle.gone()
            }

            root.contentDescription = formatContentDescription(merchant)
            root.setOnClickListener { onTap(merchant.id) }
        }
    }

    private fun formatSubtitle(merchant: MerchantUI) =
        if (merchant.document.isNullOrBlank().not()) {
            context.getString(
                R.string.super_link_impersonating_ec_number_and_ec_cnpj,
                merchant.id,
                merchant.document
            )
        } else {
            merchant.id
        }

    private fun formatContentDescription(merchant: MerchantUI) =
        if (merchant.name.isNullOrEmpty().not()) {
            if (merchant.document.isNullOrBlank().not()) {
                context.getString(
                    R.string.super_link_impersonating_content_description_card_with_ec_name_and_cnpj,
                    merchant.name,
                    merchant.id,
                    merchant.document
                )
            } else {
                context.getString(
                    R.string.super_link_impersonating_content_description_card_with_ec_name,
                    merchant.name,
                    merchant.id,
                )
            }
        } else {
            context.getString(
                R.string.super_link_impersonating_content_description_card,
                merchant.id
            )
        }

}