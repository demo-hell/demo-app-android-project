package br.com.mobicare.cielo.home.presentation.main.ui.fragment.home.adapters

import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.ItemServicesHomeBinding
import br.com.mobicare.cielo.home.presentation.main.HomeServiceContract
import br.com.mobicare.cielo.main.domain.Menu
import com.squareup.picasso.Picasso

class HomeServiceViewHolder(private val binding: ItemServicesHomeBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(homeService: Menu, listener: HomeServiceContract.View) {
        binding.apply {
            Picasso.get()
                .load(homeService.icon)
                .into(ivHomeServicesItem)
            tvServicesHomeItem.text = homeService.name
            root.setOnClickListener {
                listener.onServiceClick(homeService)
            }
            root.contentDescription = this@HomeServiceViewHolder.itemView.context.getString(
                R.string.accessibility_button_description_pattern,
                homeService.name
            )
        }
    }
}