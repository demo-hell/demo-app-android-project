package br.com.mobicare.cielo.notification.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.commons.constants.NINE_HUNDRED_AND_SIXTY
import br.com.mobicare.cielo.commons.constants.SEVEN
import br.com.mobicare.cielo.commons.constants.TWELVE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.databinding.ContentMessageBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.notification.domain.NotificationItem

class ListNotificationAdapter(val list: List<NotificationItem>) :
    RecyclerView.Adapter<ListNotificationAdapter.ListNotificationViewHolder>() {

    private var height: Int = ZERO

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListNotificationViewHolder {
        val binding = ContentMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        height = parent.context.resources.displayMetrics.heightPixels
        return ListNotificationViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ListNotificationViewHolder, position: Int) {
        holder.bind(list[position], height)
    }

    class ListNotificationViewHolder(
        private val binding: ContentMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: NotificationItem, height: Int) {
            with(binding) {
                textMessageTitle.text = notification.title
                textMessage.text = notification.description

                notification.merchants?.firstOrNull()?.let {
                    if (it.read.not()) {
                        imgRead.visible()
                        if (height < NINE_HUNDRED_AND_SIXTY) imgRead.setPadding(SEVEN, TWELVE, ZERO, ZERO)
                    } else {
                        imgRead.gone()
                    }
                }
            }
        }
    }
}