package br.com.mobicare.cielo.pix.ui.qrCode.charge.key

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.commons.utils.getFormattedKey
import br.com.mobicare.cielo.commons.utils.getKeyType
import br.com.mobicare.cielo.databinding.CardViewMyKeyInfoBinding
import br.com.mobicare.cielo.pix.domain.MyKey

class PixQRCodeMyKeysAdapter(
    val context: Context,
    var list: List<MyKey>,
    val listener: PixQRCodeMyKeysAdapterListener
) : RecyclerView.Adapter<PixQRCodeMyKeysAdapter.MyKeyVH>() {
    var mSelectedItem = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyKeyVH {
        val binding = CardViewMyKeyInfoBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyKeyVH(binding)
    }

    override fun onBindViewHolder(holder: MyKeyVH, position: Int) {
        holder.binding.rbMyKey.isChecked = position == mSelectedItem
        holder.loadData(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setInitialSelectedKey(myKey: MyKey?) {
        mSelectedItem = list.indexOfFirst { it.key == myKey?.key }
        notifyItemChanged(mSelectedItem)
    }

    inner class MyKeyVH(val binding: CardViewMyKeyInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun loadData(myKey: MyKey) {
            binding.tvMyKeyName.text = myKey.key?.let { getFormattedKey(it, myKey.keyType) }
            binding.tvMyKeyType.text = getKeyType(myKey.key, myKey.keyType)

            val listener: View.OnClickListener = View.OnClickListener {
                notifyItemChanged(mSelectedItem)
                mSelectedItem = adapterPosition
                notifyItemChanged(mSelectedItem)
                listener.handleClick(list[mSelectedItem])
            }
            binding.rbMyKey.setOnClickListener(listener)
            itemView.setOnClickListener(listener)
        }
    }
}
