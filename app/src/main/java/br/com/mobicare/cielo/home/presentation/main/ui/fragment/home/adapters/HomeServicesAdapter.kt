package br.com.mobicare.cielo.home.presentation.main.ui.fragment.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.ItemServicesHomeBinding
import br.com.mobicare.cielo.home.presentation.main.HomeServiceContract
import br.com.mobicare.cielo.main.domain.Menu

class HomeServicesAdapter(private val services: List<Menu>, val listener: HomeServiceContract.View) :  RecyclerView.Adapter<HomeServiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeServiceViewHolder {
        val binding = ItemServicesHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeServiceViewHolder(binding)
    }

    override fun getItemCount() = services.size

    override fun onBindViewHolder(holder: HomeServiceViewHolder, position: Int) {
        holder.bind(services[position], listener)
    }
}