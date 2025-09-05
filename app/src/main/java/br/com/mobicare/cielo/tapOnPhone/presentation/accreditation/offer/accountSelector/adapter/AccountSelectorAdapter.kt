package br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.offer.accountSelector.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.databinding.AccountItemRadioButtonBinding
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneAccount
import br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.offer.accountSelector.AccountSelectorContract

class AccountSelectorAdapter(
    private val items: List<TapOnPhoneAccount>,
    private val selectedAccount: TapOnPhoneAccount? = null,
    private val listener: AccountSelectorContract.View
) :
    RecyclerView.Adapter<AccountSelectorAdapter.AccountSelectorViewHolder>() {
    private companion object {
        const val NOT_SELECTED_POSITION = -1
        const val ACCOUNT_FORMAT = "%s-%s"
    }

    var currentSelectedPosition = selectedAccount?.let {
        items.indexOf(selectedAccount)
    } ?: NOT_SELECTED_POSITION

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AccountSelectorViewHolder {
        val binding = AccountItemRadioButtonBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return AccountSelectorViewHolder(binding, listener)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: AccountSelectorViewHolder, position: Int) {
        holder.bind(items[position], position == currentSelectedPosition)
    }

    override fun onBindViewHolder(
        holder: AccountSelectorViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty() && payloads.first() is Boolean) {
            holder.bindSelection(payloads.first() as Boolean)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    inner class AccountSelectorViewHolder(
        private val binding: AccountItemRadioButtonBinding,
        private val listener: AccountSelectorContract.View
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TapOnPhoneAccount, selected: Boolean) {
            binding.apply {
                tvBankName.text = item.bankName
                ImageUtils.loadImage(
                    binding.bankIcon,
                    item.imgSource,
                    R.drawable.ic_generic_brand
                )
                tvBankAccountNumber.text = ACCOUNT_FORMAT.format(item.account, item.accountDigit)
                tvBankBranchNumber.text = item.agency

                bindSelection(selected)

                root.setOnClickListener {
                    onItemClick(item)
                }

                radioButton.setOnClickListener {
                    onItemClick(item)
                }
            }
        }

        private fun onItemClick(item: TapOnPhoneAccount) {
            updateSelection()
            listener.onAccountSelected(account = item)
        }

        fun bindSelection(isSelected: Boolean) {
            binding.apply {
                radioButton.isChecked = isSelected
                if (isSelected) {
                    root.background = AppCompatResources.getDrawable(
                        root.context,
                        R.drawable.background_stroke_1dp_round_brand_400
                    )
                } else {
                    root.background = AppCompatResources.getDrawable(
                        root.context,
                        R.drawable.background_stroke_1dp_round_color_c5ced7
                    )
                }
            }
        }

        private fun updateSelection() {
            if (currentSelectedPosition != adapterPosition) {
                val previousSelectedPosition = currentSelectedPosition
                currentSelectedPosition = adapterPosition

                notifyItemChanged(currentSelectedPosition, true)

                if (previousSelectedPosition != NOT_SELECTED_POSITION) {
                    notifyItemChanged(previousSelectedPosition, false)
                }
            }

        }
    }
}