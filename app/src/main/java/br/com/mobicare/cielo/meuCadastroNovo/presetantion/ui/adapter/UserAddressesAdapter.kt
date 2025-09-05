package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.adapter

import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceTextView
import br.com.mobicare.cielo.meuCadastroNovo.domain.Address

class UserAddressesAdapter(val addresses: List<Address>, val isEditBlocked: Boolean) :
        RecyclerView.Adapter<UserAddressesAdapter.UserAddressViewHoler>() {

    var onAddressSelectedListener: OnAddressSelectedListener? = null

    interface OnAddressSelectedListener {
        fun onAddressSelected(currentAddress: Address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAddressViewHoler {
        return UserAddressViewHoler(LayoutInflater.from(parent.context)
                .inflate(R.layout.mcn_fragment_item_endereco, parent, false))
    }

    override fun getItemCount(): Int {
        return addresses.size
    }

    override fun onBindViewHolder(holder: UserAddressViewHoler, position: Int) {
        holder.bind(addresses[position], isEditBlocked)
    }


    inner class UserAddressViewHoler(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(currentAddress: Address, isEditBlocked: Boolean) {

            val textAddressType = view
                    .findViewById<TypefaceTextView>(R.id.text_address_type)

            textAddressType.text = view.context.getString(R.string.text_address_types_content_template,
                    currentAddress.addressTypes.joinToString(", "))

            val textAddressContent = view.findViewById<TypefaceTextView>(R.id.text_address_content)

            textAddressContent.text = SpannableStringBuilder.valueOf(SpannableStringBuilder
                    .valueOf("${currentAddress.streetAddress ?: ""}, ${currentAddress.number ?: ""} " +
                            "- ${currentAddress.neighborhood ?: ""} - ${currentAddress.city ?: ""} " +
                            "- ${currentAddress.state ?: ""} \n${currentAddress.zipCode ?: ""}"))


            val linearEditButton = view.findViewById<LinearLayout>(R.id.linearAddressEditItem)

            showEditButtonWhenPossible(currentAddress, linearEditButton, isEditBlocked)

            linearEditButton.setOnClickListener {
                onAddressSelectedListener?.onAddressSelected(currentAddress)
            }

        }

        private fun showEditButtonWhenPossible(currentAddress: Address,
                                               linearEditButton: LinearLayout,
                                               isEditBlocked: Boolean) {

            if (currentAddress.addressTypes.isNotEmpty() && isEditBlocked.not()) {
                linearEditButton.visibility = View.VISIBLE
            } else {
                linearEditButton.visibility = View.GONE
            }
        }


    }



}

