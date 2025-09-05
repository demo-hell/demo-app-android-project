package br.com.mobicare.cielo.pixMVVM.presentation.home.adapters

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.ItemPixMenuTransactionsBinding
import br.com.mobicare.cielo.pixMVVM.presentation.home.models.PixTransactionButton
import br.com.mobicare.cielo.pixMVVM.presentation.home.models.PixTransactionButtonId

class PixTransactionsMenuAdapter(
    private val transactionButtons: List<PixTransactionButton>,
    private val onMenuButtonClick: (PixTransactionButtonId) -> Unit
) :  RecyclerView.Adapter<PixTransactionsMenuAdapter.PixTransactionsMenuHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PixTransactionsMenuHolder {
       return LayoutInflater
           .from(parent.context)
           .inflate(R.layout.item_pix_menu_transactions, parent, false).let {
                PixTransactionsMenuHolder(it)
            }
    }

    override fun getItemCount() = transactionButtons.size

    override fun onBindViewHolder(holder: PixTransactionsMenuHolder, position: Int) {
        holder.bind(transactionButtons[position], onMenuButtonClick)
    }

    inner class PixTransactionsMenuHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemPixMenuTransactionsBinding.bind(view)

        fun bind(button: PixTransactionButton, onMenuButtonClick: (PixTransactionButtonId) -> Unit) {
            binding.apply {
                tvMenuName.text = button.title
                ivMenuIcon.apply {
                    setImageResource(button.image)
                    setColorFilter(view.context.getColor(R.color.brand_400), PorterDuff.Mode.SRC_IN)
                }
                root.contentDescription = button.contentDescription
            }
            view.setOnClickListener { onMenuButtonClick(button.id)  }
        }

    }

}