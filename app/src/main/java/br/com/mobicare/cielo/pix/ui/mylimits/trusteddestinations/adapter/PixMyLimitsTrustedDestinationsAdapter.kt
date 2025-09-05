package br.com.mobicare.cielo.pix.ui.mylimits.trusteddestinations.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.LayoutPixMyLimitsTrustedDestinationsItemBinding
import br.com.mobicare.cielo.pix.domain.PixTrustedDestinationResponse
import br.com.mobicare.cielo.pix.ui.mylimits.trusteddestinations.PixMyLimitsTrustedDestinationsContract

class PixMyLimitsTrustedDestinationsAdapter(
    private val trustedDestinations: List<PixTrustedDestinationResponse>,
    val listener: PixMyLimitsTrustedDestinationsContract.View
) : RecyclerView.Adapter<PixMyLimitsTrustedDestinationsGroupHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PixMyLimitsTrustedDestinationsGroupHolder {
        val binding = LayoutPixMyLimitsTrustedDestinationsItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PixMyLimitsTrustedDestinationsGroupHolder(binding)
    }

    override fun getItemCount() = trustedDestinations.size

    override fun onBindViewHolder(holder: PixMyLimitsTrustedDestinationsGroupHolder, position: Int) {
        holder.bind(trustedDestinations[position], listener)
    }
}