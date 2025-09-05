package br.com.mobicare.cielo.pix.ui.keys.myKeys.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.pix.domain.MyKey
import br.com.mobicare.cielo.pix.ui.keys.myKeys.home.PixMyKeysContract

class PixMyKeysAdapter(
        private val myKeys: List<MyKey>,
        val listener: PixMyKeysContract.View,
        val context: Context,
        val isVerificationKey: Boolean = false
) : RecyclerView.Adapter<PixMyKeysGroupHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PixMyKeysGroupHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_pix_my_keys_item, parent, false)
        return PixMyKeysGroupHolder(view, context)
    }

    override fun getItemCount() = myKeys.size

    override fun onBindViewHolder(holder: PixMyKeysGroupHolder, position: Int) {
        holder.bind(myKeys[position], listener, isVerificationKey)
    }
}